package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.*;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import com.ust.pos.model.OrderRepository;
import com.ust.pos.model.Orders;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import com.ust.pos.orderentry.service.OrderEntryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEntryService orderEntryService;

    @Mock
    private CartService cartService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void placeOrderSuccessTest() {
        PlaceOrderRequestDto request = new PlaceOrderRequestDto();
        request.setCartIdentifier("CART001");
        request.setPaymentMode("CASH");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART001");
        cartDto.setUsername("customer1");

        CartEntryDto entry = new CartEntryDto();
        entry.setProductIdentifier("SKU001");
        entry.setQuantity(2);
        entry.setSellingPrice(new BigDecimal("100.00"));
        entry.setMrpPrice(new BigDecimal("120.00"));
        entry.setTotalPrice(new BigDecimal("200.00"));
        entry.setDiscount(new BigDecimal("40.00"));
        cartDto.setCartEntries(List.of(entry));

        Customer customer = new Customer();
        customer.setIdentifier("customer1");

        OrderDto orderDto = new OrderDto();
        orderDto.setIdentifier("ORD-UUID");
        orderDto.setOrderId("ORD-20260620120000-ABC12345");
        orderDto.setSuccess(true);
        orderDto.setMessage("Order placed successfully.");

        when(cartService.findByIdentifier("CART001")).thenReturn(cartDto);
        when(customerRepository.findByIdentifier("customer1")).thenReturn(customer);
        when(orderRepository.save(any(Orders.class))).thenAnswer(inv -> inv.getArgument(0));
        when(modelMapper.map(any(Orders.class), eq(OrderDto.class))).thenReturn(orderDto);
        when(orderEntryService.findAllByOrderIdentifier(any())).thenReturn(new ArrayList<>());
        when(orderEntryService.save(any(OrderEntryDto.class))).thenReturn(new OrderEntryDto());

        OrderDto response = orderService.placeOrder(request);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Order placed successfully.", response.getMessage());
        verify(orderRepository).save(any(Orders.class));
        verify(orderEntryService, atLeastOnce()).save(any(OrderEntryDto.class));
        verify(cartService).delete("CART001");
    }

    @Test
    void placeOrderCartNotFoundTest() {
        PlaceOrderRequestDto request = new PlaceOrderRequestDto();
        request.setCartIdentifier("INVALID_CART");

        when(cartService.findByIdentifier("INVALID_CART")).thenReturn(null);

        OrderDto response = orderService.placeOrder(request);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart with identifier - INVALID_CART not found", response.getMessage());
        verify(orderRepository, never()).save(any(Orders.class));
    }

    @Test
    void placeOrderEmptyCartTest() {
        PlaceOrderRequestDto request = new PlaceOrderRequestDto();
        request.setCartIdentifier("EMPTY_CART");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("EMPTY_CART");
        cartDto.setCartEntries(new ArrayList<>());

        when(cartService.findByIdentifier("EMPTY_CART")).thenReturn(cartDto);

        OrderDto response = orderService.placeOrder(request);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart is empty. Cannot place order.", response.getMessage());
        verify(orderRepository, never()).save(any(Orders.class));
    }

    @Test
    void placeOrderNullCartEntriesTest() {
        PlaceOrderRequestDto request = new PlaceOrderRequestDto();
        request.setCartIdentifier("CART_NULL_ENTRIES");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART_NULL_ENTRIES");
        cartDto.setCartEntries(null);

        when(cartService.findByIdentifier("CART_NULL_ENTRIES")).thenReturn(cartDto);

        OrderDto response = orderService.placeOrder(request);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart is empty. Cannot place order.", response.getMessage());
        verify(orderRepository, never()).save(any(Orders.class));
    }

    @Test
    void findByIdentifierSuccessTest() {
        Orders orders = new Orders();
        orders.setIdentifier("ORD-UUID");
        orders.setOrderId("ORD-20260620120000-ABC12345");
        orders.setCustomerIdentifier("customer1");

        Customer customer = new Customer();
        customer.setIdentifier("customer1");

        OrderDto orderDto = new OrderDto();
        orderDto.setIdentifier("ORD-UUID");
        orderDto.setOrderId("ORD-20260620120000-ABC12345");
        orderDto.setCustomerIdentifier("customer1");

        when(orderRepository.findByIdentifier("ORD-UUID")).thenReturn(orders);
        when(modelMapper.map(orders, OrderDto.class)).thenReturn(orderDto);
        when(customerRepository.findByIdentifier("customer1")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(new CustomerDto());
        when(orderEntryService.findAllByOrderIdentifier("ORD-UUID")).thenReturn(new ArrayList<>());

        OrderDto response = orderService.findByIdentifier("ORD-UUID");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ORD-UUID", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        when(orderRepository.findByIdentifier("INVALID")).thenReturn(null);

        OrderDto response = orderService.findByIdentifier("INVALID");

        Assertions.assertNull(response);
    }

    @Test
    void findByOrderIdSuccessTest() {
        Orders orders = new Orders();
        orders.setIdentifier("ORD-UUID");
        orders.setOrderId("ORD-20260620120000-ABC12345");
        orders.setCustomerIdentifier("customer1");

        Customer customer = new Customer();
        customer.setIdentifier("customer1");

        OrderDto orderDto = new OrderDto();
        orderDto.setIdentifier("ORD-UUID");
        orderDto.setOrderId("ORD-20260620120000-ABC12345");
        orderDto.setCustomerIdentifier("customer1");

        when(orderRepository.findByOrderId("ORD-20260620120000-ABC12345")).thenReturn(orders);
        when(modelMapper.map(orders, OrderDto.class)).thenReturn(orderDto);
        when(customerRepository.findByIdentifier("customer1")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(new CustomerDto());
        when(orderEntryService.findAllByOrderIdentifier("ORD-UUID")).thenReturn(new ArrayList<>());

        OrderDto response = orderService.findByOrderId("ORD-20260620120000-ABC12345");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ORD-20260620120000-ABC12345", response.getOrderId());
    }

    @Test
    void findByOrderIdNotFoundTest() {
        when(orderRepository.findByOrderId("INVALID_ORDER_ID")).thenReturn(null);

        OrderDto response = orderService.findByOrderId("INVALID_ORDER_ID");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Orders orders = new Orders();
        orders.setIdentifier("ORD-UUID");
        orders.setCustomerIdentifier("customer1");
        List<Orders> ordersList = List.of(orders);

        Page<Orders> page = new PageImpl<>(ordersList, pageable, 1);

        OrderDto orderDto = new OrderDto();
        orderDto.setIdentifier("ORD-UUID");
        orderDto.setCustomerIdentifier("customer1");
        List<OrderDto> orderDtos = new ArrayList<>(List.of(orderDto));

        when(orderRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(ordersList), any(Type.class))).thenReturn(orderDtos);
        when(customerRepository.findByIdentifier("customer1")).thenReturn(new Customer());
        when(modelMapper.map(any(Customer.class), eq(CustomerDto.class))).thenReturn(new CustomerDto());
        when(orderEntryService.findAllByOrderIdentifier("ORD-UUID")).thenReturn(new ArrayList<>());

        List<OrderDto> response = orderService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("ORD-UUID", response.get(0).getIdentifier());
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Orders> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(orderRepository.findAll(pageable)).thenReturn(emptyPage);
        when(modelMapper.map(eq(new ArrayList<>()), any(Type.class))).thenReturn(new ArrayList<>());

        List<OrderDto> response = orderService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(0, response.size());
    }

    @Test
    void findByCustomerIdentifierTest() {
        Orders orders = new Orders();
        orders.setIdentifier("ORD-UUID");
        orders.setCustomerIdentifier("customer1");
        List<Orders> ordersList = List.of(orders);

        OrderDto orderDto = new OrderDto();
        orderDto.setIdentifier("ORD-UUID");
        orderDto.setCustomerIdentifier("customer1");
        List<OrderDto> orderDtos = new ArrayList<>(List.of(orderDto));

        when(orderRepository.findAllByCustomerIdentifierOrderByOrderDateDesc("customer1")).thenReturn(ordersList);
        when(modelMapper.map(eq(ordersList), any(Type.class))).thenReturn(orderDtos);
        when(orderEntryService.findAllByOrderIdentifier("ORD-UUID")).thenReturn(new ArrayList<>());

        List<OrderDto> response = orderService.findByCustomerIdentifier("customer1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("ORD-UUID", response.get(0).getIdentifier());
    }

    @Test
    void findByCustomerIdentifierEmptyTest() {
        when(orderRepository.findAllByCustomerIdentifierOrderByOrderDateDesc("UNKNOWN")).thenReturn(new ArrayList<>());
        when(modelMapper.map(eq(new ArrayList<>()), any(Type.class))).thenReturn(new ArrayList<>());

        List<OrderDto> response = orderService.findByCustomerIdentifier("UNKNOWN");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(0, response.size());
    }

    @Test
    void deleteOrderSuccessTest() {
        Orders orders = new Orders();
        orders.setIdentifier("ORD-UUID");

        when(orderRepository.findByIdentifier("ORD-UUID")).thenReturn(orders);

        boolean result = orderService.delete("ORD-UUID");

        Assertions.assertTrue(result);
        verify(orderEntryService).deleteByOrderIdentifier("ORD-UUID");
        verify(orderRepository).deleteByIdentifier("ORD-UUID");
        verify(orderRepository, never()).save(any(Orders.class));
    }

    @Test
    void deleteOrderNotFoundTest() {
        when(orderRepository.findByIdentifier("INVALID")).thenReturn(null);

        boolean result = orderService.delete("INVALID");

        Assertions.assertFalse(result);
        verify(orderRepository, never()).save(any(Orders.class));
        verify(orderRepository, never()).deleteByIdentifier(any());
        verify(orderEntryService, never()).deleteByOrderIdentifier(any());
    }
}