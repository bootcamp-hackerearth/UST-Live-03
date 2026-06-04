package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByPhoneNumberAndAddressType(Long phone, String addressType);

    void deleteByPhoneNumber(Long phoneNumber);

}
