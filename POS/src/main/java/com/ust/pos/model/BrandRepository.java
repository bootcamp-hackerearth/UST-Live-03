package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long>, JpaSpecificationExecutor<Brand> {

    Brand findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Brand> findByDeletedFalse(Pageable pageable);

    List<Brand> findByStatusIsTrue();

    Page<Brand> findAll(Pageable pageable);

    Page<Brand> findAll(Specification<Brand> spec, Pageable pageable);

}
 