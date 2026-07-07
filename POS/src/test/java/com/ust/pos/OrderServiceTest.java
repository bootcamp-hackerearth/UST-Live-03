package com.ust.pos;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.model.*;
import com.ust.pos.order.service.impl.OrderServiceImpl;
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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void placeOrderTest() {

        Cart cart = new Cart();
        cart.setIdentifier("CUST001");
        cart.setTotalPrice(BigDecimal.valueOf(100));
        cart.setOriginalPrice(BigDecimal.valueOf(120));
        cart.setDiscount(BigDecimal.valueOf(20));

        Mockito.when(cartRepository.findByIdentifier("CUST001"))
                .thenReturn(cart);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setIdentifier("CUST001");

        Mockito.when(orderRepository.save(Mockito.any(Order.class)))
                .thenReturn(savedOrder);

        CartEntry cartEntry = new CartEntry();
        cartEntry.setProduct("P001");
        cartEntry.setQuantity(BigDecimal.ONE);
        cartEntry.setUnitPrice(BigDecimal.valueOf(100));
        cartEntry.setOriginalPrice(BigDecimal.valueOf(120));
        cartEntry.setDiscount(BigDecimal.valueOf(20));
        cartEntry.setTotalPrice(BigDecimal.valueOf(100));

        Mockito.when(cartEntryRepository.findByCartId("CUST001"))
                .thenReturn(List.of(cartEntry));

        OrderDto orderDto = new OrderDto();
        orderDto.setIdentifier("CUST001");

        Mockito.when(modelMapper.map(savedOrder, OrderDto.class))
                .thenReturn(orderDto);

        OrderDto response =
                orderService.placeOrder("CUST001", "CASH");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("CUST001", response.getIdentifier());

        Mockito.verify(orderEntryRepository)
                .save(Mockito.any(OrderEntry.class));

        Mockito.verify(cartEntryRepository)
                .deleteByCartId("CUST001");

        Mockito.verify(cartRepository)
                .deleteByIdentifier("CUST001");
    }

    @Test
    void placeOrderCartNotFoundTest() {

        Mockito.when(cartRepository.findByIdentifier("CUST001"))
                .thenReturn(null);

        OrderDto response =
                orderService.placeOrder("CUST001", "CASH");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Cart not found for customer: CUST001",
                response.getMessage()
        );
    }

    @Test
    void findAllTest() {

        Order order = new Order();
        order.setIdentifier("ORD001");

        OrderDto orderDto = new OrderDto();
        orderDto.setIdentifier("ORD001");

        List<Order> orders = List.of(order);
        List<OrderDto> orderDtos = List.of(orderDto);

        Page<Order> orderPage = new PageImpl<>(orders);

        Mockito.when(orderRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(orderPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(orders),
                        Mockito.any(Type.class)
                )
        ).thenReturn(orderDtos);

        PaginatedResponseDto<OrderDto> response =
                orderService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findByIdentifierTest() {

        Order order = new Order();
        order.setIdentifier("ORD001");

        OrderDto orderDto = new OrderDto();
        orderDto.setIdentifier("ORD001");

        Mockito.when(orderRepository.findByIdentifier("ORD001"))
                .thenReturn(order);

        Mockito.when(modelMapper.map(order, OrderDto.class))
                .thenReturn(orderDto);

        OrderDto response =
                orderService.findByIdentifier("ORD001");

        Assertions.assertEquals("ORD001", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(orderRepository.findByIdentifier("ORD001"))
                .thenReturn(null);

        OrderDto response =
                orderService.findByIdentifier("ORD001");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Order not found: ORD001",
                response.getMessage()
        );
    }

    @Test
    void findByIdTest() {

        Order order = new Order();
        order.setId(1L);
        order.setIdentifier("ORD001");

        Mockito.when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        OrderDto orderDto = new OrderDto();
        orderDto.setIdentifier("ORD001");

        Mockito.when(modelMapper.map(order, OrderDto.class))
                .thenReturn(orderDto);

        OrderEntry orderEntry = new OrderEntry();
        orderEntry.setProduct("P001");

        List<OrderEntry> entries = List.of(orderEntry);

        OrderEntryDto entryDto = new OrderEntryDto();
        entryDto.setProduct("P001");

        List<OrderEntryDto> entryDtos = List.of(entryDto);

        Mockito.when(orderEntryRepository.findByOrderId("1"))
                .thenReturn(entries);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(entries),
                        Mockito.any(Type.class)
                )
        ).thenReturn(entryDtos);

        OrderDto response = orderService.findById(1L);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNotNull(response.getEntries());
        Assertions.assertEquals(1, response.getEntries().size());
    }

    @Test
    void findByIdNotFoundTest() {

        Mockito.when(orderRepository.findById(1L))
                .thenReturn(Optional.empty());

        OrderDto response = orderService.findById(1L);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Order not found: 1",
                response.getMessage()
        );
    }

    @Test
    void findAllSpecificationTest() {

        Order order = new Order();
        order.setIdentifier("ORD001");

        OrderDto orderDto = new OrderDto();
        orderDto.setIdentifier("ORD001");

        List<Order> orders = List.of(order);
        List<OrderDto> orderDtos = List.of(orderDto);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> page = new PageImpl<>(orders, pageable, orders.size());

        Mockito.when(
                orderRepository.findAll(
                        Mockito.<org.springframework.data.jpa.domain.Specification<Order>>any(),
                        Mockito.eq(pageable)
                )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(orders),
                        Mockito.any(Type.class)
                )
        ).thenReturn(orderDtos);

        PaginatedResponseDto<OrderDto> response =
                orderService.findAll(
                        Mockito.mock(org.springframework.data.jpa.domain.Specification.class),
                        pageable
                );

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}