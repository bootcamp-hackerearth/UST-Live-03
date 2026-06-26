package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByUsernameAndDeletedFalse(String username);

    List<User> findByDeletedFalse();

    void deleteByUsername(String username);

    Page<User> findByUsernameContainingIgnoreCaseAndDeletedFalse(String search, String search1, Pageable pageable);

    Page<User> findByDeletedFalse(Pageable pageable);
}
