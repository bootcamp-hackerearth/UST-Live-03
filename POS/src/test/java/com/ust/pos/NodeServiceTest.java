package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

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

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void save_success() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Node.class)).thenReturn(new Node());

        NodeDto response = nodeService.save(dto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void save_duplicate() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(new Node());

        NodeDto response = nodeService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifier() {
        Node node = new Node();
        NodeDto dto = new NodeDto();

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        Assertions.assertNotNull(nodeService.findByIdentifier("Admin"));
    }

    @Test
    void update_success() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Node node = new Node();
        node.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);

        NodeDto response = nodeService.update(dto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void update_notFound() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(null);

        NodeDto response = nodeService.update(dto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void delete() {
        Mockito.doNothing().when(nodeRepository).deleteByIdentifier("Admin");

        nodeService.delete("Admin");

        Mockito.verify(nodeRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAll() {
        Node node = new Node();
        NodeDto dto = new NodeDto();

        Pageable pageable = PageRequest.of(0, 50);
        Page<Node> nodePage = new PageImpl<>(List.of(node), pageable, 1);

        Mockito.when(nodeRepository.findAll(pageable)).thenReturn(nodePage);

        Mockito.when(modelMapper.map(Mockito.eq(List.of(node)), Mockito.any(Type.class))).thenReturn(List.of(dto));

        List<NodeDto> result = nodeService.findAll(pageable).getDtoList();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(dto, result.get(0));
    }

    @Test
    void toggle_trueToFalse() {
        Node node = new Node();
        node.setStatus(true);

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(new NodeDto());

        nodeService.toggleStatus("Admin");

        Assertions.assertFalse(node.isStatus());
    }

    @Test
    void toggle_falseToTrue() {
        Node node = new Node();
        node.setStatus(false);

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(new NodeDto());

        nodeService.toggleStatus("Admin");

        Assertions.assertTrue(node.isStatus());
    }

    @Test
    void findIfTrue() {
        Node node = new Node();
        NodeDto dto = new NodeDto();

        Mockito.when(nodeRepository.findByStatusIsTrue()).thenReturn(List.of(node));
        Mockito.when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(dto));

        Assertions.assertEquals(1, nodeService.findIfTrue().size());
    }

    @Test
    void getNodesForRoles_success() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "john",
                        "pwd",
                        List.of(() -> "ROLE_ADMIN")
                );

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        com.ust.pos.model.User dbUser = new com.ust.pos.model.User();
        dbUser.setUsername("john");
        dbUser.setRoles(List.of("ROLE_ADMIN"));

        Node node = new Node();
        node.setIdentifier("NODE_1");
        node.setRoles(List.of("ROLE_ADMIN"));

        NodeDto nodeDto = new NodeDto();

        Mockito.when(userRepository.findByUsername("john")).thenReturn(dbUser);
        Mockito.when(nodeRepository.findByStatusIsTrue()).thenReturn(List.of(node));
        Mockito.when(nodeRepository.findByIdentifier("NODE_1")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getNodesForRoles_noAuthentication() {
        SecurityContextHolder.clearContext();

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }
}