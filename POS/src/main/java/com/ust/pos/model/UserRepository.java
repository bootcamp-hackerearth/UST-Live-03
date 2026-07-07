package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, QueryByExampleExecutor<User> {
    User findByUsername(String username);

    void deleteByUsername(String username);

    List<User> findByDeletedFalse();

    Page<User> findByDeletedFalse(Pageable pageable);

    User findByUsernameAndDeletedFalse(String username);

    Page<User> findByNameContainingIgnoreCaseAndDeletedFalse(
            String name, Pageable pageable
    );
}
