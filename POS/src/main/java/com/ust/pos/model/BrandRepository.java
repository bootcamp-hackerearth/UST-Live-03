package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Brand findByIdentifier(String identifier);//find

    Page<Brand> findByDeletedFalse(Pageable pageable);//listing page

    List<Brand> findByStatusIsTrueAndDeletedFalse();//findbystatus

}