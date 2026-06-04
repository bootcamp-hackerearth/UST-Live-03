package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import java.util.List;

public interface NodeService {
    List<NodeDto> getNodesForRoles();
    
    NodeDto save(NodeDto userDto);

    NodeDto update(NodeDto userDto);

    void delete(String username);

    List<NodeDto> findAll();

    NodeDto findByIdentifier(String identifier);
}
