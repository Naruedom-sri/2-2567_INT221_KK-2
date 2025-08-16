package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.*;
import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.entities.SaleItemImage;
import intregatedproject.backend.services.FileService;
import intregatedproject.backend.services.SaleItemService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = {"http://localhost:5173","http://ip24kk2.sit.kmutt.ac.th"})
public class SaleItemController {
    @Autowired
    private SaleItemService service;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;

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
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdOn") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        filterBrands = filterBrands == null ? List.of() : filterBrands;
        page = page == null || page < 0 ? 0 : page;
        size = size == null || size <= 0 ? 10 : size;
        sortField = sortField == null || sortField.isBlank() ? "createdOn" : sortField;
        sortDirection = sortDirection == null || sortDirection.isBlank() ? "asc" : sortDirection;

        Page<SaleItem> pageResult = service.getAllSortedAndFiltered(filterBrands, sortField, sortDirection, page, size);
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

    @PostMapping(value = "/v2/sale-items",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseSaleItemImageDtoV2> createSaleItemImages(
                @ModelAttribute RequestSaleItemDto saleItemCreateDTO ,
                @RequestPart(value = "images", required = false) List<MultipartFile> images){

        SaleItem saleitem = service.createSaleItemImage(saleItemCreateDTO,images);

        ResponseSaleItemImageDtoV2 response = modelMapper.map(saleitem, ResponseSaleItemImageDtoV2.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/v2/sale-items/{id}")
    public ResponseEntity<ResponseSaleItemImageDtoV2> getSaleItemByIdImage(@PathVariable int id) {
        SaleItem item = service.getSaleItemById(id);
        ResponseSaleItemImageDtoV2 dto = modelMapper.map(item, ResponseSaleItemImageDtoV2.class);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/v2/sale-items/{id}/images/{imageViewOrder}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable int id,@PathVariable Integer imageViewOrder) {
        SaleItem item = service.getSaleItemById(id);
        SaleItemImage image = item.getSaleItemImages().stream()
                .filter(img -> img.getImageViewOrder().equals(imageViewOrder))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Image with order " + imageViewOrder + " not found for saleItem id " + id));

        Resource file = fileService.loadFileAsResource(image.getFileName());
        return ResponseEntity.ok().contentType(MediaType.valueOf(fileService.getFileType(file))).body(file);
    }


}
