package com.ust.pos;

import com.ust.pos.cart.CartService;
import com.ust.pos.cartentry.CartEntryService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.*;
import com.ust.pos.model.Order;
import com.ust.pos.model.OrderRepository;
import com.ust.pos.order.service.impl.OrderServiceImplementation;
import com.ust.pos.orderentry.OrderEntryService;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImplementation orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CartService cartService;

    @Mock
    private CartEntryService cartEntryService;

    @Mock
    private CustomerService customerService;

    @Mock
    private OrderEntryService orderEntryService;

    @Test
    void saveOrderSuccess() {

        OrderDto dto = new OrderDto();
        dto.setIdentifier("ORD1");

        Order order = new Order();

        Mockito.when(orderRepository.findByIdentifier("ORD1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Order.class))
                .thenReturn(order);

        OrderDto response = orderService.save(dto);

        Assertions.assertEquals("ORD1", response.getIdentifier());

        Mockito.verify(orderRepository)
                .save(order);
    }

    @Test
    void saveOrderAlreadyExists() {

        OrderDto dto = new OrderDto();
        dto.setIdentifier("ORD1");

        Mockito.when(orderRepository.findByIdentifier("ORD1"))
                .thenReturn(new Order());

        OrderDto response = orderService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Order with identifier - ORD1 already exists",
                response.getMessage()
        );
    }

    @Test
    void placeOrderCartNotFound() {

        OrderDto dto = new OrderDto();
        dto.setCartId("test@gmail.com");

        Mockito.when(
                cartService.findByIdentifier("test@gmail.com")
        ).thenReturn(null);

        OrderDto response =
                orderService.placeOrder(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Cart not found",
                response.getMessage()
        );
    }

    @Test
    void placeOrderCartEmpty() {

        OrderDto dto = new OrderDto();
        dto.setCartId("test@gmail.com");

        Mockito.when(
                cartService.findByIdentifier("test@gmail.com")
        ).thenReturn(new CartDto());

        Mockito.when(
                cartEntryService.findByCartId("test@gmail.com")
        ).thenReturn(List.of());

        OrderDto response =
                orderService.placeOrder(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Cart is empty",
                response.getMessage()
        );
    }

    @Test
    void placeOrderCustomerNotFound() {

        OrderDto dto = new OrderDto();
        dto.setCartId("test@gmail.com");

        Mockito.when(
                cartService.findByIdentifier("test@gmail.com")
        ).thenReturn(new CartDto());

        CartEntryDto entry = new CartEntryDto();
        entry.setProduct("P1");

        Mockito.when(
                cartEntryService.findByCartId("test@gmail.com")
        ).thenReturn(List.of(entry));

        Mockito.when(
                customerService.findByEmail("test@gmail.com")
        ).thenReturn(null);

        OrderDto response =
                orderService.placeOrder(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Customer not found",
                response.getMessage()
        );
    }

    @Test
    void placeOrderSuccess() {

        OrderDto request = new OrderDto();
        request.setCartId("test@gmail.com");
        request.setPaymentMethod("UPI");
        request.setPaymentTime(LocalDateTime.now());

        CartDto cart = new CartDto();

        CustomerDto customer = new CustomerDto();
        customer.setEmail("test@gmail.com");
        customer.setAddress("Chennai");

        CartEntryDto entry = new CartEntryDto();
        entry.setProduct("P1");
        entry.setQuantity(BigDecimal.valueOf(2));
        entry.setUnitPrice(BigDecimal.valueOf(100));
        entry.setDiscount(BigDecimal.valueOf(10));
        entry.setTotalPrice(BigDecimal.valueOf(180));

        Order mappedOrder = new Order();

        Mockito.when(
                cartService.findByIdentifier("test@gmail.com")
        ).thenReturn(cart);

        Mockito.when(
                cartEntryService.findByCartId("test@gmail.com")
        ).thenReturn(List.of(entry));

        Mockito.when(
                customerService.findByEmail("test@gmail.com")
        ).thenReturn(customer);

        Mockito.when(
                modelMapper.map(
                        Mockito.any(OrderDto.class),
                        Mockito.eq(Order.class)
                )
        ).thenReturn(mappedOrder);

        OrderDto response =
                orderService.placeOrder(request);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Order placed successfully",
                response.getMessage()
        );

        Mockito.verify(orderRepository)
                .save(mappedOrder);

        Mockito.verify(orderEntryService)
                .save(Mockito.any(OrderEntryDto.class));

        Mockito.verify(cartEntryService)
                .clearCart("test@gmail.com");

        Mockito.verify(cartService)
                .recalculateCart("test@gmail.com");
    }

    @Test
    void updateOrderSuccess() {

        OrderDto dto = new OrderDto();
        dto.setIdentifier("ORD1");

        Order existing = new Order();

        Mockito.when(
                orderRepository.findByIdentifier("ORD1")
        ).thenReturn(existing);

        OrderDto response =
                orderService.update(dto);

        Mockito.verify(modelMapper)
                .map(dto, existing);

        Mockito.verify(orderRepository)
                .save(existing);

        Assertions.assertNull(response.getMessage());
    }

    @Test
    void updateOrderNotFound() {

        OrderDto dto = new OrderDto();
        dto.setIdentifier("ORD1");

        Mockito.when(
                orderRepository.findByIdentifier("ORD1")
        ).thenReturn(null);

        OrderDto response =
                orderService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Order with identifier - ORD1 not found",
                response.getMessage()
        );
    }

    @Test
    void deleteOrderTest() {

        orderService.delete("ORD1");

        Mockito.verify(orderRepository)
                .deleteByIdentifier("ORD1");
    }

    @Test
    void findAllOrdersTest() {

        List<Order> orders =
                List.of(new Order(), new Order());

        List<OrderDto> dtos =
                List.of(new OrderDto(), new OrderDto());

        Mockito.when(orderRepository.findAll())
                .thenReturn(orders);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(orders),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        List<OrderDto> response =
                orderService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findByIdentifierTest() {

        Order order = new Order();
        order.setIdentifier("ORD1");

        OrderDto dto = new OrderDto();
        dto.setIdentifier("ORD1");

        Mockito.when(
                orderRepository.findByIdentifier("ORD1")
        ).thenReturn(order);

        Mockito.when(
                modelMapper.map(order, OrderDto.class)
        ).thenReturn(dto);

        OrderDto response =
                orderService.findByIdentifier("ORD1");

        Assertions.assertEquals(
                "ORD1",
                response.getIdentifier()
        );
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Order> orders =
                List.of(new Order());

        Page<Order> page =
                new PageImpl<>(orders);

        List<OrderDto> dtos =
                List.of(new OrderDto());

        Type listType =
                new TypeToken<List<OrderDto>>(){}.getType();

        Mockito.when(
                orderRepository.findAll(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(orders, listType)
        ).thenReturn(dtos);

        WsDto<OrderDto> response =
                orderService.findAll(pageable);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );
    }

    @Test
    void findByCustomerEmailTest() {

        List<Order> orders =
                List.of(new Order(), new Order());

        List<OrderDto> dtos =
                List.of(new OrderDto(), new OrderDto());

        Mockito.when(
                orderRepository.findByCustomerEmail(
                        "test@gmail.com"
                )
        ).thenReturn(orders);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(orders),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        List<OrderDto> response =
                orderService.findByCustomerEmail(
                        "test@gmail.com"
                );

        Assertions.assertEquals(
                2,
                response.size()
        );
    }
}