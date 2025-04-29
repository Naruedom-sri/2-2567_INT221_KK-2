package intregatedproject.backend.repositories;

import intregatedproject.backend.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
}