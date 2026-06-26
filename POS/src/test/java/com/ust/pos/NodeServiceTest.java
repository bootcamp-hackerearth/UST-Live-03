package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Test
    void saveTestSuccess() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        Node node = new Node();
        node.setIdentifier("Admin");

        Mockito.when(modelMapper.map(dto, Node.class))
                .thenReturn(node);

        Mockito.when(nodeRepository.save(node))
                .thenReturn(node);

        NodeDto result = nodeService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Admin", result.getIdentifier());

        Mockito.verify(nodeRepository)
                .save(node);
    }
    @Test
    void getNodesForRoles_withNonUserPrincipal() {

        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(authentication.getPrincipal())
                .thenReturn("anonymousUser");

        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRoles_withValidUser() {

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "admin", "password", List.of()
                );

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(principal);

        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        com.ust.pos.model.User currentUser = new com.ust.pos.model.User();
        currentUser.setUsername("admin");
        currentUser.setRoles(List.of("ROLE_ADMIN"));

        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(currentUser);

        Node matchingNode = new Node();
        matchingNode.setIdentifier("Dashboard");
        matchingNode.setRoles(List.of("ROLE_ADMIN"));

        Node nonMatchingNode = new Node();
        nonMatchingNode.setIdentifier("Settings");
        nonMatchingNode.setRoles(List.of("ROLE_SUPERADMIN"));

        Mockito.when(nodeRepository.findAll())
                .thenReturn(List.of(matchingNode, nonMatchingNode));

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Dashboard");

        Mockito.when(modelMapper.map(matchingNode, NodeDto.class))
                .thenReturn(nodeDto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Dashboard", result.get(0).getIdentifier());

        Mockito.verify(userRepository).findByUsername("admin");
        Mockito.verify(nodeRepository).findAll();
    }

    @Test
    void getNodesForRoles_withNoMatchingRoles() {

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "user", "password", List.of()
                );

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(principal);

        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        com.ust.pos.model.User currentUser = new com.ust.pos.model.User();
        currentUser.setUsername("user");
        currentUser.setRoles(List.of("ROLE_CASHIER"));

        Mockito.when(userRepository.findByUsername("user"))
                .thenReturn(currentUser);

        Node node = new Node();
        node.setIdentifier("Dashboard");
        node.setRoles(List.of("ROLE_ADMIN"));

        Mockito.when(nodeRepository.findAll())
                .thenReturn(List.of(node));

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());

        Mockito.verify(modelMapper, Mockito.never())
                .map(Mockito.any(), Mockito.eq(NodeDto.class));
    }

    @Test
    void saveTestFailureExistingNode() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Node existing = new Node();
        existing.setIdentifier("Admin");
        existing.setDeleted(false);

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(existing);

        NodeDto result = nodeService.save(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertEquals(
                "Node with identifier - Admin already exists",
                result.getMessage());

        Mockito.verify(nodeRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void saveTestFailureSoftDeletedNode() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Node existing = new Node();
        existing.setIdentifier("Admin");
        existing.setDeleted(true);

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(existing);

        NodeDto result = nodeService.save(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertEquals(
                "Node with identifier - Admin has been soft deleted. Restore it by changing status.",
                result.getMessage());

        Mockito.verify(nodeRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {

        Node node = new Node();
        node.setIdentifier("Admin");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(node);

        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        NodeDto result = nodeService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", result.getIdentifier());
    }

    @Test
    void updateTestSuccess() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Node existing = new Node();
        existing.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(existing);

        Mockito.when(nodeRepository.save(existing))
                .thenReturn(existing);

        NodeDto result = nodeService.update(dto);

        Assertions.assertNotNull(result);

        Mockito.verify(nodeRepository)
                .save(existing);
    }

    @Test
    void updateTestFailure() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        NodeDto result = nodeService.update(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertEquals(
                "Node with identifier - Admin not found",
                result.getMessage());
    }

    @Test
    void deleteTest() {

        Node node = new Node();
        node.setIdentifier("Admin");
        node.setDeleted(false);
        node.setStatus(true);

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(node);

        Mockito.when(nodeRepository.save(node))
                .thenReturn(node);

        boolean result = nodeService.delete("Admin");

        Assertions.assertTrue(result);

        Assertions.assertTrue(node.getDeleted());

        Assertions.assertFalse(node.getStatus());

        Mockito.verify(nodeRepository)
                .save(node);
    }

    @Test
    void deleteTestFailure() {

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        boolean result = nodeService.delete("Admin");

        Assertions.assertFalse(result);

        Mockito.verify(nodeRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void toggleStatusTest() {

        Node node = new Node();
        node.setIdentifier("Admin");
        node.setStatus(true);

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(node);

        Mockito.when(nodeRepository.save(node))
                .thenReturn(node);

        nodeService.toggleStatus("Admin");

        Assertions.assertFalse(node.getStatus());

        Mockito.verify(nodeRepository)
                .save(node);
    }

    @Test
    void toggleStatusTestFailure() {

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        nodeService.toggleStatus("Admin");

        Mockito.verify(nodeRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findActiveNodesTest() {

        Node node = new Node();
        node.setIdentifier("Admin");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        List<Node> list = List.of(node);

        Type type = new TypeToken<List<NodeDto>>() {}.getType();

        Mockito.when(nodeRepository.findByStatusTrue())
                .thenReturn(list);

        Mockito.when(modelMapper.map(list, type))
                .thenReturn(List.of(dto));

        List<NodeDto> result = nodeService.findActiveNodes();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Admin", result.get(0).getIdentifier());
    }

    @Test
    void findAllPaginationTest() {

        Node node = new Node();
        node.setIdentifier("Admin");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Node> page =
                new PageImpl<>(List.of(node), pageable, 1);

        Mockito.when(nodeRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Type type = new TypeToken<List<NodeDto>>() {}.getType();

        Mockito.when(modelMapper.map(page.getContent(), type))
                .thenReturn(List.of(dto));

        PageDto<NodeDto> result =
                nodeService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void getNodesForRoles_NoAuth() {

        SecurityContext context =
                Mockito.mock(SecurityContext.class);

        Mockito.when(context.getAuthentication())
                .thenReturn(null);

        SecurityContextHolder.setContext(context);

        List<NodeDto> result =
                nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }
}