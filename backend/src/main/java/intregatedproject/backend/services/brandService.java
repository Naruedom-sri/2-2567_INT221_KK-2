package intregatedproject.backend.services;

import intregatedproject.backend.entities.Brand;
import intregatedproject.backend.repositories.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class brandService {
    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> getAllBrands() {
        try {
            return brandRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve brands from database.", e);
        }
    }
}
