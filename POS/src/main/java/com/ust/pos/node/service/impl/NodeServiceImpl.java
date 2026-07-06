package com.ust.pos.node.service.impl;
import com.ust.pos.CommonService;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.lang.reflect.Type;
import java.util.*;

@Service
@Transactional
public class NodeServiceImpl extends CommonService implements NodeService {
    private final UserRepository userRepository;
    private final NodeRepository nodeRepository;
    private final ModelMapper modelMapper;

    public NodeServiceImpl(UserRepository userRepository, NodeRepository nodeRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.nodeRepository = nodeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<NodeDto> getNodesForRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Collections.emptyList();
        }
        Object principalObj = authentication.getPrincipal();
        if (!(principalObj instanceof org.springframework.security.core.userdetails.User)) {
            return Collections.emptyList();
        }
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) principalObj;
        User currentUser = userRepository.findByUsername(principal.getUsername());
        if (currentUser == null) {
            return Collections.emptyList();
        }
        Set<Node> allowedNodes = new HashSet<>();
        List<Node> nodes = nodeRepository.findByIsDeletedFalse();
        for (String role : currentUser.getRoles()) {
            for (Node node : nodes) {
                if (node.getRoles().contains(role)) {
                    allowedNodes.add(node);
                }
            }
        }
        List<NodeDto> nodeDtos = new ArrayList<>();
        for (Node node : allowedNodes) {
            nodeDtos.add(modelMapper.map(node, NodeDto.class));
        }
        return nodeDtos;
    }

    @Override
    public NodeDto save(NodeDto nodeDto) {
        String identifier = nodeDto.getIdentifier();
        Node existingNode = nodeRepository.findByIdentifier(identifier);
        if (existingNode != null) {
            if(existingNode.isDeleted()){
                nodeDto.setMessage("Node identifier - " + identifier + " not available");
                nodeDto.setSuccess(false);
                return nodeDto;
            }
            nodeDto.setMessage("Node with identifier - " + identifier + " already exists");
            nodeDto.setSuccess(false);
            return nodeDto;
        }
        Node node = modelMapper.map(nodeDto, Node.class);
        setAuditFields(node,true);
        nodeRepository.save(node);
        return nodeDto;
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
        setAuditFields(existingNode,false);
        nodeRepository.save(existingNode);
        return nodeDto;
    }

    @Override
    public void delete(String identifier) {
        Node node=nodeRepository.findByIdentifier(identifier.trim());
        softDelete(node);
        setAuditFields(node,false);
    }

    @Override
    public WsDto<NodeDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<NodeDto>>() {}.getType();
        Page<Node> nodePage = nodeRepository.findByIsDeletedFalse(pageable);
        WsDto<NodeDto> nodeWsDto = new WsDto<>();
        nodeWsDto.setDtoList(modelMapper.map(nodePage.getContent(), listType));
        nodeWsDto.setTotalRecords(nodePage.getTotalElements());
        nodeWsDto.setTotalPages(nodePage.getTotalPages());
        nodeWsDto.setSizePerPage(pageable.getPageSize());
        nodeWsDto.setPage(pageable.getPageNumber());
        return nodeWsDto;
    }

    @Override
    public NodeDto findByIdentifier(String identifier) {
        Node node = nodeRepository.findByIdentifier(identifier);
        return node != null ? modelMapper.map(node, NodeDto.class) : null;
    }

    @Override
    public WsDto<NodeDto> findAll(Specification<Node> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Page<Node> page = nodeRepository.findAll(example, pageable);
        WsDto<NodeDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}