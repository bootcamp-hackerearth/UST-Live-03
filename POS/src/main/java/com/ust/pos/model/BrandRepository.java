package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Brand findByIdentifier(String identifier);

    Page<Brand> findByDeletedFalse(Pageable pageable);

    List<Brand> findAllByStatusAndDeletedFalse(Boolean status);
}
