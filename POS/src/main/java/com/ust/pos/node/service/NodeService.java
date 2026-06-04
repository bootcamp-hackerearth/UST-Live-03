package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;

import java.util.List;

public interface NodeService {
    NodeDto save(NodeDto nodeDto);

    NodeDto update(NodeDto nodeDto);

    void delete(String identifier);

    List<NodeDto> findAll();

    NodeDto findByIdentifier(String identifier);

    List<NodeDto> getNodesForRoles();
}