package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.Collections;
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

    private SecurityContext mockSecurityContext;

    @BeforeEach
    void setUp() {
        mockSecurityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(mockSecurityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getNodesForRolesTest() {
        Authentication authentication = Mockito.mock(Authentication.class);
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("testuser", "password", Collections.emptyList());

        Mockito.when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        User currentUser = new User();
        currentUser.setRoles(List.of("ROLE_ADMIN"));
        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(currentUser);

        Node node = new Node();
        node.setIdentifier("NODE01");
        node.setRoles(List.of("ROLE_ADMIN"));
        Mockito.when(nodeRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(List.of(node));
        Mockito.when(nodeRepository.findByIdentifier("NODE01")).thenReturn(node);

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE01");
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        List<NodeDto> response = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("NODE01", response.get(0).getIdentifier());
    }

    @Test
    void getNodesForRolesTestNoAuth() {
        Mockito.when(mockSecurityContext.getAuthentication()).thenReturn(null);

        List<NodeDto> response = nodeService.getNodesForRoles();

        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void findByIdentifierTest() {
        Node node = new Node();
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE01");

        Mockito.when(nodeRepository.findByIdentifier("NODE01")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto response = nodeService.findByIdentifier("NODE01");

        Assertions.assertEquals("NODE01", response.getIdentifier());
    }

    @Test
    void findByIdentifierTestFailure() {
        Mockito.when(nodeRepository.findByIdentifier("NODE01")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            nodeService.findByIdentifier("NODE01");
        });
    }

    @Test
    void saveTestSuccess() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE01");

        Mockito.when(nodeRepository.findByIdentifier("NODE01")).thenReturn(null);
        Node node = new Node();
        Mockito.when(modelMapper.map(nodeDto, Node.class)).thenReturn(node);
        Mockito.when(nodeRepository.save(node)).thenReturn(node);

        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertEquals("NODE01", response.getIdentifier());
    }

    @Test
    void saveTestFailureAlreadyExists() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE01");

        Node existingNode = new Node();
        existingNode.setIdentifier("NODE01");
        existingNode.setDeleted(false);

        Mockito.when(nodeRepository.findByIdentifier("NODE01")).thenReturn(existingNode);

        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Node with identifier - NODE01 already exists", response.getMessage());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE01");

        Node existingNode = new Node();
        existingNode.setIdentifier("NODE01");
        existingNode.setDeleted(true);

        Mockito.when(nodeRepository.findByIdentifier("NODE01")).thenReturn(existingNode);

        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Node with identifier NODE01 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void updateTestSuccess() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE01");

        Node existingNode = new Node();
        existingNode.setIdentifier("NODE01");

        Mockito.when(nodeRepository.findByIdentifier("NODE01")).thenReturn(existingNode);
        Mockito.when(nodeRepository.save(existingNode)).thenReturn(existingNode);

        NodeDto response = nodeService.update(nodeDto);

        Assertions.assertEquals("NODE01", response.getIdentifier());
    }

    @Test
    void updateTestFailure() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE01");

        Mockito.when(nodeRepository.findByIdentifier("NODE01")).thenReturn(null);

        NodeDto response = nodeService.update(nodeDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Node with identifier - NODE01not found", response.getMessage());
    }

    @Test
    void deleteTestSuccess() {
        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("NODE01")).thenReturn(node);
        Mockito.when(nodeRepository.save(node)).thenReturn(node);

        boolean response = nodeService.delete("NODE01");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(nodeRepository.findByIdentifier("NODE01")).thenReturn(null);

        boolean response = nodeService.delete("NODE01");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Node node = new Node();
        List<Node> nodesList = List.of(node);
        Page<Node> nodePage = new PageImpl<>(nodesList, pageable, nodesList.size());

        NodeDto nodeDto = new NodeDto();
        List<NodeDto> nodeDtos = List.of(nodeDto);

        Mockito.when(nodeRepository.findByDeletedFalse(pageable)).thenReturn(nodePage);
        Mockito.when(modelMapper.map(Mockito.eq(nodesList), Mockito.any(Type.class))).thenReturn(nodeDtos);

        WsDto<NodeDto> response = nodeService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findIfTrueTest() {
        Node node = new Node();
        List<Node> nodesList = List.of(node);
        NodeDto nodeDto = new NodeDto();
        List<NodeDto> nodeDtos = List.of(nodeDto);

        Mockito.when(nodeRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(nodesList);
        Mockito.when(modelMapper.map(Mockito.eq(nodesList), Mockito.any(Type.class))).thenReturn(nodeDtos);

        List<NodeDto> response = nodeService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {
        Node node = new Node();
        node.setStatus(false);
        NodeDto nodeDto = new NodeDto();
        nodeDto.setStatus(true);

        Mockito.when(nodeRepository.findByIdentifier("NODE01")).thenReturn(node);
        Mockito.when(nodeRepository.save(node)).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto response = nodeService.toggleStatus("NODE01");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<Node> specification = Mockito.mock(Specification.class);
        Node node = new Node();
        List<Node> nodesList = List.of(node);
        Page<Node> page = new PageImpl<>(nodesList, pageable, nodesList.size());

        NodeDto nodeDto = new NodeDto();
        List<NodeDto> nodeDtos = List.of(nodeDto);

        Mockito.when(nodeRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(nodesList), Mockito.any(Type.class))).thenReturn(nodeDtos);

        WsDto<NodeDto> response = nodeService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}