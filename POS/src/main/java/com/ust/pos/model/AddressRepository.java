package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByIdentifier(String identifier);

    Address findByIdentifierAndIsShippingTrue(String identifier);

    Address findByIdentifierAndIsBillingTrue(String identifier);

    void deleteByIdentifier(String identifier);
}