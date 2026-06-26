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
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartEntryService cartEntryService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private CartDto cartDto;
    private CartEntryDto cartEntryDto;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setIdentifier("C1");

        cartDto = new CartDto();
        cartDto.setIdentifier("C1");

        cartEntryDto = new CartEntryDto();
        cartEntryDto.setTotalPrice(BigDecimal.valueOf(100));
        cartEntryDto.setDiscount(BigDecimal.valueOf(10));
    }

    @Test
    void testSave_NewCart() {
        when(cartRepository.existsByIdentifier("C1")).thenReturn(false);
        when(modelMapper.map(cartDto, Cart.class)).thenReturn(cart);

        CartDto result = cartService.save(cartDto);

        assertNotNull(result);
        verify(cartRepository).save(cart);
    }

    @Test
    void testSave_AlreadyExists() {
        when(cartRepository.existsByIdentifier("C1")).thenReturn(true);

        CartDto result = cartService.save(cartDto);

        assertFalse(result.isSuccess());
        assertEquals("Already exists", result.getMessage());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void testRecalculate() {
        List<CartEntryDto> entries = List.of(cartEntryDto);

        when(cartEntryService.findAllCarts("C1")).thenReturn(entries);
        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        when(modelMapper.map(eq(entries), any(Type.class)))
                .thenReturn(Collections.singletonList(cartEntryDto));

        CartDto result = cartService.recalculate("C1");

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100), cart.getTotalPrice());
        assertEquals(BigDecimal.valueOf(10), cart.getTotalDiscount());

        verify(cartRepository).save(cart);
    }

    @Test
    void testFindByIdentifier() {
        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        when(cartEntryService.findAllCarts("C1"))
                .thenReturn(Collections.singletonList(cartEntryDto));

        CartDto result = cartService.findByIdentifier("C1");

        assertNotNull(result);
        assertEquals(1, result.getEntryDtoList().size());
    }

    @Test
    void testFindActiveStatus() {
        cart.setStatus(true);
        Cart inactive = new Cart();
        inactive.setStatus(false);

        List<Cart> carts = List.of(cart, inactive);

        when(cartRepository.findAll()).thenReturn(carts);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(cartDto));

        List<CartDto> result = cartService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteByIdentifier() {
        cartService.deleteByIdentifier("C1");

        verify(cartRepository).deleteByIdentifier("C1");
        verify(cartEntryService).deleteAllByCart("C1");
    }
}
