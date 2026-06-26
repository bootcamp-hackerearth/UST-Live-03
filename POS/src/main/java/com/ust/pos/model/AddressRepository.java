package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByIdentifier(String identifier);

    Address findByPhoneNoAndAddressTypeAndDeletedFalse(Long phoneNo, String addressType);

    List<Address> findByDeletedFalse();

    List<Address> findByPhoneNoAndDeletedFalse(Long phoneNo);

    Page<Address> findAllByDeletedFalse(Pageable pageable);
}