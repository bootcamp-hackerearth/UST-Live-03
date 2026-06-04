package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.modell.Node;
import com.ust.pos.modell.NodeRepository;
import com.ust.pos.modell.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @InjectMocks
    private NodeServiceImpl service;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper mapper;

    @Test
    void save_shouldHandleBothCases() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");
        Node node = new Node();
        node.setIdentifier("NODE1");
        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(null);
        when(mapper.map(dto, Node.class)).thenReturn(node);
        NodeDto result = service.save(dto);
        verify(nodeRepository).save(node);
        assertTrue(result.isSuccess() || result.getMessage() == null);
        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);
        NodeDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void update_shouldHandleBothCases() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");
        Node node = new Node();
        node.setIdentifier("NODE1");
        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);
        service.update(dto);
        verify(mapper).map(dto, node);
        verify(nodeRepository).save(node);
        when(nodeRepository.findByIdentifier("X")).thenReturn(null);
        dto.setIdentifier("X");
        NodeDto failure = service.update(dto);
        assertFalse(failure.isSuccess());
        assertTrue(failure.getMessage().contains("not found"));
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete("NODE1");
        verify(nodeRepository).deleteByIdentifier("NODE1");
    }

    @Test
    void findAll_shouldHandleDataAndEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<NodeDto>>() {}.getType();
        Node node = new Node();
        node.setIdentifier("NODE1");
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");
        Page<Node> page =
                new PageImpl<>(List.of(node), pageable, 1);
        when(nodeRepository.findAll(pageable)).thenReturn(page);
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));
        assertEquals(1, service.findAll(pageable).size());
        Page<Node> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);
        when(nodeRepository.findAll(pageable)).thenReturn(emptyPage);
        when(mapper.map(List.of(), type)).thenReturn(List.of());
        assertTrue(service.findAll(pageable).isEmpty());
    }

    @Test
    void findAndRoles_shouldCoverAllScenarios() {
        Node node = new Node();
        node.setIdentifier("NODE1");
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");
        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);
        when(mapper.map(node, NodeDto.class)).thenReturn(dto);
        assertEquals("NODE1",
                service.findByIdentifier("NODE1").getIdentifier());
        SecurityContextHolder.clearContext();
        assertTrue(service.getNodesForRoles().isEmpty());
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "admin", "pwd", List.of(() -> "ROLE_ADMIN"));
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.getContext().setAuthentication(auth);
        com.ust.pos.modell.User appUser = new com.ust.pos.modell.User();
        appUser.setUsername("admin");
        appUser.setRoles(List.of("ROLE_ADMIN"));
        Node roleNode = new Node();
        roleNode.setIdentifier("NODE1");
        roleNode.setRoles(List.of("ROLE_ADMIN"));
        when(userRepository.findByUsername("admin")).thenReturn(appUser);
        when(nodeRepository.findAll()).thenReturn(List.of(roleNode));
        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(roleNode);
        when(mapper.map(roleNode, NodeDto.class)).thenReturn(dto);
        List<NodeDto> result = service.getNodesForRoles();
        assertEquals(1, result.size());
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }
}