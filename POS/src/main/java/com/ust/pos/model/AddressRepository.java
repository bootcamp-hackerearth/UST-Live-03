package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByIdentifierAndDeletedFalse(String identifier);

    Address findByIdentifierAndIsShippingTrueAndDeletedFalse(String identifier);

    Address findByIdentifierAndIsBillingTrueAndDeletedFalse(String identifier);

    Address findByIdentifierAndIsShippingTrue(String identifier);

    Address findByIdentifierAndIsBillingTrue(String identifier);

    List<Address> findByDeletedFalse();
}