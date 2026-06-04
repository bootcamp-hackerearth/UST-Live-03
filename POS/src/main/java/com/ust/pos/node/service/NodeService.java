package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NodeService {
    List<NodeDto> getNodesForRoles();

    NodeDto save(NodeDto nodeDto);

    NodeDto update(NodeDto nodeDto);

    void delete(String username);

    List<NodeDto> findAll();

    List<NodeDto> findAll(Pageable pageable);

    NodeDto findByIdentifier(String identifier);
}