package intregatedproject.backend.controllers;

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

    @DeleteMapping("/v1/brands/{id}")
    public ResponseEntity<Object> deleteSaleItem(@PathVariable int id) {
        service.deleteBrand(id);
        return ResponseEntity.status(204).body(null);
    }
}
