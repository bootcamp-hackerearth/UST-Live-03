package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private CartDto cartDto;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setIdentifier("CART001");

        cartDto = new CartDto();
        cartDto.setIdentifier("CART001");
    }

    @Test
    void saveExistingCartTest() {

        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(cart);

        doNothing().when(modelMapper)
                .map(cartDto, cart);

        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(cartDto);

        CartDto result = cartService.save(cartDto);

        assertNotNull(result);
        assertEquals("CART001", result.getIdentifier());

        verify(cartRepository).save(cart);
    }

    @Test
    void saveNewCartTest() {

        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(null);

        doNothing().when(modelMapper)
                .map(any(CartDto.class), any(Cart.class));

        when(modelMapper.map(any(Cart.class), eq(CartDto.class)))
                .thenReturn(cartDto);

        CartDto result = cartService.save(cartDto);

        assertNotNull(result);
        assertEquals("CART001", result.getIdentifier());

        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void recalculateCartExistingCartTest() {

        CartEntry entry1 = new CartEntry();
        entry1.setTotalPrice(new BigDecimal("100"));
        entry1.setDiscount(new BigDecimal("10"));

        CartEntry entry2 = new CartEntry();
        entry2.setTotalPrice(new BigDecimal("200"));
        entry2.setDiscount(new BigDecimal("20"));

        List<CartEntry> entries = Arrays.asList(entry1, entry2);

        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(cart);

        when(cartEntryRepository.findByCartId("CART001"))
                .thenReturn(entries);

        when(modelMapper.map(eq(entries), any(Type.class)))
                .thenReturn(Collections.emptyList());

        CartDto mappedDto = new CartDto();
        mappedDto.setIdentifier("CART001");

        when(modelMapper.map(any(Cart.class), eq(CartDto.class)))
                .thenReturn(mappedDto);

        CartDto result = cartService.recalculateCart("CART001");

        assertNotNull(result);

        verify(cartRepository).save(cart);

        assertEquals(
                new BigDecimal("30"),
                cart.getDiscount()
        );

        assertEquals(
                new BigDecimal("270"),
                cart.getTotalPrice()
        );
    }

    @Test
    void recalculateCartNewCartTest() {

        when(cartRepository.findByIdentifier("NEW_CART"))
                .thenReturn(null);

        when(cartEntryRepository.findByCartId("NEW_CART"))
                .thenReturn(Collections.emptyList());

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        when(modelMapper.map(any(Cart.class), eq(CartDto.class)))
                .thenReturn(new CartDto());

        CartDto result = cartService.recalculateCart("NEW_CART");

        assertNotNull(result);

        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void deleteAllTest() {

        cartService.deleteAll();

        verify(cartRepository).deleteAll();
    }

    @Test
    void updateSuccessTest() {

        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(cart);

        when(modelMapper.map(cartDto, Cart.class))
                .thenReturn(cart);

        CartDto result = cartService.update(cartDto);

        assertNotNull(result);

        verify(cartRepository).save(cart);
    }

    @Test
    void updateCartNotFoundTest() {

        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(null);

        CartDto result = cartService.update(cartDto);

        assertFalse(result.isSuccess());
        assertTrue(
                result.getMessage()
                        .contains("Cart with identifier - CART001 is not found")
        );

        verify(cartRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        cartService.delete("CART001");

        verify(cartRepository).deleteByIdentifier("CART001");
    }

    @Test
    void findAllTest() {

        List<Cart> carts = Arrays.asList(cart);

        List<CartDto> dtos = Arrays.asList(cartDto);

        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();

        when(cartRepository.findAll())
                .thenReturn(carts);

        when(modelMapper.map(carts, listType))
                .thenReturn(dtos);

        List<CartDto> result = cartService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findByIdentifierTest() {

        when(cartRepository.findByIdentifier("CART001"))
                .thenReturn(cart);

        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(cartDto);

        CartDto result =
                cartService.findByIdentifier("CART001");

        assertNotNull(result);
        assertEquals("CART001", result.getIdentifier());
    }

    @Test
    void findAllWithPageableTest() {

        Page<Cart> page =
                new PageImpl<>(Collections.singletonList(cart));

        List<CartDto> dtos =
                Collections.singletonList(cartDto);

        Type listType =
                new TypeToken<List<CartDto>>() {
                }.getType();

        when(cartRepository.findAll(any(PageRequest.class)))
                .thenReturn(page);

        when(modelMapper.map(page.getContent(), listType))
                .thenReturn(dtos);

        List<CartDto> result =
                cartService.findAll(PageRequest.of(0, 10));

        assertEquals(1, result.size());
    }
}