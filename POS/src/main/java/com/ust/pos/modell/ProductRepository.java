package com.ust.pos.modell;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByIdentifier(String identifier);

    List<Product> findByStatusTrueAndDeletedFalse();

    Product findByIdentifierAndDeletedFalse (String identifier);

    Page<Product> findALlByDeletedFalse (Pageable pageable);

}
