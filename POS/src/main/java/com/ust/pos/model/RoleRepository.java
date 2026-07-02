package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    Role findByIdentifier(String identifier);

    Page<Role> findByDeletedFalse(Pageable pageable);

    List<Role> findByStatusTrue();

    Page<Role> findAll(Specification spec, Pageable pageable);
}
