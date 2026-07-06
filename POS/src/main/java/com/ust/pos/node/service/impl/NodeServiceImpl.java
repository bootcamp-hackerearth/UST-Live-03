package com.ust.pos.node.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.*;
import com.ust.pos.node.service.NodeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NodeServiceImpl extends BaseService implements NodeService {

    public static final String NODE_WITH_IDENTIFIER = "Node with identifier - ";
    private final UserRepository userRepository;
    private final NodeRepository nodeRepository;
    private final ModelMapper modelMapper;

    public NodeServiceImpl(
            UserRepository userRepository,
            NodeRepository nodeRepository,
            ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.nodeRepository = nodeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<NodeDto> getNodesForRoles() {
        List<NodeDto> nodeDtos = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            if (principalObject != null) findNodes(principalObject, nodeDtos);
        }
        return nodeDtos;
    }

    private void findNodes(org.springframework.security.core.userdetails.User principalObject, List<NodeDto> nodeDtos) {
        User currentUser = userRepository.findByUsername(principalObject.getUsername());
        Set<String> nodesStr = new HashSet<>();
        List<Node> nodes = nodeRepository.findAll();
        for (String role : currentUser.getRoles()) {
            for (Node node : nodes) {
                if (node.getRoles() != null && node.getRoles().contains(role)) {
                    nodesStr.add(node.getIdentifier());
                }
            }
        }
        for (String nodeStr : nodesStr) {
            nodeDtos.add(modelMapper.map(nodeRepository.findByIdentifier(nodeStr), NodeDto.class));
        }
    }

    @Override
    public PaginationResponseDto<NodeDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<NodeDto>>() {}.getType();
        PaginationResponseDto<NodeDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<Node> nodes = nodeRepository.findAll();
            response.setDtoList(modelMapper.map(nodes, listType));
            response.setTotalRecords(nodes.size());
            response.setTotalPages(1);
            response.setSizePerPage(nodes.size());
            response.setPage(0);
        } else {
            Page<Node> nodePage = nodeRepository.findByDeletedFalse(pageable);
            response.setDtoList(modelMapper.map(nodePage.getContent(), listType));
            response.setTotalRecords(nodePage.getTotalElements());
            response.setTotalPages(nodePage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingRole = nodeRepository.findByIdentifier(identifier);
        if (existingRole != null) {
            if (existingRole.isDeleted()) {
                nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " has been soft deleted. (Rollback by changing status)");
                nodeDto.setSuccess(false);
                return nodeDto;
            }
            nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " already exists");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        Node node = modelMapper.map(nodeDto, Node.class);
        setCreatedDetails(node);
        nodeRepository.save(node);
        return nodeDto;
    }

    @Override
    public NodeDto update(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);
        if (existingNode == null) {
            nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " not found");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        modelMapper.map(nodeDto, existingNode);
        setModifiedDetails(existingNode);
        nodeRepository.save(existingNode);
        return nodeDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Node node = nodeRepository.findByIdentifier(identifier);
        if (node == null) {
            throw new EntityNotFoundException("Node not found");
        }
        softDelete(node);
        setModifiedDetails(node);
        nodeRepository.save(node);
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {
        return modelMapper.map(nodeRepository.findByIdentifier(identifier), NodeDto.class);
    }

    @Override
    public PaginationResponseDto<NodeDto> findAll(Specification<Node> example, Pageable pageable) {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Page<Node> page = nodeRepository.findAll(example, pageable);
        PaginationResponseDto<NodeDto> paginationresponse = new PaginationResponseDto<>();
        paginationresponse.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationresponse.setTotalRecords(page.getTotalElements());
        paginationresponse.setTotalPages(page.getTotalPages());
        paginationresponse.setSizePerPage(pageable.getPageSize());
        paginationresponse.setPage(pageable.getPageNumber());
        return paginationresponse;
    }
}