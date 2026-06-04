package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Brand findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Brand> findByStatusIsTrue();

}