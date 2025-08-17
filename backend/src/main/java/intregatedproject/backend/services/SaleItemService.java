package intregatedproject.backend.services;

import intregatedproject.backend.dtos.saleitems.RequestSaleItemDto;
import intregatedproject.backend.entities.Brand;
import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.exceptions.PageIsNotPresentException;
import intregatedproject.backend.exceptions.PriceException;
import intregatedproject.backend.repositories.SaleItemRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SaleItemService {
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private BrandService brandService;
    @Autowired
    private EntityManager entityManager;

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
        Brand brand = brandService.getBrandById(saleItemDto.getBrand().getId());
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


    public Page<SaleItem> getAllSortedAndFiltered(List<String> filterBrands, List<Integer> filterStorages, Integer filterPriceLower, Integer filterPriceUpper, String sortField, String sortDirection, Integer page, Integer size) {
        filterBrands = filterBrands == null || filterBrands.isEmpty() ? null : filterBrands;
        filterStorages = filterStorages == null || filterStorages.isEmpty() ? null : filterStorages;
        size = size <= 0 ? 10 : size;
        if (page == null || page < 0) {
            throw new PageIsNotPresentException("Required parameter 'page' is not present.");
        }

        if (filterPriceLower == null && filterPriceUpper != null) {
            throw new PriceException("Required parameter 'filterPriceLower' is not present.");
        } else if (filterPriceLower != null && filterPriceUpper == null) {
            throw new PriceException("Required parameter 'filterPriceUpper' is not present.");
        }
        Sort sort;
        if ("brand.name".equalsIgnoreCase(sortField)) {
            sort = (sortDirection.equalsIgnoreCase("asc")) ? Sort.by(Sort.Order.asc(sortField)) : Sort.by(Sort.Order.desc(sortField));
        } else {
            sort = Sort.by(Sort.Order.asc("id"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        if (filterBrands == null && filterStorages == null && filterPriceLower == null) {
            return saleItemRepository.findAllWithPage(pageable);
        } else if (filterBrands == null && filterStorages == null) {
            return saleItemRepository.findAllByPriceBetween(filterPriceLower, filterPriceUpper, pageable);
        } else if (filterBrands != null && filterStorages == null && filterPriceLower == null) {
                return saleItemRepository.findAllByBrand_NameIn(filterBrands, pageable);
        } else if (filterBrands == null && filterPriceLower == null) {
                return saleItemRepository.findAllByStorageGb_In(filterStorages, pageable);
        } else if (filterBrands != null && filterStorages == null) {
            return saleItemRepository.findAllByBrand_NameInAndPriceBetween(filterBrands,filterPriceLower, filterPriceUpper, pageable);
        } else if (filterBrands == null) {
            return saleItemRepository.findAllByStorageGb_InAndPriceBetween(filterStorages,filterPriceLower, filterPriceUpper, pageable);
        } else if (filterPriceLower == null) {
            return saleItemRepository.findAllByBrand_NameInAndStorageGb_In(filterBrands, filterStorages, pageable);
        } else {
            return saleItemRepository.findAllByBrand_NameInAndStorageGbInAndPriceBetween(filterBrands, filterStorages, filterPriceLower, filterPriceUpper, pageable);
        }
    }

}




