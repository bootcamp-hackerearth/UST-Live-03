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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
    void getNodesForRoles_NoAuth() {

        SecurityContextHolder.clearContext();

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRoles_Success() {

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("admin", "pass", new ArrayList<>());

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        User user = new User();
        user.setUsername("admin");
        user.setRoles(List.of("ROLE1"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(List.of("ROLE1"));

        when(userRepository.findByUsername("admin")).thenReturn(user);
        when(nodeRepository.findAll()).thenReturn(List.of(node));
        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        when(modelMapper.map(any(), eq(NodeDto.class))).thenReturn(new NodeDto());

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertEquals(1, result.size());
    }

    @Test
    void getNodesForRoles_UserRolesNull() {

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("admin", "pass", new ArrayList<>());

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        User user = new User();
        user.setUsername("admin");
        user.setRoles(List.of());

        when(userRepository.findByUsername("admin")).thenReturn(user);

        when(nodeRepository.findAll()).thenReturn(List.of());

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());
    }
    
    @Test
    void getNodesForRoles_NodeRolesNull() {

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("admin", "pass", new ArrayList<>());

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        User user = new User();
        user.setUsername("admin");
        user.setRoles(List.of("ROLE1"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(null);

        when(userRepository.findByUsername("admin")).thenReturn(user);
        when(nodeRepository.findAll()).thenReturn(List.of(node));

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRoles_NoMatchingRole() {

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("admin", "pass", new ArrayList<>());

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        User user = new User();
        user.setUsername("admin");
        user.setRoles(List.of("ROLE1"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(List.of("ROLE2"));

        when(userRepository.findByUsername("admin")).thenReturn(user);
        when(nodeRepository.findAll()).thenReturn(List.of(node));

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_WithPageable() {

        Pageable pageable = PageRequest.of(0, 10);
        List<Node> nodes = List.of(new Node());
        Page<Node> page = new PageImpl<>(nodes);

        when(nodeRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new NodeDto()));

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Node> nodes = List.of(new Node());
        Page<Node> page = new PageImpl<>(nodes);

        when(nodeRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new NodeDto()));

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());

        verify(nodeRepository).findAll(pageable);
    }

    @Test
    void findByIdentifierSuccess() {
        Node node = new Node();

        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(new NodeDto());

        assertNotNull(nodeService.findByIdentifier("N1"));
    }

    @Test
    void findByIdentifierNull() {
        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        assertNull(nodeService.findByIdentifier("N1"));
    }

    @Test
    void saveSuccess() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        when(modelMapper.map(dto, Node.class)).thenReturn(new Node());

        assertTrue(nodeService.save(dto).isSuccess());
    }

    @Test
    void saveDuplicate() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        when(nodeRepository.findByIdentifier("N1")).thenReturn(new Node());

        assertFalse(nodeService.save(dto).isSuccess());
    }

    @Test
    void updateSuccess() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node existing = new Node();

        when(nodeRepository.findByIdentifier("N1")).thenReturn(existing);

        assertTrue(nodeService.update(dto).isSuccess());
        verify(nodeRepository).save(existing);
    }

    @Test
    void updateNotFound() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        assertFalse(nodeService.update(dto).isSuccess());
    }

    @Test
    void deleteTest() {
        nodeService.delete("N1");
        verify(nodeRepository).deleteByIdentifier("N1");
    }
}
