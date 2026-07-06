package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
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

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private CartDto cartDto;
    private CartEntryDto cartEntryDto;

    @BeforeEach
    void setUp() {

        cart = new Cart();
        cart.setIdentifier("C1");
        cart.setStatus(true);

        cartDto = new CartDto();
        cartDto.setIdentifier("C1");

        cartEntryDto = new CartEntryDto();
        cartEntryDto.setTotalPrice(BigDecimal.valueOf(100));
        cartEntryDto.setDiscount(BigDecimal.valueOf(20));
    }

    // ✅ SAVE SUCCESS
    @Test
    void testSave_Success() {

        when(cartRepository.existsByIdentifier("C1"))
                .thenReturn(false);

        when(modelMapper.map(cartDto, Cart.class))
                .thenReturn(cart);

        CartDto result = cartService.save(cartDto);

        assertNotNull(result);

        verify(cartRepository).save(cart);
    }

    // ✅ SAVE ALREADY EXISTS
    @Test
    void testSave_AlreadyExists() {

        when(cartRepository.existsByIdentifier("C1"))
                .thenReturn(true);

        CartDto result = cartService.save(cartDto);

        assertFalse(result.isSuccess());
        assertEquals("Already exists", result.getMessage());

        verify(cartRepository, never()).save(any());
    }

    // ✅ RECALCULATE
    @Test
    void testRecalculate() {

        List<CartEntryDto> entries = List.of(cartEntryDto);

        when(cartEntryService.findAllCarts("C1"))
                .thenReturn(entries);

        when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(cartDto);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(cartDto));

        CartDto result = cartService.recalculate("C1");

        assertNotNull(result);

        assertEquals(
                BigDecimal.valueOf(100),
                cart.getTotalPrice());

        assertEquals(
                BigDecimal.valueOf(20),
                cart.getTotalDiscount());

        verify(cartRepository).save(cart);
    }

    // ✅ FIND BY IDENTIFIER
    @Test
    void testFindByIdentifier() {

        when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(cartDto);

        when(cartEntryService.findAllCarts("C1"))
                .thenReturn(List.of(cartEntryDto));

        CartDto result = cartService.findByIdentifier("C1");

        assertNotNull(result);
        assertEquals("C1", result.getIdentifier());
        assertNotNull(result.getEntryDtoList());
    }

    // ✅ FIND ACTIVE STATUS
    @Test
    void testFindActiveStatus() {

        Cart inactiveCart = new Cart();
        inactiveCart.setStatus(false);

        when(cartRepository.findAll())
                .thenReturn(List.of(cart, inactiveCart));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(cartDto));

        List<CartDto> result = cartService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ✅ DELETE BY IDENTIFIER
    @Test
    void testDeleteByIdentifier() {

        cartService.deleteByIdentifier("C1");

        verify(cartRepository).deleteByIdentifier("C1");
        verify(cartEntryService).deleteAllByCart("C1");
    }

    // ✅ RECALCULATE WITH MULTIPLE ENTRIES
    @Test
    void testRecalculate_MultipleEntries() {

        CartEntryDto entry1 = new CartEntryDto();
        entry1.setTotalPrice(BigDecimal.valueOf(100));
        entry1.setDiscount(BigDecimal.valueOf(20));

        CartEntryDto entry2 = new CartEntryDto();
        entry2.setTotalPrice(BigDecimal.valueOf(200));
        entry2.setDiscount(BigDecimal.valueOf(30));

        when(cartEntryService.findAllCarts("C1"))
                .thenReturn(List.of(entry1, entry2));

        when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(cartDto);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        cartService.recalculate("C1");

        assertEquals(
                BigDecimal.valueOf(300),
                cart.getTotalPrice());

        assertEquals(
                BigDecimal.valueOf(50),
                cart.getTotalDiscount());

        verify(cartRepository).save(cart);
    }
}