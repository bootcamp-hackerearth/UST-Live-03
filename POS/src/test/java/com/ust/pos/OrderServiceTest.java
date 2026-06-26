package com.ust.pos;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginationResponseDto;
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

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void checkoutSuccessTest() {

        OrderDto request = new OrderDto();
        request.setCustomer("CART1");
        request.setPaymentMethod("UPI");

        Cart cart = new Cart();
        cart.setIdentifier("CART1");
        cart.setTotalPrice(BigDecimal.valueOf(100));
        cart.setTotalDiscount(BigDecimal.valueOf(10));
        cart.setTotalOriginalPrice(BigDecimal.valueOf(110));

        CartEntry cartEntry = new CartEntry();
        cartEntry.setProduct("P1");
        cartEntry.setQuantity(BigDecimal.ONE);
        cartEntry.setOriginalPrice(BigDecimal.valueOf(110));
        cartEntry.setDiscount(BigDecimal.valueOf(10));
        cartEntry.setUnitPrice(BigDecimal.valueOf(100));
        cartEntry.setTotalPrice(BigDecimal.valueOf(100));

        Stock stock = new Stock();
        stock.setProduct("P1");
        stock.setQuantity(10L);

        OrderDto mappedDto = new OrderDto();

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        Mockito.when(cartEntryRepository.findByCart("CART1"))
                .thenReturn(List.of(cartEntry));

        Mockito.when(stockRepository.findByProduct("P1"))
                .thenReturn(stock);

        Mockito.when(modelMapper.map(
                Mockito.any(Order.class),
                Mockito.eq(OrderDto.class)
        )).thenReturn(mappedDto);

        Mockito.when(orderEntryRepository.findByOrderIdentifier(
                Mockito.anyString()
        )).thenReturn(List.of());

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(List.of());

        OrderDto response = orderService.checkout(request);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Order placed successfully",
                response.getMessage()
        );

        Mockito.verify(orderRepository)
                .save(Mockito.any(Order.class));

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void checkoutCartNotFoundTest() {

        OrderDto dto = new OrderDto();
        dto.setCustomer("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(null);

        OrderDto response = orderService.checkout(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Cart not found",
                response.getMessage()
        );
    }

    @Test
    void checkoutCartEmptyTest() {

        OrderDto dto = new OrderDto();
        dto.setCustomer("CART1");

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        Mockito.when(cartEntryRepository.findByCart("CART1"))
                .thenReturn(List.of());

        OrderDto response = orderService.checkout(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Cart is empty",
                response.getMessage()
        );
    }

    @Test
    void checkoutStockNotFoundTest() {

        OrderDto dto = new OrderDto();
        dto.setCustomer("CART1");

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        CartEntry entry = new CartEntry();
        entry.setProduct("P1");
        entry.setQuantity(BigDecimal.ONE);

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        Mockito.when(cartEntryRepository.findByCart("CART1"))
                .thenReturn(List.of(entry));

        Mockito.when(stockRepository.findByProduct("P1"))
                .thenReturn(null);

        OrderDto response = orderService.checkout(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Stock not found for product: P1",
                response.getMessage()
        );
    }

    @Test
    void checkoutInsufficientStockTest() {

        OrderDto dto = new OrderDto();
        dto.setCustomer("CART1");

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        CartEntry entry = new CartEntry();
        entry.setProduct("P1");
        entry.setQuantity(BigDecimal.TEN);

        Stock stock = new Stock();
        stock.setProduct("P1");
        stock.setQuantity(5L);

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        Mockito.when(cartEntryRepository.findByCart("CART1"))
                .thenReturn(List.of(entry));

        Mockito.when(stockRepository.findByProduct("P1"))
                .thenReturn(stock);

        OrderDto response = orderService.checkout(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Insufficient stock for product: P1",
                response.getMessage()
        );
    }

    @Test
    void checkoutCashAmountLessThanTotalTest() {

        OrderDto dto = new OrderDto();
        dto.setCustomer("CART1");
        dto.setPaymentMethod("CASH");
        dto.setReceivedAmount(BigDecimal.valueOf(50));

        Cart cart = new Cart();
        cart.setIdentifier("CART1");
        cart.setTotalPrice(BigDecimal.valueOf(100));

        CartEntry entry = new CartEntry();
        entry.setProduct("P1");
        entry.setQuantity(BigDecimal.ONE);

        Stock stock = new Stock();
        stock.setQuantity(10L);

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        Mockito.when(cartEntryRepository.findByCart("CART1"))
                .thenReturn(List.of(entry));

        Mockito.when(stockRepository.findByProduct("P1"))
                .thenReturn(stock);

        OrderDto response = orderService.checkout(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Received amount is less than total price",
                response.getMessage()
        );
    }

    @Test
    void getSuccessTest() {

        Order order = new Order();
        order.setIdentifier("ORD1");

        OrderDto dto = new OrderDto();

        Mockito.when(orderRepository.findByIdentifier("ORD1"))
                .thenReturn(order);

        Mockito.when(modelMapper.map(order, OrderDto.class))
                .thenReturn(dto);

        Mockito.when(orderEntryRepository.findByOrderIdentifier("ORD1"))
                .thenReturn(List.of());

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(List.of());

        OrderDto response = orderService.get("ORD1");

        Assertions.assertNotNull(response);
    }

    @Test
    void getOrderNotFoundTest() {

        Mockito.when(orderRepository.findByIdentifier("ORD1"))
                .thenReturn(null);

        OrderDto response = orderService.get("ORD1");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Order not found",
                response.getMessage()
        );
    }

    @Test
    void findAllWithPageableTest() {

        Order order = new Order();
        order.setIdentifier("ORD1");

        OrderDto dto = new OrderDto();
        dto.setIdentifier("ORD1");

        List<Order> orders = List.of(order);
        List<OrderDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Order> page = new PageImpl<>(orders);

        Mockito.when(orderRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                Mockito.eq(orders),
                Mockito.any(Type.class)
        )).thenReturn(dtos);

        PaginationResponseDto<OrderDto> response =
                orderService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );
    }

    @Test
    void findAllWithoutPageableTest() {

        Order order = new Order();
        order.setIdentifier("ORD1");

        OrderDto dto = new OrderDto();
        dto.setIdentifier("ORD1");

        List<Order> orders = List.of(order);
        List<OrderDto> dtos = List.of(dto);

        Mockito.when(orderRepository.findAll())
                .thenReturn(orders);

        Mockito.when(modelMapper.map(
                Mockito.eq(orders),
                Mockito.any(Type.class)
        )).thenReturn(dtos);

        PaginationResponseDto<OrderDto> response =
                orderService.findAll(null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );
    }
}
