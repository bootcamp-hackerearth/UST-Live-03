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
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
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
    void saveSuccessTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("C1");
        Cart cart = new Cart();
        Mockito.when(cartRepository.findByIdentifier("C1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Cart.class)).thenReturn(cart);
        Mockito.when(cartRepository.save(cart)).thenReturn(cart);
        CartDto response = cartService.save(dto);
        Assertions.assertEquals("C1", response.getIdentifier());
    }

    @Test
    void saveAlreadyExistsTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("C1");
        Cart existing = new Cart();
        Mockito.when(cartRepository.findByIdentifier("C1")).thenReturn(existing);
        CartDto response = cartService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));
    }

    @Test
    void recalculateTest() {
        String cartId = "C1";
        CartEntryDto entry1 = new CartEntryDto();
        entry1.setTotalPrice(BigDecimal.valueOf(100));
        entry1.setDiscount(BigDecimal.valueOf(10));
        CartEntryDto entry2 = new CartEntryDto();
        entry2.setTotalPrice(BigDecimal.valueOf(200));
        entry2.setDiscount(BigDecimal.valueOf(20));
        List<CartEntryDto> entries = List.of(entry1, entry2);
        Cart cart = new Cart();
        CartDto mappedDto = new CartDto();
        Mockito.when(cartEntryService.findAllEntriesForCart(cartId)).thenReturn(entries);
        Mockito.when(cartRepository.findByIdentifier(cartId)).thenReturn(cart);
        Mockito.when(cartRepository.save(cart)).thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class)).thenReturn(mappedDto);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class))).thenReturn(entries);
        CartDto response = cartService.recalculate(cartId);
        Assertions.assertEquals(BigDecimal.valueOf(300), cart.getTotalPrice());
        Assertions.assertEquals(BigDecimal.valueOf(30), cart.getTotalDiscount());
        Assertions.assertNotNull(response.getCartEntryDtoList());
        Assertions.assertEquals(2, response.getCartEntryDtoList().size());
    }

    @Test
    void recalculateEmptyEntriesTest() {
        String cartId = "C1";
        List<CartEntryDto> entries = new ArrayList<>();
        Cart cart = new Cart();
        CartDto dto = new CartDto();
        Mockito.when(cartEntryService.findAllEntriesForCart(cartId)).thenReturn(entries);
        Mockito.when(cartRepository.findByIdentifier(cartId)).thenReturn(cart);
        Mockito.when(cartRepository.save(cart)).thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class)).thenReturn(dto);
        Mockito.when(modelMapper.map(Mockito.eq(entries), Mockito.any(Type.class))).thenReturn(entries);
        CartDto response = cartService.recalculate(cartId);
        Assertions.assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
        Assertions.assertEquals(BigDecimal.ZERO, cart.getTotalDiscount());
        Assertions.assertTrue(response.getCartEntryDtoList().isEmpty());
    }

    @Test
    void findByIdentifierTest() {
        String id = "C1";
        List<CartEntryDto> entries = new ArrayList<>();
        entries.add(new CartEntryDto());
        Mockito.when(cartRepository.findByIdentifier(id)).thenReturn(new Cart());
        Mockito.when(cartEntryService.findAllEntriesForCart(id)).thenReturn(entries);
        Mockito.doAnswer(invocation -> {
            CartDto target = invocation.getArgument(1);
            target.setIdentifier("C1");
            return null;
        }).when(modelMapper).map(Mockito.any(Cart.class), Mockito.any(CartDto.class));
        CartDto response = cartService.findByIdentifier(id);
        Assertions.assertEquals("C1", response.getIdentifier());
        Assertions.assertEquals(1, response.getCartEntryDtoList().size());
    }

    @Test
    void deleteByIdentifierTest() {
        Mockito.doNothing().when(cartRepository).deleteByIdentifier("C1");
        Mockito.doNothing().when(cartEntryService).deleteAllByCart("C1");
        cartService.deleteByIdentifier("C1");
        Mockito.verify(cartRepository, Mockito.times(1)).deleteByIdentifier("C1");
        Mockito.verify(cartEntryService, Mockito.times(1)).deleteAllByCart("C1");
    }
}