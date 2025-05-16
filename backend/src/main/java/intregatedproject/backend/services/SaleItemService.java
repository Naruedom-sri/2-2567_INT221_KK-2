package intregatedproject.backend.services;

import intregatedproject.backend.dtos.RequestSaleItemDto;
import intregatedproject.backend.entities.Brand;
import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.repositories.SaleItemRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
            return saleItemRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve sale items from database.", e);
        }
    }

    public SaleItem getSaleItemById(int id) {
        try {
            return saleItemRepository.findById(id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Sale item with id " + id + " not found"));
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
        covertDtoToEntity(saleItemDto,updateSaleItem);
        return saleItemRepository.save(updateSaleItem);
    }

    public void deleteSaleItemById(int id) {
        SaleItem existingItem = getSaleItemById(id);
        saleItemRepository.delete(existingItem);
    }

    //dont touch my pbi10,11
    public List<SaleItem> getAllSortedByBrandName(){
        return saleItemRepository.findAll(
                Sort.by(Sort.Direction.ASC, "brand.name")
        );
    }


}
