package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryService cartEntryService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void testSave() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        Cart cart = new Cart();

        when(cartRepository.existsByIdentifier("CART1")).thenReturn(false);
        when(modelMapper.map(dto, Cart.class)).thenReturn(cart);

        CartDto result = cartService.save(dto);

        verify(cartRepository).save(cart);
        assertEquals("CART1", result.getIdentifier());
    }

    @Test
    void testRecalculate() {

        CartEntryDto entry = new CartEntryDto();
        entry.setTotalPrice(BigDecimal.valueOf(100));
        entry.setDiscount(BigDecimal.valueOf(10));

        Cart cart = new Cart();

        when(cartEntryService.findAllCarts("CART1"))
                .thenReturn(Collections.singletonList(entry));
        when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(new CartDto());

        CartDto result = cartService.recalculate("CART1");

        assertEquals(BigDecimal.valueOf(100), cart.getTotalPrice());
        assertEquals(BigDecimal.valueOf(10), cart.getTotalDiscount());

        verify(cartRepository).save(cart);
        assertNotNull(result);
    }

    @Test
    void testFindByIdentifier() {

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(dto);
        when(cartEntryService.findAllCarts("CART1"))
                .thenReturn(Collections.emptyList());

        CartDto result = cartService.findByIdentifier("CART1");

        assertEquals("CART1", result.getIdentifier());
        assertNotNull(result.getEntryDtoList());
    }

    @Test
    void testDeleteByIdentifier() {

        cartService.deleteByIdentifier("CART1");

        verify(cartRepository).deleteByIdentifier("CART1");
        verify(cartEntryService).deleteAllByCart("CART1");
    }

    @Test
    void testFindActiveStatus() {
        // 1. Arrange: Create Cart data instead of Brand data
        Cart active = new Cart();
        active.setStatus(true);

        Cart inactive = new Cart();
        inactive.setStatus(false);

        when(cartRepository.findAll())
                .thenReturn(List.of(active, inactive));

        CartDto dto = new CartDto();
        List<CartDto> expectedDtoList = List.of(dto);

        when(modelMapper.map(
                Mockito.eq(List.of(active)),
                Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(expectedDtoList);

        List<CartDto> result = cartService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
