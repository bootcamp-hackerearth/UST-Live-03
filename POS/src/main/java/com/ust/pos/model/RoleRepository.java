package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<Role> findAllByStatus(boolean status);
}
