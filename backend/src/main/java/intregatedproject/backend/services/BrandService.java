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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;
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

    public Brand getBrandById(int id) {
        try {
            return brandRepository.findById(id)
                    .orElseThrow(() ->
                            new RuntimeException("Brand with id " + id + " not found"));
        } catch (RuntimeException e) {
            throw e;
        }
    }
    public void deleteBrand(int id) {
        Brand brand = getBrandById(id);
        long count = saleItemRepository.countByBrandId(id);
        if (count > 0) {
            throw new IllegalStateException("Cannot delete brand: " + count + " phones are using this brand.");
        }
        brandRepository.delete(brand);
    }

    private void convertDtoToEntity(RequestBrandDto brandDto, Brand brand) {
        brand.setName(brandDto.getName());
        brand.setWebsiteUrl(brandDto.getWebsiteUrl());
        brand.setCountryOfOrigin(brandDto.getCountryOfOrigin());
        brand.setIsActive(brandDto.getIsActive());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Brand createBrand(RequestBrandDto brandDto) {
        var newBrand = new Brand();
        convertDtoToEntity(brandDto, newBrand);
        var savedBrand = brandRepository.save(newBrand);
        entityManager.refresh(savedBrand);
        return savedBrand;
    }

    public Brand updateBrand(int id, RequestBrandDto brandDto) {
        Brand updateBrand = getBrandById(id);
        convertDtoToEntity(brandDto,updateBrand);
        return brandRepository.save(updateBrand);
    }
}
