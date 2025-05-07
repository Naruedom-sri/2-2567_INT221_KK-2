package intregatedproject.backend.services;

import intregatedproject.backend.dtos.BrandDto;
import intregatedproject.backend.dtos.RequestSaleItemDto;
import intregatedproject.backend.entities.Brand;
import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.repositories.BrandRepository;
import intregatedproject.backend.repositories.SaleItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class saleItemService {
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private BrandRepository brandRepository;

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
        if (saleItemRepository.existsById(saleItem.getId())) {
            throw new RuntimeException("Sale item with id " + saleItem.getId() + " already exists");
        }else {
        return saleItemRepository.save(saleItem);
        }
    }

    public SaleItem updateSaleItem(int id, RequestSaleItemDto saleItemDetailDto) {
        SaleItem updateSaleItem = getSaleItemById(id);

        updateSaleItem.setModel(saleItemDetailDto.getModel());
        updateSaleItem.setRamGb(saleItemDetailDto.getRamGb());
        updateSaleItem.setStorageGb(saleItemDetailDto.getStorageGb());
        updateSaleItem.setPrice(saleItemDetailDto.getPrice());
        updateSaleItem.setDescription(saleItemDetailDto.getDescription());
        updateSaleItem.setScreenSizeInch(saleItemDetailDto.getScreenSizeInch());
        updateSaleItem.setColor(saleItemDetailDto.getColor());
        updateSaleItem.setQuantity(saleItemDetailDto.getQuantity());
        Brand brand = brandRepository.findById(saleItemDetailDto.getBrand().getId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        updateSaleItem.setBrand(brand);
        return saleItemRepository.save(updateSaleItem);
    }
}
