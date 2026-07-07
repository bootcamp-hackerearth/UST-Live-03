package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
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

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(null);

        Node node = new Node();
        node.setIdentifier("Admin");

        Mockito.when(modelMapper.map(dto, Node.class)).thenReturn(node);

        Mockito.when(nodeRepository.save(node)).thenReturn(node);

        NodeDto result = nodeService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Admin", result.getIdentifier());

        Mockito.verify(nodeRepository).save(node);
    }

    @Test
    void saveShouldSetDefaultValuesTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(null);

        Mockito.when(modelMapper.map(dto, Node.class)).thenReturn(node);

        nodeService.save(dto);

        Assertions.assertFalse(node.getDeleted());
        Assertions.assertTrue(node.getStatus());

        Mockito.verify(nodeRepository).save(node);
    }

    @Test
    void saveTestFailureExistingNode() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Node existing = new Node();
        existing.setDeleted(false);

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(existing);

        NodeDto result = nodeService.save(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertEquals("Node with identifier - Admin already exists", result.getMessage());

        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveTestFailureSoftDeletedNode() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Node existing = new Node();
        existing.setDeleted(true);

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(existing);

        NodeDto result = nodeService.save(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertEquals("Node with identifier - Admin has been soft deleted. Restore it by changing status.", result.getMessage());

        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {

        Node node = new Node();
        node.setIdentifier("Admin");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);

        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        NodeDto result = nodeService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", result.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> nodeService.findByIdentifier("Admin"));

        Assertions.assertEquals("Node with identifier 'Admin' not found", exception.getMessage());
    }

    @Test
    void updateTestSuccess() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Node existing = new Node();
        existing.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(existing);

        Mockito.when(nodeRepository.save(existing)).thenReturn(existing);

        NodeDto result = nodeService.update(dto);

        Assertions.assertNotNull(result);

        Mockito.verify(modelMapper).map(dto, existing);

        Mockito.verify(nodeRepository).save(existing);
    }

    @Test
    void updateShouldMapAndSaveTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Node existing = new Node();
        existing.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(existing);

        nodeService.update(dto);

        Mockito.verify(modelMapper).map(dto, existing);

        Mockito.verify(nodeRepository).save(existing);
    }

    @Test
    void updateTestFailure() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(null);

        NodeDto result = nodeService.update(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertEquals("Node with identifier - Admin not found", result.getMessage());
    }

    @Test
    void deleteTest() {

        Node node = new Node();
        node.setIdentifier("Admin");
        node.setDeleted(false);
        node.setStatus(true);

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);

        Mockito.when(nodeRepository.save(node)).thenReturn(node);

        boolean result = nodeService.delete("Admin");

        Assertions.assertTrue(result);
        Assertions.assertTrue(node.getDeleted());
        Assertions.assertFalse(node.getStatus());

        Mockito.verify(nodeRepository).save(node);
    }

    @Test
    void deleteTestFailure() {

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(null);

        boolean result = nodeService.delete("Admin");

        Assertions.assertFalse(result);

        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void toggleStatusTest() {

        Node node = new Node();
        node.setIdentifier("Admin");
        node.setStatus(true);

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);

        Mockito.when(nodeRepository.save(node)).thenReturn(node);

        nodeService.toggleStatus("Admin");

        Assertions.assertFalse(node.getStatus());

        Mockito.verify(nodeRepository).save(node);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Node node = new Node();
        node.setIdentifier("Admin");
        node.setStatus(false);

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);

        Mockito.when(nodeRepository.save(node)).thenReturn(node);

        nodeService.toggleStatus("Admin");

        Assertions.assertTrue(node.getStatus());

        Mockito.verify(nodeRepository).save(node);
    }

    @Test
    void toggleStatusTestFailure() {

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(null);

        nodeService.toggleStatus("Admin");

        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveNodesTest() {

        Node node = new Node();
        node.setIdentifier("Admin");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        List<Node> nodes = List.of(node);

        Type type = new TypeToken<List<NodeDto>>() {
                }.getType();

        Mockito.when(nodeRepository.findByStatusTrue()).thenReturn(nodes);

        Mockito.when(modelMapper.map(nodes, type)).thenReturn(List.of(dto));

        List<NodeDto> result = nodeService.findActiveNodes();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Admin", result.get(0).getIdentifier());
    }

    @Test
    void findActiveNodesEmptyTest() {

        Type type = new TypeToken<List<NodeDto>>() {
                }.getType();

        Mockito.when(nodeRepository.findByStatusTrue()).thenReturn(List.of());

        Mockito.when(modelMapper.map(List.of(), type)).thenReturn(List.of());

        List<NodeDto> result = nodeService.findActiveNodes();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findAllPaginationTest() {

        Node node = new Node();
        node.setIdentifier("Admin");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Node> page = new PageImpl<>(List.of(node), pageable, 1);

        Mockito.when(nodeRepository.findByDeletedFalse(pageable)).thenReturn(page);

        Type type = new TypeToken<List<NodeDto>>() {
                }.getType();

        Mockito.when(modelMapper.map(page.getContent(), type)).thenReturn(List.of(dto));

        PageDto<NodeDto> result = nodeService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Node> spec = Mockito.mock(Specification.class);

        Node node = new Node();
        node.setIdentifier("Admin");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Page<Node> page = new PageImpl<>(List.of(node), pageable, 1);

        Mockito.when(nodeRepository.findAll(spec, pageable)).thenReturn(page);

        Type type = new TypeToken<List<NodeDto>>() {
                }.getType();

        Mockito.when(modelMapper.map(page.getContent(), type)).thenReturn(List.of(dto));

        PageDto<NodeDto> result = nodeService.findAll(spec, pageable, "admin");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals("Admin", result.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals("admin", result.getKeyword());
        Assertions.assertEquals(1, result.getTotalRecords());
    }

    @Test
    void getNodesForRoles_NoAuth() {

        SecurityContext context = Mockito.mock(SecurityContext.class);

        Mockito.when(context.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(context);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRoles_withNonUserPrincipal() {

        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(authentication.getPrincipal()).thenReturn("anonymousUser");

        SecurityContext context = Mockito.mock(SecurityContext.class);

        Mockito.when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRoles_withValidUser() {

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("admin", "password", List.of());

        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContext context = Mockito.mock(SecurityContext.class);

        Mockito.when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        com.ust.pos.model.User currentUser = new com.ust.pos.model.User();

        currentUser.setUsername("admin");
        currentUser.setRoles(List.of("ROLE_ADMIN"));

        Mockito.when(userRepository.findByUsername("admin")).thenReturn(currentUser);

        Node node = new Node();
        node.setIdentifier("Dashboard");
        node.setRoles(List.of("ROLE_ADMIN"));

        Mockito.when(nodeRepository.findAll()).thenReturn(List.of(node));

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Dashboard");

        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Dashboard", result.get(0).getIdentifier());
    }

    @Test
    void getNodesForRoles_withNoMatchingRoles() {

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("user", "password", List.of());

        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContext context = Mockito.mock(SecurityContext.class);

        Mockito.when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        com.ust.pos.model.User currentUser = new com.ust.pos.model.User();

        currentUser.setUsername("user");
        currentUser.setRoles(List.of("ROLE_CASHIER"));

        Mockito.when(userRepository.findByUsername("user")).thenReturn(currentUser);

        Node node = new Node();
        node.setIdentifier("Dashboard");
        node.setRoles(List.of("ROLE_ADMIN"));

        Mockito.when(nodeRepository.findAll()).thenReturn(List.of(node));

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRoles_DuplicateMatch_ReturnSingleNode() {

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("admin", "password", List.of());

        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContext context = Mockito.mock(SecurityContext.class);

        Mockito.when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        com.ust.pos.model.User currentUser = new com.ust.pos.model.User();

        currentUser.setUsername("admin");
        currentUser.setRoles(List.of("ROLE_ADMIN", "ROLE_MANAGER"));

        Mockito.when(userRepository.findByUsername("admin")).thenReturn(currentUser);

        Node node = new Node();
        node.setIdentifier("Dashboard");
        node.setRoles(List.of("ROLE_ADMIN", "ROLE_MANAGER"));

        Mockito.when(nodeRepository.findAll()).thenReturn(List.of(node));

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Dashboard");

        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Dashboard", result.get(0).getIdentifier());
    }
}