package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Address> findAllByPhoneNo(String phoneNo);

    Page<Address> findByDeletedFalse(Pageable pageable);
}