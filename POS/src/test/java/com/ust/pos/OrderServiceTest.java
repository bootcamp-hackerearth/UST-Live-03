package com.ust.pos;

import com.ust.pos.dto.OrdersDto;
import com.ust.pos.model.Orders;
import com.ust.pos.model.OrdersRepository;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllSuccessTest() {
        Orders order = new Orders();
        List<Orders> ordersList = List.of(order);

        OrdersDto ordersDto = new OrdersDto();
        List<OrdersDto> ordersDtoList = List.of(ordersDto);

        Mockito.when(ordersRepository.findAll()).thenReturn(ordersList);
        Mockito.when(modelMapper.map(Mockito.eq(ordersList), Mockito.any(Type.class))).thenReturn(ordersDtoList);

        List<OrdersDto> result = orderService.findAll();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        verify(ordersRepository).findAll();
    }

    @Test
    void findAllEmptyListTest() {
        Mockito.when(ordersRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(modelMapper.map(Mockito.eq(Collections.emptyList()), Mockito.any(Type.class))).thenReturn(Collections.emptyList());

        List<OrdersDto> result = orderService.findAll();

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Orders order = new Orders();
        OrdersDto ordersDto = new OrdersDto();
        ordersDto.setIdentifier("ORD-1001");

        Mockito.when(ordersRepository.findByIdentifier("ORD-1001")).thenReturn(order);
        Mockito.when(modelMapper.map(order, OrdersDto.class)).thenReturn(ordersDto);

        OrdersDto result = orderService.findByIdentifier("ORD-1001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("ORD-1001", result.getIdentifier());
        verify(ordersRepository).findByIdentifier("ORD-1001");
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(ordersRepository.findByIdentifier("ORD-UNKNOWN")).thenReturn(null);
        Mockito.when(modelMapper.map(null, OrdersDto.class)).thenReturn(null);

        OrdersDto result = orderService.findByIdentifier("ORD-UNKNOWN");

        Assertions.assertNull(result);
    }
}
