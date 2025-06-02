package intregatedproject.backend.controllers;

import intregatedproject.backend.dtos.RequestBrandDto;
import intregatedproject.backend.dtos.ResponseBrandDetailDto;
import intregatedproject.backend.dtos.ResponseBrandDto;
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
@CrossOrigin(origins = {"http://localhost:5173","http://ip24kk2.sit.kmutt.ac.th"})
public class BrandController {
    @Autowired
    private BrandService service;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/v1/brands")
    public ResponseEntity<List<ResponseBrandDto>> getAllBrands() {
        List<Brand> brands = service.getAllBrands();
        List<ResponseBrandDto> listBrandDto = brands.stream().map(brand -> modelMapper.map(brand, ResponseBrandDto.class)).collect(Collectors.toList());
        return ResponseEntity.ok(listBrandDto);
    }

    @GetMapping("/v1/brands/{id}")
    public ResponseEntity<ResponseBrandDetailDto> getBrandById(@PathVariable int id) {
        Brand brand = service.getBrandById(id);
        ResponseBrandDetailDto responseBrandDto = modelMapper.map(brand, ResponseBrandDetailDto.class);
        return ResponseEntity.ok(responseBrandDto);
    }

    @PostMapping("/v1/brands")
    public ResponseEntity<ResponseBrandDetailDto> createBrand(@RequestBody RequestBrandDto brand) {
        Brand newBrand = service.createBrand(brand);
        ResponseBrandDetailDto newBrandDto = modelMapper.map(newBrand, ResponseBrandDetailDto.class);
        return ResponseEntity.status(201).body(newBrandDto);
    }

    @PutMapping("/v1/brands/{id}")
    public ResponseEntity<ResponseBrandDetailDto> updateBrand(@PathVariable int id, @RequestBody RequestBrandDto requestBrandDto) {
        Brand updatedBrand = service.updateBrand(id, requestBrandDto);
        ResponseBrandDetailDto updatedBrandDto = modelMapper.map(updatedBrand, ResponseBrandDetailDto.class);
        return ResponseEntity.ok(updatedBrandDto);
    }

    @DeleteMapping("/v1/brands/{id}")
    public ResponseEntity<Object> deleteSaleItem(@PathVariable int id) {
        service.deleteBrand(id);
        return ResponseEntity.status(204).body(null);
    }

}
