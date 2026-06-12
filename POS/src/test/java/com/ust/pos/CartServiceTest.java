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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

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
    void saveTest_Success() {
        CartDto dto = new CartDto();
        dto.setIdentifier("C1");

        Cart cart = new Cart();
        cart.setIdentifier("C1");

        Mockito.when(cartRepository.findByIdentifier("C1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Cart.class)).thenReturn(cart);

        CartDto response = cartService.save(dto);
        Assertions.assertEquals("C1", response.getIdentifier());
        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void saveTest_Duplicate() {
        CartDto dto = new CartDto();
        dto.setIdentifier("C1");

        Mockito.when(cartRepository.findByIdentifier("C1")).thenReturn(new Cart());

        CartDto response = cartService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Already exists", response.getMessage());
        Mockito.verify(cartRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void recalculateTest() {
        CartEntryDto entry = new CartEntryDto();
        entry.setOriginalPrice(BigDecimal.valueOf(100));
        entry.setDiscount(BigDecimal.valueOf(20));
        entry.setTotalPrice(BigDecimal.valueOf(80));

        List<CartEntryDto> entries = List.of(entry);
        Cart cart = new Cart();
        cart.setIdentifier("C1");
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("C1");

        Mockito.when(cartEntryService.findAllEntriesForCart("C1")).thenReturn(entries);
        Mockito.when(cartRepository.findByIdentifier("C1")).thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class))).thenReturn(entries);

        CartDto response = cartService.recalculate("C1");
        Assertions.assertNotNull(response);
        Assertions.assertEquals(BigDecimal.valueOf(100), cart.getOriginalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(20), cart.getDiscount());
        Assertions.assertEquals(BigDecimal.valueOf(80), cart.getTotalPrice());
        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void deleteByIdentifierTest() {
        cartService.deleteByIdentifier("C1");
        Mockito.verify(cartEntryService).deleteAllByCartIdentifier("C1");
        Mockito.verify(cartRepository).deleteByIdentifier("C1");
    }

    @Test
    void findByIdentifierTest() {
        Cart cart = new Cart();
        cart.setIdentifier("C1");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("C1");
        List<CartEntryDto> entries = List.of(new CartEntryDto());

        Mockito.when(cartRepository.findByIdentifier("C1")).thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        Mockito.when(cartEntryService.findAllEntriesForCart("C1")).thenReturn(entries);

        CartDto response = cartService.findByIdentifier("C1");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("C1", response.getIdentifier());
        Assertions.assertEquals(1, response.getEntryList().size());
    }

}