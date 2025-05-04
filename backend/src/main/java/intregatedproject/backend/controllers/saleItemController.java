package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.SaleItemDetailDto;
import intregatedproject.backend.dtos.SaleItemDto;
import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.services.saleItemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = "http://localhost:5173")
public class saleItemController {
    @Autowired
    private saleItemService service;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/v1/sale-items")
    public ResponseEntity<List<SaleItemDto>> getAllSaleItems() {
        List<SaleItem> items = service.getAllSaleItems();
        List<SaleItemDto> dtoList = items.stream()
                .map(item -> modelMapper.map(item, SaleItemDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/v1/sale-items/{id}")
    public ResponseEntity<SaleItemDetailDto> getSaleItemById(@PathVariable int id) {
        SaleItem item = service.getSaleItemById(id);
        SaleItemDetailDto dto = modelMapper.map(item, SaleItemDetailDto.class);
        return ResponseEntity.ok(dto);
    }

}
