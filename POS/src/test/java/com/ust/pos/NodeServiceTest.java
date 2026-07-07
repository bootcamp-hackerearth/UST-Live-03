package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    public static final String INVALID = "INVALID";
    public static final String NODE_1 = "NODE1";
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String ADMIN1 = "admin";
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

        SecurityContextHolder.clearContext();

        assertTrue(service.getNodesForRoles().isEmpty());

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        ADMIN1,
                        "password",
                        List.of(() -> ADMIN)
                );

        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        com.ust.pos.modell.User user =
                new com.ust.pos.modell.User();

        user.setUsername(ADMIN1);
        user.setRoles(List.of(ADMIN));

        Node node = new Node();
        node.setIdentifier(NODE_1);
        node.setRoles(List.of(ADMIN));

        NodeDto dto = new NodeDto();

        when(userRepository.findByUsername(ADMIN1))
                .thenReturn(user);

        when(nodeRepository.findAllByDeletedFalse())
                .thenReturn(List.of(node));

        when(nodeRepository.findByIdentifierAndDeletedFalse(NODE_1))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        List<NodeDto> result = service.getNodesForRoles();

        assertEquals(1, result.size());
    }

    @Test
    void findByIdentifierTest() {

        Node node = new Node();
        NodeDto dto = new NodeDto();

        when(nodeRepository.findByIdentifierAndDeletedFalse(NODE_1))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        assertNotNull(service.findByIdentifier(NODE_1));

        when(nodeRepository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findByIdentifier(INVALID)
                );

        assertEquals(
                "node with identifier 'INVALID' not found",
                exception.getMessage()
        );
    }

    @Test
    void saveTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier(NODE_1);

        Node node = new Node();
        node.setStatus(null);

        when(nodeRepository.findByIdentifier(NODE_1))
                .thenReturn(null);

        when(modelMapper.map(dto, Node.class))
                .thenReturn(node);

        NodeDto result = service.save(dto);

        verify(nodeRepository).save(node);

        assertNotNull(result);
        assertTrue(node.getStatus());

        Node existing = new Node();
        existing.setDeleted(false);

        when(nodeRepository.findByIdentifier(NODE_1))
                .thenReturn(existing);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Node with identifier - NODE1 already exists",
                result.getMessage()
        );

        existing.setDeleted(true);

        when(nodeRepository.findByIdentifier(NODE_1))
                .thenReturn(existing);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Node with Identifier NODE1 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier(NODE_1);

        Node node = new Node();
        node.setIdentifier(NODE_1);
        node.setCreatedBy(ADMIN1);
        node.setCreatedOn(LocalDateTime.now());

        when(nodeRepository.findByIdentifierAndDeletedFalse(NODE_1))
                .thenReturn(node);

        NodeDto result = service.update(dto);

        verify(modelMapper).map(dto, node);
        verify(nodeRepository).save(node);

        assertNotNull(result);

        NodeDto invalidDto = new NodeDto();
        invalidDto.setIdentifier(INVALID);

        when(nodeRepository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        result = service.update(invalidDto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Node with identifier - INVALID not found",
                result.getMessage()
        );
    }

    @Test
    void deleteTest() {

        Node node = new Node();

        when(nodeRepository.findByIdentifierAndDeletedFalse(NODE_1))
                .thenReturn(node)
                .thenReturn(null);

        service.delete(NODE_1);

        verify(nodeRepository).save(node);

        service.delete(NODE_1);

        verify(nodeRepository, times(1)).save(node);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Node> page =
                new PageImpl<>(
                        List.of(new Node()),
                        pageable,
                        1
                );

        when(nodeRepository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(nodeRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new NodeDto()));

        WsDto<NodeDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        Specification<Node> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<NodeDto> specResult =
                service.findAll(specification, pageable);

        assertEquals(1, specResult.getDtoList().size());
        assertEquals(1, specResult.getTotalRecords());
        assertEquals(1, specResult.getTotalPage());

        verify(nodeRepository).findAllByDeletedFalse(pageable);
        verify(nodeRepository).findAll(any(Specification.class), eq(pageable));
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }
}