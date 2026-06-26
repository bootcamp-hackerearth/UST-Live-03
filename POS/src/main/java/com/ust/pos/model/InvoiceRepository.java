package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Invoice findByIdentifier(String identifier);

    Invoice findByOrderId(String orderId);
}