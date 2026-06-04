package com.ust.pos.node.service.impl;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.modell.Node;
import com.ust.pos.modell.NodeRepository;
import com.ust.pos.modell.User;
import com.ust.pos.modell.UserRepository;
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
import java.util.List;

@Service
public class NodeServiceImpl implements NodeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<NodeDto> getNodesForRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

            if (principalObject != null) {
                User currentUser = userRepository.findByUsername(principalObject.getUsername());
                List<Node> nodes = nodeRepository.findByRoles(currentUser.getRoles());
                Type listType = new TypeToken<List<NodeDto>>() {
                }.getType();
                return modelMapper.map(nodes, listType);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {
        return modelMapper.map(nodeRepository.findByIdentifier(identifier), NodeDto.class
        );
    }

    public NodeDto save(NodeDto nodeDto) {
        NodeDto response = new NodeDto();

        if (nodeRepository.existsByIdentifier(nodeDto.getIdentifier())) {
            response.setSuccess(false);
            response.setMessage("Node already exists");
            return response;
        }

        Node node = new Node();
        node.setIdentifier(nodeDto.getIdentifier());
        node.setPath(nodeDto.getPath());
        node.setRoles(nodeDto.getRoles());
        nodeRepository.save(node);
        response.setSuccess(true);
        return response;
    }

    @Override
    public NodeDto update(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);

        if (existingNode == null) {
            nodeDto.setMessage("Node with identifier - " + identifier + " not found");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        modelMapper.map(nodeDto, existingNode);
        nodeRepository.save(existingNode);
        return nodeDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        nodeRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<NodeDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Page<Node> nodePage = nodeRepository.findAll(pageable);
        return modelMapper.map(nodePage.getContent(), listType);
    }
}