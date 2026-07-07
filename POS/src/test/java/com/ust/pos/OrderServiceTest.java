package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.OrderEntryDto;
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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private CartService cartService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void placeOrderNewOrderTest() {
        Cart cart = new Cart();
        cart.setIdentifier("9999999999");
        cart.setTotalPrice(BigDecimal.valueOf(1000));
        cart.setTotalDiscount(BigDecimal.valueOf(100));

        Customer customer = new Customer();
        customer.setName("John");

        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("ENTRY1");
        cartEntry.setProduct("PROD1");
        cartEntry.setQuantity(BigDecimal.TWO);
        cartEntry.setPrice(BigDecimal.valueOf(100));
        cartEntry.setSellingPrice(BigDecimal.valueOf(90));
        cartEntry.setTotalPrice(BigDecimal.valueOf(180));
        cartEntry.setDiscount(BigDecimal.valueOf(20));

        Order order = new Order();
        order.setIdentifier("9999999999");
        OrderDto dto = new OrderDto();

        Mockito.when(cartRepository.findByIdentifier("9999999999")).thenReturn(cart);
        Mockito.when(orderRepository.findByIdentifier("9999999999")).thenReturn(null);
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(customer);
        Mockito.when(cartEntryRepository.findAllByCart("9999999999")).thenReturn(List.of(cartEntry));
        Mockito.when(orderEntryRepository.findByIdentifier("ENTRY1")).thenReturn(null);
        Mockito.when(orderRepository.findByIdentifier("9999999999")).thenReturn(order);
        Mockito.when(modelMapper.map(Mockito.any(Order.class), Mockito.eq(OrderDto.class))).thenReturn(dto);
        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class))).thenReturn(List.of(new OrderEntryDto()));

        orderService.placeOrder("9999999999");

        Mockito.verify(orderRepository).save(Mockito.any(Order.class));
        Mockito.verify(orderEntryRepository).save(Mockito.any(OrderEntry.class));
        Mockito.verify(cartEntryRepository).deleteAll(Mockito.anyList());
        Mockito.verify(cartService).recalculate("9999999999");
    }

    @Test
    void placeOrderExistingOrderAndExistingEntryTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART1");
        Order existingOrder = new Order();
        Customer customer = new Customer();
        customer.setName("Alex");
        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("ENTRY1");

        OrderEntry existingOrderEntry = new OrderEntry();

        OrderDto dto = new OrderDto();
        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        Mockito.when(orderRepository.findByIdentifier("CART1")).thenReturn(existingOrder);
        Mockito.when(customerRepository.findByIdentifier("CART1")).thenReturn(customer);
        Mockito.when(cartEntryRepository.findAllByCart("CART1")).thenReturn(List.of(cartEntry));
        Mockito.when(orderEntryRepository.findByIdentifier("ENTRY1")).thenReturn(existingOrderEntry);
        Mockito.when(modelMapper.map(Mockito.any(Order.class), Mockito.eq(OrderDto.class))).thenReturn(dto);
        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class))).thenReturn(List.of());

        orderService.placeOrder("CART1");

        Mockito.verify(orderEntryRepository).save(existingOrderEntry);
    }

    @Test
    void cancelOrderWithExistingCartEntryTest() {
        Order order = new Order();
        OrderEntry orderEntry = new OrderEntry();
        orderEntry.setIdentifier("ENTRY1");
        orderEntry.setQuantity(BigDecimal.TWO);
        CartEntry existingCartEntry = new CartEntry();
        existingCartEntry.setQuantity(BigDecimal.ONE);

        OrderDto dto = new OrderDto();

        Mockito.when(orderRepository.findByIdentifier("ORDER1")).thenReturn(order);
        Mockito.when(orderEntryRepository.findAllByOrder("ORDER1")).thenReturn(List.of(orderEntry));
        Mockito.when(cartEntryRepository.findByIdentifier("ENTRY1")).thenReturn(existingCartEntry);
        Mockito.when(modelMapper.map(Mockito.any(Order.class), Mockito.eq(OrderDto.class))).thenReturn(dto);
        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class))).thenReturn(List.of());

        orderService.cancelOrder("ORDER1");

        Assertions.assertEquals(BigDecimal.valueOf(3), existingCartEntry.getQuantity());

        Mockito.verify(cartService).recalculate("ORDER1");
    }

    @Test
    void cancelOrderWithNewCartEntryTest() {
        Order order = new Order();
        OrderEntry orderEntry = new OrderEntry();
        orderEntry.setIdentifier("ENTRY1");
        orderEntry.setProduct("PROD1");
        orderEntry.setQuantity(BigDecimal.TWO);
        OrderDto dto = new OrderDto();

        Mockito.when(orderRepository.findByIdentifier("ORDER2")).thenReturn(order);
        Mockito.when(orderEntryRepository.findAllByOrder("ORDER2")).thenReturn(List.of(orderEntry));
        Mockito.when(cartEntryRepository.findByIdentifier("ENTRY1")).thenReturn(null);
        Mockito.when(modelMapper.map(Mockito.any(Order.class), Mockito.eq(OrderDto.class))).thenReturn(dto);
        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class))).thenReturn(List.of());

        orderService.cancelOrder("ORDER2");

        Mockito.verify(cartEntryRepository).save(Mockito.any(CartEntry.class));
    }

    @Test
    void findByIdentifierTest() {
        Order order = new Order();
        order.setIdentifier("ORD1");
        OrderDto dto = new OrderDto();

        Mockito.when(orderRepository.findByIdentifier("ORD1")).thenReturn(order);
        Mockito.when(modelMapper.map(order, OrderDto.class)).thenReturn(dto);
        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class))).thenReturn(List.of(new OrderEntryDto()));

        OrderDto result = orderService.findByIdentifier("ORD1");

        Assertions.assertNotNull(result);
    }

    @Test
    void findAllByCustomerTest() {
        Order order = new Order();
        order.setIdentifier("ORD1");
        OrderDto dto = new OrderDto();

        Mockito.when(orderRepository.findAllByCustomer("John")).thenReturn(List.of(order));
        Mockito.when(modelMapper.map(order, OrderDto.class)).thenReturn(dto);
        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class))).thenReturn(List.of(new OrderEntryDto()));

        List<OrderDto> result = orderService.findAllByCustomer("John");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void findAllTest() {
        Order order = new Order();
        order.setIdentifier("ORD1");
        OrderDto dto = new OrderDto();

        Mockito.when(orderRepository.findAll()).thenReturn(List.of(order));
        Mockito.when(modelMapper.map(order, OrderDto.class)).thenReturn(dto);
        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class))).thenReturn(List.of(new OrderEntryDto()));

        List<OrderDto> result = orderService.findAll();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void deleteTest() {
        orderService.delete("ORD1");
        Mockito.verify(orderRepository).deleteByIdentifier("ORD1");
    }

    @Test
    void placeOrderWithHyphenIdentifierCustomerNotFoundAndNullTotalsTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART-9999999999");
        cart.setTotalPrice(null);
        cart.setTotalDiscount(null);
        Order savedOrder = new Order();
        savedOrder.setIdentifier("CART-9999999999");

        OrderDto dto = new OrderDto();

        Mockito.when(cartRepository.findByIdentifier("CART-9999999999")).thenReturn(cart);
        Mockito.when(orderRepository.findByIdentifier("CART-9999999999")).thenReturn(null).thenReturn(savedOrder);
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(null);
        Mockito.when(cartEntryRepository.findAllByCart("CART-9999999999")).thenReturn(List.of());
        Mockito.when(modelMapper.map(Mockito.any(Order.class), Mockito.eq(OrderDto.class))).thenReturn(dto);
        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class))).thenReturn(List.of());

        orderService.placeOrder("CART-9999999999");

        Mockito.verify(orderRepository).save(Mockito.argThat(order -> "9999999999".equals(order.getCustomer()) && BigDecimal.ZERO.equals(order.getTotalPrice()) && BigDecimal.ZERO.equals(order.getTotalDiscount())));
        Mockito.verify(cartService).recalculate("CART-9999999999");
    }

    @Test
    void placeOrderExistingOrderWithEmptyCartEntriesTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART1");
        cart.setTotalPrice(BigDecimal.TEN);
        cart.setTotalDiscount(BigDecimal.ONE);
        Order existingOrder = new Order();
        existingOrder.setIdentifier("CART1");
        Customer customer = new Customer();
        customer.setName("Alex");

        OrderDto dto = new OrderDto();

        Mockito.when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        Mockito.when(orderRepository.findByIdentifier("CART1")).thenReturn(existingOrder);
        Mockito.when(customerRepository.findByIdentifier("CART1")).thenReturn(customer);
        Mockito.when(cartEntryRepository.findAllByCart("CART1")).thenReturn(List.of());
        Mockito.when(modelMapper.map(Mockito.any(Order.class), Mockito.eq(OrderDto.class))).thenReturn(dto);
        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class))).thenReturn(List.of());

        orderService.placeOrder("CART1");

        Mockito.verify(orderRepository).save(existingOrder);
        Mockito.verify(cartEntryRepository).deleteAll(List.of());
        Mockito.verify(cartService).recalculate("CART1");
    }

    @Test
    void constructorTest() {
        OrderServiceImpl service = new OrderServiceImpl(
                orderRepository,
                orderEntryRepository,
                cartRepository,
                cartEntryRepository,
                cartService,
                customerRepository,
                modelMapper);
        Assertions.assertNotNull(service);
    }

    @Test
    void cancelOrderMultipleEntriesTest() {
        Order order = new Order();

        OrderEntry e1 = new OrderEntry();
        e1.setIdentifier("E1");
        e1.setQuantity(BigDecimal.ONE);
        OrderEntry e2 = new OrderEntry();
        e2.setIdentifier("E2");
        e2.setQuantity(BigDecimal.TWO);

        Mockito.when(orderRepository.findByIdentifier("ORDER3")).thenReturn(order);
        Mockito.when(orderEntryRepository.findAllByOrder("ORDER3")).thenReturn(List.of(e1, e2));
        Mockito.when(cartEntryRepository.findByIdentifier("E1")).thenReturn(null);

        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.ONE);

        Mockito.when(cartEntryRepository.findByIdentifier("E2")).thenReturn(existing);

        OrderDto dto = new OrderDto();

        Mockito.when(modelMapper.map(Mockito.any(Order.class), Mockito.eq(OrderDto.class))).thenReturn(dto);
        Mockito.when(modelMapper.map(Mockito.any(List.class), Mockito.any(Type.class))).thenReturn(List.of());

        orderService.cancelOrder("ORDER3");

        Mockito.verify(cartEntryRepository, Mockito.times(2)).save(Mockito.any(CartEntry.class));
        Mockito.verify(orderRepository).save(order);
    }

}