package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.SaleItemImageRequest;
import intregatedproject.backend.dtos.SaleItemWithImageInfo;
import intregatedproject.backend.dtos.saleitems.*;
import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.entities.SaleItemImage;
import intregatedproject.backend.services.FileService;
import intregatedproject.backend.services.SaleItemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = {"http://localhost:5173", "http://ip24kk2.sit.kmutt.ac.th"})
public class SaleItemController {
    @Autowired
    private SaleItemService service;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;

    @GetMapping("/v1/sale-items")
    public ResponseEntity<List<ResponseSaleItemDto>> getAllSaleItems() {
        List<SaleItem> items = service.getAllSaleItems();
        List<ResponseSaleItemDto> dtoList = items.stream()
                .map(item -> modelMapper.map(item, ResponseSaleItemDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }


    @GetMapping("/v1/sale-items/{id}")
    public ResponseEntity<ResponseSaleItemDetailDto> getSaleItemById(@PathVariable int id) {
        SaleItem item = service.getSaleItemById(id);
        ResponseSaleItemDetailDto dto = modelMapper.map(item, ResponseSaleItemDetailDto.class);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/v1/sale-items/edit/{id}")
    public ResponseEntity<ResponseSaleItemDtoEdit> getSaleItemByIdEdit(@PathVariable int id) {
        SaleItem item = service.getSaleItemById(id);
        ResponseSaleItemDtoEdit dto = modelMapper.map(item, ResponseSaleItemDtoEdit.class);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/v1/sale-items")
    public ResponseEntity<ResponseSaleItemDetailDto> createSaleItem(@RequestBody RequestSaleItemDto saleItem) {
        SaleItem newSaleItem = service.createSaleItem(saleItem);
        ResponseSaleItemDetailDto newSaleItemDto = modelMapper.map(newSaleItem, ResponseSaleItemDetailDto.class);
        return ResponseEntity.status(201).body(newSaleItemDto);
    }

    @PutMapping("/v1/sale-items/{id}")
    public ResponseEntity<ResponseSaleItemDetailDto> updateSaleItem(@PathVariable int id, @RequestBody RequestSaleItemDto requestSaleItemDto) {
        SaleItem updatedSaleItem = service.updateSaleItem(id, requestSaleItemDto);
        ResponseSaleItemDetailDto updateSaleItemDto = modelMapper.map(updatedSaleItem, ResponseSaleItemDetailDto.class);
        return ResponseEntity.ok(updateSaleItemDto);
    }

    @DeleteMapping("/v1/sale-items/{id}")
    public ResponseEntity<Object> deleteSaleItem(@PathVariable int id) {
        service.deleteSaleItemById(id);
        return ResponseEntity.status(204).body(null);
    }

    // V2
    @GetMapping("/v2/sale-items/{id}")
    public ResponseEntity<ResponseSaleItemImageDtoV2> getSaleItemByIdWithImage(@PathVariable int id) {
        // ดึงรายละเอียดสินค้า (เวอร์ชัน V2) พร้อมรายการรูปภาพในรูปแบบ DTO สำหรับตอบกลับหน้าบ้าน
        SaleItem item = service.getSaleItemById(id);
        ResponseSaleItemImageDtoV2 dto = modelMapper.map(item, ResponseSaleItemImageDtoV2.class);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/v2/sale-items/edit/{id}")
    public ResponseEntity<ResponseSaleItemDtoEditV2> getSaleItemByIdEditV2(@PathVariable int id) {
        SaleItem item = service.getSaleItemById(id);
        ResponseSaleItemDtoEditV2 dto = modelMapper.map(item, ResponseSaleItemDtoEditV2.class);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/v2/sale-items/{id}/images/{imageViewOrder}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable int id, @PathVariable Integer imageViewOrder) {
        // ให้บริการไฟล์รูปตามลำดับการแสดงผล (imageViewOrder) ของสินค้า id ที่ระบุ
        // ใช้เมื่อหน้าบ้านต้องการดาวน์โหลด/แสดงไฟล์จริงจาก storage
        SaleItem item = service.getSaleItemById(id);
        SaleItemImage image = item.getSaleItemImages().stream()
                .filter(img -> img.getImageViewOrder().equals(imageViewOrder))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Image with order " + imageViewOrder + " not found for saleItem id " + id));

        Resource file = fileService.loadFileAsResource(image.getFileName());
        return ResponseEntity.ok().contentType(MediaType.valueOf(fileService.getFileType(file))).body(file);
    }

    @PostMapping(value = "/v2/sale-items",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseSaleItemImageDtoV2> createSaleItemImages(
            @ModelAttribute RequestSaleItemDto saleItemCreateDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        // สร้างสินค้าใหม่พร้อมอัปโหลดรูปภาพหลายไฟล์ในครั้งเดียว
        // ฟิลด์สินค้า: saleItem.xxx
        // รูปภาพ (หลายไฟล์): key = images (เป็น MultipartFile list)
        SaleItem saleitem = service.createSaleItemImage(saleItemCreateDTO, images);

        ResponseSaleItemImageDtoV2 response = modelMapper.map(saleitem, ResponseSaleItemImageDtoV2.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/v2/sale-items/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseSaleItemImageDtoV2> updateSaleItem(
            @PathVariable int id,
            @ModelAttribute SaleItemWithImageInfo request) {

        // อัปเดตข้อมูลสินค้า + จัดการรูปภาพ (CREATE / UPDATE / REPLACE / DELETE / KEEP)
        // รูปแบบฟอร์มรูปภาพ: imageInfos[n].state, .fileName, .imageViewOrder, .imageFile
        SaleItem updated = service.updateSaleItemWithImages(id, request);
        ResponseSaleItemImageDtoV2 dto = modelMapper.map(updated, ResponseSaleItemImageDtoV2.class);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/v2/sale-items/{id}")
    public ResponseEntity<Object> deleteSaleItemWImg(@PathVariable int id) {
        service.deleteSaleItemByIdWImage(id);
        return ResponseEntity.status(204).body(null);
    }

    @GetMapping("/v2/sale-items")
    public ResponseEntity<PageDto> getAllSaleItemBySortedAndFilterByBrandName(
            @RequestParam(required = false) List<String> filterBrands,
            @RequestParam(required = false) List<String> filterStorages,
            @RequestParam(required = false) String searchContent,
            @RequestParam(required = false) Integer filterPriceLower,
            @RequestParam(required = false) Integer filterPriceUpper,
            @RequestParam Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdOn") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        List<Integer> covertFilterStorages = filterStorages == null || filterStorages.isEmpty() ? null : filterStorages.stream().map(s -> Objects.equals(s, "null") ? null : Integer.parseInt(s)).toList();
        Page<SaleItem> pageResult = service.getAllSortedAndFiltered(filterBrands, covertFilterStorages, searchContent, filterPriceLower, filterPriceUpper, sortField, sortDirection, page, size);
        List<ResponseSaleItemDetailDto> saleItemsDto = pageResult.getContent().stream()
                .map(saleItem -> modelMapper.map(saleItem, ResponseSaleItemDetailDto.class))
                .collect(Collectors.toList());
        PageDto dto = new PageDto();
        dto.setContent(saleItemsDto);
        dto.setPage(page);
        dto.setSize(pageResult.getSize());
        dto.setSort(sortField + ": " + sortDirection.toUpperCase());
        dto.setFirst(pageResult.isFirst());
        dto.setLast(pageResult.isLast());
        dto.setTotalPages(pageResult.getTotalPages());
        dto.setTotalElements((int) pageResult.getTotalElements());
        return ResponseEntity.ok(dto);
    }
}

