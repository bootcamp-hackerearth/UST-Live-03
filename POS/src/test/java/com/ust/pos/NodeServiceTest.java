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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
    void save_success() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Node.class))
                .thenReturn(node);

        when(nodeRepository.save(node))
                .thenReturn(node);

        NodeDto response = nodeService.save(dto);

        Assertions.assertNotNull(response);

        verify(nodeRepository).save(node);
    }

    @Test
    void save_failure_duplicate() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(new Node());

        NodeDto response = nodeService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Node with identifier - N1 already exists",
                response.getMessage()
        );

        verify(nodeRepository, never()).save(any());
    }

    @Test
    void find_success() {

        Node node = new Node();

        NodeDto dto = new NodeDto();

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(dto);

        NodeDto response =
                nodeService.findByIdentifier("N1");

        Assertions.assertNotNull(response);
    }

    @Test
    void find_null() {

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(null);

        NodeDto response =
                nodeService.findByIdentifier("N1");

        Assertions.assertNull(response);
    }

    @Test
    void update_success() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node existing = new Node();

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(existing);

        doNothing().when(modelMapper)
                .map(any(NodeDto.class), any(Node.class));

        when(nodeRepository.save(existing))
                .thenReturn(existing);

        NodeDto response = nodeService.update(dto);

        Assertions.assertNotNull(response);

        verify(nodeRepository).save(existing);
    }

    @Test
    void update_failure() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(null);

        NodeDto response = nodeService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Node with identifier - N1 not found",
                response.getMessage()
        );
    }

    @Test
    void delete_test() {

        nodeService.delete("N1");

        verify(nodeRepository)
                .deleteByIdentifier("N1");
    }

    @Test
    void findAll_success() {

        List<Node> nodes = List.of(new Node());
        Page<Node> page = new PageImpl<>(nodes);

        List<NodeDto> mappedList = List.of(new NodeDto());

        when(nodeRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(
                ArgumentMatchers.eq(nodes),
                ArgumentMatchers.<Type>any()))
                .thenReturn(mappedList);

        WsDto<NodeDto> result =
                nodeService.findAll(PageRequest.of(0, 5));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
    }

    @Test
    void getNodesForRoles_empty() {

        SecurityContextHolder.clearContext();

        List<NodeDto> result =
                nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRoles_success() {

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        "john",
                        "password",
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        springUser,
                        null
                )
        );

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(List.of("ADMIN"));

        NodeDto nodeDto = new NodeDto();

        when(userRepository.findByUsername("john"))
                .thenReturn(user);

        when(nodeRepository.findAll())
                .thenReturn(List.of(node));

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(node);

        when(modelMapper.map(node, NodeDto.class))
                .thenReturn(nodeDto);

        List<NodeDto> result =
                nodeService.getNodesForRoles();

        Assertions.assertEquals(1, result.size());

        SecurityContextHolder.clearContext();
    }
}