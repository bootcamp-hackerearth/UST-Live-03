package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
public interface NodeService {
    List<NodeDto> getNodesForRoles();

    NodeDto save(NodeDto nodeDto);

    NodeDto update(NodeDto nodeDto);

    boolean delete(String identifier);

    List<NodeDto> findAll();

    NodeDto findByIdentifier(String identifier);
}
