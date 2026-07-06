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
    void saveTestSuccess() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART01");

        Mockito.when(cartRepository.existsByIdentifier("CART01")).thenReturn(false);
        Cart cart = new Cart();
        Mockito.when(modelMapper.map(cartDto, Cart.class)).thenReturn(cart);
        Mockito.when(cartRepository.save(cart)).thenReturn(cart);

        CartDto response = cartService.save(cartDto);

        Assertions.assertEquals("CART01", response.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART01");

        Mockito.when(cartRepository.existsByIdentifier("CART01")).thenReturn(true);

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
        entry2.setTotalPrice(new BigDecimal("50.00"));
        entry2.setDiscount(new BigDecimal("5.00"));

        List<CartEntryDto> cartEntries = List.of(entry1, entry2);
        Cart cartModel = new Cart();

        CartDto cartDto = new CartDto();

        Mockito.when(cartEntryService.findAllCarts("CART01")).thenReturn(cartEntries);
        Mockito.when(cartRepository.findByIdentifier("CART01")).thenReturn(cartModel);
        Mockito.when(cartRepository.save(cartModel)).thenReturn(cartModel);
        Mockito.when(modelMapper.map(cartModel, CartDto.class)).thenReturn(cartDto);
        Mockito.when(modelMapper.map(Mockito.eq(cartEntries), Mockito.any(java.lang.reflect.Type.class))).thenReturn(cartEntries);

        CartDto response = cartService.recalculate("CART01");

        Assertions.assertEquals(new BigDecimal("15.00"), cartModel.getTotalDiscount());
        Assertions.assertEquals(new BigDecimal("150.00"), cartModel.getTotalPrice());
        Assertions.assertEquals(cartEntries, response.getEntryDtoList());
    }

    @Test
    void findByIdentifierTest() {
        Cart cart = new Cart();
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART01");
        List<CartEntryDto> cartEntries = List.of(new CartEntryDto());

        Mockito.when(cartRepository.findByIdentifier("CART01")).thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        Mockito.when(cartEntryService.findAllCarts("CART01")).thenReturn(cartEntries);

        CartDto response = cartService.findByIdentifier("CART01");

        Assertions.assertEquals("CART01", response.getIdentifier());
        Assertions.assertEquals(cartEntries, response.getEntryDtoList());
    }

    @Test
    void deleteTest() {
        boolean response = cartService.delete("CART01");

        Assertions.assertTrue(response);
        Mockito.verify(cartRepository, Mockito.times(1)).deleteByIdentifier("CART01");
        Mockito.verify(cartEntryService, Mockito.times(1)).deleteAllByCart("CART01");
    }
}