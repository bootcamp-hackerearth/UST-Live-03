package com.ust.pos.node.service.impl;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.NodeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NodeServiceImpl implements NodeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private ModelMapper modelMapper;


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
    public List<NodeDto> findAll() {
        List<Node> nodeList = nodeRepository.findAll();
        List<NodeDto> nodeDtoList = new ArrayList<>();
        for (Node node : nodeList) {
            nodeDtoList.add(modelMapper.map(node, NodeDto.class));
        }
        return nodeDtoList;
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingRole = nodeRepository.findByIdentifier(identifier);
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
    public void update(NodeDto nodeDto) {
        nodeRepository.save(modelMapper.map(nodeDto, Node.class));
    }

    @Override
    public void delete(String identifier) {
        nodeRepository.deleteByIdentifier(identifier);
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {
        return modelMapper.map(nodeRepository.findByIdentifier(identifier), NodeDto.class);
    }
}