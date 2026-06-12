package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CartEntryService cartEntryService;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void save_success() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        when(cartRepository.existsByIdentifier("CART1")).thenReturn(false);
        when(modelMapper.map(dto, Cart.class)).thenReturn(new Cart());

        CartDto result = cartService.save(dto);

        assertTrue(result.isSuccess());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void save_failure_alreadyExists() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        when(cartRepository.existsByIdentifier("CART1")).thenReturn(true);

        CartDto result = cartService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Already exists", result.getMessage());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void recalculate_success() {
        CartEntryDto entry1 = new CartEntryDto();
        entry1.setTotalPrice(new BigDecimal("200"));
        entry1.setDiscount(new BigDecimal("20"));

        CartEntryDto entry2 = new CartEntryDto();
        entry2.setTotalPrice(new BigDecimal("100"));
        entry2.setDiscount(new BigDecimal("10"));

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        when(cartEntryService.findAllEntriesForCart("CART1")).thenReturn(List.of(entry1, entry2));
        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        when(modelMapper.map(Mockito.eq(List.of(entry1, entry2)), Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(List.of(entry1, entry2));

        CartDto result = cartService.recalculate("CART1");
        assertEquals(new BigDecimal("270"), cart.getTotalPrice());
        assertEquals(new BigDecimal("30"), cart.getTotalDiscount());
        assertEquals(new BigDecimal("300"), cart.getOriginalPrice());
        assertEquals(2, result.getEntryList().size());
        verify(cartRepository).save(cart);
    }

    @Test
    void recalculate_emptyCart() {
        Cart cart = new Cart();
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        when(cartEntryService.findAllEntriesForCart("CART1")).thenReturn(List.of());
        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(List.of());

        cartService.recalculate("CART1");

        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
        assertEquals(BigDecimal.ZERO, cart.getTotalDiscount());
        assertEquals(BigDecimal.ZERO, cart.getOriginalPrice());
        verify(cartRepository).save(cart);
    }

    @Test
    void deleteByIdentifier_success() {
        cartService.deleteByIdentifier("CART1");

        verify(cartRepository).deleteByIdentifier("CART1");
        verify(cartEntryService).deleteAllByCart("CART1");
    }

    @Test
    void findByIdentifier_success() {
        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        List<CartEntryDto> entries = List.of(new CartEntryDto());

        when(cartRepository.findByIdentifier("CART1")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        when(cartEntryService.findAllEntriesForCart("CART1")).thenReturn(entries);

        CartDto result = cartService.findByIdentifier("CART1");

        assertEquals("CART1", result.getIdentifier());
        assertEquals(1, result.getEntryList().size());
    }
}