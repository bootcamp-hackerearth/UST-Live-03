package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NodeServiceImpl nodeService;

    private Node node;
    private NodeDto nodeDto;

    @BeforeEach
    void setup() {

        node = new Node();
        node.setIdentifier("NODE1");
        node.setRoles(new ArrayList<>(Arrays.asList("ADMIN")));
        nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE1");
    }

    @Test
    void testFindByIdentifier_found() {

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto result = nodeService.findByIdentifier("NODE1");
        assertNotNull(result);
        assertEquals("NODE1", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_notFound() {

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(null);
        NodeDto result = nodeService.findByIdentifier("NODE1");
        assertNull(result);
    }

    @Test
    void testSave_duplicate() {

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);
        NodeDto result = nodeService.save(nodeDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_success() {

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(null);
        when(modelMapper.map(nodeDto, Node.class)).thenReturn(node);
        NodeDto result = nodeService.save(nodeDto);
        verify(nodeRepository).save(node);
        assertEquals("NODE1", result.getIdentifier());
    }

    @Test
    void testUpdate_notFound() {

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(null);
        NodeDto result = nodeService.update(nodeDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_success() {

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);
        NodeDto result = nodeService.update(nodeDto);

        verify(modelMapper).map(nodeDto, node);
        verify(nodeRepository).save(node);
        assertEquals("NODE1", result.getIdentifier());
    }

    @Test
    void testDelete() {

        nodeService.delete("NODE1");
        verify(nodeRepository).deleteByIdentifier("NODE1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Node node5 = new Node();
        NodeDto dto = new NodeDto();

        List<Node> nodes = List.of(node5);
        Page<Node> page = new PageImpl<>(nodes);

        when(nodeRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(node5, NodeDto.class))
                .thenReturn(dto);

        List<NodeDto> result = nodeService.findAll(pageable).getContent();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(nodeRepository).findAll(pageable);
    }

    @Test
    void testGetNodesForRoles_conditionTrue() {

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        "testUser", "pass", new ArrayList<>());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(springUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User appUser = new User();
        appUser.setUsername("testUser");
        appUser.setRoles(List.of("ADMIN"));

        when(userRepository.findByUsername("testUser")).thenReturn(appUser);
        Node node1 = new Node();
        node1.setIdentifier("NODE1");
        node1.setRoles(List.of("ADMIN"));

        when(nodeRepository.findAll()).thenReturn(List.of(node1));
        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node1);

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        when(modelMapper.map(node1, NodeDto.class)).thenReturn(dto);
        List<NodeDto> result = nodeService.getNodesForRoles();
        assertEquals(1, result.size());
    }

    @Test
    void testGetNodesForRoles_conditionFalse_rolesNotMatching() {

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        "testUser", "pass", new ArrayList<>());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(springUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User appUser = new User();
        appUser.setUsername("testUser");
        appUser.setRoles(List.of("USER"));

        when(userRepository.findByUsername("testUser")).thenReturn(appUser);

        Node node4 = new Node();
        node4.setIdentifier("NODE1");
        node4.setRoles(List.of("ADMIN"));

        when(nodeRepository.findAll()).thenReturn(List.of(node4));
        List<NodeDto> result = nodeService.getNodesForRoles();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNodesForRoles_rolesNull() {

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        "testUser", "pass", new ArrayList<>());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(springUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User appUser = new User();
        appUser.setUsername("testUser");
        appUser.setRoles(List.of("ADMIN"));

        when(userRepository.findByUsername("testUser")).thenReturn(appUser);

        Node node2 = new Node();
        node2.setIdentifier("NODE1");
        node2.setRoles(null);

        when(nodeRepository.findAll()).thenReturn(List.of(node2));

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNodesForRoles_withAuth() {

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        "testUser", "pass", new ArrayList<>());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(springUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = new User();
        user.setUsername("testUser");
        user.setRoles(List.of("ADMIN"));

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(nodeRepository.findAll()).thenReturn(List.of(node));
        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        List<NodeDto> result = nodeService.getNodesForRoles();
        assertEquals(1, result.size());
    }

    @Test
    void testGetNodesForRoles_principalNull() {

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(null);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<NodeDto> result = nodeService.getNodesForRoles();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository, never()).findByUsername(anyString());
        verify(nodeRepository, never()).findAll();
    }

    @Test
    void testGetNodesForRoles_noAuth() {

        SecurityContextHolder.getContext().setAuthentication(null);
        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());
    }
}