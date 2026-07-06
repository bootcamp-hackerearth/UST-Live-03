package com.ust.pos.node.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.*;
import com.ust.pos.node.service.NodeService;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Transactional
@Service
public class NodeServiceImpl extends BaseService implements NodeService {

    private static final String DELETED_MESSAGE =
            " has been deleted. Please contact the administrator.";

    private final UserRepository userRepository;
    private final NodeRepository nodeRepository;
    private final ModelMapper modelMapper;

    public NodeServiceImpl(UserRepository userRepository, NodeRepository nodeRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.nodeRepository = nodeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {

        String identifier = nodeDto.getIdentifier();
        String path = nodeDto.getPath();

        Node existingNode = nodeRepository.findByIdentifier(identifier);

        if (existingNode != null) {

            if (existingNode.isDeleted()) {
                nodeDto.setSuccess(false);
                nodeDto.setMessage(
                        "Node " + identifier +
                                DELETED_MESSAGE
                );
                return nodeDto;
            }

            nodeDto.setSuccess(false);
            nodeDto.setMessage("A node with this identifier already exists.");
            return nodeDto;
        }

        Node existingPathNode = nodeRepository.findByPath(path);

        if (existingPathNode != null) {

            if (existingPathNode.isDeleted()) {
                nodeDto.setSuccess(false);
                nodeDto.setMessage(
                        "Node with path " + path +
                                DELETED_MESSAGE
                );
                return nodeDto;
            }

            nodeDto.setSuccess(false);
            nodeDto.setMessage("A node with this path already exists.");
            return nodeDto;
        }

        Node node = modelMapper.map(nodeDto, Node.class);
        setCreatedDetails(node);
        nodeRepository.save(node);

        nodeDto.setSuccess(true);
        nodeDto.setMessage("Node created successfully.");

        return nodeDto;
    }

    @Override
    public NodeDto update(NodeDto nodeDto) {

        String identifier = nodeDto.getIdentifier();
        String path = nodeDto.getPath();

        Node existingNode = nodeRepository.findByIdentifier(identifier);

        if (existingNode == null) {
            nodeDto.setMessage("Node not found.");
            nodeDto.setSuccess(false);
            return nodeDto;
        }

        if (existingNode.isDeleted()) {
            nodeDto.setMessage(
                    "Node " + identifier +
                            DELETED_MESSAGE
            );
            nodeDto.setSuccess(false);
            return nodeDto;
        }

        Node nodeWithSamePath = nodeRepository.findByPath(path);

        if (nodeWithSamePath != null &&
                !nodeWithSamePath.getIdentifier().equals(identifier)) {

            if (nodeWithSamePath.isDeleted()) {
                nodeDto.setMessage(
                        "Node with path " + path +
                                DELETED_MESSAGE
                );
                nodeDto.setSuccess(false);
                return nodeDto;
            }

            nodeDto.setMessage("A node with this path already exists.");
            nodeDto.setSuccess(false);
            return nodeDto;
        }

        modelMapper.map(nodeDto, existingNode);

        setModifiedDetails(existingNode);
        nodeRepository.save(existingNode);

        nodeDto.setMessage("Node updated successfully.");
        nodeDto.setSuccess(true);

        return nodeDto;
    }

    @Override
    public void delete(String identifier) {
        Node node = nodeRepository.findByIdentifier(identifier);
        softDelete(node);
        setModifiedDetails(node);
        nodeRepository.save(node);
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {
        return modelMapper.map(nodeRepository.findByIdentifier(identifier), NodeDto.class);
    }

    @Override
    public PaginationResponseDto<NodeDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Page<Node> nodePage = nodeRepository.findByIsDeletedFalse(pageable);
        List<NodeDto> productDtoList = modelMapper.map(nodePage.getContent(), listType);

        PaginationResponseDto<NodeDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(productDtoList);
        paginationResponseDto.setPage(nodePage.getNumber());
        paginationResponseDto.setSizePerPage(nodePage.getSize());
        paginationResponseDto.setTotalPages(nodePage.getTotalPages());
        paginationResponseDto.setTotalRecords(nodePage.getTotalElements());

        return paginationResponseDto;
    }

    @Override
    public PaginationResponseDto<NodeDto> findAll(Specification<Node> example, Pageable pageable) {

        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Page<Node> page = nodeRepository.findAll(example, pageable);

        PaginationResponseDto<NodeDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationResponseDto.setTotalRecords(page.getTotalElements());
        paginationResponseDto.setTotalPages(page.getTotalPages());
        paginationResponseDto.setSizePerPage(pageable.getPageSize());
        paginationResponseDto.setPage(pageable.getPageNumber());

        return paginationResponseDto;
    }

    @Override
    @Transactional
    public NodeDto updateStatus(String identifier, boolean status) {
        NodeDto response = new NodeDto();

        Node node = nodeRepository.findByIdentifier(identifier);
        if (node == null) {
            response.setSuccess(false);
            response.setMessage("Node not found");
            return response;
        }

        setModifiedDetails(node);
        node.setStatus(status);
        nodeRepository.save(node);

        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    public List<NodeDto> getNodesForRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User)) {
            return new ArrayList<>();
        }

        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        User currentUser = userRepository.findByUsername(principal.getUsername());

        if (currentUser == null || currentUser.getRoles() == null) {
            return new ArrayList<>();
        }

        Page<Node> nodes = nodeRepository.findByIsDeletedFalse(null);
        Set<String> allowedNodeIds = new LinkedHashSet<>();

        for (String role : currentUser.getRoles()) {
            for (Node node : nodes) {
                if (node.getRoles() != null && node.getRoles().contains(role)) {
                    allowedNodeIds.add(node.getIdentifier());
                }
            }
        }

        List<NodeDto> nodeDtos = new ArrayList<>();

        for (String id : allowedNodeIds) {
            Node node = nodeRepository.findByIdentifier(id);
            if (node != null) {
                nodeDtos.add(modelMapper.map(node, NodeDto.class));
            }
        }

        return nodeDtos;
    }
}
