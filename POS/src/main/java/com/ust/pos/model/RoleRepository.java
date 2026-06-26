package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Role> findByIsDeletedFalse(Pageable pageable);

    List<Role> findByStatus(boolean status);

}
