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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    private CartEntryService cartEntryService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("C1");

        Cart cart = new Cart();

        Mockito.when(modelMapper.map(dto, Cart.class))
                .thenReturn(cart);

        CartDto response = cartService.save(dto);

        Assertions.assertEquals("C1", response.getIdentifier());
        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void recalculateCartTest() {
        Cart cart = new Cart();
        cart.setIdentifier("C1");
        cart.setDiscount(BigDecimal.TEN);

        CartEntryDto e1 = new CartEntryDto();
        e1.setTotalPrice(BigDecimal.valueOf(100));

        CartEntryDto e2 = new CartEntryDto();
        e2.setTotalPrice(BigDecimal.valueOf(50));

        List<CartEntryDto> entries = List.of(e1, e2);

        CartDto mapped = new CartDto();

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);
        Mockito.when(cartEntryService.findByCartId("C1"))
                .thenReturn(entries);
        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(mapped);

        CartDto response = cartService.recalculateCart("C1");

        Assertions.assertEquals(BigDecimal.valueOf(140), cart.getTotalPrice());
        Assertions.assertEquals(entries, response.getCartEntries());

        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void recalculateCartWhenCartNotFoundTest() {
        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(null);
        Mockito.when(cartEntryService.findByCartId("C1"))
                .thenReturn(List.of());

        Mockito.when(modelMapper.map(Mockito.any(Cart.class), Mockito.eq(CartDto.class)))
                .thenReturn(new CartDto());

        CartDto response = cartService.recalculateCart("C1");

        Assertions.assertNotNull(response);
        Mockito.verify(cartRepository).save(Mockito.any(Cart.class));
    }

    @Test
    void updateTest() {
        CartDto dto = new CartDto();
        dto.setIdentifier("C1");

        Cart existing = new Cart();
        Cart mapped = new Cart();

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(existing);
        Mockito.when(modelMapper.map(dto, Cart.class))
                .thenReturn(mapped);

        cartService.update(dto);

        Mockito.verify(cartRepository).save(mapped);
    }

    @Test
    void updateTestFailure() {
        CartDto dto = new CartDto();
        dto.setIdentifier("C1");

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(null);

        CartDto response = cartService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(cartRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(cartRepository)
                .deleteByIdentifier("C1");

        cartService.delete("C1");

        Mockito.verify(cartRepository)
                .deleteByIdentifier("C1");
    }

    @Test
    void findAllTest() {
        List<Cart> carts = List.of(new Cart());
        List<CartDto> dtos = List.of(new CartDto());

        Type type = new TypeToken<List<CartDto>>() {
        }.getType();

        Mockito.when(cartRepository.findAll())
                .thenReturn(carts);
        Mockito.when(modelMapper.map(carts, type))
                .thenReturn(dtos);

        Assertions.assertEquals(1, cartService.findAll().size());
    }

    @Test
    void findByIdentifierTest() {
        Cart cart = new Cart();
        CartDto dto = new CartDto();

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(dto);

        Assertions.assertNotNull(cartService.findByIdentifier("C1"));
    }

    @Test
    void findAllPaginationTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Cart> carts = List.of(new Cart());
        Page<Cart> page = new PageImpl<>(carts);

        List<CartDto> dtos = List.of(new CartDto());

        Type type = new TypeToken<List<CartDto>>() {
        }.getType();

        Mockito.when(cartRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(carts, type))
                .thenReturn(dtos);

        List<CartDto> response = cartService.findAll(pageable);

        Assertions.assertEquals(1, response.size());

        Mockito.verify(cartRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(carts, type);
    }
}