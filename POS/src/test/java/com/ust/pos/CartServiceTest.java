package com.ust.pos;

import com.ust.pos.cart.impl.CartServiceImpl;
import com.ust.pos.cartentry.CartEntryService;
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
    private ModelMapper modelMapper;

    @Mock
    private CartEntryService cartEntryService;

    @Test
    void saveCartSuccess() {

        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        Cart cart = new Cart();

        Mockito.when(
                modelMapper.map(dto, Cart.class)
        ).thenReturn(cart);

        CartDto response = cartService.save(dto);

        Assertions.assertEquals("CART1", response.getIdentifier());

        Mockito.verify(cartRepository)
                .save(cart);
    }

    @Test
    void recalculateCartExistingCartSuccess() {

        Cart cart = new Cart();
        cart.setIdentifier("CART1");
        cart.setDiscount(BigDecimal.valueOf(10));

        CartEntryDto entry1 = new CartEntryDto();
        entry1.setTotalPrice(BigDecimal.valueOf(100));

        CartEntryDto entry2 = new CartEntryDto();
        entry2.setTotalPrice(BigDecimal.valueOf(50));

        List<CartEntryDto> entries =
                List.of(entry1, entry2);

        CartDto mappedDto = new CartDto();

        Mockito.when(
                cartRepository.findByIdentifier("CART1")
        ).thenReturn(cart);

        Mockito.when(
                cartEntryService.findByCartId("CART1")
        ).thenReturn(entries);

        Mockito.when(
                modelMapper.map(cart, CartDto.class)
        ).thenReturn(mappedDto);

        CartDto response =
                cartService.recalculateCart("CART1");

        Assertions.assertEquals(
                BigDecimal.valueOf(140),
                cart.getTotalPrice()
        );

        Assertions.assertEquals(
                2,
                response.getCartEntries().size()
        );

        Mockito.verify(cartRepository)
                .save(cart);
    }

    @Test
    void recalculateCartNewCartSuccess() {

        CartEntryDto entry = new CartEntryDto();
        entry.setTotalPrice(BigDecimal.valueOf(100));

        CartDto mappedDto = new CartDto();

        Mockito.when(
                cartRepository.findByIdentifier("CART1")
        ).thenReturn(null);

        Mockito.when(
                cartEntryService.findByCartId("CART1")
        ).thenReturn(List.of(entry));

        Mockito.when(
                modelMapper.map(Mockito.any(Cart.class), Mockito.eq(CartDto.class))
        ).thenReturn(mappedDto);

        CartDto response =
                cartService.recalculateCart("CART1");

        Assertions.assertNotNull(response);

        Mockito.verify(cartRepository)
                .save(Mockito.any(Cart.class));
    }

    @Test
    void updateCartSuccess() {

        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        Cart existingCart = new Cart();

        Cart mappedCart = new Cart();

        Mockito.when(
                cartRepository.findByIdentifier("CART1")
        ).thenReturn(existingCart);

        Mockito.when(
                modelMapper.map(dto, Cart.class)
        ).thenReturn(mappedCart);

        CartDto response =
                cartService.update(dto);

        Assertions.assertNull(response.getMessage());

        Mockito.verify(cartRepository)
                .save(mappedCart);
    }

    @Test
    void updateCartNotFound() {

        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        Mockito.when(
                cartRepository.findByIdentifier("CART1")
        ).thenReturn(null);

        CartDto response =
                cartService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Cart with identifier - CART1 not found",
                response.getMessage()
        );

        Mockito.verify(cartRepository,
                Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteCartTest() {

        cartService.delete("CART1");

        Mockito.verify(cartRepository)
                .deleteByIdentifier("CART1");
    }

    @Test
    void findAllCartsTest() {

        List<Cart> carts =
                List.of(new Cart(), new Cart());

        List<CartDto> dtoList =
                List.of(new CartDto(), new CartDto());

        Mockito.when(
                cartRepository.findAll()
        ).thenReturn(carts);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(carts),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<CartDto> response =
                cartService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findByIdentifierTest() {

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        CartDto dto = new CartDto();
        dto.setIdentifier("CART1");

        Mockito.when(
                cartRepository.findByIdentifier("CART1")
        ).thenReturn(cart);

        Mockito.when(
                modelMapper.map(cart, CartDto.class)
        ).thenReturn(dto);

        CartDto response =
                cartService.findByIdentifier("CART1");

        Assertions.assertEquals(
                "CART1",
                response.getIdentifier()
        );
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Cart> carts =
                List.of(new Cart());

        Page<Cart> cartPage =
                new PageImpl<>(carts);

        List<CartDto> dtoList =
                List.of(new CartDto());

        Type listType =
                new TypeToken<List<CartDto>>() {}.getType();

        Mockito.when(
                cartRepository.findAll(pageable)
        ).thenReturn(cartPage);

        Mockito.when(
                modelMapper.map(carts, listType)
        ).thenReturn(dtoList);

        List<CartDto> response =
                cartService.findAll(pageable);

        Assertions.assertEquals(
                1,
                response.size()
        );
    }
}