package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByUsername(String identifier);

    User findByUsernameAndIsDeleteFalse(String identifier);

    void deleteByUsername(String identifier);

    List<User> findByIsDeleteFalse();

    Page<User> findByUsernameContainingIgnoreCaseAndIsDeleteFalse
            (String identifier, Pageable pageable);

    Page<User> findByIsDeleteFalse(Pageable pageable);
}
