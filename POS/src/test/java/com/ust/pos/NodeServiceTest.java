package com.ust.pos;


import java.util.Arrays;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Type;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Test
    void getNodesForRolesTest() {
        UserDetails springUser =
                new org.springframework.security.core.userdetails.User(
                        "admin", "pwd", new ArrayList<>());
        Mockito.when(authentication.getPrincipal()).thenReturn(springUser);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User user = new User();
        user.setUsername("admin");
        List<String> roles = new ArrayList<>();
        roles.add("ADMIN");
        user.setRoles(roles);
        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(user);
        Node node1 = new Node();
        node1.setIdentifier("NODE1");
        node1.setRoles(Arrays.asList("ADMIN"));
        Node node2 = new Node();
        node2.setIdentifier("NODE2");
        node2.setRoles(Arrays.asList("USER"));
        Mockito.when(nodeRepository.findByDeletedFalse())
                .thenReturn(Arrays.asList(node1, node2));
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("NODE1"))
                .thenReturn(node1);
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE1");
        Mockito.when(modelMapper.map(node1, NodeDto.class))
                .thenReturn(nodeDto);
        List<NodeDto> result = nodeService.getNodesForRoles();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("NODE1", result.get(0).getIdentifier());
        SecurityContextHolder.clearContext();
    }

    @Test
    void saveTest_Success() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Node entity = new Node();
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Node.class))
                .thenReturn(entity);
        Mockito.when(nodeRepository.save(entity))
                .thenReturn(entity);
        NodeDto response = nodeService.save(dto);
        Assertions.assertEquals("N1", response.getIdentifier());
        Mockito.verify(nodeRepository).save(entity);
    }

    @Test
    void saveTest_Failure_WhenExists() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1"))
                .thenReturn(new Node());
        NodeDto response = nodeService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertEquals("Node with identifier - N1 already exists",
                response.getMessage()
        );
        Mockito.verify(nodeRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest_Success() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Node existing = new Node();
        existing.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1"))
                .thenReturn(existing);
        Mockito.doNothing()
                .when(modelMapper).map(dto, existing);
        Mockito.when(nodeRepository.save(existing))
                .thenReturn(existing);
        NodeDto response = nodeService.update(dto);
        Assertions.assertEquals("N1", response.getIdentifier());
        Mockito.verify(nodeRepository).save(existing);
    }

    @Test
    void updateTest_Failure_WhenNotFound() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1"))
                .thenReturn(null);
        NodeDto response = nodeService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertEquals("Node with identifier - N1 not found",
                response.getMessage()
        );
    }

    @Test
    void findAllTest() {
        List<Node> entities = List.of(new Node());
        List<NodeDto> dtos = List.of(new NodeDto());
        Type listType = new TypeToken<List<NodeDto>>() {}.getType();
        Mockito.when(nodeRepository.findByDeletedFalse())
                .thenReturn(entities);
        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);
        List<NodeDto> response = nodeService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByIdentifierTest() {
        Node node = new Node();
        node.setIdentifier("N1");
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1"))
                .thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);
        NodeDto response = nodeService.findByIdentifier("N1");
        Assertions.assertEquals("N1", response.getIdentifier());
    }

    @Test
    void deleteTest() {
        Node node = new Node();
        node.setIdentifier("N1");
        node.setDeleted(false);
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(node);
        nodeService.delete("N1");
        Assertions.assertTrue(node.isDeleted());
        Mockito.verify(nodeRepository).save(node);
    }

    @Test
    void deleteTest_WhenNodeNotFound() {
        Mockito.when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(null);
        nodeService.delete("N1");
        Mockito.verify(nodeRepository, Mockito.never())
                .save(Mockito.any());
    }
    @Test
    void findAll_WithPagination_ShouldReturnNodeDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Node node = new Node();
        NodeDto nodeDto = new NodeDto();
        Page<Node> page = new PageImpl<>(List.of(node));
        Mockito.when(nodeRepository.findByDeletedFalse(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(nodeDto);
        Page<NodeDto> response =
                nodeService.findAll(pageable, null);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
        Mockito.verify(nodeRepository).findByDeletedFalse(pageable);
        Mockito.verify(modelMapper).map(node, NodeDto.class);
    }


    @Test
    void findAll_WithSearch_ShouldReturnNodeDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Node node = new Node();
        NodeDto nodeDto = new NodeDto();
        Page<Node> page = new PageImpl<>(List.of(node));
        Mockito.when(nodeRepository.findAll(
                        Mockito.<Specification<Node>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(page);
        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(nodeDto);
        Page<NodeDto> response = nodeService.findAll(pageable, "NODE");
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
        Mockito.verify(nodeRepository)
                .findAll(
                        Mockito.<Specification<Node>>any(),
                        Mockito.eq(pageable)
                );
    }
}