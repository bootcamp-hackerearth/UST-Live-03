package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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
    @DisplayName("Save Cart - Success")
    void saveTest_Success() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART_01");

        Cart cartEntity = new Cart();

        Mockito.when(cartRepository.findByIdentifier("CART_01")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Cart.class)).thenReturn(cartEntity);

        CartDto response = cartService.save(dto);

        Assertions.assertNotNull(response);
        Mockito.verify(cartRepository).save(cartEntity);
    }

    @Test
    @DisplayName("Save Cart - Already Exists")
    void saveTest_AlreadyExists() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART_01");

        Mockito.when(cartRepository.findByIdentifier("CART_01")).thenReturn(new Cart());

        CartDto response = cartService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cart with identifier - CART_01 already exists", response.getMessage());
        Mockito.verify(cartRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Recalculate Cart - Success with Entries")
    void recalculateTest_WithEntries() {
        String cartId = "CART_01";

        CartEntryDto entry1 = new CartEntryDto();
        entry1.setTotalPrice(new BigDecimal("100.00"));
        entry1.setDiscount(new BigDecimal("10.00"));

        CartEntryDto entry2 = new CartEntryDto();
        entry2.setTotalPrice(new BigDecimal("50.00"));
        entry2.setDiscount(new BigDecimal("5.00"));

        List<CartEntryDto> entries = List.of(entry1, entry2);
        Cart cartModel = new Cart();

        CartDto expectedDto = new CartDto();

        Mockito.when(cartEntryService.findAllEntriesForCart(cartId)).thenReturn(entries);
        Mockito.when(cartRepository.findByIdentifier(cartId)).thenReturn(cartModel);
        Mockito.when(modelMapper.map(cartModel, CartDto.class)).thenReturn(expectedDto);
        Mockito.when(modelMapper.map(eq(entries), any(Type.class))).thenReturn(entries);

        CartDto response = cartService.recalculate(cartId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(new BigDecimal("150.00"), cartModel.getTotalPrice());
        Assertions.assertEquals(new BigDecimal("15.00"), cartModel.getTotalDiscount());
        Assertions.assertEquals(entries, response.getCartEntryDtoList());
        Mockito.verify(cartRepository).save(cartModel);
    }

    @Test
    @DisplayName("Recalculate Cart - Empty Entries")
    void recalculateTest_EmptyEntries() {
        String cartId = "CART_EMPTY";
        List<CartEntryDto> entries = Collections.emptyList();
        Cart cartModel = new Cart();
        CartDto expectedDto = new CartDto();

        Mockito.when(cartEntryService.findAllEntriesForCart(cartId)).thenReturn(entries);
        Mockito.when(cartRepository.findByIdentifier(cartId)).thenReturn(cartModel);
        Mockito.when(modelMapper.map(cartModel, CartDto.class)).thenReturn(expectedDto);
        Mockito.when(modelMapper.map(eq(entries), any(Type.class))).thenReturn(Collections.emptyList());

        CartDto response = cartService.recalculate(cartId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(BigDecimal.ZERO, cartModel.getTotalPrice());
        Assertions.assertEquals(BigDecimal.ZERO, cartModel.getTotalDiscount());
        Mockito.verify(cartRepository).save(cartModel);
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifierTest() {
        String cartId = "CART_01";
        Cart cartModel = new Cart();
        List<CartEntryDto> entries = List.of(new CartEntryDto());

        Mockito.when(cartRepository.findByIdentifier(cartId)).thenReturn(cartModel);
        Mockito.when(cartEntryService.findAllEntriesForCart(cartId)).thenReturn(entries);

        CartDto response = cartService.findByIdentifier(cartId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(entries, response.getCartEntryDtoList());
        Mockito.verify(modelMapper).map(cartModel, response);
    }

    @Test
    @DisplayName("Delete By Identifier - Success")
    void deleteByIdentifierTest() {
        String cartId = "CART_01";

        cartService.deleteByIdentifier(cartId);

        Mockito.verify(cartRepository).deleteByIdentifier(cartId);
        Mockito.verify(cartEntryService).deleteAllByCart(cartId);
    }
}