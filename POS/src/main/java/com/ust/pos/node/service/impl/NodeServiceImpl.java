package com.ust.pos.node.service.impl;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.NodeService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class NodeServiceImpl implements NodeService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public NodeDto save(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();

        if (nodeRepository.findByIdentifier(identifier) != null) {
            nodeDto.setSuccess(false);
            return nodeDto;
        }

        Node node = modelMapper.map(nodeDto, Node.class);
        nodeRepository.save(node);
        nodeDto.setSuccess(true);
        return nodeDto;
    }

    public NodeDto update(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node exisingNode = nodeRepository.findByIdentifier(identifier);
        if (exisingNode == null) {
            nodeDto.setMessage("Node not found");
            nodeDto.setSuccess(false);
            return nodeDto;
        }

        modelMapper.map(nodeDto, exisingNode);
        nodeRepository.save(exisingNode);
        nodeDto.setMessage("Node updated successfully");
        nodeDto.setSuccess(true);

        return nodeDto;
    }

    @Override
    public void delete(String identifier) {
        nodeRepository.deleteByIdentifier(identifier);
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {
        return modelMapper.map(nodeRepository.findByIdentifier(identifier), NodeDto.class);
    }

    @Override
    public List<NodeDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        if (pageable == null) {
            return modelMapper.map(nodeRepository.findAll(), listType);
        }
        Page<Node> nodePage = nodeRepository.findAll(pageable);
        return modelMapper.map(nodePage.getContent(), listType);
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

        List<Node> nodes = nodeRepository.findAll();
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
