package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, QueryByExampleExecutor<Role> {
    Role findByIdentifier(String identifier);
    List<Role> findByDeletedFalse();
    Page<Role> findByDeletedFalse(Pageable pageable);
    void deleteByIdentifier(String identifier);
    Page<Role> findByIdentifierContainingIgnoreCaseAndDeletedFalse(
            String identifier, Pageable pageable
    );
    Role findByIdentifierAndDeletedFalse(String identifier);
}
