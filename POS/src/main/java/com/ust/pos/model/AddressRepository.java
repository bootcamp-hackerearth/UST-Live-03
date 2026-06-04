package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByPhoneNoAndAddressType(String phoneNo, String addressType);

    void deleteByPhoneNo(String phoneNo);

    Address findByPhoneNo(String phoneNo);


}
