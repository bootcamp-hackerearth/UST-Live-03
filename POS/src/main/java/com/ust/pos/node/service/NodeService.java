package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NodeService {
    NodeDto save(NodeDto nodeDto);

    List<NodeDto> findAll(Pageable pageable);

    NodeDto findByIdentifier(String identifier);

    List<NodeDto> getNodesForRoles();

    NodeDto update(NodeDto nodeDto);

    NodeDto toggleStatus(String identifier);

    boolean delete(String username);
}
