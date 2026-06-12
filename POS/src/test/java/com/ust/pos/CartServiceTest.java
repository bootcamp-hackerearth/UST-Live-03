package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartEntryService cartEntryService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private CartDto cartDto;
    private Cart cart;

    @BeforeEach
    void setUp() {
        cartDto = new CartDto();
        cartDto.setIdentifier("cart123");

        cart = new Cart();
        cart.setIdentifier("cart123");
    }

    @Test
    void save_whenCartAlreadyExists_shouldReturnFailure() {
        when(cartRepository.existsByIdentifier("cart123")).thenReturn(true);

        CartDto result = cartService.save(cartDto);

        assertFalse(result.isSuccess());
        assertEquals("Already exists", result.getMessage());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void save_whenCartDoesNotExist_shouldSaveCart() {
        when(cartRepository.existsByIdentifier("cart123")).thenReturn(false);
        when(modelMapper.map(cartDto, Cart.class)).thenReturn(cart);

        CartDto result = cartService.save(cartDto);

        verify(cartRepository).save(cart);
        assertEquals(cartDto, result);
    }

    @Test
    void recalculate_shouldCalculateTotalPriceAndDiscount() {
        CartEntryDto entry1 = new CartEntryDto();
        entry1.setTotalPrice(BigDecimal.valueOf(100));
        entry1.setDiscount(BigDecimal.valueOf(10));

        CartEntryDto entry2 = new CartEntryDto();
        entry2.setTotalPrice(BigDecimal.valueOf(200));
        entry2.setDiscount(BigDecimal.valueOf(20));

        List<CartEntryDto> entries = Arrays.asList(entry1, entry2);

        CartDto mappedCartDto = new CartDto();
        List<CartDto> mappedList = List.of(new CartDto(), new CartDto());

        when(cartEntryService.findAllCarts("cart123")).thenReturn(entries);
        when(cartRepository.findByIdentifier("cart123")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(mappedCartDto);


        when(modelMapper.map(eq(entries), any(Type.class)))
                .thenReturn(mappedList);

        CartDto result = cartService.recalculate("cart123");

        assertEquals(BigDecimal.valueOf(300), cart.getTotalPrice());
        assertEquals(BigDecimal.valueOf(30), cart.getTotalDiscount());

        verify(cartRepository).save(cart);
        assertNotNull(result);
    }

    @Test
    void findByIdentifier_shouldReturnCartWithEntries() {
        List<CartEntryDto> entries = List.of(new CartEntryDto());

        when(cartRepository.findByIdentifier("cart123")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        when(cartEntryService.findAllCarts("cart123")).thenReturn(entries);

        CartDto result = cartService.findByIdentifier("cart123");

        assertNotNull(result);
        assertEquals(entries, result.getEntryDtoList());
    }

    @Test
    void deleteByIdentifier_shouldDeleteCartAndEntries() {
        cartService.deleteByIdentifier("cart123");

        verify(cartRepository).deleteByIdentifier("cart123");
        verify(cartEntryService).deleteAllByCart("cart123");
    }
}