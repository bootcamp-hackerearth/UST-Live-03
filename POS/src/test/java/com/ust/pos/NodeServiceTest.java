package com.ust.pos;

import com.ust.pos.dto.WsDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

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
        Pageable pageable = Mockito.mock(Pageable.class);

        Page<Node> page = new PageImpl<>(List.of(node));

        when(nodeRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(
                        Mockito.eq(List.of(node)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<NodeDto> result =
                nodeService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());    }

    @Test
    void save_success() {

        NodeDto input = new NodeDto();
        input.setIdentifier("NODE1");

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(null);

        Node entity = new Node();

        when(modelMapper.map(input, Node.class))
                .thenReturn(entity);

        when(nodeRepository.save(entity))
                .thenReturn(entity);

        NodeDto result = nodeService.save(input);

        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        NodeDto input = new NodeDto();
        input.setIdentifier("NODE1");

        when(nodeRepository.findByIdentifier("NODE1"))
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

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        NodeDto result = nodeService.findByIdentifier("NODE1");

        assertEquals("NODE1", result.getIdentifier());
    }

    @Test
    void update_success() {

        NodeDto input = new NodeDto();
        input.setIdentifier("NODE1");

        Node existing = new Node();

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        when(nodeRepository.save(existing))
                .thenReturn(existing);

        NodeDto result = nodeService.update(input);

        assertEquals("NODE1", result.getIdentifier());
    }

    @Test
    void update_failure_notFound() {

        NodeDto input = new NodeDto();
        input.setIdentifier("NODE1");

        when(nodeRepository.findByIdentifier("NODE1"))
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

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node);

        when(nodeRepository.save(node))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        NodeDto result =
                nodeService.changeToggleStatus("NODE1", true);

        Assertions.assertTrue(node.isStatus());
        assertNotNull(result);
    }

    @Test
    void getNodesForRoles_success() {

        UserDetails principal =
                new org.springframework.security.core.userdetails.User(
                        "john", "password", List.of()
                );

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContext context = Mockito.mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        User user = new User();
        user.setUsername("john");
        user.setRoles(List.of("ADMIN"));

        when(userRepository.findByUsername("john"))
                .thenReturn(user);

        Node node1 = new Node();
        node1.setIdentifier("NODE1");
        node1.setRoles(List.of("ADMIN"));

        Node node2 = new Node();
        node2.setIdentifier("NODE2");
        node2.setRoles(List.of("USER"));

        when(nodeRepository.findAll())
                .thenReturn(List.of(node1, node2));

        NodeDto dto = new NodeDto();

        when(nodeRepository.findByIdentifier("NODE1"))
                .thenReturn(node1);

        when(modelMapper.map(node1, NodeDto.class))
                .thenReturn(dto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertEquals(1, result.size());
    }

    @Test
    void testFindActiveStatus() {
        // 1. Arrange: Create Node data
        Node active = new Node();
        active.setStatus(true);

        Node inactive = new Node();
        inactive.setStatus(false);

        // Stub the repository to return both active and inactive nodes
        when(nodeRepository.findAll())
                .thenReturn(List.of(active, inactive));

        // Prepare the expected DTO output list
        NodeDto dto = new NodeDto();
        List<NodeDto> expectedDtoList = List.of(dto);

        // FIX: Stub modelMapper to expect the precisely filtered list and ANY generic Type
        when(modelMapper.map(
                Mockito.eq(List.of(active)),
                Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(expectedDtoList);

        // 2. Act: Call your service layer method
        List<NodeDto> result = nodeService.findActiveStatus();

        // 3. Assert: Verify the behavior
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}