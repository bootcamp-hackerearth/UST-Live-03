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

    List<Role> findByStatusAndIsDeleted(boolean status, boolean isDeleted);

    Page<Role> findByIsDeleted(boolean isDeleted, Pageable pageable);
}