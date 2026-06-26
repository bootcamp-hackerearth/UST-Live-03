package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    private CartDto cartDto;
    private Cart cart;

    @BeforeEach
    void setUp() {
        cartDto = new CartDto();
        cartDto.setIdentifier("CART-01");

        cart = new Cart();
        cart.setIdentifier("CART-01");
    }

    @Test
    @DisplayName("Save Cart - Success")
    void save_Success() {
        when(cartRepository.findByIdentifier("CART-01")).thenReturn(null);
        when(modelMapper.map(cartDto, Cart.class)).thenReturn(cart);

        CartDto result = cartService.save(cartDto);

        Assertions.assertNotNull(result);
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("Save Cart - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        when(cartRepository.findByIdentifier("CART-01")).thenReturn(cart);

        CartDto result = cartService.save(cartDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("Recalculate Cart - Success")
    void recalculate_Success() {
        CartEntryDto entry1 = new CartEntryDto();
        entry1.setTotalPrice(BigDecimal.valueOf(100));
        entry1.setDiscount(BigDecimal.valueOf(10));

        CartEntryDto entry2 = new CartEntryDto();
        entry2.setTotalPrice(BigDecimal.valueOf(200));
        entry2.setDiscount(BigDecimal.valueOf(20));

        List<CartEntryDto> entries = List.of(entry1, entry2);

        when(cartEntryService.findAllEntriesForCart("CART-01")).thenReturn(entries);
        when(cartRepository.findByIdentifier("CART-01")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        when(modelMapper.map(eq(entries), any(Type.class))).thenReturn(entries);

        CartDto result = cartService.recalculate("CART-01");

        Assertions.assertEquals(BigDecimal.valueOf(300), cart.getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(30), cart.getTotalDiscount());
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        List<CartEntryDto> entries = List.of(new CartEntryDto());
        when(cartRepository.findByIdentifier("CART-01")).thenReturn(cart);
        when(cartEntryService.findAllEntriesForCart("CART-01")).thenReturn(entries);

        CartDto result = cartService.findByIdentifier("CART-01");

        Assertions.assertNotNull(result);
        verify(modelMapper).map(cart, result);
    }

    @Test
    @DisplayName("Delete By Identifier - Success")
    void deleteByIdentifier_Success() {
        cartService.deleteByIdentifier("CART-01");

        verify(cartRepository).deleteByIdentifier("CART-01");
        verify(cartEntryService).deleteAllByCart("CART-01");
    }
}