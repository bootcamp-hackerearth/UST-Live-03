package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByIdentifier(String identifier);

    List<Address> findAllByPhoneNo(String phoneNo);

    void deleteByIdentifier(String identifier);

    List<Brand> findAllByStatus(Boolean status);
}
