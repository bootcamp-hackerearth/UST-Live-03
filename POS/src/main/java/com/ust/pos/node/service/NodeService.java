package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;

import java.util.List;

public interface NodeService {
    List<NodeDto> getNodesForRoles();

    NodeDto save(NodeDto nodeDto);

    void update(NodeDto nodeDto);

    void delete(String identifier);

    NodeDto findByIdentifier(String identifier);

    List<NodeDto> findAll();
}
