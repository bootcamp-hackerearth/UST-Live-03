package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByPhoneNoAndAddressType(String phoneNo, String addressType);

    void deleteByPhoneNo(String phoneNo);

    Address findByPhoneNo(String phoneNo);

    List<Address> findByIsDeletedFalse();
}
