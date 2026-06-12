package com.ust.pos.node.service;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.RoleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NodeService {
    List<NodeDto> getNodesForRoles();

    NodeDto save(NodeDto nodeDto);

    NodeDto update(NodeDto nodeDto);

     void delete(String username);

    List<NodeDto> findAll();

    Page<NodeDto> findAll(Pageable pageable,String search);

    NodeDto findByIdentifier(String identifier);
}
