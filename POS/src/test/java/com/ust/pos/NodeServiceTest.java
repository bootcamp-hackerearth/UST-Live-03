package com.ust.pos;

import com.ust.pos.dto.NodeDto;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.List;

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
    void getNodesForRolesTest() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("user", "pass", List.of());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(principal);

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(List.of("ADMIN"));

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        when(userRepository.findByUsername("user")).thenReturn(user);
        when(nodeRepository.findAll()).thenReturn(List.of(node));
        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getNodesForRolesNoMatchTest() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("user", "pass", List.of());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(principal);

        User user = new User();
        user.setRoles(List.of("USER"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(List.of("ADMIN"));

        when(userRepository.findByUsername("user")).thenReturn(user);
        when(nodeRepository.findAll()).thenReturn(List.of(node));

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertEquals(0, result.size());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Node node = new Node();
        node.setIdentifier("N1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        NodeDto result = nodeService.findByIdentifier("N1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("N1", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        NodeDto result = nodeService.findByIdentifier("N1");

        Assertions.assertNull(result);
    }

    @Test
    void saveTestSuccess() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();

        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        when(modelMapper.map(dto, Node.class)).thenReturn(node);

        NodeDto result = nodeService.save(dto);

        Assertions.assertEquals("N1", result.getIdentifier());
        verify(nodeRepository).save(node);
    }

    @Test
    void saveTestFailure() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();
        node.setIdentifier("N1");

        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        NodeDto result = nodeService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();

        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        NodeDto result = nodeService.update(dto);

        Assertions.assertEquals("N1", result.getIdentifier());
        verify(modelMapper).map(dto, node);
        verify(nodeRepository).save(node);
    }

    @Test
    void updateTestFailure() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        NodeDto result = nodeService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        nodeService.delete("N1");
        verify(nodeRepository).deleteByIdentifier("N1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = mock(Pageable.class);
        Page<Node> page = mock(Page.class);

        List<Node> nodes = List.of(new Node(), new Node());
        List<NodeDto> dtoList = List.of(new NodeDto(), new NodeDto());

        when(nodeRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(nodes);
        when(modelMapper.map(eq(nodes), any(Type.class))).thenReturn(dtoList);

        List<NodeDto> result = nodeService.findAll(pageable);

        Assertions.assertEquals(2, result.size());

        verify(nodeRepository).findAll(pageable);
        verify(page).getContent();
        verify(modelMapper).map(eq(nodes), any(Type.class));
    }
}