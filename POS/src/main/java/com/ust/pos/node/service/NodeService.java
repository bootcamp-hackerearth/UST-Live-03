package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NodeService {
    List<NodeDto> getNodesForRoles();

    NodeDto save(NodeDto nodeDto);

    NodeDto update(NodeDto nodeDto);

    void delete(String nodename);

    List<NodeDto> findAll();

    NodeDto findByIdentifier(String identifier);

    Page<NodeDto> findAll(Pageable pageable, String search);
}