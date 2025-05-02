package intregatedproject.backend.controllers;

import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.services.saleItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sale-items")
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
