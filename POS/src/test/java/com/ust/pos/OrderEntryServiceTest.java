package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartEntry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.OrdersDto;
import com.ust.pos.model.*;
import com.ust.pos.orderentry.service.impl.OrderEntryServiceImpl;
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
class OrderEntryServiceTest {

    @InjectMocks
    private OrderEntryServiceImpl orderEntryService;

    @Mock
    private CartEntryService cartEntryService;

    @Mock
    private CartService cartService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private OrderEntryReopsitory orderEntryReopsitory;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    void saveSuccessTest() {
        OrdersDto ordersDto = new OrdersDto();
        ordersDto.setCustomerId("CART12345");
        ordersDto.setSuccess(true);

        CartEntryDto cartEntryDto = new CartEntryDto();
        cartEntryDto.setProduct("PROD01");
        List<CartEntryDto> cartEntries = List.of(cartEntryDto);

        OrderEntry orderEntry = new OrderEntry();
        Product product = new Product();
        product.setName("Premium Laptop");

        CartDto cartDto = new CartDto();
        Orders order = new Orders();

        Mockito.when(cartEntryService.findByCartId("CART12345")).thenReturn(cartEntries);
        Mockito.when(productRepository.findByIdentifier("PROD01")).thenReturn(product);
        Mockito.when(cartService.findByIdentifier("CART12345")).thenReturn(cartDto);

        Mockito.when(modelMapper.map(Mockito.any(CartEntryDto.class), Mockito.eq(OrderEntry.class))).thenReturn(orderEntry);

        Mockito.doNothing().when(modelMapper).map(Mockito.any(CartDto.class), Mockito.any(OrdersDto.class));

        Mockito.when(modelMapper.map(Mockito.any(OrdersDto.class), Mockito.eq(Orders.class))).thenReturn(order);

        OrdersDto result = orderEntryService.save(ordersDto);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertNotNull(result.getIdentifier());
        Assertions.assertTrue(result.getIdentifier().startsWith("ORD-"));

        verify(orderEntryReopsitory).save(orderEntry);
        verify(ordersRepository).save(order);
        verify(cartEntryService).deleteAllByCartId("CART12345");
    }


    @Test
    void findByOrderIdSuccessTest() {
        OrderEntry entry = new OrderEntry();
        List<OrderEntry> mockEntries = List.of(entry);

        OrderEntryDto entryDto = new OrderEntryDto();
        List<OrderEntryDto> mockDtos = List.of(entryDto);

        Mockito.when(orderEntryReopsitory.findAllByOrderId("ORD-123")).thenReturn(mockEntries);
        Mockito.when(modelMapper.map(Mockito.eq(mockEntries), Mockito.any(Type.class))).thenReturn(mockDtos);

        List<OrderEntryDto> result = orderEntryService.findByOrderId("ORD-123");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        verify(orderEntryReopsitory).findAllByOrderId("ORD-123");
    }

    @Test
    void findByOrderIdEmptyTest() {
        Mockito.when(orderEntryReopsitory.findAllByOrderId("ORD-EMPTY")).thenReturn(Collections.emptyList());
        Mockito.when(modelMapper.map(Mockito.eq(Collections.emptyList()), Mockito.any(Type.class))).thenReturn(Collections.emptyList());

        List<OrderEntryDto> result = orderEntryService.findByOrderId("ORD-EMPTY");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }
}