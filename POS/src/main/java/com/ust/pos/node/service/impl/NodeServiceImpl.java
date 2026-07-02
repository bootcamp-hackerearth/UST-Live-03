package com.ust.pos.node.service.impl;

import com.ust.pos.api.BaseService;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.model.*;
import com.ust.pos.node.service.NodeService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
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

@Transactional
@Service
public class NodeServiceImpl extends BaseService implements NodeService {

    private final UserRepository userRepository;


    private final NodeRepository nodeRepository;


    private final ModelMapper modelMapper;

    public NodeServiceImpl(UserRepository userRepository, NodeRepository nodeRepository,
                           ModelMapper modelMapper) {
        this.nodeRepository = nodeRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public List<NodeDto> getNodesForRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<NodeDto> nodeDtos = new ArrayList<>();
        if (authentication != null) {
            org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            if (principalObject != null) findEligibleNodes(principalObject, nodeDtos);
        }
        return nodeDtos;
    }

    private void findEligibleNodes(org.springframework.security.core.userdetails.User principalObject, List<NodeDto> nodeDtos) {
        User currentUser = userRepository.findByUsernameAndDeletedFalse(principalObject.getUsername());
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
            nodeDtos.add(modelMapper.map(nodeRepository.findByIdentifierAndDeletedFalse(nodeStr), NodeDto.class));
        }
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingRole = nodeRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingRole != null) {
            nodeDto.setMessage("Node with identifier - " + identifier + " already exists");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        Node node = modelMapper.map(nodeDto, Node.class);
        nodeRepository.save(node);
        return nodeDto;
    }

    @Override
    public NodeDto update(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingRole = nodeRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingRole == null) {
            nodeDto.setMessage("Role with identifier - " + identifier + " not found");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        modelMapper.map(nodeDto, existingRole);
        nodeRepository.save(existingRole);
        return nodeDto;
    }

    @Override
    public void delete(String identifier) {
        Node node = nodeRepository.findByIdentifierAndDeletedFalse(identifier);
        if (node != null) {
            node.setDeleted(true);
            nodeRepository.save(node);
        }
    }

    @Override
    public List<NodeDto> findAll() {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        return modelMapper.map(nodeRepository.findByDeletedFalse(), listType);

    }

    @Override
    public Page<NodeDto> findAll(String search,Pageable pageable) {
        Page<Node> Nodes;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Node> specification = buildGlobalSearchSpec(Node.class, search);
            Nodes = nodeRepository.findAll(specification, pageable);
        } else {
            Nodes = nodeRepository.findByDeletedFalse(pageable);
        }

        return Nodes.map(node -> modelMapper.map(node, NodeDto.class));
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {
        return modelMapper.map(nodeRepository.findByIdentifierAndDeletedFalse(identifier), NodeDto.class);
    }
}
