package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;

import com.ust.pos.dto.PageDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
public interface NodeService {
    List<NodeDto> getNodesForRoles();

    NodeDto save(NodeDto nodeDto);

    NodeDto update(NodeDto nodeDto);

    boolean delete(String identifier);

    PageDto<NodeDto> findAll(Pageable pageable);

    NodeDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<NodeDto> findActiveNodes();
}
