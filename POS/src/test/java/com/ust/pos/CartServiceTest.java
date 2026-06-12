package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    void findAll_success() {

        Cart cart = new Cart();
        Page<Cart> page = new PageImpl<>(List.of(cart));
        Pageable pageable = PageRequest.of(0, 5);

        when(cartRepository.findAll(pageable))
                .thenReturn(page);

        Type type = new TypeToken<List<CartDto>>() {}.getType();

        when(modelMapper.map(anyList(), eq(type)))
                .thenReturn(List.of(new CartDto()));

        List<CartDto> result = cartService.findAll(pageable);

        assertEquals(1, result.size());
    }

    @Test
    void findByIdentifier_success() {

        Cart cart = new Cart();
        CartDto dto = new CartDto();
        dto.setSuccess(true);

        when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        when(modelMapper.map(cart, CartDto.class))
                .thenReturn(dto);

        CartDto result = cartService.findByIdentifier("C1");

        assertTrue(result.isSuccess());
    }

    @Test
    void findByIdentifier_failure() {

        when(cartRepository.findByIdentifier("C1"))
                .thenReturn(null);

        CartDto result = cartService.findByIdentifier("C1");

        assertFalse(result.isSuccess());
        assertEquals("Cart not found", result.getMessage());
    }

    @Test
    void save_new_cart() {

        CartDto mappedDto = new CartDto();
        mappedDto.setSuccess(true);
        mappedDto.setMessage("Cart created successfully");

        when(cartRepository.findByIdentifier("C1"))
                .thenReturn(null);

        when(cartRepository.save(any(Cart.class)))
                .thenReturn(new Cart());

        when(modelMapper.map(any(Cart.class), eq(CartDto.class)))
                .thenReturn(mappedDto);

        CartDto result = cartService.save("C1");

        assertTrue(result.isSuccess());
        assertEquals("Cart created successfully", result.getMessage());
    }

    @Test
    void save_existing_cart() {

        Cart existing = new Cart();
        CartDto mappedDto = new CartDto();

        when(cartRepository.findByIdentifier("C1"))
                .thenReturn(existing);

        when(modelMapper.map(existing, CartDto.class))
                .thenReturn(mappedDto);

        CartDto result = cartService.save("C1");

        assertTrue(result.isSuccess());
        assertEquals("Cart already exists", result.getMessage());
    }

    @Test
    void delete_cart() {

        cartService.delete("C1");

        verify(cartEntryRepository).deleteByCartId("C1");
        verify(cartRepository).deleteByIdentifier("C1");
    }

    @Test
    void recalculate_success() {

        Cart cart = new Cart();
        cart.setIdentifier("C1");

        CartEntry entry = new CartEntry();
        entry.setTotalPrice(BigDecimal.valueOf(100));
        entry.setOriginalPrice(BigDecimal.valueOf(120));
        entry.setDiscount(BigDecimal.valueOf(20));

        when(cartEntryRepository.findByCartId("C1"))
                .thenReturn(List.of(entry));

        when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        when(cartRepository.save(any(Cart.class)))
                .thenReturn(cart);

        when(modelMapper.map(any(Cart.class), eq(CartDto.class)))
                .thenReturn(new CartDto());

        CartDto result = cartService.recalculate("C1");

        assertNotNull(result);
    }

    @Test
    void recalculate_failure() {

        when(cartEntryRepository.findByCartId("C1"))
                .thenReturn(List.of());

        when(cartRepository.findByIdentifier("C1"))
                .thenReturn(null);

        CartDto result = cartService.recalculate("C1");

        assertFalse(result.isSuccess());
        assertEquals("Cart not found", result.getMessage());
    }
}