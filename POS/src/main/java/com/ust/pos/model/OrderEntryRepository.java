package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderEntryRepository extends JpaRepository<OrderEntry, Long> {

    OrderEntry findByIdentifier(String identifier);

    List<OrderEntry> findByOrderId(String orderId);

    Page<OrderEntry> findAll(Specification<OrderEntry> example, Pageable pageable);

    void deleteByIdentifier(String identifier);

    List<OrderEntry> findByStatus(boolean status);

}
