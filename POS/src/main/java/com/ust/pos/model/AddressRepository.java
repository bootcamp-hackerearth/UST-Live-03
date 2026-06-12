package com.ust.pos.model;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByPhoneNoAndAddressType(String phoneNo, String addressType);

    @Transactional
    void deleteByPhoneNo(String phoneNo);

}