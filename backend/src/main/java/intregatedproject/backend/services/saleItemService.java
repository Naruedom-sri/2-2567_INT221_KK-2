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
        return saleItemRepository.findAll();
    }

    public SaleItem getSaleItemById(int id) {
        return saleItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sale item with id " + id + " not found"));
    }


}
