package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private NodeServiceImpl nodeService;

    private NodeDto nodeDto;
    private Node node;

    @BeforeEach
    void setUp() {
        nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE-001");

        node = new Node();
        node.setIdentifier("NODE-001");
        node.setStatus(true);
        node.setDeleted(false);
        // FIX: Changed from Set.of to List.of to match your domain model's expected type
        node.setRoles(List.of("ROLE_ADMIN"));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Save Node - Success")
    void save_Success() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(null);
        when(modelMapper.map(nodeDto, Node.class)).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Node created successfully", result.getMessage());
        verify(nodeRepository).save(node);
    }

    @Test
    @DisplayName("Save Node - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        node.setDeleted(false);
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(nodeRepository, never()).save(any(Node.class));
    }

    @Test
    @DisplayName("Save Node - Failure: Previously Soft-Deleted")
    void save_Failure_PreviouslyDeleted() {
        node.setDeleted(true);
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
        verify(nodeRepository, never()).save(any(Node.class));
    }

    @Test
    @DisplayName("Find All Nodes - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Node> nodePage = new PageImpl<>(List.of(node));

        when(nodeRepository.findByDeletedFalse(pageable)).thenReturn(nodePage);
        when(modelMapper.map(eq(nodePage.getContent()), any(Type.class))).thenReturn(List.of(nodeDto));

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertFalse(result.getDtoList().isEmpty());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto result = nodeService.findByIdentifier("NODE-001");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Get Nodes For Roles - Success with SecurityContext validation")
    void getNodesForRoles_Success() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("adminUser", "password", List.of());

        User currentUser = new User();
        currentUser.setUsername("adminUser");
        // FIX: Changed from Set.of to List.of to support User's role mapping type requirements
        currentUser.setRoles(List.of("ROLE_ADMIN"));

        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername("adminUser")).thenReturn(currentUser);
        when(nodeRepository.findAllByStatusAndDeletedFalse(true)).thenReturn(List.of(node));
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Update Node - Success")
    void update_Success() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        NodeDto result = nodeService.update(nodeDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Node updated successfully", result.getMessage());
        verify(nodeRepository).save(node);
    }

    @Test
    @DisplayName("Update Node - Failure: Not Found")
    void update_Failure_NotFound() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(null);

        NodeDto result = nodeService.update(nodeDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(nodeRepository, never()).save(any(Node.class));
    }

    @Test
    @DisplayName("Update Node - Failure: Target Node Soft Deleted")
    void update_Failure_Deleted() {
        node.setDeleted(true);
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        NodeDto result = nodeService.update(nodeDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
        verify(nodeRepository, never()).save(any(Node.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto result = nodeService.toggleStatus("NODE-001");

        Assertions.assertFalse(node.isStatus());
        verify(nodeRepository).save(node);
    }

    @Test
    @DisplayName("Delete Node - Success")
    void delete_Success() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);

        boolean result = nodeService.delete("NODE-001");

        Assertions.assertTrue(result);
        verify(nodeRepository).save(node);
    }

    @Test
    @DisplayName("Delete Node - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(null);

        boolean result = nodeService.delete("NODE-001");

        Assertions.assertFalse(result);
        verify(nodeRepository, never()).save(any(Node.class));
    }
}