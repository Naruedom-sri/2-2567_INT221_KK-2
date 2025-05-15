package intregatedproject.backend.services;

import intregatedproject.backend.entities.Brand;
import intregatedproject.backend.repositories.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;

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
        brandRepository.delete(brand);
    }
}
