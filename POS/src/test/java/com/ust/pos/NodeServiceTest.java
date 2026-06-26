package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Node;
import com.ust.pos.modell.NodeRepository;
import com.ust.pos.modell.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.AfterEach;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    public static final String NODE_1 = "NODE1";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ADMIN = "admin";
    @InjectMocks
    private NodeServiceImpl service;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierTest() {

        Node node = new Node();
        NodeDto dto = new NodeDto();

        when(nodeRepository.findByIdentifierAndDeletedFalse(NODE_1))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        NodeDto result =
                service.findByIdentifier(NODE_1);

        assertNotNull(result);
    }

    @Test
    void saveSuccessTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier(NODE_1);

        Node node = new Node();

        when(nodeRepository.findByIdentifier(NODE_1))
                .thenReturn(null);

        when(modelMapper.map(dto, Node.class))
                .thenReturn(node);

        NodeDto result = service.save(dto);

        assertNotNull(result);

        verify(nodeRepository).save(node);
    }

    @Test
    void saveDuplicateTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier(NODE_1);

        Node existing = new Node();
        existing.setDeleted(false);

        when(nodeRepository.findByIdentifier(NODE_1))
                .thenReturn(existing);

        NodeDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Node with identifier - NODE1 already exists",
                result.getMessage()
        );
    }

    @Test
    void saveSoftDeletedTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier(NODE_1);

        Node existing = new Node();
        existing.setDeleted(true);

        when(nodeRepository.findByIdentifier(NODE_1))
                .thenReturn(existing);

        NodeDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Node with Identifier NODE1 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateSuccessTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier(NODE_1);

        Node node = new Node();
        node.setIdentifier(NODE_1);
        node.setCreatedBy(ADMIN);
        node.setCreatedOn(LocalDateTime.now());

        when(nodeRepository.findByIdentifierAndDeletedFalse(NODE_1))
                .thenReturn(node);

        NodeDto result = service.update(dto);

        assertNotNull(result);

        verify(modelMapper).map(dto, node);
        verify(nodeRepository).save(node);
    }

    @Test
    void updateNotFoundTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier(NODE_1);

        when(nodeRepository.findByIdentifierAndDeletedFalse(NODE_1))
                .thenReturn(null);

        NodeDto result = service.update(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Node with identifier - NODE1 not found",
                result.getMessage()
        );
    }

    @Test
    void deleteSuccessTest() {

        Node node = new Node();

        when(nodeRepository.findByIdentifierAndDeletedFalse(NODE_1))
                .thenReturn(node);

        service.delete(NODE_1);

        verify(nodeRepository).save(node);
    }

    @Test
    void deleteNotFoundTest() {

        when(nodeRepository.findByIdentifierAndDeletedFalse(NODE_1))
                .thenReturn(null);

        service.delete(NODE_1);

        verify(nodeRepository, never()).save(any());
    }

    @Test
    void findAllTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Node node = new Node();
        NodeDto dto = new NodeDto();

        Page<Node> page =
                new PageImpl<>(
                        List.of(node),
                        pageable,
                        1
                );

        when(nodeRepository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<NodeDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Node> page =
                new PageImpl<>(
                        Collections.emptyList(),
                        pageable,
                        0
                );

        when(nodeRepository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        WsDto<NodeDto> result =
                service.findAll(pageable);

        assertTrue(result.getDtoList().isEmpty());
    }

    @Test
    void getNodesForRolesNoAuthenticationTest() {

        SecurityContextHolder.clearContext();

        List<NodeDto> result =
                service.getNodesForRoles();

        assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRolesTest() {

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        ADMIN,
                        "password",
                        List.of(() -> ROLE_ADMIN)
                );

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getPrincipal())
                .thenReturn(principal);

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        com.ust.pos.modell.User user =
                new com.ust.pos.modell.User();

        user.setUsername(ADMIN);
        user.setRoles(List.of(ROLE_ADMIN));

        Node node = new Node();
        node.setIdentifier(NODE_1);
        node.setRoles(List.of(ROLE_ADMIN));

        NodeDto dto = new NodeDto();

        when(userRepository.findByUsername(ADMIN))
                .thenReturn(user);

        when(nodeRepository.findAllByDeletedFalse())
                .thenReturn(List.of(node));

        when(nodeRepository.findByIdentifierAndDeletedFalse(NODE_1))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        List<NodeDto> result =
                service.getNodesForRoles();

        assertEquals(1, result.size());
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }
}