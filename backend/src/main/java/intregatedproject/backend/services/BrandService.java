package intregatedproject.backend.services;

import intregatedproject.backend.dtos.brands.RequestBrandDto;
import intregatedproject.backend.entities.Brand;
import intregatedproject.backend.exceptions.BrandAlreadyExistsException;
import intregatedproject.backend.exceptions.BrandHasSaleItemException;
import intregatedproject.backend.repositories.BrandRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private EntityManager entityManager;

    public List<Brand> getAllBrands() {
        try {
            return brandRepository.findAll(Sort.by(Sort.Order.asc("createdOn"), Sort.Order.asc("id")));
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
        if (!checkDup.isEmpty()) {
            throw new BrandAlreadyExistsException("Brand with name " + brandDto.getName() + " already exists.");
        }
        var newBrand = new Brand();
        convertDtoToEntity(brandDto, newBrand);
        var savedBrand = brandRepository.save(newBrand);
        entityManager.refresh(savedBrand);
        return savedBrand;
    }

    public Brand updateBrand(int id, RequestBrandDto brandDto) {
        Brand updateBrand = getBrandById(id);
        var checkDup = getAllBrands().stream().filter(brand -> brand.getName().equalsIgnoreCase(brandDto.getName())).toList();
        if (!checkDup.isEmpty() && !checkDup.get(0).getId().equals(id)) {
            throw new BrandAlreadyExistsException("Duplicate brand found");
        }
        convertDtoToEntity(brandDto, updateBrand);
        return brandRepository.save(updateBrand);
    }

    public void deleteBrand(int id) {
        Brand brand = getBrandById(id);
        if (!brand.getSaleItems().isEmpty()) {
            throw new BrandHasSaleItemException("Can't delete brand " + brand.getId() + " because it has sale items associated with it.");
        }
        brandRepository.delete(brand);
    }

}
