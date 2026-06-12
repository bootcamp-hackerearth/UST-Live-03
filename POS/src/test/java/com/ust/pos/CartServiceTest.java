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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CartEntryService cartEntryService;

    @Test
    void saveTest_WhenCartExists() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Cart existingCart = new Cart();

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(existingCart);

        CartDto response = cartService.save(cartDto);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("already exists"));
    }

    @Test
    void saveTest_WhenNewCart() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Cart cart = new Cart();

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(cartDto, Cart.class))
                .thenReturn(cart);

        Mockito.when(cartRepository.save(cart))
                .thenReturn(cart);

        CartDto response = cartService.save(cartDto);

        assertNotNull(response);
        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void recalculateTest() {
        String identifier = "CART1";

        CartEntryDto entry1 = new CartEntryDto();
        entry1.setTotalPrice(new BigDecimal("100"));
        entry1.setDiscount(new BigDecimal("10"));

        CartEntryDto entry2 = new CartEntryDto();
        entry2.setTotalPrice(new BigDecimal("200"));
        entry2.setDiscount(new BigDecimal("20"));

        List<CartEntryDto> entries = Arrays.asList(entry1, entry2);

        Cart cart = new Cart();

        Mockito.when(cartEntryService.findAllEntriesForCart(identifier))
                .thenReturn(entries);

        Mockito.when(cartRepository.findByIdentifier(identifier))
                .thenReturn(cart);

        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(new CartDto());

        CartDto response = cartService.recalculate(identifier);

        assertNotNull(response);
        assertEquals(new BigDecimal("300"), cart.getTotalPrice());
        assertEquals(new BigDecimal("30"), cart.getTotalDiscount());

        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void findByIdentifierTest() {
        String identifier = "CART1";

        Cart cart = new Cart();

        List<CartEntryDto> entries = Arrays.asList(new CartEntryDto());

        Mockito.when(cartRepository.findByIdentifier(identifier))
                .thenReturn(cart);

        Mockito.when(cartEntryService.findAllEntriesForCart(identifier))
                .thenReturn(entries);

        CartDto response = cartService.findByIdentifier(identifier);

        assertNotNull(response);
        assertEquals(entries, response.getCartEntryDtoList());

        Mockito.verify(cartRepository).findByIdentifier(identifier);
        Mockito.verify(cartEntryService).findAllEntriesForCart(identifier);
    }
}
