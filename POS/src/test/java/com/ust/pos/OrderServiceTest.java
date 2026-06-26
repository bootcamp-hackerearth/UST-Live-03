package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.OrdersDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.model.OrderEntry;
import com.ust.pos.model.OrderEntryRepository;
import com.ust.pos.model.Orders;
import com.ust.pos.model.OrdersRepository;
import com.ust.pos.orders.impl.OrderServiceImpl;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OrderEntryRepository orderEntryRepository;

    @Mock
    private CartEntryService cartEntryService;

    @Mock
    private CartService cartService;

    @Mock
    private StockServiceImpl stockService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccessTest() {

        Orders orders = new Orders();
        orders.setIdentifier("ORD001");

        OrdersDto dto = new OrdersDto();
        dto.setIdentifier("ORD001");

        when(ordersRepository.findByIdentifier("ORD001"))
                .thenReturn(orders);

        when(modelMapper.map(orders, OrdersDto.class))
                .thenReturn(dto);

        OrdersDto response =
                orderService.findByIdentifier("ORD001");

        assertNotNull(response);
        assertEquals("ORD001", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        when(ordersRepository.findByIdentifier("ORD001"))
                .thenReturn(null);

        OrdersDto response =
                orderService.findByIdentifier("ORD001");

        assertNull(response);
    }

    @Test
    void saveTest() {

        OrdersDto ordersDto = new OrdersDto();
        ordersDto.setCustomerId("CUST001");

        CartEntryDto cartEntryDto = new CartEntryDto();

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CUST001");

        Orders order = new Orders();

        when(cartEntryService.findByCartId("CUST001"))
                .thenReturn(List.of(cartEntryDto));

        when(modelMapper.map(any(CartEntryDto.class),
                eq(OrderEntry.class)))
                .thenAnswer(invocation -> {
                    OrderEntry orderEntry = new OrderEntry();
                    orderEntry.setQuantity(java.math.BigDecimal.valueOf(2));
                    return orderEntry;
                });

        when(cartService.findByIdentifier("CUST001"))
                .thenReturn(cartDto);

        when(stockService.updateQuantity(any(StockDto.class)))
                .thenReturn(new StockDto());

        doNothing().when(modelMapper)
                .map(any(CartDto.class), any(OrdersDto.class));

        when(modelMapper.map(any(OrdersDto.class),
                eq(Orders.class)))
                .thenReturn(order);

        OrdersDto response = orderService.save(ordersDto);

        assertNotNull(response);
        assertNotNull(response.getIdentifier());
        assertTrue(response.getIdentifier().startsWith("ORD-"));

        verify(cartEntryService)
                .findByCartId("CUST001");

        verify(stockService)
                .updateQuantity(any(StockDto.class));

        verify(orderEntryRepository)
                .save(any(OrderEntry.class));

        verify(ordersRepository)
                .save(any(Orders.class));

        verify(cartEntryService)
                .deleteAllByCartId("CUST001");
    }

    @Test
    void saveTest_MultipleCartEntries() {

        OrdersDto ordersDto = new OrdersDto();
        ordersDto.setCustomerId("CUST001");

        CartEntryDto entry1 = new CartEntryDto();
        CartEntryDto entry2 = new CartEntryDto();

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CUST001");

        when(cartEntryService.findByCartId("CUST001"))
                .thenReturn(List.of(entry1, entry2));

        when(modelMapper.map(any(CartEntryDto.class),
                eq(OrderEntry.class)))
                .thenAnswer(invocation -> {
                    OrderEntry orderEntry = new OrderEntry();
                    orderEntry.setQuantity(java.math.BigDecimal.valueOf(1));
                    return orderEntry;
                });

        when(cartService.findByIdentifier("CUST001"))
                .thenReturn(cartDto);

        when(stockService.updateQuantity(any(StockDto.class)))
                .thenReturn(new StockDto());

        doNothing().when(modelMapper)
                .map(any(CartDto.class), any(OrdersDto.class));

        when(modelMapper.map(any(OrdersDto.class),
                eq(Orders.class)))
                .thenReturn(new Orders());

        OrdersDto response = orderService.save(ordersDto);

        assertNotNull(response);

        verify(stockService, times(2))
                .updateQuantity(any(StockDto.class));

        verify(orderEntryRepository, times(2))
                .save(any(OrderEntry.class));

        verify(ordersRepository)
                .save(any(Orders.class));

        verify(cartEntryService)
                .deleteAllByCartId("CUST001");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Orders order = new Orders();
        OrdersDto dto = new OrdersDto();

        Page<Orders> page =
                new PageImpl<>(List.of(order), pageable, 1);

        when(ordersRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(order, OrdersDto.class))
                .thenReturn(dto);

        List<OrdersDto> result =
                orderService.findAll(pageable).getContent();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(ordersRepository)
                .findAll(pageable);

        verify(modelMapper)
                .map(order, OrdersDto.class);
    }
}