package com.ust.pos;

import com.ust.pos.node.service.impl.NodeServiceImpl;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.model.*;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Test
    void findAll_success() {

        Node node = new Node();
        NodeDto dto = new NodeDto();

        Page<Node> page = new PageImpl<>(List.of(node));

        Mockito.when(nodeRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(node)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<NodeDto> result =
                nodeService.findAll(Mockito.mock(Pageable.class));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void save_success() {

        NodeDto input = new NodeDto();
        input.setIdentifier("NODE1");

        Mockito.when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(null);

        Node entity = new Node();

        Mockito.when(modelMapper.map(input, Node.class))
                .thenReturn(entity);

        Mockito.when(nodeRepository.save(entity))
                .thenReturn(entity);

        NodeDto result = nodeService.save(input);

        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        NodeDto input = new NodeDto();
        input.setIdentifier("NODE1");

        Mockito.when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(new Node());

        NodeDto result = nodeService.save(input);

        Assertions.assertFalse(result.isSuccess());
    }

    @Test
    void findByIdentifier_success() {

        Node node = new Node();
        node.setIdentifier("NODE1");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Mockito.when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        NodeDto result = nodeService.findByIdentifier("NODE1");

        Assertions.assertEquals("NODE1", result.getIdentifier());
    }

    @Test
    void update_success() {

        NodeDto input = new NodeDto();
        input.setIdentifier("NODE1");

        Node existing = new Node();

        Mockito.when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        Mockito.when(nodeRepository.save(existing))
                .thenReturn(existing);

        NodeDto result = nodeService.update(input);

        Assertions.assertEquals("NODE1", result.getIdentifier());
    }

    @Test
    void update_failure_notFound() {

        NodeDto input = new NodeDto();
        input.setIdentifier("NODE1");

        Mockito.when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(null);

        NodeDto result = nodeService.update(input);

        Assertions.assertFalse(result.isSuccess());
    }

    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(nodeRepository).deleteByIdentifier("NODE1");

        nodeService.delete("NODE1");

        Mockito.verify(nodeRepository)
                .deleteByIdentifier("NODE1");
    }

    @Test
    void changeToggleStatus_success() {

        Node node = new Node();
        node.setStatus(false);

        NodeDto dto = new NodeDto();

        Mockito.when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        Mockito.when(nodeRepository.save(node))
                .thenReturn(node);

        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        NodeDto result =
                nodeService.changeToggleStatus("NODE1", true);

        Assertions.assertTrue(node.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void getNodesForRoles_success() {

        UserDetails principal =
                new org.springframework.security.core.userdetails.User(
                        "john", "password", List.of()
                );

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        User user = new User();
        user.setUsername("john");
        user.setRoles(List.of("ADMIN"));

        Mockito.when(userRepository.findByUsername("john"))
                .thenReturn(user);

        Node node1 = new Node();
        node1.setIdentifier("NODE1");
        node1.setRoles(List.of("ADMIN"));

        Node node2 = new Node();
        node2.setIdentifier("NODE2");
        node2.setRoles(List.of("USER"));

        Mockito.when(nodeRepository.findAll())
                .thenReturn(List.of(node1, node2));

        NodeDto dto = new NodeDto();

        Mockito.when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node1);

        Mockito.when(modelMapper.map(node1, NodeDto.class))
                .thenReturn(dto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, result.size());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}