package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.RequestBrandDto;
import intregatedproject.backend.dtos.ResponseBrandDto;
import intregatedproject.backend.dtos.ResponseSaleItemDto;
import intregatedproject.backend.entities.Brand;
import intregatedproject.backend.services.BrandService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/itb-mshop")
@CrossOrigin(origins = "http://localhost:5173")
public class BrandController {
    @Autowired
    private BrandService service;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/v1/brands")
    public ResponseEntity<List<ResponseBrandDto>> getAllBrands() {
        List<Brand> brands = service.getAllBrands();
        List<ResponseBrandDto> brandDtos = brands.stream().map(brand -> modelMapper.map(brand, ResponseBrandDto.class)).collect(Collectors.toList());

        return ResponseEntity.ok(brandDtos);
    }

    private ResponseBrandDto convertToDto(Brand brand) {
        ResponseBrandDto dto = new ResponseBrandDto();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setWebsiteUrl(brand.getWebsiteUrl());
        dto.setCountryOfOrigin(brand.getCountryOfOrigin());
        dto.setIsActive(brand.getIsActive());
        dto.setNoOfSaleItems(brand.getSaleItems() != null ? brand.getSaleItems().size() : 0);
        return dto;
    }

    @GetMapping("/v1/brands/{id}")
    public ResponseEntity<ResponseBrandDto> getBrandById(@PathVariable int id) {
        Brand brand = service.getBrandById(id);
        return ResponseEntity.ok(convertToDto(brand));
    }

//    @GetMapping("/v1/brands/{id}")
//    public ResponseEntity<ResponseBrandDto> getBrandById(@PathVariable int id) {
//        Brand brand = service.getBrandById(id);
//        ResponseBrandDto responseBrandDto = modelMapper.map(brand, ResponseBrandDto.class);
//        return ResponseEntity.ok(responseBrandDto);
//    }

    @PostMapping("/v1/brands")
    public ResponseEntity<ResponseBrandDto> createBrand(@RequestBody RequestBrandDto brand) {
        Brand newBrand = service.createBrand(brand);
        ResponseBrandDto newBrandDto = modelMapper.map(newBrand, ResponseBrandDto.class);
        return ResponseEntity.status(201).body(newBrandDto);
    }

    @PutMapping("/v1/brands/{id}")
    public ResponseEntity<ResponseBrandDto> updateBrand(@PathVariable int id, @RequestBody RequestBrandDto requestBrandDto) {
        Brand updatedBrand = service.updateBrand(id, requestBrandDto);
        ResponseBrandDto updatedBrandDto = modelMapper.map(updatedBrand, ResponseBrandDto.class);
        return ResponseEntity.ok(updatedBrandDto);
    }

    @DeleteMapping("/v1/brands/{id}")
    public ResponseEntity<Object> deleteSaleItem(@PathVariable int id) {
        service.deleteBrand(id);
        return ResponseEntity.status(204).body(null);
    }

}
