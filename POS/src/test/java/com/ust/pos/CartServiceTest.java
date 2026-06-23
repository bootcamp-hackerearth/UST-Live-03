package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryService cartEntryService;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setId(1L);
        cart.setIdentifier("CART-001");
        cart.setOriginalPrice(BigDecimal.ZERO);
        cart.setTotalDiscount(BigDecimal.ZERO);
        cart.setTotalPrice(BigDecimal.ZERO);

        cartDto = new CartDto();
        cartDto.setIdentifier("CART-001");
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cartService.save(null));
    }

    @Test
    void testSave_WhenIdentifierIsNull() {
        cartDto.setIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> cartService.save(cartDto));
    }

    @Test
    void testSave_WhenCartAlreadyExists() {
        when(cartRepository.existsByIdentifier("CART-001")).thenReturn(true);

        CartDto result = cartService.save(cartDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_Success() {
        when(cartRepository.existsByIdentifier("CART-001")).thenReturn(false);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDto result = cartService.save(cartDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Cart created successfully", result.getMessage());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testRecalculate_WhenCartNotFound() {
        when(cartRepository.findByIdentifier("CART-001")).thenReturn(null);

        CartDto result = cartService.recalculate("CART-001");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Cart not found"));
    }

    @Test
    void testRecalculate_Success() {
        List<CartEntryDto> entries = new ArrayList<>();

        CartEntryDto entry1 = new CartEntryDto();
        entry1.setTotalPrice(new BigDecimal("100.00"));
        entry1.setDiscount(new BigDecimal("10.00"));

        CartEntryDto entry2 = new CartEntryDto();
        entry2.setTotalPrice(new BigDecimal("50.00"));
        entry2.setDiscount(new BigDecimal("5.00"));

        entries.add(entry1);
        entries.add(entry2);

        when(cartRepository.findByIdentifier("CART-001")).thenReturn(cart);
        when(cartEntryService.findAllEntriesForCart("CART-001")).thenReturn(entries);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDto result = cartService.recalculate("CART-001");

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Cart recalculated successfully", result.getMessage());
        assertNotNull(result.getEntryList());
        assertEquals(2, result.getEntryList().size());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testDeleteByIdentifier() {
        doNothing().when(cartRepository).deleteByIdentifier("CART-001");
        doNothing().when(cartEntryService).deleteAllByCart("CART-001");

        cartService.deleteByIdentifier("CART-001");

        verify(cartRepository, times(1)).deleteByIdentifier("CART-001");
        verify(cartEntryService, times(1)).deleteAllByCart("CART-001");
    }

    @Test
    void testFindByIdentifier_WhenCartDoesNotExist() {
        when(cartRepository.findByIdentifier("CART-001")).thenReturn(null);
        when(cartRepository.existsByIdentifier("CART-001")).thenReturn(false);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDto result = cartService.findByIdentifier("CART-001");

        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testFindByIdentifier_WhenCartExists() {
        List<CartEntryDto> entries = new ArrayList<>();
        when(cartRepository.findByIdentifier("CART-001")).thenReturn(cart);
        when(cartEntryService.findAllEntriesForCart("CART-001")).thenReturn(entries);

        CartDto result = cartService.findByIdentifier("CART-001");

        assertNotNull(result);
        assertEquals("CART-001", result.getIdentifier());
        assertNotNull(result.getEntryList());
        verify(cartRepository, never()).save(any(Cart.class));
    }
}