package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Node;
import com.ust.pos.modell.NodeRepository;
import com.ust.pos.modell.User;
import com.ust.pos.modell.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    void testSave_Success() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Node mapped = new Node();
        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        when(modelMapper.map(dto, Node.class)).thenReturn(mapped);
        NodeDto result = nodeService.save(dto);
        assertNotNull(result);
        verify(nodeRepository).save(mapped);
    }

    @Test
    void testSave_AlreadyExists() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        when(nodeRepository.findByIdentifier("N1")).thenReturn(new Node());
        NodeDto result = nodeService.save(dto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_DeletedExists() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Node node = new Node();
        node.setDeleted(true);
        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        NodeDto result = nodeService.save(dto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("was deleted"));
    }

    @Test
    void testFindByIdentifier() {
        Node node = new Node();
        node.setIdentifier("N1");
        NodeDto dto = new NodeDto();
        when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);
        NodeDto result = nodeService.findByIdentifier("N1");
        assertNotNull(result);
    }

    @Test
    void testUpdate_Success() {
        Node existing = new Node();
        existing.setIdentifier("N1");
        existing.setCreatedBy("admin");
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(existing);
        NodeDto result = nodeService.update(dto);
        assertNotNull(result);
        verify(nodeRepository).save(existing);
    }

    @Test
    void testUpdate_NotFound() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(null);
        NodeDto result = nodeService.update(dto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testDelete_Success() {
        Node node = new Node();
        when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(node);
        nodeService.delete("N1");
        verify(nodeRepository).save(node);
    }

    @Test
    void testDelete_NotFound() {
        when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(null);
        nodeService.delete("N1");
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Node> nodes = List.of(new Node());
        Page<Node> page = new PageImpl<>(nodes, pageable, 1);
        List<NodeDto> dtoList = List.of(new NodeDto());
        when(nodeRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(nodes), any(Type.class))).thenReturn(dtoList);
        WsDto<NodeDto> result = nodeService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        verify(nodeRepository).findAllByDeletedFalse(pageable);
    }

    @Test
    void testGetNodesForRoles_Success() {
        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);
        org.springframework.security.core.userdetails.User springUser = new org.springframework.security.core.userdetails.User("user1", "pass", List.of());
        when(auth.getPrincipal()).thenReturn(springUser);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        User user = new User();
        user.setUsername("user1");
        user.setRoles(List.of("ADMIN"));
        when(userRepository.findByUsername("user1")).thenReturn(user);
        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(List.of("ADMIN"));
        when(nodeRepository.findAllByDeletedFalse()).thenReturn(List.of(node));
        when(nodeRepository.findByIdentifierAndDeletedFalse("N1")).thenReturn(node);
        when(modelMapper.map(any(Node.class), eq(NodeDto.class))).thenReturn(new NodeDto());
        List<NodeDto> result = nodeService.getNodesForRoles();
        assertEquals(1, result.size());
    }

    @Test
    void testGetNodesForRoles_NoAuth() {
        SecurityContextHolder.clearContext();
        List<NodeDto> result = nodeService.getNodesForRoles();
        assertTrue(result.isEmpty());
    }
}