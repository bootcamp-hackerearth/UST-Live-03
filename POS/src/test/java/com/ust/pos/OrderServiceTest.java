package com.ust.pos;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.*;
import com.ust.pos.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    public static final String CART_1 = "CART1";
    @InjectMocks
    private OrderServiceImpl service;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void checkoutTest() {

        OrderDto dto = new OrderDto();
        dto.setCustomerIdentifier(CART_1);

        when(cartRepository.findByIdentifierAndDeletedFalse(CART_1))
                .thenReturn(null);

        OrderDto result = service.checkout(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Cart not found matching customer details",
                result.getMessage()
        );

        Cart cart = new Cart();
        cart.setIdentifier(CART_1);

        when(cartRepository.findByIdentifierAndDeletedFalse(CART_1))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(Collections.emptyList());

        result = service.checkout(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Cart is currently empty",
                result.getMessage()
        );

        cart.setOriginalPrice(BigDecimal.valueOf(900));
        cart.setDiscount(BigDecimal.valueOf(100));
        cart.setTotalPrice(BigDecimal.valueOf(800));

        CartEntry cartEntry = new CartEntry();
        cartEntry.setProductIdentifier("P1");
        cartEntry.setQuantity(2);
        cartEntry.setUnitPrice(BigDecimal.valueOf(400));
        cartEntry.setOriginalPrice(BigDecimal.valueOf(500));
        cartEntry.setDiscount(BigDecimal.valueOf(100));
        cartEntry.setTotalPrice(BigDecimal.valueOf(800));

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse(CART_1))
                .thenReturn(List.of(cartEntry));

        when(orderEntryRepository.findByOrderIdentifierAndDeletedFalse(anyString()))
                .thenReturn(List.of(new OrderEntry()));

        when(modelMapper.map(any(Order.class), eq(OrderDto.class)))
                .thenReturn(new OrderDto());

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new OrderEntryDto()));

        dto.setPaymentMethod("CASH");
        dto.setReceivedAmount(BigDecimal.valueOf(1000));

        result = service.checkout(dto);

        assertTrue(result.isSuccess());

        ArgumentCaptor<Order> orderCaptor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderRepository, atLeastOnce())
                .save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();

        assertEquals(
                BigDecimal.valueOf(200),
                savedOrder.getChangeAmount()
        );

        dto.setPaymentMethod("CARD");

        service.checkout(dto);

        verify(orderRepository, atLeast(2)).save(any(Order.class));
    }

    @Test
    void getTest() {

        when(orderRepository.findByIdentifierAndDeletedFalse("ORD1"))
                .thenReturn(null);

        OrderDto result = service.get("ORD1");

        assertFalse(result.isSuccess());

        assertEquals(
                "Order registry trace not found",
                result.getMessage()
        );

        Order order = new Order();

        when(orderRepository.findByIdentifierAndDeletedFalse("ORD1"))
                .thenReturn(order);

        when(modelMapper.map(order, OrderDto.class))
                .thenReturn(new OrderDto());

        when(orderEntryRepository.findByOrderIdentifierAndDeletedFalse("ORD1"))
                .thenReturn(List.of(new OrderEntry()));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new OrderEntryDto()));

        result = service.get("ORD1");

        assertNotNull(result);
    }

    @Test
    void checkoutCartEntryListNullCoverage() {

        OrderDto dto = new OrderDto();
        dto.setCustomerIdentifier("C1");

        Cart cart = new Cart();
        cart.setIdentifier("C1");

        when(cartRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse("C1"))
                .thenReturn(null);

        OrderDto result = service.checkout(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Cart is currently empty",
                result.getMessage()
        );
    }

    @Test
    void checkoutCashNegativeChangeCoverage() {

        OrderDto dto = new OrderDto();
        dto.setCustomerIdentifier("C1");
        dto.setPaymentMethod("CASH");
        dto.setReceivedAmount(BigDecimal.valueOf(50));

        Cart cart = new Cart();
        cart.setIdentifier("C1");
        cart.setOriginalPrice(BigDecimal.valueOf(100));
        cart.setDiscount(BigDecimal.ZERO);
        cart.setTotalPrice(BigDecimal.valueOf(100));

        CartEntry cartEntry = new CartEntry();
        cartEntry.setProductIdentifier("P1");
        cartEntry.setQuantity(1);
        cartEntry.setUnitPrice(BigDecimal.TEN);
        cartEntry.setOriginalPrice(BigDecimal.TEN);
        cartEntry.setDiscount(BigDecimal.ZERO);
        cartEntry.setTotalPrice(BigDecimal.TEN);

        when(cartRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse("C1"))
                .thenReturn(List.of(cartEntry));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(orderEntryRepository.findByOrderIdentifierAndDeletedFalse(any()))
                .thenReturn(List.of());

        when(modelMapper.map(any(Order.class), eq(OrderDto.class)))
                .thenReturn(new OrderDto());

        when(modelMapper.map(any(), any(java.lang.reflect.Type.class)))
                .thenReturn(List.of());

        OrderDto result = service.checkout(dto);

        assertTrue(result.isSuccess());
    }

    @Test
    void checkoutCashPositiveChangeCoverage() {

        OrderDto dto = new OrderDto();
        dto.setCustomerIdentifier("C1");
        dto.setPaymentMethod("CASH");
        dto.setReceivedAmount(BigDecimal.valueOf(200));

        Cart cart = new Cart();
        cart.setIdentifier("C1");
        cart.setOriginalPrice(BigDecimal.valueOf(100));
        cart.setDiscount(BigDecimal.ZERO);
        cart.setTotalPrice(BigDecimal.valueOf(100));

        CartEntry entry = new CartEntry();
        entry.setProductIdentifier("P1");
        entry.setQuantity(1);
        entry.setUnitPrice(BigDecimal.TEN);
        entry.setOriginalPrice(BigDecimal.TEN);
        entry.setDiscount(BigDecimal.ZERO);
        entry.setTotalPrice(BigDecimal.TEN);

        when(cartRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse("C1"))
                .thenReturn(List.of(entry));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(orderEntryRepository.findByOrderIdentifierAndDeletedFalse(any()))
                .thenReturn(List.of());

        when(modelMapper.map(any(Order.class), eq(OrderDto.class)))
                .thenReturn(new OrderDto());

        when(modelMapper.map(any(), any(java.lang.reflect.Type.class)))
                .thenReturn(List.of());

        service.checkout(dto);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void checkoutCardPaymentCoverage() {

        OrderDto dto = new OrderDto();
        dto.setCustomerIdentifier("C1");
        dto.setPaymentMethod("CARD");

        Cart cart = new Cart();
        cart.setIdentifier("C1");
        cart.setOriginalPrice(BigDecimal.TEN);
        cart.setDiscount(BigDecimal.ZERO);
        cart.setTotalPrice(BigDecimal.TEN);

        CartEntry entry = new CartEntry();
        entry.setProductIdentifier("P1");
        entry.setQuantity(1);

        when(cartRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartIdentifierAndDeletedFalse("C1"))
                .thenReturn(List.of(entry));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(orderEntryRepository.findByOrderIdentifierAndDeletedFalse(any()))
                .thenReturn(List.of());

        when(modelMapper.map(any(Order.class), eq(OrderDto.class)))
                .thenReturn(new OrderDto());

        when(modelMapper.map(any(), any(java.lang.reflect.Type.class)))
                .thenReturn(List.of());

        service.checkout(dto);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void deleteTest() {

        when(orderRepository.findByIdentifierAndDeletedFalse("ORD1"))
                .thenReturn(null);

        assertFalse(service.delete("ORD1"));

        Order order = new Order();
        OrderEntry entry = new OrderEntry();

        when(orderRepository.findByIdentifierAndDeletedFalse("ORD1"))
                .thenReturn(order);

        when(orderEntryRepository.findByOrderIdentifierAndDeletedFalse("ORD1"))
                .thenReturn(List.of(entry));

        assertTrue(service.delete("ORD1"));

        verify(orderRepository).save(order);
        verify(orderEntryRepository).save(entry);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> page =
                new PageImpl<>(
                        List.of(new Order()),
                        pageable,
                        1
                );

        when(orderRepository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new OrderDto()));

        WsDto<OrderDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void specificationFindAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> page =
                new PageImpl<>(
                        List.of(new Order()),
                        pageable,
                        1
                );

        when(orderRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new OrderDto()));

        Specification<Order> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<OrderDto> result =
                service.findAll(specification, pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());

        verify(orderRepository)
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void orderConstantsCoverageTest() {

        assertEquals(
                "Product not found",
                OrderServiceImpl.PRODUCT_NOT_FOUND.getMessage()
        );
    }
}