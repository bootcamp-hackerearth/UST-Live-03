package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private ModelMapper modelMapper;

    @Test
    void saveNewCartTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");
        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        when(cartRepository.findByIdentifier("CART001")).thenReturn(null);
        when(modelMapper.map(dto, Cart.class)).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(dto);

        CartDto result = cartService.save(dto);

        Assertions.assertEquals("CART001", result.getIdentifier());
        Assertions.assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
        Assertions.assertEquals(BigDecimal.ZERO, cart.getTotalDiscount());

        verify(cartRepository).save(cart);
    }

    @Test
    void saveNewCartWithExistingTotalsTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART002");
        Cart cart = new Cart();
        cart.setIdentifier("CART002");
        cart.setTotalPrice(new BigDecimal("100"));
        cart.setTotalDiscount(new BigDecimal("20"));

        when(cartRepository.findByIdentifier("CART002")).thenReturn(null);
        when(modelMapper.map(dto, Cart.class)).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(dto);

        CartDto result = cartService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(new BigDecimal("100"), cart.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("20"), cart.getTotalDiscount());

        verify(cartRepository).save(cart);
    }

    @Test
    void saveExistingCartTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        when(cartRepository.findByIdentifier("CART001")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(dto);
        when(modelMapper.map(Mockito.anyList(), Mockito.any(Type.class))).thenReturn(List.of());
        when(cartEntryRepository.findAllByCart("CART001")).thenReturn(List.of());

        CartDto result = cartService.save(dto);

        Assertions.assertNotNull(result);

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void recalculateTest() {
        CartEntry e1 = new CartEntry();
        e1.setTotalPrice(new BigDecimal("100"));
        e1.setDiscount(new BigDecimal("10"));
        CartEntry e2 = new CartEntry();
        e2.setTotalPrice(new BigDecimal("200"));
        e2.setDiscount(new BigDecimal("20"));

        List<CartEntry> entries = List.of(e1, e2);

        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        when(cartEntryRepository.findAllByCart("CART001")).thenReturn(entries);
        when(cartRepository.findByIdentifier("CART001")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(dto);
        when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class))).thenReturn(List.of());

        CartDto result = cartService.recalculate("CART001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(new BigDecimal("300"), cart.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("30"), cart.getTotalDiscount());

        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void recalculateCreatesCartWhenMissingTest() {
        when(cartEntryRepository.findAllByCart("NEW_CART")).thenReturn(List.of());
        when(cartRepository.findByIdentifier("NEW_CART")).thenReturn(null);

        CartDto dto = new CartDto();

        when(modelMapper.map(any(Cart.class), eq(CartDto.class))).thenReturn(dto);
        when(modelMapper.map(Mockito.anyList(), Mockito.any(Type.class))).thenReturn(List.of());

        CartDto result = cartService.recalculate("NEW_CART");

        Assertions.assertNotNull(result);

        verify(cartRepository, atLeastOnce()).save(any(Cart.class));
    }

    @Test
    void findByIdentifierExistingCartTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        when(cartRepository.findByIdentifier("CART001")).thenReturn(cart);
        when(modelMapper.map(cart, CartDto.class)).thenReturn(dto);
        when(cartEntryRepository.findAllByCart("CART001")).thenReturn(List.of());
        when(modelMapper.map(Mockito.anyList(), Mockito.any(Type.class))).thenReturn(List.of());

        CartDto result = cartService.findByIdentifier("CART001");

        Assertions.assertNotNull(result);

        verify(cartRepository).findByIdentifier("CART001");
    }

    @Test
    void findByIdentifierCreatesCartWhenMissingTest() {
        when(cartRepository.findByIdentifier("NEW_CART")).thenReturn(null);

        CartDto dto = new CartDto();

        when(modelMapper.map(any(Cart.class), eq(CartDto.class))).thenReturn(dto);
        when(cartEntryRepository.findAllByCart("NEW_CART")).thenReturn(List.of());
        when(modelMapper.map(Mockito.anyList(), Mockito.any(Type.class))).thenReturn(List.of());

        CartDto result = cartService.findByIdentifier("NEW_CART");

        Assertions.assertNotNull(result);

        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void deleteTest() {
        cartService.delete("CART001");
        verify(cartRepository).deleteByIdentifier("CART001");
    }
}