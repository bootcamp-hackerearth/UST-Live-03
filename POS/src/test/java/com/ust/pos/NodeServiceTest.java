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

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(null);

        when(modelMapper.map(null, NodeDto.class))
                .thenReturn(null);

        NodeDto result =
                nodeService.findByIdentifier("NODE1");

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
    void toggleStatus_trueToFalse() {

        Node node1 = new Node();
        node1.setIdentifier("NODE1");
        node1.setStatus(true);

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node1);

        nodeService.toggleStatus("NODE1");

        assertFalse(node1.isStatus());

        verify(nodeRepository).save(node1);
    }

    @Test
    void testGetNodesForRoles_multipleRoles() {

        Pageable pageable = PageRequest.of(0, 10);

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        "testUser", "pass", new ArrayList<>());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(springUser);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        User appUser = new User();
        appUser.setUsername("testUser");
        appUser.setRoles(List.of("ADMIN", "MANAGER"));

        when(userRepository.findByUsername("testUser"))
                .thenReturn(appUser);

        Node adminNode = new Node();
        adminNode.setIdentifier("NODE1");
        adminNode.setRoles(List.of("ADMIN"));

        Node managerNode = new Node();
        managerNode.setIdentifier("NODE2");
        managerNode.setRoles(List.of("MANAGER"));

        Page<Node> page = new PageImpl<>(
                List.of(adminNode, managerNode));

        when(nodeRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(adminNode);

        when(nodeRepository.findByIdentifier("NODE2"))
                .thenReturn(managerNode);

        NodeDto dto1 = new NodeDto();
        dto1.setIdentifier("NODE1");

        NodeDto dto2 = new NodeDto();
        dto2.setIdentifier("NODE2");

        when(modelMapper.map(adminNode, NodeDto.class))
                .thenReturn(dto1);

        when(modelMapper.map(managerNode, NodeDto.class))
                .thenReturn(dto2);

        List<NodeDto> result =
                nodeService.getNodesForRoles(pageable);

        assertEquals(2, result.size());
    }

    @Test
    void testGetNodesForRoles_emptyPage() {

        Pageable pageable = PageRequest.of(0, 10);

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        "testUser", "pass", new ArrayList<>());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(springUser);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        User appUser = new User();
        appUser.setUsername("testUser");
        appUser.setRoles(List.of("ADMIN"));

        when(userRepository.findByUsername("testUser"))
                .thenReturn(appUser);

        when(nodeRepository.findByIsDeletedFalse(pageable))
                .thenReturn(Page.empty());

        List<NodeDto> result =
                nodeService.getNodesForRoles(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toggleStatus_falseToTrue() {

        Node node1 = new Node();
        node.setIdentifier("NODE1");
        node.setStatus(false);

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node1);

        nodeService.toggleStatus("NODE1");

        assertTrue(node1.isStatus());

        verify(nodeRepository).save(node1);
    }
    @Test
    void toggleStatus_nodeNotFound() {

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(null);

        nodeService.toggleStatus("NODE1");

        verify(nodeRepository).findByIdentifier("NODE1");
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void testGetNodesForRoles_duplicateNodesRemoved() {

        Pageable pageable = PageRequest.of(0, 10);

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        "testUser", "pass", new ArrayList<>());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(springUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User appUser = new User();
        appUser.setUsername("testUser");
        appUser.setRoles(List.of("ADMIN"));

        when(userRepository.findByUsername("testUser"))
                .thenReturn(appUser);

        Node node1 = new Node();
        node1.setIdentifier("NODE1");
        node1.setRoles(List.of("ADMIN"));

        Node node2 = new Node();
        node2.setIdentifier("NODE1");
        node2.setRoles(List.of("ADMIN"));

        Page<Node> page =
                new PageImpl<>(List.of(node1, node2));

        when(nodeRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node1);

        when(modelMapper.map(node1, NodeDto.class))
                .thenReturn(nodeDto);

        List<NodeDto> result =
                nodeService.getNodesForRoles(pageable);

        assertEquals(1, result.size());
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

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        nodeService.delete("NODE1");

        assertTrue(node.isDeleted());

        verify(nodeRepository)
                .findByIdentifier("NODE1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Node node1 = new Node();
        node.setIdentifier("NODE1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Page<Node> page =
                new PageImpl<>(List.of(node1), pageable, 1);

        when(nodeRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(node1, NodeDto.class))
                .thenReturn(dto);

        var result = nodeService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("NODE1",
                result.getContent().getFirst().getIdentifier());

        assertEquals(0, result.getPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalRecords());

        verify(nodeRepository)
                .findByIsDeletedFalse(pageable);
    }

    @Test
    void testSave_duplicateDeleted() {

        node.setDeleted(true);

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertFalse(result.isSuccess());

        assertTrue(result.getMessage()
                .contains("already exists but was deleted"));
    }

    @Test
    void testGetNodesForRoles_conditionTrue() {

        Pageable pageable = PageRequest.of(0, 10);
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

        Page<Node> page =
                new PageImpl<>(List.of(node1));

        when(nodeRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);
        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node1);

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        when(modelMapper.map(node1, NodeDto.class)).thenReturn(dto);
        List<NodeDto> result = nodeService.getNodesForRoles(pageable);
        assertEquals(1, result.size());
    }

    @Test
    void testGetNodesForRoles_conditionFalse_rolesNotMatching() {

        Pageable pageable = PageRequest.of(0, 10);
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

        Page<Node> page =
                new PageImpl<>(List.of(node4));

        when(nodeRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);        List<NodeDto> result = nodeService.getNodesForRoles(pageable);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNodesForRoles_rolesNull() {

        Pageable pageable = PageRequest.of(0, 10);
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

        Page<Node> page =
                new PageImpl<>(List.of(node2));

        when(nodeRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);
        List<NodeDto> result = nodeService.getNodesForRoles(pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNodesForRoles_withAuth() {

        Pageable pageable = PageRequest.of(0, 10);
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
        Page<Node> page =
                new PageImpl<>(List.of(node));

        when(nodeRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);
        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        List<NodeDto> result = nodeService.getNodesForRoles(pageable);
        assertEquals(1, result.size());
    }

    @Test
    void testGetNodesForRoles_principalNull() {

        Pageable pageable = PageRequest.of(0, 10);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(null);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<NodeDto> result = nodeService.getNodesForRoles(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository, never()).findByUsername(anyString());
        verify(nodeRepository, never()).findByIsDeletedFalse(any());    }

    @Test
    void testGetNodesForRoles_noAuth() {

        Pageable pageable = PageRequest.of(0, 10);
        SecurityContextHolder.getContext().setAuthentication(null);
        List<NodeDto> result = nodeService.getNodesForRoles(pageable);

        assertTrue(result.isEmpty());
    }
}