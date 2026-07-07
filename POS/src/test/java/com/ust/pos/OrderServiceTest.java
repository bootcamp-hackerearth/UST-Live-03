package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.*;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import com.ust.pos.model.OrderRepository;
import com.ust.pos.model.Orders;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import com.ust.pos.orderentry.service.OrderEntryService;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final String CART_ID = "CART001";
    private static final String ORDER_IDENTIFIER = "ORD-UUID";
    private static final String CUSTOMER = "customer1";

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

    @InjectMocks
    private OrderServiceImpl service;


    @Test
    void placeOrderSuccess() {

        PlaceOrderRequestDto request = new PlaceOrderRequestDto();
        request.setCartIdentifier(CART_ID);
        request.setPaymentMode("CASH");

        CartEntryDto entry = new CartEntryDto();
        entry.setProductIdentifier("P1");
        entry.setQuantity(2);
        entry.setSellingPrice(BigDecimal.TEN);
        entry.setMrpPrice(BigDecimal.valueOf(15));
        entry.setTotalPrice(BigDecimal.valueOf(20));
        entry.setDiscount(BigDecimal.valueOf(5));

        CartDto cart = new CartDto();
        cart.setIdentifier(CART_ID);
        cart.setUsername(CUSTOMER);
        cart.setCoupon("DISC");
        cart.setCartEntries(List.of(entry));

        Customer customer = new Customer();
        customer.setIdentifier(CUSTOMER);

        OrderDto mapped = new OrderDto();

        when(cartService.findByIdentifier(CART_ID)).thenReturn(cart);

        when(customerRepository.findByIdentifier(CUSTOMER)).thenReturn(customer);

        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        when(modelMapper.map(any(Orders.class), eq(OrderDto.class))).thenReturn(mapped);

        when(modelMapper.map(any(Customer.class), eq(CustomerDto.class))).thenReturn(new CustomerDto());

        when(orderEntryService.findAllByOrderIdentifier(any())).thenReturn(List.of(new OrderEntryDto()));

        OrderDto result = service.placeOrder(request);

        assertTrue(result.isSuccess());
        assertEquals("Order placed successfully.", result.getMessage());

        verify(orderRepository).save(any());
        verify(orderEntryService).save(any());
        verify(cartService).delete(CART_ID);

    }


    @Test
    void placeOrderCustomerNull() {

        PlaceOrderRequestDto request = new PlaceOrderRequestDto();

        request.setCartIdentifier(CART_ID);

        CartEntryDto e = new CartEntryDto();

        e.setTotalPrice(BigDecimal.TEN);

        CartDto cart = new CartDto();

        cart.setIdentifier(CART_ID);
        cart.setCartEntries(List.of(e));

        when(cartService.findByIdentifier(CART_ID)).thenReturn(cart);

        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        when(modelMapper.map(any(Orders.class), eq(OrderDto.class))).thenReturn(new OrderDto());

        when(orderEntryService.findAllByOrderIdentifier(any())).thenReturn(List.of());

        OrderDto dto = service.placeOrder(request);

        assertTrue(dto.isSuccess());

        verify(customerRepository).findByIdentifier(any());

    }


    @Test
    void placeOrderEmptyCart() {

        PlaceOrderRequestDto request = new PlaceOrderRequestDto();

        request.setCartIdentifier(CART_ID);

        CartDto cart = new CartDto();

        cart.setCartEntries(List.of());

        when(cartService.findByIdentifier(CART_ID)).thenReturn(cart);

        OrderDto result = service.placeOrder(request);

        assertFalse(result.isSuccess());

        assertEquals("Cart is empty. Cannot place order.", result.getMessage());

        verify(orderRepository, never()).save(any());

    }


    @Test
    void placeOrderCartNotFound() {

        when(cartService.findByIdentifier(CART_ID)).thenReturn(null);

        PlaceOrderRequestDto request = new PlaceOrderRequestDto();

        request.setCartIdentifier(CART_ID);

        assertThrows(ResourceNotFoundException.class, () -> service.placeOrder(request));

    }


    @Test
    void findByIdentifier() {

        Orders order = new Orders();

        order.setIdentifier(ORDER_IDENTIFIER);
        order.setCustomerIdentifier(CUSTOMER);

        OrderDto dto = new OrderDto();

        dto.setIdentifier(ORDER_IDENTIFIER);
        dto.setCustomerIdentifier(CUSTOMER);

        when(orderRepository.findByIdentifier(ORDER_IDENTIFIER)).thenReturn(order);

        when(modelMapper.map(order, OrderDto.class)).thenReturn(dto);

        when(customerRepository.findByIdentifier(CUSTOMER)).thenReturn(new Customer());

        when(modelMapper.map(any(Customer.class), eq(CustomerDto.class))).thenReturn(new CustomerDto());

        when(orderEntryService.findAllByOrderIdentifier(ORDER_IDENTIFIER)).thenReturn(List.of());

        OrderDto response = service.findByIdentifier(ORDER_IDENTIFIER);

        assertNotNull(response);

    }


    @Test
    void findByIdentifierNotFound() {

        when(orderRepository.findByIdentifier(ORDER_IDENTIFIER)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.findByIdentifier(ORDER_IDENTIFIER));

    }


    @Test
    void findByOrderId() {

        Orders order = new Orders();

        order.setIdentifier(ORDER_IDENTIFIER);

        OrderDto dto = new OrderDto();

        dto.setIdentifier(ORDER_IDENTIFIER);

        when(orderRepository.findByOrderId("ORDER1")).thenReturn(order);

        when(modelMapper.map(order, OrderDto.class)).thenReturn(dto);

        when(orderEntryService.findAllByOrderIdentifier(ORDER_IDENTIFIER)).thenReturn(List.of());

        OrderDto result = service.findByOrderId("ORDER1");

        assertNotNull(result);

    }


    @Test
    void findByCustomerIdentifier() {

        Orders order = new Orders();

        order.setIdentifier(ORDER_IDENTIFIER);

        List<Orders> entities = List.of(order);

        OrderDto dto = new OrderDto();

        dto.setIdentifier(ORDER_IDENTIFIER);

        List<OrderDto> dtos = new ArrayList<>();

        dtos.add(dto);

        when(orderRepository.findAllByCustomerIdentifierOrderByOrderDateDesc(CUSTOMER)).thenReturn(entities);

        when(modelMapper.map(eq(entities), any(Type.class))).thenReturn(dtos);

        when(orderEntryService.findAllByOrderIdentifier(ORDER_IDENTIFIER)).thenReturn(List.of());

        List<OrderDto> result = service.findByCustomerIdentifier(CUSTOMER);

        assertEquals(1, result.size());

    }


    @Test
    void deleteSuccess() {

        Orders order = new Orders();

        order.setIdentifier(ORDER_IDENTIFIER);

        when(orderRepository.findByIdentifier(ORDER_IDENTIFIER)).thenReturn(order);

        boolean deleted = service.delete(ORDER_IDENTIFIER);

        assertTrue(deleted);

        verify(orderEntryService).deleteByOrderIdentifier(ORDER_IDENTIFIER);

        verify(orderRepository).deleteByIdentifier(ORDER_IDENTIFIER);

    }


    @Test
    void deleteNotFound() {

        when(orderRepository.findByIdentifier(ORDER_IDENTIFIER)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(ORDER_IDENTIFIER));

    }


    @Test
    void findAllPageable() {

        Pageable pageable = PageRequest.of(0, 10);

        Orders entity = new Orders();

        entity.setIdentifier(ORDER_IDENTIFIER);

        Page<Orders> page = new PageImpl<>(List.of(entity), pageable, 1);

        OrderDto dto = new OrderDto();

        dto.setIdentifier(ORDER_IDENTIFIER);

        List<OrderDto> mapped = List.of(dto);

        when(orderRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(mapped);

        when(orderEntryService.findAllByOrderIdentifier(ORDER_IDENTIFIER)).thenReturn(List.of());

        WsDto<OrderDto> result = service.findAll(pageable);

        assertEquals(1, result.getTotalRecords());

        assertEquals(1, result.getDtoList().size());

    }


    @Test
    void findAllSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Orders> spec = mock(Specification.class);

        Orders order = new Orders();

        order.setIdentifier(ORDER_IDENTIFIER);

        Page<Orders> page = new PageImpl<>(List.of(order));

        OrderDto dto = new OrderDto();

        dto.setIdentifier(ORDER_IDENTIFIER);

        when(orderRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(dto));

        when(orderEntryService.findAllByOrderIdentifier(ORDER_IDENTIFIER)).thenReturn(List.of());

        WsDto<OrderDto> ws = service.findAll(spec, pageable, "abc");

        assertEquals("abc", ws.getKeyword());

        assertEquals(1, ws.getDtoList().size());

    }

}