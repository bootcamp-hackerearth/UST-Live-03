package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;

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
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("C1");

        Cart cart = new Cart();

        Mockito.when(modelMapper.map(cartDto, Cart.class))
                .thenReturn(cart);

        Mockito.when(cartRepository.save(cart))
                .thenReturn(cart);

        CartDto response = cartService.save(cartDto);

        Assertions.assertEquals("C1", response.getIdentifier());

        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void recalculateCartTest() {
        Cart cart = new Cart();
        cart.setIdentifier("C1");
        cart.setDiscount(BigDecimal.TEN);

        CartEntryDto entry1 = new CartEntryDto();
        entry1.setTotalPrice(new BigDecimal("100"));

        CartEntryDto entry2 = new CartEntryDto();
        entry2.setTotalPrice(new BigDecimal("50"));

        List<CartEntryDto> entries = List.of(entry1, entry2);

        CartDto mappedDto = new CartDto();

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        Mockito.when(cartEntryService.findByCartId("C1"))
                .thenReturn(entries);

        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(mappedDto);

        CartDto response = cartService.recalculateCart("C1");

        Assertions.assertEquals(
                new BigDecimal("140"),
                cart.getTotalPrice()
        );

        Assertions.assertEquals(entries, response.getCartEntries());

        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void recalculateCartWhenCartNotFoundTest() {
        CartEntryDto entry = new CartEntryDto();
        entry.setTotalPrice(new BigDecimal("100"));

        List<CartEntryDto> entries = List.of(entry);

        CartDto mappedDto = new CartDto();

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(null);

        Mockito.when(cartEntryService.findByCartId("C1"))
                .thenReturn(entries);

        Mockito.when(modelMapper.map(Mockito.any(Cart.class), Mockito.eq(CartDto.class)))
                .thenReturn(mappedDto);

        CartDto response = cartService.recalculateCart("C1");

        Assertions.assertNotNull(response);

        Mockito.verify(cartRepository).save(Mockito.any(Cart.class));
    }

    @Test
    void updateTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("C1");

        Cart existingCart = new Cart();

        Cart mappedCart = new Cart();

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(existingCart);

        Mockito.when(modelMapper.map(cartDto, Cart.class))
                .thenReturn(mappedCart);

        cartService.update(cartDto);

        Mockito.verify(cartRepository).save(mappedCart);
    }

    @Test
    void updateTestFailure() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("C1");

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(null);

        CartDto response = cartService.update(cartDto);

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
        Cart cart = new Cart();
        CartDto cartDto = new CartDto();

        List<Cart> carts = List.of(cart);
        List<CartDto> cartDtos = List.of(cartDto);

        Mockito.when(cartRepository.findAll())
                .thenReturn(carts);

        Mockito.when(modelMapper.map(Mockito.eq(carts), Mockito.any(Type.class)))
                .thenReturn(cartDtos);

        List<CartDto> response = cartService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByIdentifierTest() {
        Cart cart = new Cart();
        cart.setIdentifier("C1");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("C1");

        Mockito.when(cartRepository.findByIdentifier("C1"))
                .thenReturn(cart);

        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(cartDto);

        CartDto response = cartService.findByIdentifier("C1");

        Assertions.assertEquals("C1", response.getIdentifier());
    }

    @Test
    void findAllWithPaginationShouldReturnCartDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Cart> carts = List.of(new Cart());

        Page<Cart> cartPage = new PageImpl<>(carts);

        List<CartDto> cartDtos = List.of(new CartDto());

        Type listType = new TypeToken<List<CartDto>>() {
        }.getType();

        Mockito.when(cartRepository.findAll(pageable))
                .thenReturn(cartPage);

        Mockito.when(modelMapper.map(carts, listType))
                .thenReturn(cartDtos);

        List<CartDto> response = cartService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());

        Mockito.verify(cartRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(carts, listType);
    }
}