package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findByPhoneNumberAndAddressTypeAndDeletedFalse(Long phoneNumber, String addressType);

    List<Address> findByDeletedFalse();

    List<Address> findByPhoneNumberAndDeletedFalse(Long phoneNumber);

}
