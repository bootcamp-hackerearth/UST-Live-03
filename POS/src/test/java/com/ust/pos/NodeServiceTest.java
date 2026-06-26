package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    void findByIdentifier_Found() {

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
    void save_NewNode() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();

        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        when(modelMapper.map(dto, Node.class)).thenReturn(node);
        when(nodeRepository.save(node)).thenReturn(node);

        NodeDto result = nodeService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("N1", result.getIdentifier());
        verify(nodeRepository).save(node);
    }

    @Test
    void save_NodeExists() {

        Node existing = new Node();

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        when(nodeRepository.findByIdentifier("N1")).thenReturn(existing);

        NodeDto result = nodeService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void update_NodeExists() {

        Node existing = new Node();

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        when(nodeRepository.findByIdentifier("N1")).thenReturn(existing);
        when(nodeRepository.save(existing)).thenReturn(existing);

        NodeDto result = nodeService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("N1", result.getIdentifier());
        verify(modelMapper).map(dto, existing);
        verify(nodeRepository).save(existing);
    }

    @Test
    void update_NodeNotFound() {

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

        Node node = new Node();

        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);

        nodeService.delete("N1");

        verify(nodeRepository).findByIdentifier("N1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Node node1 = new Node();
        Node node2 = new Node();

        Page<Node> page = new PageImpl<>(List.of(node1, node2), pageable, 2);

        List<NodeDto> dtoList = List.of(new NodeDto(), new NodeDto());

        when(nodeRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtoList);

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(2, result.getTotalRecords());

        verify(nodeRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Node> page = new PageImpl<>(List.of(), pageable, 0);

        when(nodeRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of());

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertEquals(0, result.getTotalRecords());

        verify(nodeRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void getNodesForRoles_Test() {

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("user1", "pass", List.of());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(principal);

        User user = new User();
        user.setUsername("user1");
        user.setRoles(List.of("ROLE_ADMIN"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(List.of("ROLE_ADMIN"));

        NodeDto dto = new NodeDto();

        when(userRepository.findByUsername("user1")).thenReturn(user);
        when(nodeRepository.findAll()).thenReturn(List.of(node));
        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }
}