package intregatedproject.backend.services;

import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.repositories.SaleItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class saleItemService {
    @Autowired
    private SaleItemRepository saleItemRepository;

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


}
