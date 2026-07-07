package com.ust.pos.node.service.impl;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.NodeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
@Transactional
@RequiredArgsConstructor
public class NodeServiceImpl implements NodeService {

    private static final String NODE_WITH_IDENTIFIER = "Node with identifier - ";

    private final UserRepository userRepository;
    private final NodeRepository nodeRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<NodeDto> getNodesForRoles() {
        List<NodeDto> nodeDtos = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            org.springframework.security.core.userdetails.User principalObject =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
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
    public NodeDto save(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);

        if (existingNode != null) {
            if (Boolean.TRUE.equals(existingNode.getIsDeleted())) {
                nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " was deleted. Contact admin for further support or try with a different identifier.");
            } else {
                nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " already exists");
            }
            nodeDto.setSuccess(false);
            return nodeDto;
        }

        Node node = modelMapper.map(nodeDto, Node.class);
        node.setIsDeleted(false);
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
        nodeRepository.save(existingNode);
        return nodeDto;
    }

    @Override
    public NodeDto delete(String identifier) {
        NodeDto nodeDto = new NodeDto();
        Node node = nodeRepository.findByIdentifier(identifier);

        if (node == null) {
            nodeDto.setMessage(NODE_WITH_IDENTIFIER + identifier + " not found");
            nodeDto.setSuccess(false);
            return nodeDto;
        }

        node.setIsDeleted(true);
        node.setStatus(false);
        nodeRepository.save(node);
        nodeDto.setSuccess(true);
        nodeDto.setMessage("Node deleted successfully");
        return nodeDto;
    }

    @Override
    public PaginatedResponseDto<NodeDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Page<Node> nodePage = nodeRepository.findByIsDeleted(false, pageable);
        List<NodeDto> items = modelMapper.map(nodePage.getContent(), listType);
        PaginatedResponseDto<NodeDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(nodePage.getTotalElements());
        response.setTotalPages(nodePage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());
        return response;
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {
        Node node = nodeRepository.findByIdentifier(identifier);
        if (node == null) {
            throw new ResourceNotFoundException("Node with identifier " + identifier + " not found");
        }
        return modelMapper.map(node, NodeDto.class);
    }

    @Override
    public List<NodeDto> findAllActive() {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        return modelMapper.map(nodeRepository.findByStatusAndIsDeleted(true, false), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {
        Node node = nodeRepository.findByIdentifier(identifier);
        node.setStatus(status);
        nodeRepository.save(node);
    }

    @Override
    public PaginatedResponseDto<NodeDto> findAll(Specification<Node> example, Pageable pageable) {

        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Page<Node> page = nodeRepository.findAll(example, pageable);

        PaginatedResponseDto<NodeDto> paginatedResponseDto = new PaginatedResponseDto<>();
        paginatedResponseDto.setItems(modelMapper.map(page.getContent(), listType));
        paginatedResponseDto.setTotalRecords(page.getTotalElements());
        paginatedResponseDto.setTotalPages(page.getTotalPages());
        paginatedResponseDto.setSizePerPage(pageable.getPageSize());
        paginatedResponseDto.setPage(pageable.getPageNumber());

        return paginatedResponseDto;
    }
}