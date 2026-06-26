package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

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
    void findByIdentifierTest() {
        Node node = new Node();
        node.setIdentifier("User");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("User");

        Mockito.when(nodeRepository.findByIdentifier("User")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);
        NodeDto response = nodeService.findByIdentifier("User");
        Assertions.assertEquals("User", response.getIdentifier());
    }

    @Test
    void saveTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("User");

        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("User")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Node.class)).thenReturn(node);
        Mockito.when(nodeRepository.save(node)).thenReturn(node);
        NodeDto response = nodeService.save(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveFailure_existingActive() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("User");

        Node existing = new Node();
        existing.setDeleted(false);

        Mockito.when(nodeRepository.findByIdentifier("User")).thenReturn(existing);
        NodeDto response = nodeService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveFailure_softDeleted() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("User");

        Node existing = new Node();
        existing.setDeleted(true);

        Mockito.when(nodeRepository.findByIdentifier("User")).thenReturn(existing);
        NodeDto response = nodeService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void updateTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("User");

        Node existing = new Node();
        existing.setIdentifier("User");
        existing.setCreatedBy("admin");

        Mockito.when(nodeRepository.findByIdentifier("User")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(nodeRepository.save(existing)).thenReturn(existing);
        Mockito.when(modelMapper.map(existing, NodeDto.class)).thenReturn(dto);

        NodeDto response = nodeService.update(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("User", response.getIdentifier());
    }

    @Test
    void updateFailure() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("User");

        Mockito.when(nodeRepository.findByIdentifier("User")).thenReturn(null);
        NodeDto response = nodeService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Node node = new Node();
        node.setDeleted(false);

        Mockito.when(nodeRepository.findByIdentifier("User")).thenReturn(node);
        Mockito.when(nodeRepository.save(node)).thenReturn(node);

        nodeService.delete("User");
        Mockito.verify(nodeRepository).findByIdentifier("User");
        Mockito.verify(nodeRepository).save(node);
        Assertions.assertTrue(node.isDeleted());
    }

    @Test
    void findAllTest() {
        Node node = new Node();
        node.setIdentifier("User");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("User");

        List<Node> list = List.of(node);
        Page<Node> page = new PageImpl<>(list, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(nodeRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));

        WsDto<NodeDto> response = nodeService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("User", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void getNodesForRolesTest() {
        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User("admin", "pass", new ArrayList<>());
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(springUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = new User();
        user.setUsername("admin");
        user.setRoles(List.of("ROLE_ADMIN"));

        Mockito.when(userRepository.findByUsername("admin")).thenReturn(user);
        Node node = new Node();
        node.setIdentifier("dashboard");
        node.setRoles(List.of("ROLE_ADMIN"));

        Mockito.when(nodeRepository.findAll()).thenReturn(List.of(node));
        Mockito.when(nodeRepository.findByIdentifier("dashboard")).thenReturn(node);

        NodeDto dto = new NodeDto();
        dto.setIdentifier("dashboard");

        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("dashboard", response.get(0).getIdentifier());
    }

    @Test
    void getNodesForRolesAnonymousTest() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertTrue(response.isEmpty());
    }
}