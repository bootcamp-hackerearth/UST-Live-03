package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByIdentifier(String identifier);

    Address findByPhoneNoAndAddressType(Long phoneNo, String addressType);

    List<Address> findAllByPhoneNo(Long phoneNo);

    void deleteByPhoneNo(Long phoneNo);
}