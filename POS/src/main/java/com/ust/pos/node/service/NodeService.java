package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NodeService {
    List<NodeDto> getNodesForRoles();

    NodeDto save(NodeDto userDto);

    NodeDto update(NodeDto userDto);

    void delete(String username);

    List<NodeDto> findAll();

    Page<NodeDto> findAll(Pageable pageable, String search);

    NodeDto findByIdentifier(String identifier);
}
