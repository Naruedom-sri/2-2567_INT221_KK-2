package intregatedproject.backend.controllers;

import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.services.saleItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/sale-items")
@CrossOrigin(origins = "http://localhost:5173")
public class saleItemController {
    @Autowired
    private saleItemService service;

    @GetMapping("")
    public ResponseEntity<List<SaleItem>> getAllSaleItems(){
        List<SaleItem> items = service.getAllSaleItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleItem> getSaleItemById(@PathVariable int id){
        SaleItem item = service.getSaleItemById(id);
        return ResponseEntity.ok(item);
    }
}
