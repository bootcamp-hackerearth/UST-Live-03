package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private CartEntryService cartEntryService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest_Success() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART001");

        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        when(cartRepository.findByIdentifier("CART001")).thenReturn(null);
        when(modelMapper.map(cartDto, Cart.class)).thenReturn(cart);
        when(cartRepository.save(cart)).thenReturn(cart);

        CartDto result = cartService.save(cartDto);

        Assertions.assertEquals("CART001", result.getIdentifier());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void saveTest_Failure_CartAlreadyExists() {
        Cart existingCart = new Cart();
        existingCart.setIdentifier("CART001");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART001");

        when(cartRepository.findByIdentifier("CART001")).thenReturn(existingCart);

        CartDto result = cartService.save(cartDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(cartRepository, never()).save(any());
    }

    @Test
    void recalculateTest() {
        CartEntry entry1 = new CartEntry();
        entry1.setTotalPrice(new BigDecimal("100.00"));
        entry1.setDiscount(new BigDecimal("10.00"));

        CartEntry entry2 = new CartEntry();
        entry2.setTotalPrice(new BigDecimal("200.00"));
        entry2.setDiscount(new BigDecimal("20.00"));

        List<CartEntry> cartEntries = List.of(entry1, entry2);

        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART001");

        when(cartEntryRepository.findAllByCart("CART001")).thenReturn(cartEntries);
        when(cartRepository.findByIdentifier("CART001")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        when(modelMapper.map(Mockito.eq(cartEntries), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of());

        CartDto result = cartService.recalculate("CART001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(new BigDecimal("300.00"), cart.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("30.00"), cart.getTotalDiscount());

        verify(cartRepository, times(1)).save(cart);
        verify(modelMapper, times(1)).map(cart, CartDto.class);
    }

    @Test
    void findByIdentifierTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART001");

        List<CartEntry> cartEntries = List.of();

        when(cartRepository.findByIdentifier("CART001")).thenReturn(cart);
        when(cartEntryRepository.findAllByCart("CART001")).thenReturn(cartEntries);
        doNothing().when(modelMapper).map(any(), any(CartDto.class));
        when(modelMapper.map(Mockito.eq(cartEntries), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of());

        CartDto result = cartService.findByIdentifier("CART001");

        Assertions.assertNotNull(result);
        verify(cartRepository, times(1)).findByIdentifier("CART001");
        verify(cartEntryRepository, times(1)).findAllByCart("CART001");
    }
}