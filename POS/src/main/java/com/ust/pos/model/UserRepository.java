package com.ust.pos.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameAndDeletedFalse(String username);

    Page<User> findByUsernameContainingIgnoreCaseAndDeletedFalse(
            String username, Pageable pageable
    );

    List<User> findByDeletedFalse();

    Page<User> findByDeletedFalse(Pageable pageable);
}
