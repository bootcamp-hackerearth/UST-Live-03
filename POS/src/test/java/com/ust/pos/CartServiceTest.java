package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
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
    void saveTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART001");

        Mockito.when(cartRepository.existsByIdentifier("CART001")).thenReturn(false);
        Cart cart = new Cart();
        Mockito.when(modelMapper.map(cartDto, Cart.class)).thenReturn(cart);
        Mockito.when(cartRepository.save(cart)).thenReturn(cart);

        CartDto response = cartService.save(cartDto);

        Assertions.assertEquals("CART001", response.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART001");

        Mockito.when(cartRepository.existsByIdentifier("CART001")).thenReturn(true);

        CartDto response = cartService.save(cartDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Already exists", response.getMessage());
    }

    @Test
    void recalculateTest() {
        CartEntryDto entry1 = new CartEntryDto();
        entry1.setTotalPrice(new BigDecimal("100.00"));
        entry1.setDiscount(new BigDecimal("10.00"));

        CartEntryDto entry2 = new CartEntryDto();
        entry2.setTotalPrice(new BigDecimal("200.00"));
        entry2.setDiscount(new BigDecimal("20.00"));

        List<CartEntryDto> entries = List.of(entry1, entry2);

        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART001");

        Mockito.when(cartEntryService.findAllCarts("CART001")).thenReturn(entries);
        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(cart);
        Mockito.when(cartRepository.save(cart)).thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);

        CartDto response = cartService.recalculate("CART001");

        Assertions.assertEquals("CART001", response.getIdentifier());
        Assertions.assertEquals(new BigDecimal("300.00"), cart.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("30.00"), cart.getTotalDiscount());
    }

    @Test
    void findByIdentifierTest() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART001");

        CartEntryDto entry = new CartEntryDto();
        List<CartEntryDto> entries = List.of(entry);

        Mockito.when(cartRepository.findByIdentifier("CART001")).thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        Mockito.when(cartEntryService.findAllCarts("CART001")).thenReturn(entries);

        CartDto response = cartService.findByIdentifier("CART001");

        Assertions.assertEquals("CART001", response.getIdentifier());
        Assertions.assertEquals(1, response.getEntryDtoList().size());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(cartRepository).deleteByIdentifier("CART001");
        Mockito.when(cartEntryService.deleteAllByCart("CART001")).thenReturn(true);

        boolean response = cartService.delete("CART001");

        Assertions.assertTrue(response);
    }
}