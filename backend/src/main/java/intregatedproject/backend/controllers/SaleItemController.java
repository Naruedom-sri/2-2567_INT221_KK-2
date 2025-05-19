package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.RequestSaleItemDto;
import intregatedproject.backend.dtos.ResponseSaleItemDetailDto;
import intregatedproject.backend.dtos.ResponseSaleItemDto;
import intregatedproject.backend.dtos.ResponseSaleItemDtoEdit;
import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.services.SaleItemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/itb-mshop")
public class SaleItemController {
    @Autowired
    private SaleItemService service;
    @Autowired
    private ModelMapper modelMapper;

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

    //dont touch my pbi10,11
    @GetMapping("/v1/sale-items/sort")
    public ResponseEntity<List<ResponseSaleItemDto>> getAllSortedByBrandName() {
        List<SaleItem> items = service.getAllSortedByBrandName();
        List<ResponseSaleItemDto> dtoList = items.stream()
                .map(item -> modelMapper.map(item, ResponseSaleItemDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }
}
