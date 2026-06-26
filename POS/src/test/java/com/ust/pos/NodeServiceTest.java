package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    private ModelMapper modelMapper;

    @Test
    void getNodesForRolesTest() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("user", "pass", List.of());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = new User();
        user.setRoles(List.of("ROLE_ADMIN"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(List.of("ROLE_ADMIN"));

        when(userRepository.findByUsername("user")).thenReturn(user);
        when(nodeRepository.findAll()).thenReturn(List.of(node));
        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(new NodeDto());

        List<NodeDto> result = service.getNodesForRoles();

        assertEquals(1, result.size());
    }

    @Test
    void getNodesForRolesNoAuthTest() {
        SecurityContextHolder.clearContext();

        List<NodeDto> result = service.getNodesForRoles();

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Node> page = new PageImpl<>(List.of(new Node()), pageable, 1);

        when(nodeRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new NodeDto()));

        WsDto<NodeDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void saveSuccessTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        when(modelMapper.map(dto, Node.class)).thenReturn(new Node());

        NodeDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(nodeRepository).save(any());
    }

    @Test
    void saveDuplicateActiveTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node existing = new Node();
        existing.setDeleted(false);

        when(nodeRepository.findByIdentifier("N1")).thenReturn(existing);

        NodeDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void saveDuplicateDeletedTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node existing = new Node();
        existing.setDeleted(true);

        when(nodeRepository.findByIdentifier("N1")).thenReturn(existing);

        NodeDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void findByIdentifierFoundTest() {
        Node node = new Node();
        NodeDto dto = new NodeDto();

        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        assertNotNull(service.findByIdentifier("N1"));
    }

    @Test
    void findByIdentifierNotFoundTest() {
        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        assertNull(service.findByIdentifier("N1"));
    }

    @Test
    void updateSuccessTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node existing = new Node();

        when(nodeRepository.findByIdentifier("N1")).thenReturn(existing);

        NodeDto result = service.update(dto);

        assertTrue(result.isSuccess());
        verify(nodeRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        NodeDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Node node = new Node();
        node.setDeleted(false);

        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        service.delete("N1");

        assertTrue(node.isDeleted());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Node node = new Node();
        node.setStatus(true);

        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        service.toggleStatus("N1");

        assertFalse(node.isStatus());
        verify(nodeRepository).save(node);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Node node = new Node();
        node.setStatus(false);

        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        service.toggleStatus("N1");

        assertTrue(node.isStatus());
        verify(nodeRepository).save(node);
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        service.toggleStatus("N1");

        verify(nodeRepository, never()).save(any());
    }
}