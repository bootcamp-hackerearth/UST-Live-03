package com.ust.pos;

import com.ust.pos.dto.OrderItemDto;
import com.ust.pos.model.OrderItem;
import com.ust.pos.model.OrderItemRepository;
import com.ust.pos.orderitem.service.impl.OrderItemServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private OrderItem orderItem;
    private OrderItemDto orderItemDto;

    @BeforeEach
    void setUp() {

        orderItem = new OrderItem();
        orderItem.setIdentifier("ORD001_PROD001");

        orderItemDto = new OrderItemDto();
        orderItemDto.setIdentifier("ORD001_PROD001");
        orderItemDto.setOrderIdentifier("ORD001");
        orderItemDto.setProduct("PROD001");
    }

    @Test
    void saveNewOrderItemTest() {

        Mockito.when(
                        orderItemRepository.findByIdentifierAndDeletedFalse(
                                "ORD001_PROD001"))
                .thenReturn(null);

        Mockito.doNothing().when(modelMapper)
                .map(Mockito.any(OrderItemDto.class),
                        Mockito.any(OrderItem.class));

        Mockito.when(
                        orderItemRepository.findByIdentifierAndDeletedFalse(
                                "ORD001_PROD001"))
                .thenReturn(orderItem);

        Mockito.when(
                        modelMapper.map(orderItem, OrderItemDto.class))
                .thenReturn(orderItemDto);

        OrderItemDto response =
                orderItemService.save(orderItemDto);

        Assertions.assertNotNull(response);

        Mockito.verify(orderItemRepository)
                .save(Mockito.any(OrderItem.class));
    }

    @Test
    void saveExistingOrderItemTest() {

        Mockito.when(
                        orderItemRepository.findByIdentifierAndDeletedFalse(
                                "ORD001_PROD001"))
                .thenReturn(orderItem);

        Mockito.doNothing().when(modelMapper)
                .map(orderItemDto, orderItem);

        Mockito.when(
                        modelMapper.map(orderItem, OrderItemDto.class))
                .thenReturn(orderItemDto);

        OrderItemDto response =
                orderItemService.save(orderItemDto);

        Assertions.assertNotNull(response);

        Mockito.verify(orderItemRepository)
                .save(orderItem);
    }

    @Test
    void updateSuccessTest() {

        Mockito.when(
                        orderItemRepository.findByIdentifierAndDeletedFalse(
                                "ORD001_PROD001"))
                .thenReturn(orderItem);

        Mockito.doNothing().when(modelMapper)
                .map(orderItemDto, orderItem);

        Mockito.when(
                        modelMapper.map(orderItem, OrderItemDto.class))
                .thenReturn(orderItemDto);

        OrderItemDto response =
                orderItemService.update(orderItemDto);

        Assertions.assertNotNull(response);

        Mockito.verify(orderItemRepository)
                .save(orderItem);
    }

    @Test
    void updateOrderItemNotFoundTest() {

        Mockito.when(
                        orderItemRepository.findByIdentifierAndDeletedFalse(
                                "ORD001_PROD001"))
                .thenReturn(null);

        OrderItemDto response =
                orderItemService.update(orderItemDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage().contains("OrderItem not found"));
    }

    @Test
    void deleteSuccessTest() {

        Mockito.when(
                        orderItemRepository.findByIdentifierAndDeletedFalse(
                                "ORD001_PROD001"))
                .thenReturn(orderItem);

        orderItemService.delete("ORD001_PROD001");

        Assertions.assertTrue(orderItem.isDeleted());

        Mockito.verify(orderItemRepository)
                .save(orderItem);
    }

    @Test
    void deleteOrderItemNotFoundTest() {

        Mockito.when(
                        orderItemRepository.findByIdentifierAndDeletedFalse(
                                "ORD001_PROD001"))
                .thenReturn(null);

        orderItemService.delete("ORD001_PROD001");

        Mockito.verify(orderItemRepository,
                        Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierSuccessTest() {

        Mockito.when(
                        orderItemRepository.findByIdentifierAndDeletedFalse(
                                "ORD001_PROD001"))
                .thenReturn(orderItem);

        Mockito.when(
                        modelMapper.map(orderItem, OrderItemDto.class))
                .thenReturn(orderItemDto);

        OrderItemDto response =
                orderItemService.findByIdentifier(
                        "ORD001_PROD001");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "ORD001_PROD001",
                response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(
                        orderItemRepository.findByIdentifierAndDeletedFalse(
                                "ORD001_PROD001"))
                .thenReturn(null);

        OrderItemDto response =
                orderItemService.findByIdentifier(
                        "ORD001_PROD001");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "OrderItem not found",
                response.getMessage());
    }

    @Test
    void findAllTest() {

        List<OrderItem> items =
                List.of(orderItem);

        List<OrderItemDto> dtos =
                List.of(orderItemDto);

        Type listType =
                new TypeToken<List<OrderItemDto>>() {
                }.getType();

        Mockito.when(
                        orderItemRepository.findByDeletedFalse())
                .thenReturn(items);

        Mockito.when(
                        modelMapper.map(items, listType))
                .thenReturn(dtos);

        List<OrderItemDto> response =
                orderItemService.findAll();

        Assertions.assertEquals(1,
                response.size());
    }

    @Test
    void findAllPageableTest() {

        Page<OrderItem> page =
                new PageImpl<>(List.of(orderItem));

        List<OrderItemDto> dtos =
                List.of(orderItemDto);

        Type listType =
                new TypeToken<List<OrderItemDto>>() {
                }.getType();

        Mockito.when(
                        orderItemRepository.findAll(
                                Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Mockito.when(
                        modelMapper.map(
                                page.getContent(),
                                listType))
                .thenReturn(dtos);

        List<OrderItemDto> response =
                orderItemService.findAll(
                        PageRequest.of(0, 10));

        Assertions.assertEquals(1,
                response.size());
    }

    @Test
    void findByOrderIdentifierTest() {

        List<OrderItem> items =
                List.of(orderItem);

        List<OrderItemDto> dtos =
                List.of(orderItemDto);

        Type listType =
                new TypeToken<List<OrderItemDto>>() {
                }.getType();

        Mockito.when(
                        orderItemRepository
                                .findByOrderIdentifierAndDeletedFalse(
                                        "ORD001"))
                .thenReturn(items);

        Mockito.when(
                        modelMapper.map(items, listType))
                .thenReturn(dtos);

        List<OrderItemDto> response =
                orderItemService.findByOrderIdentifier(
                        "ORD001");

        Assertions.assertEquals(1,
                response.size());
    }
}