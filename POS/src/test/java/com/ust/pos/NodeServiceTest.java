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
import org.mockito.Mockito;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;

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
        Authentication authentication = Mockito.mock(Authentication.class);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("user", "pass", Arrays.asList());

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        User user = new User();
        user.setRoles(Arrays.asList("ADMIN"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(Arrays.asList("ADMIN"));

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(userRepository.findByUsername("user")).thenReturn(user);
        Mockito.when(nodeRepository.findAll()).thenReturn(Arrays.asList(node));
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("N1", result.get(0).getIdentifier());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Node node = new Node();
        node.setIdentifier("N1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        NodeDto result = nodeService.findByIdentifier("N1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("N1", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        NodeDto result = nodeService.findByIdentifier("N1");

        Assertions.assertNull(result);
    }

    @Test
    void saveTestSuccess() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Node.class)).thenReturn(node);

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

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        NodeDto result = nodeService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void updateTestSuccess() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        NodeDto result = nodeService.update(dto);

        Assertions.assertEquals("N1", result.getIdentifier());

        verify(modelMapper).map(dto, node);
        verify(nodeRepository).save(node);
    }

    @Test
    void updateTestFailure() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        NodeDto result = nodeService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void deleteTest() {
        nodeService.delete("N1");
        verify(nodeRepository).deleteByIdentifier("N1");
    }

    @Test
    void findAllTest() {
        List<Node> nodes = Arrays.asList(new Node(), new Node());
        List<NodeDto> dtoList = Arrays.asList(new NodeDto(), new NodeDto());

        Pageable pageable = PageRequest.of(0, 10);

        Page<Node> nodePage = new PageImpl<>(nodes, pageable, nodes.size());

        Mockito.when(nodeRepository.findAll(pageable)).thenReturn(nodePage);
        Mockito.when(modelMapper.map(Mockito.eq(nodes), Mockito.any(Type.class))).thenReturn(dtoList);

        List<NodeDto> result = nodeService.findAll(pageable);

        Assertions.assertEquals(2, result.size());

        Mockito.verify(nodeRepository).findAll(pageable);
    }

    @Test
    void getNodesForRolesAuthenticationNullTest() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRolesInvalidPrincipalTest() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("user");

        SecurityContextHolder.setContext(securityContext);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRolesUserNotFoundTest() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("user", "pass", Arrays.asList());

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContextHolder.setContext(securityContext);

        Mockito.when(userRepository.findByUsername("user")).thenReturn(null);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findAllWithoutPageableTest() {
        List<Node> nodes = Arrays.asList(new Node(), new Node());

        List<NodeDto> dtoList = Arrays.asList(new NodeDto(), new NodeDto());

        Mockito.when(nodeRepository.findAll()).thenReturn(nodes);
        Mockito.when(modelMapper.map(Mockito.eq(nodes), Mockito.any(Type.class))).thenReturn(dtoList);

        List<NodeDto> result = nodeService.findAll();

        Assertions.assertEquals(2, result.size());

        verify(nodeRepository).findAll();
    }
}