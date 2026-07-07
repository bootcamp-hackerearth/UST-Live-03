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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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

        OrderItem item = new OrderItem();
        OrderItemDto mappedDto = new OrderItemDto();

        Mockito.when(orderItemRepository.findByIdentifier("ORD1_P1"))
                .thenReturn(null)
                .thenReturn(item);

        Mockito.doNothing()
                .when(modelMapper)
                .map(eq(dto), any(OrderItem.class));

        Mockito.when(modelMapper.map(any(OrderItem.class), eq(OrderItemDto.class)))
                .thenReturn(mappedDto);

        OrderItemDto response = orderItemService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ORD1_P1", dto.getIdentifier());

        Mockito.verify(modelMapper)
                .map(eq(dto), any(OrderItem.class));

        Mockito.verify(orderItemRepository)
                .save(any(OrderItem.class));
    }

    @Test
    void saveExistingTest() {
        OrderItemDto dto = new OrderItemDto();
        dto.setOrderIdentifier("ORD1");
        dto.setProduct("P1");

        OrderItem item = new OrderItem();
        OrderItemDto mappedDto = new OrderItemDto();

        Mockito.when(orderItemRepository.findByIdentifier("ORD1_P1"))
                .thenReturn(item);

        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, item);

        Mockito.when(modelMapper.map(item, OrderItemDto.class))
                .thenReturn(mappedDto);

        OrderItemDto response = orderItemService.save(dto);

        Assertions.assertNotNull(response);

        Mockito.verify(modelMapper).map(dto, item);
        Mockito.verify(orderItemRepository).save(item);
    }

    @Test
    void updateTest() {
        OrderItemDto dto = new OrderItemDto();
        dto.setIdentifier("ORD1_P1");

        OrderItem item = new OrderItem();
        OrderItemDto mappedDto = new OrderItemDto();

        Mockito.when(orderItemRepository.findByIdentifier("ORD1_P1"))
                .thenReturn(item);

        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, item);

        Mockito.when(modelMapper.map(item, OrderItemDto.class))
                .thenReturn(mappedDto);

        OrderItemDto response = orderItemService.update(dto);

        Assertions.assertNotNull(response);

        Mockito.verify(modelMapper).map(dto, item);
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
        Assertions.assertEquals(
                "OrderItem not found - ORD1_P1",
                response.getMessage()
        );

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

        OrderItemDto response =
                orderItemService.findByIdentifier("ORD1_P1");

        Assertions.assertNotNull(response);
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(orderItemRepository.findByIdentifier("ORD1_P1"))
                .thenReturn(null);

        OrderItemDto response =
                orderItemService.findByIdentifier("ORD1_P1");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "OrderItem not found",
                response.getMessage()
        );
    }

    @Test
    void findAllTest() {
        List<OrderItem> items = List.of(new OrderItem());
        List<OrderItemDto> dtos = List.of(new OrderItemDto());

        Type listType = new TypeToken<List<OrderItemDto>>() {
        }.getType();

        Mockito.when(orderItemRepository.findAll())
                .thenReturn(items);

        Mockito.when(modelMapper.map(items, listType))
                .thenReturn(dtos);

        List<OrderItemDto> response = orderItemService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllPaginationTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<OrderItem> items = List.of(new OrderItem());
        Page<OrderItem> page = new PageImpl<>(items);

        Type listType = new TypeToken<List<OrderItemDto>>() {
        }.getType();

        Mockito.when(orderItemRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(items, listType))
                .thenReturn(List.of(new OrderItemDto()));

        List<OrderItemDto> response =
                orderItemService.findAll(pageable);

        Assertions.assertEquals(1, response.size());

        Mockito.verify(orderItemRepository)
                .findAll(pageable);
    }

    @Test
    void findByOrderIdentifierTest() {
        List<OrderItem> items = List.of(new OrderItem());
        List<OrderItemDto> dtos = List.of(new OrderItemDto());

        Type listType = new TypeToken<List<OrderItemDto>>() {
        }.getType();

        Mockito.when(orderItemRepository.findByOrderIdentifier("ORD1"))
                .thenReturn(items);

        Mockito.when(modelMapper.map(items, listType))
                .thenReturn(dtos);

        List<OrderItemDto> response =
                orderItemService.findByOrderIdentifier("ORD1");

        Assertions.assertEquals(1, response.size());
    }
}