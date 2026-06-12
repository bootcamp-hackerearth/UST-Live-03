package com.ust.pos.modell;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Brand findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

}