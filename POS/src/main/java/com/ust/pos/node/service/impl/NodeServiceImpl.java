package com.ust.pos.node.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.NodeService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class NodeServiceImpl extends BaseService implements NodeService {

    private final UserRepository userRepository;
    private final NodeRepository nodeRepository;
    private final ModelMapper modelMapper;

    public NodeServiceImpl(UserRepository userRepository,NodeRepository nodeRepository,
                           ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.nodeRepository = nodeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {

        Node node = nodeRepository.findByIdentifierAndDeletedFalse(identifier);

        if (node == null) {
            NodeDto dto = new NodeDto();
            dto.setSuccess(false);
            dto.setMessage("Node not found");
            return dto;
        }

        return modelMapper.map(node, NodeDto.class);
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {

        String identifier = nodeDto.getIdentifier();

        if (identifier == null || identifier.trim().isEmpty()) {
            nodeDto.setSuccess(false);
            nodeDto.setMessage("Identifier required");
            return nodeDto;
        }

        Node existing = nodeRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existing != null) {
            nodeDto.setSuccess(false);
            nodeDto.setMessage("Node already exists");
            return nodeDto;
        }

        Node node = modelMapper.map(nodeDto, Node.class);
        node.setIdentifier(identifier);
        node.setDeleted(false);

        setCreatedDetails(node);

        nodeRepository.save(node);

        nodeDto.setSuccess(true);
        nodeDto.setMessage("Node saved successfully");

        return nodeDto;
    }

    @Override
    public NodeDto update(NodeDto nodeDto) {

        Node node = nodeRepository.findByIdentifierAndDeletedFalse(nodeDto.getIdentifier());

        if (node == null) {
            nodeDto.setSuccess(false);
            nodeDto.setMessage("Node not found");
            return nodeDto;
        }

        modelMapper.map(nodeDto, node);

        setModifiedDetails(node);

        nodeRepository.save(node);

        nodeDto.setSuccess(true);
        nodeDto.setMessage("Node updated successfully");

        return nodeDto;
    }

    @Override
    public void delete(String identifier) {

        Node node = nodeRepository.findByIdentifierAndDeletedFalse(identifier);

        if (node == null) {
            return;
        }

        node.setDeleted(true);

        setModifiedDetails(node);

        nodeRepository.save(node);
    }

    @Override
    public WsDto<NodeDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<NodeDto>>() {}.getType();

        Page<Node> page = nodeRepository.findByDeletedFalse(pageable);

        WsDto<NodeDto> ws = new WsDto<>();
        ws.setDtoList(modelMapper.map(page.getContent(), listType));
        ws.setTotalRecords(page.getTotalElements());
        ws.setTotalPages(page.getTotalPages());
        ws.setSizePerPage(pageable.getPageSize());
        ws.setPage(pageable.getPageNumber());

        return ws;
    }

    @Override
    public List<NodeDto> getNodesForRoles() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            return new ArrayList<>();
        }

        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) auth.getPrincipal();

        User currentUser = userRepository.findByUsername(principal.getUsername());

        if (currentUser == null || currentUser.getRoles() == null) {
            return new ArrayList<>();
        }

        List<Node> allNodes = nodeRepository.findByDeletedFalse();

        Set<String> allowedNodes = new HashSet<>();

        for (String role : currentUser.getRoles()) {
            for (Node node : allNodes) {
                if (node.getRoles() != null && node.getRoles().contains(role)) {
                    allowedNodes.add(node.getIdentifier());
                }
            }
        }

        List<NodeDto> result = new ArrayList<>();

        for (String identifier : allowedNodes) {

            Node node = nodeRepository.findByIdentifierAndDeletedFalse(identifier);

            if (node != null) {
                result.add(modelMapper.map(node, NodeDto.class));
            }
        }

        return result;
    }
}