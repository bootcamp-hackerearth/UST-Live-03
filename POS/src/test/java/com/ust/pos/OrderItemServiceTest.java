package com.ust.pos;

import com.ust.pos.dto.OrderItemDto;
import com.ust.pos.model.OrderItem;
import com.ust.pos.model.OrderItemRepository;
import com.ust.pos.orderitem.service.impl.OrderItemServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        OrderItemDto dto = new OrderItemDto();
        dto.setOrderIdentifier("ORD1");
        dto.setProduct("P1");

        String identifier = "ORD1_P1";

        OrderItem savedItem = new OrderItem();
        savedItem.setIdentifier(identifier);

        OrderItemDto responseDto = new OrderItemDto();

        Mockito.when(orderItemRepository.findByIdentifier(identifier))
                .thenReturn(null)
                .thenReturn(savedItem);

        Mockito.doAnswer(invocation -> {
                    OrderItem target = invocation.getArgument(1);
                    target.setIdentifier(identifier);
                    return null;
                }).when(modelMapper)
                .map(Mockito.eq(dto), Mockito.any(OrderItem.class));

        Mockito.when(modelMapper.map(savedItem, OrderItemDto.class))
                .thenReturn(responseDto);

        OrderItemDto response = orderItemService.save(dto);

        Assertions.assertEquals(identifier, dto.getIdentifier());
        Assertions.assertNotNull(response);

        Mockito.verify(modelMapper)
                .map(Mockito.eq(dto), Mockito.any(OrderItem.class));

        Mockito.verify(orderItemRepository)
                .save(Mockito.any(OrderItem.class));
    }

    @Test
    void saveExistingTest() {
        OrderItemDto dto = new OrderItemDto();
        dto.setOrderIdentifier("ORD1");
        dto.setProduct("P1");

        String identifier = "ORD1_P1";

        OrderItem existing = new OrderItem();
        existing.setIdentifier(identifier);

        Mockito.when(orderItemRepository.findByIdentifier(identifier))
                .thenReturn(existing)
                .thenReturn(existing);

        Mockito.doNothing().when(modelMapper)
                .map(Mockito.eq(dto), Mockito.any(OrderItem.class));

        Mockito.when(modelMapper.map(existing, OrderItemDto.class))
                .thenReturn(new OrderItemDto());

        OrderItemDto response = orderItemService.save(dto);

        Assertions.assertNotNull(response);

        Mockito.verify(modelMapper)
                .map(Mockito.eq(dto), Mockito.any(OrderItem.class));

        Mockito.verify(orderItemRepository).save(existing);
    }

    @Test
    void updateTest() {
        OrderItemDto dto = new OrderItemDto();
        dto.setIdentifier("ORD1_P1");

        OrderItem item = new OrderItem();
        item.setIdentifier("ORD1_P1");

        OrderItemDto responseDto = new OrderItemDto();

        Mockito.when(orderItemRepository.findByIdentifier("ORD1_P1"))
                .thenReturn(item);

        Mockito.doNothing().when(modelMapper)
                .map(Mockito.eq(dto), Mockito.any(OrderItem.class));

        Mockito.when(modelMapper.map(item, OrderItemDto.class))
                .thenReturn(responseDto);

        OrderItemDto response = orderItemService.update(dto);

        Assertions.assertNotNull(response);

        Mockito.verify(modelMapper)
                .map(Mockito.eq(dto), Mockito.any(OrderItem.class));

        Mockito.verify(orderItemRepository).save(item);
    }

    @Test
    void updateFailureTest() {
        OrderItemDto dto = new OrderItemDto();
        dto.setIdentifier("ORD1_P1");

        Mockito.when(orderItemRepository.findByIdentifier("ORD1_P1"))
                .thenReturn(null);

        OrderItemDto response = orderItemService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(orderItemRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteTest() {
        orderItemService.delete("ORD1_P1");

        Mockito.verify(orderItemRepository)
                .deleteByIdentifier("ORD1_P1");
    }

    @Test
    void findByIdentifierTest() {
        OrderItem item = new OrderItem();
        OrderItemDto dto = new OrderItemDto();

        Mockito.when(orderItemRepository.findByIdentifier("ORD1_P1"))
                .thenReturn(item);
        Mockito.when(modelMapper.map(item, OrderItemDto.class))
                .thenReturn(dto);

        OrderItemDto response = orderItemService.findByIdentifier("ORD1_P1");

        Assertions.assertNotNull(response);
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(orderItemRepository.findByIdentifier("ORD1_P1"))
                .thenReturn(null);

        OrderItemDto response = orderItemService.findByIdentifier("ORD1_P1");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("OrderItem not found", response.getMessage());
    }

    @Test
    void findAllTest() {
        List<OrderItem> items = List.of(new OrderItem());
        List<OrderItemDto> dtos = List.of(new OrderItemDto());

        Type type = new TypeToken<List<OrderItemDto>>() {
        }.getType();

        Mockito.when(orderItemRepository.findAll())
                .thenReturn(items);
        Mockito.when(modelMapper.map(items, type))
                .thenReturn(dtos);

        List<OrderItemDto> response = orderItemService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllPaginationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderItem> page = new PageImpl<>(List.of(new OrderItem()));

        Type type = new TypeToken<List<OrderItemDto>>() {
        }.getType();

        Mockito.when(orderItemRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(page.getContent(), type))
                .thenReturn(List.of(new OrderItemDto()));

        List<OrderItemDto> response = orderItemService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Mockito.verify(orderItemRepository).findAll(pageable);
    }

    @Test
    void findByOrderIdentifierTest() {
        List<OrderItem> items = List.of(new OrderItem());
        List<OrderItemDto> dtos = List.of(new OrderItemDto());

        Type type = new TypeToken<List<OrderItemDto>>() {
        }.getType();

        Mockito.when(orderItemRepository.findByOrderIdentifier("ORD1"))
                .thenReturn(items);
        Mockito.when(modelMapper.map(items, type))
                .thenReturn(dtos);

        List<OrderItemDto> response = orderItemService.findByOrderIdentifier("ORD1");

        Assertions.assertEquals(1, response.size());
    }
}