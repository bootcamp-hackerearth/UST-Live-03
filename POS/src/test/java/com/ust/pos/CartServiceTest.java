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
        cartDto.setIdentifier("CRT-001");

        cart = new Cart();
        cart.setIdentifier("CRT-001");
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setTotalDiscount(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Save Cart - Success")
    void save_Success() {
        when(cartRepository.findByIdentifier("CRT-001")).thenReturn(null);
        when(modelMapper.map(cartDto, Cart.class)).thenReturn(cart);

        CartDto result = cartService.save(cartDto);

        Assertions.assertNotNull(result);
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("Save Cart - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        when(cartRepository.findByIdentifier("CRT-001")).thenReturn(cart);

        CartDto result = cartService.save(cartDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Recalculate Cart - Success")
    void recalculate_Success() {
        CartEntryDto entry1 = new CartEntryDto();
        entry1.setTotalPrice(new BigDecimal("100.00"));
        entry1.setDiscount(new BigDecimal("10.00"));

        CartEntryDto entry2 = new CartEntryDto();
        entry2.setTotalPrice(new BigDecimal("50.00"));
        entry2.setDiscount(new BigDecimal("5.00"));

        List<CartEntryDto> entries = List.of(entry1, entry2);

        when(cartEntryService.findAllEntriesForCart("CRT-001")).thenReturn(entries);
        when(cartRepository.findByIdentifier("CRT-001")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        when(modelMapper.map(eq(entries), any(Type.class))).thenReturn(entries);

        CartDto result = cartService.recalculate("CRT-001");

        Assertions.assertEquals(new BigDecimal("150.00"), cart.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("15.00"), cart.getTotalDiscount());
        Assertions.assertNotNull(result.getCartEntryDtoList());
        Assertions.assertEquals(2, result.getCartEntryDtoList().size());
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        CartEntryDto entryDto = new CartEntryDto();
        List<CartEntryDto> entries = List.of(entryDto);

        when(cartRepository.findByIdentifier("CRT-001")).thenReturn(cart);
        when(cartEntryService.findAllEntriesForCart("CRT-001")).thenReturn(entries);

        CartDto result = cartService.findByIdentifier("CRT-001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getCartEntryDtoList().size());
        verify(modelMapper).map(cart, result);
    }

    @Test
    @DisplayName("Delete By Identifier - Success")
    void deleteByIdentifier_Success() {
        doNothing().when(cartRepository).deleteByIdentifier("CRT-001");
        doNothing().when(cartEntryService).deleteAllByCart("CRT-001");

        cartService.deleteByIdentifier("CRT-001");

        verify(cartRepository).deleteByIdentifier("CRT-001");
        verify(cartEntryService).deleteAllByCart("CRT-001");
    }
}