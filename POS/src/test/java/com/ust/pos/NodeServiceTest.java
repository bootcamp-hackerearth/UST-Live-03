package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void save_success() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Node.class)).thenReturn(node);

        NodeDto response = nodeService.save(dto);

        verify(nodeRepository).save(node);
        assertTrue(response.isSuccess());
    }

    @Test
    void save_failure_existingNode() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(new Node());

        NodeDto response = nodeService.save(dto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void save_existingNodeDeleted() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();
        node.setIdentifier("N1");
        node.setDeleted(true);

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(node);

        NodeDto response =
                nodeService.save(dto);

        assertFalse(response.isSuccess());

        assertTrue(
                response.getMessage()
                        .contains("deleted")
        );


        verify(nodeRepository, never())
                .save(any());
    }

    @Test
    void save_existingPath() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        dto.setPath("/test");

        Node existing = new Node();
        existing.setIdentifier("N2");

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(null);

        when(nodeRepository.findByPath("/test"))
                .thenReturn(existing);

        NodeDto response =
                nodeService.save(dto);

        assertFalse(response.isSuccess());

        assertEquals(
                "A node with this path already exists.",
                response.getMessage()
        );
    }

    @Test
    void save_existingDeletedPath() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        dto.setPath("/test");

        Node existing = new Node();
        existing.setDeleted(true);

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(null);

        when(nodeRepository.findByPath("/test"))
                .thenReturn(existing);

        NodeDto response =
                nodeService.save(dto);

        assertFalse(response.isSuccess());

        assertTrue(
                response.getMessage()
                        .contains("deleted")
        );
    }

    @Test
    void update_success() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();

        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        doNothing().when(modelMapper).map(dto, node);

        when(nodeRepository.save(node)).thenReturn(node);

        NodeDto response = nodeService.update(dto);

        assertTrue(response.isSuccess());
        assertEquals("Node updated successfully.", response.getMessage());
        verify(nodeRepository).save(node);
    }

    @Test
    void update_nodeNotFound() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        NodeDto response = nodeService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals("Node not found.", response.getMessage());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void update_deletedNode() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();
        node.setIdentifier("N1");
        node.setDeleted(true);

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(node);

        NodeDto response =
                nodeService.update(dto);

        assertFalse(response.isSuccess());

        assertTrue(
                response.getMessage()
                        .contains("deleted")
        );
        verify(nodeRepository, never())
                .save(any());
    }

    @Test
    void deleteTest() {

        Node node = new Node();
        node.setIdentifier("N1");
        node.setDeleted(false);

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(node);

        when(nodeRepository.save(node))
                .thenReturn(node);

        nodeService.delete("N1");

        assertTrue(node.isDeleted());

        verify(nodeRepository)
                .findByIdentifier("N1");

        verify(nodeRepository)
                .save(node);
    }

    @Test
    void findByIdentifierTest() {
        Node node = new Node();
        NodeDto dto = new NodeDto();

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        NodeDto response = nodeService.findByIdentifier("N1");

        Assertions.assertNotNull(response);
    }

    @Test
    void findAllWithPageableTest() {

        Node node = new Node();
        node.setIdentifier("CUST1");

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("CUST1");

        List<Node> nodes = List.of(node);
        List<NodeDto> nodeDtos = List.of(nodeDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Node> nodePage = new PageImpl<>(nodes);

        when(nodeRepository.findByIsDeletedFalse(pageable))
                .thenReturn(nodePage);

        when(modelMapper.map(
                eq(nodes),
                any(Type.class)
        )).thenReturn(nodeDtos);

        PaginationResponseDto<NodeDto> result =
                nodeService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(
                "CUST1",
                result.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void findAllWithPageable_emptyResult() {

        Pageable pageable = PageRequest.of(0, 5);
        Page<Node> emptyPage = Page.empty();

        when(nodeRepository.findByIsDeletedFalse(pageable))
                .thenReturn(emptyPage);

        when(modelMapper.map(
                eq(List.of()),
                any(Type.class)
        )).thenReturn(List.of());

        PaginationResponseDto<NodeDto> result =
                nodeService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getDtoList().isEmpty());
    }

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable =
                PageRequest.of(0,5);

        Specification<Node> specification =
                mock(Specification.class);

        Node node = new Node();
        node.setIdentifier("N1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Page<Node> page =
                new PageImpl<>(
                        List.of(node),
                        pageable,
                        1
                );

        when(
                nodeRepository.findAll(
                        eq(specification),
                        eq(pageable)
                )
        ).thenReturn(page);

        when(
                modelMapper.map(
                        eq(List.of(node)),
                        any(Type.class)
                )
        ).thenReturn(List.of(dto));

        PaginationResponseDto<NodeDto> response =
                nodeService.findAll(
                        specification,
                        pageable
                );

        assertEquals(
                1,
                response.getDtoList().size()
        );

        assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void updateStatus_success() {
        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(node);

        NodeDto response = nodeService.updateStatus("N1", true);

        assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
    }

    @Test
    void updateStatus_failure() {
        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(null);

        NodeDto response = nodeService.updateStatus("N1", true);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Node not found", response.getMessage());
    }

    @Test
    void updateStatus_verifyStatusChange() {

        Node node = new Node();
        node.setStatus(false);

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(node);

        NodeDto response =
                nodeService.updateStatus(
                        "N1",
                        true
                );

        assertTrue(response.isSuccess());
        assertTrue(node.isStatus());

        verify(nodeRepository)
                .save(node);
    }

    @Test
    void update_pathConflict() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        dto.setPath("/test");

        Node node = new Node();
        node.setIdentifier("N1");

        Node conflict = new Node();
        conflict.setIdentifier("N2");

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(node);

        when(nodeRepository.findByPath("/test"))
                .thenReturn(conflict);

        NodeDto response =
                nodeService.update(dto);

        assertFalse(response.isSuccess());

        assertEquals(
                "A node with this path already exists.",
                response.getMessage()
        );
    }

    @Test
    void update_deletedPathConflict() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        dto.setPath("/test");

        Node node = new Node();
        node.setIdentifier("N1");

        Node conflict = new Node();
        conflict.setIdentifier("N2");
        conflict.setDeleted(true);

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(node);

        when(nodeRepository.findByPath("/test"))
                .thenReturn(conflict);

        NodeDto response =
                nodeService.update(dto);

        assertFalse(response.isSuccess());

        assertTrue(
                response.getMessage()
                        .contains("deleted")
        );
    }

    @Test
    void getNodesForRoles_emptyAuth() {
        SecurityContextHolder.clearContext();

        List<NodeDto> response = nodeService.getNodesForRoles();

        assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRoles_noUser() {
        var auth = new UsernamePasswordAuthenticationToken("test", "pass");
        SecurityContextHolder.getContext().setAuthentication(auth);

        List<NodeDto> response = nodeService.getNodesForRoles();

        assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRoles_success() {

        var springUser =
                new org.springframework.security.core.userdetails.User(
                        "john",
                        "pass",
                        List.of()
                );

        var auth =
                new UsernamePasswordAuthenticationToken(
                        springUser,
                        null
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(auth);

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(List.of("ADMIN"));

        NodeDto dto = new NodeDto();

        Page<Node> nodePage =
                new PageImpl<>(List.of(node));

        Mockito.when(userRepository.findByUsername("john"))
                .thenReturn(user);

        Mockito.when(nodeRepository.findByIsDeletedFalse(null))
                .thenReturn(nodePage);

        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(node);

        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        List<NodeDto> response =
                nodeService.getNodesForRoles();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void getNodesForRoles_userRolesNull() {
        var springUser = new org.springframework.security.core.userdetails.User(
                "john", "pass", List.of()
        );

        var auth = new UsernamePasswordAuthenticationToken(springUser, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setRoles(null);

        Mockito.when(userRepository.findByUsername("john")).thenReturn(user);

        List<NodeDto> response = nodeService.getNodesForRoles();

        assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRoles_nodeRolesNull() {
        var springUser = new org.springframework.security.core.userdetails.User(
                "john", "pass", List.of()
        );

        var auth = new UsernamePasswordAuthenticationToken(springUser, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(null);

        Mockito.when(userRepository.findByUsername("john")).thenReturn(user);
        Mockito.when(nodeRepository.findByIsDeletedFalse(null))
                .thenReturn(new PageImpl<>(List.of(node)));

        List<NodeDto> response = nodeService.getNodesForRoles();

        assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRoles_nodeMissingOnLookup() {
        var springUser = new org.springframework.security.core.userdetails.User(
                "john", "pass", List.of()
        );

        var auth = new UsernamePasswordAuthenticationToken(springUser, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(List.of("ADMIN"));

        Mockito.when(userRepository.findByUsername("john")).thenReturn(user);
        Mockito.when(nodeRepository.findByIsDeletedFalse(null))
                .thenReturn(new PageImpl<>(List.of(node)));
        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        List<NodeDto> response = nodeService.getNodesForRoles();

        assertTrue(response.isEmpty());
    }
}