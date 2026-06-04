package com.ust.pos.modell;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Address> findAllByPhoneNo(String phoneNo);

    Address findByPhoneNoAndAddressType(String phoneNo, String addressType);
}