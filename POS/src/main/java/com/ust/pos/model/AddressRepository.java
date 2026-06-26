package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address , Long> {
    Address findByIdentifier(String identifier);
    Address findByIdentifierAndIsShippingTrue(String identifier);

    Address findByIdentifierAndIsBillingTrue(String identifier);
    void deleteByIdentifier(String identifier);

    List<Address> findByDeletedFalse();

    Address findByIdentifierAndIsShippingTrueAndDeletedFalse(String identifier);

    Address findByIdentifierAndIsBillingTrueAndDeletedFalse(String identifier);
}
