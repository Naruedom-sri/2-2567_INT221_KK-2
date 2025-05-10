package intregatedproject.backend.services;

import intregatedproject.backend.dtos.RequestSaleItemDto;
import intregatedproject.backend.entities.Brand;
import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.repositories.SaleItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleItemService {
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private BrandService brandService;

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

    public SaleItem createSaleItem(SaleItem saleItem) {
        if (saleItem.getId() != null && saleItemRepository.existsById(saleItem.getId())) {
            throw new RuntimeException("Sale item with id " + saleItem.getId() + " already exists");
        }
        return saleItemRepository.save(saleItem);

    }

    public SaleItem updateSaleItem(int id, RequestSaleItemDto saleItemDto) {
        SaleItem updateSaleItem = getSaleItemById(id);
        updateSaleItem.setModel(saleItemDto.getModel());
        updateSaleItem.setRamGb(saleItemDto.getRamGb());
        updateSaleItem.setStorageGb(saleItemDto.getStorageGb());
        updateSaleItem.setPrice(saleItemDto.getPrice());
        updateSaleItem.setDescription(saleItemDto.getDescription());
        updateSaleItem.setScreenSizeInch(saleItemDto.getScreenSizeInch());
        updateSaleItem.setColor(saleItemDto.getColor());
        updateSaleItem.setQuantity(saleItemDto.getQuantity());
        Brand brand = brandService.getBrandById(saleItemDto.getBrand().getId());
        updateSaleItem.setBrand(brand);
        return saleItemRepository.save(updateSaleItem);
    }

    public SaleItem deleteSaleItemById(int id) {
        SaleItem existingItem = saleItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale item with id " + id + " not found"));
        saleItemRepository.delete(existingItem);
        return existingItem;
    }
}
