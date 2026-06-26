package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByIdentifier(String identifier);

    Address findByIdentifierAndIsDeleteFalse(String identifier);

    void deleteByIdentifier(String identifier);

    List<Address> findByIsDeleteFalse();

    Page<Address> findByIdentifierContainingIgnoreCaseAndIsDeleteFalse
            (String identifier, Pageable pageable);

    Page<Address> findByIsDeleteFalse(Pageable pageable);

    Address findByIdentifierAndIsShippingTrueAndIsDeleteFalse(String identifier);

    Address findByIdentifierAndIsBillingTrueAndIsDeleteFalse(String identifier);


}
