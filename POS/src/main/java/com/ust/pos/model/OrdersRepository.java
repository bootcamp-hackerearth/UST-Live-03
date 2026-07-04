package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Orders findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Orders> findAll(Specification<Orders> example, Pageable pageable);

    List<Orders> findByStatus(boolean status);
}
