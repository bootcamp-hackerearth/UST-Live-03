package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    Page<Role> findByIsDeletedFalse(Pageable pageable);

    Page<Role> findAll(Specification<Role> example, Pageable pageable);

    Role findByIdentifierAndIsDeletedFalse(String identifier);
}
