package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.saleitems.*;
import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.services.SaleItemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = {"http://localhost:5173", "http://ip24kk2.sit.kmutt.ac.th"})
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


    @GetMapping("/v2/sale-items")
    public ResponseEntity<ResponseSaleItemDtoV2> getAllSaleItemBySortedAndFilterByBrandName(
            @RequestParam(required = false) List<String> filterBrands,
            @RequestParam(required = false) List<Integer> filterStorages,
            @RequestParam(required = false) Integer filterPriceLower,
            @RequestParam(required = false) Integer filterPriceUpper,
            @RequestParam Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdOn") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Page<SaleItem> pageResult = service.getAllSortedAndFiltered(filterBrands, filterStorages, filterPriceLower, filterPriceUpper, sortField, sortDirection, page, size);
        List<ResponseSaleItemDetailDto> saleItemsDto = pageResult.getContent().stream()
                .map(saleItem -> modelMapper.map(saleItem, ResponseSaleItemDetailDto.class))
                .collect(Collectors.toList());

        ResponseSaleItemDtoV2 dto = new ResponseSaleItemDtoV2();
        dto.setContent(saleItemsDto);
        dto.setPage(page);
        dto.setSize(pageResult.getSize());
        dto.setSort(sortField + ": " + sortDirection.toUpperCase());
        dto.setFirst(pageResult.isFirst());
        dto.setLast(pageResult.isLast());
        dto.setTotalPages(pageResult.getTotalPages());
        dto.setTotalElements((int) pageResult.getTotalElements());

        return ResponseEntity.ok(dto);
    }

}
