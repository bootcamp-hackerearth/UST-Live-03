package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;

import java.util.List;

public interface NodeService {
    List<NodeDto> getNodesForRoles();

    List<NodeDto> findAll();

    NodeDto save(NodeDto nodeDto);

    NodeDto findByIdentifier(String identifier);

    NodeDto update(NodeDto nodeDto);

    void delete(String identifier);
}
