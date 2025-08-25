package intregatedproject.backend.services;

import intregatedproject.backend.dtos.SaleItemImageRequest;
import intregatedproject.backend.dtos.SaleItemWithImageInfo;
import intregatedproject.backend.dtos.saleitems.RequestSaleItemDto;
import intregatedproject.backend.entities.Brand;
import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.entities.SaleItemImage;
import intregatedproject.backend.exceptions.PriceException;
import intregatedproject.backend.repositories.SaleItemImageRepository;
import intregatedproject.backend.repositories.SaleItemRepository;
import intregatedproject.backend.utils.SaleItemSpecification;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SaleItemService {
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private BrandService brandService;
    @Autowired
    private EntityManager entityManager;    
    @Autowired
    private SaleItemImageRepository saleItemImageRepository;
    @Autowired
    private FileService fileService;

    public List<SaleItem> getAllSaleItems() {
        try {
            return saleItemRepository.findAll(Sort.by(Sort.Order.asc("createdOn"), Sort.Order.asc("id")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve sale items from database.", e);
        }
    }

    public SaleItem getSaleItemById(int id) {
        try {
            return saleItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sale item with id " + id + " not found"));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while fetching SaleItem with id " + id, e);
        }
    }

    private void covertDtoToEntity(RequestSaleItemDto saleItemDto, SaleItem item) {
        item.setModel(saleItemDto.getModel());
        item.setRamGb(saleItemDto.getRamGb());
        item.setStorageGb(saleItemDto.getStorageGb());
        item.setPrice(saleItemDto.getPrice());
        item.setDescription(saleItemDto.getDescription());
        item.setScreenSizeInch(saleItemDto.getScreenSizeInch());
        item.setColor(saleItemDto.getColor());
        item.setQuantity(saleItemDto.getQuantity());
        Brand brand = brandService.getBrandById(saleItemDto.getBrandId());
        item.setBrand(brand);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SaleItem createSaleItem(RequestSaleItemDto saleItemDto) {
        if (saleItemDto.getId() != null && saleItemRepository.existsById(saleItemDto.getId())) {
            throw new RuntimeException("Sale item with id " + saleItemDto.getId() + " already exists");
        }
        var newSaleItem = new SaleItem();
        covertDtoToEntity(saleItemDto, newSaleItem);
        var saved = saleItemRepository.save(newSaleItem);
        entityManager.refresh(saved);
        return saved;
    }

    public SaleItem updateSaleItem(int id, RequestSaleItemDto saleItemDto) {
        SaleItem updateSaleItem = getSaleItemById(id);
        covertDtoToEntity(saleItemDto, updateSaleItem);
        return saleItemRepository.save(updateSaleItem);
    }

    public void deleteSaleItemById(int id) {
        SaleItem existingItem = getSaleItemById(id);
        saleItemRepository.delete(existingItem);
    }


    public Page<SaleItem> getAllSortedAndFiltered(List<String> filterBrands, List<Integer> filterStorages, String searchContent, Integer filterPriceLower, Integer filterPriceUpper, String sortField, String sortDirection, Integer page, Integer size) {
        filterBrands = filterBrands == null || filterBrands.isEmpty() ? null : filterBrands;
        searchContent = searchContent == null || searchContent.isEmpty() ? null : searchContent;
        size = size <= 0 ? 10 : size;
        page = (page < 0 ? 0 : page);
        if (filterPriceLower == null && filterPriceUpper != null) {
            throw new PriceException("Required parameter 'filterPriceLower' is not present.");
        } else if (filterPriceLower != null && filterPriceUpper == null) {
            throw new PriceException("Required parameter 'filterPriceUpper' is not present.");
        }
        Sort sort;
        if ("brand.name".equalsIgnoreCase(sortField) ||
                "price".equalsIgnoreCase(sortField) ||
                "model".equalsIgnoreCase(sortField) ||
                "storageGb".equalsIgnoreCase(sortField) ||
                "ramGb".equalsIgnoreCase(sortField) ||
                "description".equalsIgnoreCase(sortField) ||
                "screenSizeInch".equalsIgnoreCase(sortField) ||
                "color".equalsIgnoreCase(sortField) ||
                "quantity".equalsIgnoreCase(sortField)) {
            sort = (sortDirection.equalsIgnoreCase("asc")) ? Sort.by(Sort.Order.asc(sortField)) : Sort.by(Sort.Order.desc(sortField));
        } else {
            sort = Sort.by(Sort.Order.asc("createdOn"), Sort.Order.asc("id"));
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<SaleItem> searchSpec = Specification.where(SaleItemSpecification.hasColor(searchContent)).or(SaleItemSpecification.hasDescription(searchContent)).or(SaleItemSpecification.hasModel(searchContent));
        Specification<SaleItem> filterSpec = Specification.where(SaleItemSpecification.hasFilterBrands(filterBrands)).and(SaleItemSpecification.hasFilterStorages(filterStorages)).and(SaleItemSpecification.hasPrices(filterPriceLower, filterPriceUpper)).and(searchSpec);
        return saleItemRepository.findAll(filterSpec, pageable);

//        if (filterBrands == null && filterStorages == null && filterPriceLower == null) {
//            return saleItemRepository.findAllWithPage(pageable);
//        } else if (filterBrands == null && filterStorages == null) {
//            return saleItemRepository.findAllByPriceBetween(filterPriceLower, filterPriceUpper, pageable);
//        } else if (filterBrands != null && filterStorages == null && filterPriceLower == null) {
//            return saleItemRepository.findAllByBrand_NameIn(filterBrands, pageable);
//        } else if (filterBrands == null && filterPriceLower == null) {
//            return saleItemRepository.findAllByStorageGb(filterStorages, includeNull, pageable);
//        } else if (filterBrands != null && filterStorages == null) {
//            return saleItemRepository.findAllByBrand_NameInAndPriceBetween(filterBrands, filterPriceLower, filterPriceUpper, pageable);
//        } else if (filterBrands == null) {
//            return saleItemRepository.findAllByStorageGbAndPrice(filterStorages, includeNull, filterPriceLower, filterPriceUpper, pageable);
//        } else if (filterPriceLower == null) {
//            return saleItemRepository.findAllByBrandNameAndStorageGb(filterBrands, filterStorages, includeNull, pageable);
//        } else {
//
//          return saleItemRepository.findAllByBrandNameAndStorageGbAndPrice(filterBrands, filterStorages, includeNull, filterPriceLower, filterPriceUpper, pageable);
//    }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SaleItem createSaleItemImage(RequestSaleItemDto saleItemDto, List<MultipartFile> files) {
        // กันซ้ำ id ตามที่คุณทำไว้เดิม
        if (saleItemDto.getId() != null && saleItemRepository.existsById(saleItemDto.getId())) {
            throw new RuntimeException("Sale item with id " + saleItemDto.getId() + " already exists");
        }

        // 1) เซฟตัวสินค้าเพื่อให้ได้ PK ก่อน
        var newSaleItem = new SaleItem();
        covertDtoToEntity(saleItemDto, newSaleItem);
        var savedSaleItem = saleItemRepository.save(newSaleItem);

        // 2) เก็บไฟล์ลง storage รอบเดียวด้วย multiStore -> ได้ "ชื่อไฟล์ที่ถูกเซฟจริง" กลับมาตามลำดับ
        List<MultipartFile> safeFiles = (files == null) ? List.of() : files;
        List<String> storedFileNames = fileService.multiStore(safeFiles); // คืนชื่อไฟล์จริง

        // 3) สร้าง SaleItemImage entities (จำกัด imageViewOrder = 1..4 ต่อ sale item)
        List<SaleItemImage> imageEntities = new ArrayList<>();
        int limit = Math.min(storedFileNames.size(), 4);
        for (int i = 0; i < limit; i++) {
            MultipartFile file = safeFiles.get(i);

            // กันกรณี originalFilename เป็น null จาก MultipartFile บางชนิด
            String originalName = file.getOriginalFilename();
            String ogFileName = (originalName == null) ? "" : org.springframework.util.StringUtils.cleanPath(originalName);
            String storedName = storedFileNames.get(i);

            SaleItemImage image = new SaleItemImage();
            image.setSaleItem(savedSaleItem);
            image.setFileName(storedName);
            image.setOgFileName(ogFileName);
            image.setImageViewOrder(i + 1);

            imageEntities.add(image);
        }

        // 4) เพิ่ม imageEntities เข้า savedSaleItem เพื่อให้ bidirectional sync
        savedSaleItem.getSaleItemImages().addAll(imageEntities);

        // 5) เซฟลง sale_item_image
        if (!imageEntities.isEmpty()) {
            saleItemImageRepository.saveAll(imageEntities);
        }
        entityManager.refresh(savedSaleItem);
        // **ไม่ต้อง refresh อีกแล้ว**
        return savedSaleItem;
    }

    public void deleteSaleItemByIdWImage(int id) {
        // ลบสินค้า พร้อมลบไฟล์รูปภาพบนดิสก์ทั้งหมดที่เกี่ยวข้องเพื่อไม่ให้เหลือไฟล์ขยะ
        SaleItem existingItem = getSaleItemById(id);
        List<String> filenames = existingItem.getSaleItemImages().stream().map(SaleItemImage::getFileName).toList();
        for (String filename : filenames) {
            fileService.removeFile(filename);
        }
        saleItemRepository.delete(existingItem);
    }

    @Transactional
    public SaleItem updateSaleItemWithImages(int id, SaleItemWithImageInfo request) {
        SaleItem saleItem = getSaleItemById(id);
        // Avoid mapping "id" from DTO (which is intentionally null) into a managed entity.
        // Using the dedicated copier ensures we don't alter primary key and only update allowed fields.
        covertDtoToEntity(request.getSaleItem(), saleItem);

        List<SaleItemImageRequest> imageRequests = request.getImageInfos();
        if (imageRequests == null || imageRequests.isEmpty()) {
            // No image operations requested — just persist the basic field updates.
            return saleItemRepository.save(saleItem);
        }

        // 1) ทำ DELETE ก่อน เพื่อให้การรีเรียงลำดับที่เหลือทำได้ถูกต้อง
        imageRequests.stream().filter(req -> req.getState() == SaleItemImageRequest.ImageState.DELETE).forEach(req -> {
            saleItem.getSaleItemImages().removeIf(img -> {
                if (img.getFileName().equals(req.getFileName())) {
                    fileService.removeFile(img.getFileName());
                    return true;
                }
                return false;
            });
        });

        // รีเรียงลำดับหลังลบ
        List<SaleItemImage> currentImages = saleItem.getSaleItemImages().stream().sorted(Comparator.comparing(SaleItemImage::getImageViewOrder)).collect(Collectors.toList());

        for (int i = 0; i < currentImages.size(); i++) {
            currentImages.get(i).setImageViewOrder(i + 1);
        }

        // 2) จัดการคำสั่ง UPDATE / REPLACE / CREATE ตามลำดับคำขอ
        for (SaleItemImageRequest req : imageRequests) {
            switch (req.getState()) {
                case CREATE -> {
                    // เพิ่มรูปใหม่: ไม่ต้องส่ง order มาก็ได้ → ถ้าไม่มีจะต่อท้าย
                    String newFileName = fileService.store(req.getImageFile());
                    SaleItemImage newImage = new SaleItemImage();
                    newImage.setSaleItem(saleItem);
                    newImage.setFileName(newFileName);
                    newImage.setOgFileName(req.getImageFile().getOriginalFilename());

                    // ถ้า request ไม่ได้ส่ง imageViewOrder → ให้ต่อท้าย
                    int nextOrder;
                    if (req.getImageViewOrder() == null || req.getImageViewOrder() <= 0) {
                        nextOrder = saleItem.getSaleItemImages().size() + 1;
                    } else {
                        nextOrder = req.getImageViewOrder();
                    }
                    newImage.setImageViewOrder(nextOrder);

                    saleItem.getSaleItemImages().add(newImage);
                }

                case UPDATE -> {
                    // เปลี่ยนลำดับของรูปเดิม: อ้างอิงจาก fileName ที่อยู่ในระบบ
                    saleItem.getSaleItemImages().stream().filter(img -> img.getFileName().equals(req.getFileName())).findFirst().ifPresent(img -> img.setImageViewOrder(req.getImageViewOrder()));
                }
                case REPLACE -> {
                    // แทนที่ไฟล์เดิมด้วยไฟล์ใหม่: ต้องมี fileName ของรูปเดิม + imageFile ใหม่
                    saleItem.getSaleItemImages().stream().filter(img -> img.getFileName().equals(req.getFileName())).findFirst().ifPresent(img -> {
                        fileService.removeFile(img.getFileName());
                        String newFileName = fileService.store(req.getImageFile());
                        img.setFileName(newFileName);
                        img.setOgFileName(req.getImageFile().getOriginalFilename());
                    });
                }
                case DELETE -> {
                    // ทำการลบไปแล้วในขั้นตอนที่ 1 (ด้านบน) ตรงนี้จึงไม่ต้องทำอะไรเพิ่ม
                }
                case KEEP -> {
                    // ไม่ต้องเปลี่ยนแปลงรูป
                }
            }
        }

        // 3) เรียงลำดับรูปภาพอีกรอบเพื่อความชัดเจน (1..n)
        saleItem.getSaleItemImages().sort(Comparator.comparing(SaleItemImage::getImageViewOrder));

        return saleItemRepository.save(saleItem);
    }

}



