package intregatedproject.backend.services;

import intregatedproject.backend.dtos.RequestBrandDto;
import intregatedproject.backend.dtos.RequestSaleItemDto;
import intregatedproject.backend.entities.Brand;
import intregatedproject.backend.entities.SaleItem;
import intregatedproject.backend.repositories.BrandRepository;
import intregatedproject.backend.repositories.SaleItemRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private EntityManager entityManager;

    public List<Brand> getAllBrands() {
        try {
            return brandRepository.findAll(Sort.by("name").ascending());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve brands from database.", e);
        }
    }

    public Brand getBrandById(Integer id) {
        try{
            return brandRepository.findById(id).orElseThrow(() ->
                    new ResourceNotFoundException("Brand with id " + id + " not found"));
        }catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while fetching Brand with id " + id, e);
        }
    }

    private void convertDtoToEntity(RequestBrandDto brandDto, Brand brand) {
        brand.setName(brandDto.getName());
        brand.setWebsiteUrl(brandDto.getWebsiteUrl());
        brand.setCountryOfOrigin(brandDto.getCountryOfOrigin());
        brand.setIsActive(brandDto.getIsActive() != null ? brandDto.getIsActive() : true);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Brand createBrand(RequestBrandDto brandDto) {
        var checkDup = getAllBrands().stream().filter(brand -> brand.getName().equalsIgnoreCase(brandDto.getName())).toList();
        if(!checkDup.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate brand found");
        }
        var newBrand = new Brand();
        convertDtoToEntity(brandDto, newBrand);
        var savedBrand = brandRepository.save(newBrand);
        entityManager.refresh(savedBrand);
        return savedBrand;
    }

    public Brand updateBrand(int id, RequestBrandDto brandDto) {
        Brand updateBrand = getBrandById(id);
        if (!brandDto.getName().equalsIgnoreCase(updateBrand.getName())) {
            boolean nameExists = brandRepository.existsById(updateBrand.getId());
            if (nameExists) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand name already exists");
            }
        }
        convertDtoToEntity(brandDto,updateBrand);
        return brandRepository.save(updateBrand);
    }

    public void deleteBrand(int id) {
        Brand brand = getBrandById(id);
        long count = saleItemRepository.countByBrandId(id);
        if (count > 0) {
            throw new IllegalStateException("Cannot delete brand: " + count + " phones are using this brand.");
        }
        brandRepository.delete(brand);
    }

}
