package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface OrderEntryRepository extends JpaRepository<OrderEntry, Long> {

    List<OrderEntry> findByOrderId(String orderId);

}
