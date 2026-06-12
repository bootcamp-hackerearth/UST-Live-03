package com.ust.pos.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    Node findByIdentifier(String identifier);

    Node findByPath(String path);

    void deleteByIdentifier(String identifier);
}
