package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.BrandDto;
import intregatedproject.backend.entities.Brand;
import intregatedproject.backend.services.brandService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/itb-mshop/v1")
public class brandController {
    @Autowired
    private brandService service;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/brands")
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        List<Brand> brands = service.getAllBrands();
        List<BrandDto> brandDtos = brands.stream().map(brand -> modelMapper.map(brand,BrandDto.class)).collect(Collectors.toList());

        return ResponseEntity.ok(brandDtos);
    }
}
