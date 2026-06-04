package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface NodeService {

    List<NodeDto> getNodesForRoles();

    NodeDto save(NodeDto nodeDto);

    NodeDto update(NodeDto nodeDto);

    boolean delete(String identifier);

    List<NodeDto> findAll(Pageable pageable);

    NodeDto findByIdentifier(String identifier);

    NodeDto toggleStatus(String identifier);

    List<NodeDto> findIfTrue();

}