package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByIdentifier(String address);

    void deleteByIdentifier(String identifier);

    List<Address> findByPhoneNo(String email);
}
