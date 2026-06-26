package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByIdentifierAndDeletedFalse(String identifier);

    List<Role> findByDeletedFalse();

    Page<Role> findByDeletedFalse(Pageable pageable);

    Page<Role> findByIdentifierContainingIgnoreCaseAndDeletedFalse(String identifier, Pageable pageable);
}