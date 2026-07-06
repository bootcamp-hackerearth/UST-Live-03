package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByIdentifierAndDeletedFalse(String identifier);

    void deleteByIdentifier(String identifier);

    Page<User> findAllByDeletedFalse(Pageable pageable);

    User findByIdentifier(String identifier);

    Page<User> findAll(Specification<User> example, Pageable pageable);
}
