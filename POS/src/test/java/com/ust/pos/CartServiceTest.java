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
    private PriceService priceService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        Mockito.when(modelMapper.map(cartDto, Cart.class))
                .thenReturn(cart);

        Mockito.when(cartRepository.save(cart))
                .thenReturn(cart);

        CartDto response = cartService.save(cartDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "CART1",
                response.getIdentifier()
        );

        Mockito.verify(cartRepository)
                .save(cart);
    }

    @Test
    void recalculateCart_WhenCartExists() {

        Cart cart = new Cart();
        cart.setIdentifier("CART1");
        cart.setDiscount(BigDecimal.TEN);

        CartEntryDto entry1 = new CartEntryDto();
        entry1.setTotalPrice(new BigDecimal("100"));

        CartEntryDto entry2 = new CartEntryDto();
        entry2.setTotalPrice(new BigDecimal("50"));

        CartDto cartDto = new CartDto();

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        Mockito.when(cartEntryService.findByCartId("CART1"))
                .thenReturn(List.of(entry1, entry2));

        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(cartDto);

        CartDto response =
                cartService.recalulateCart("CART1");

        Assertions.assertNotNull(response);

        Mockito.verify(cartRepository)
                .save(cart);

        Assertions.assertEquals(
                new BigDecimal("140"),
                cart.getTotalPrice()
        );
    }

    @Test
    void recalculateCart_WhenCartDoesNotExist() {

        CartDto cartDto = new CartDto();

        Mockito.when(cartRepository.findByIdentifier("CART2"))
                .thenReturn(null);

        Mockito.when(cartEntryService.findByCartId("CART2"))
                .thenReturn(List.of());

        Mockito.when(modelMapper.map(
                Mockito.any(Cart.class),
                Mockito.eq(CartDto.class)
        )).thenReturn(cartDto);

        CartDto response =
                cartService.recalulateCart("CART2");

        Assertions.assertNotNull(response);

        Mockito.verify(cartRepository)
                .save(Mockito.any(Cart.class));
    }

    @Test
    void updateTest() {

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Cart existingCart = new Cart();
        existingCart.setIdentifier("CART1");

        Cart mappedCart = new Cart();
        mappedCart.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(existingCart);

        Mockito.when(modelMapper.map(cartDto, Cart.class))
                .thenReturn(mappedCart);

        Mockito.when(cartRepository.save(mappedCart))
                .thenReturn(mappedCart);

        CartDto response = cartService.update(cartDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "CART1",
                response.getIdentifier()
        );

        Mockito.verify(cartRepository)
                .save(mappedCart);
    }

    @Test
    void updateTestFailure() {

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(null);

        CartDto response = cartService.update(cartDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(cartRepository,
                        Mockito.never())
                .save(Mockito.any(Cart.class));
    }

    @Test
    void deleteTest() {

        Mockito.doNothing()
                .when(cartRepository)
                .deleteByIdentifier("CART1");

        cartService.delete("CART1");

        Mockito.verify(cartRepository)
                .deleteByIdentifier("CART1");
    }

    @Test
    void deleteAllTest() {

        Mockito.doNothing()
                .when(cartRepository)
                .deleteAll();

        cartService.deletAll();

        Mockito.verify(cartRepository)
                .deleteAll();
    }

    @Test
    void findByIdentifierTest() {

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Mockito.when(cartRepository.findByIdentifier("CART1"))
                .thenReturn(cart);

        Mockito.when(modelMapper.map(cart, CartDto.class))
                .thenReturn(cartDto);

        CartDto response =
                cartService.findByIdentifier("CART1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "CART1",
                response.getIdentifier()
        );
    }

    @Test
    void findAllTest() {

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        List<Cart> carts = List.of(cart);
        List<CartDto> cartDtos = List.of(cartDto);

        Type listType =
                new org.modelmapper.TypeToken<List<CartDto>>() {
                }.getType();

        Mockito.when(cartRepository.findAll())
                .thenReturn(carts);

        Mockito.when(modelMapper.map(carts, listType))
                .thenReturn(cartDtos);

        List<CartDto> response =
                cartService.findAll();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "CART1",
                response.get(0).getIdentifier()
        );
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Cart cart = new Cart();
        cart.setIdentifier("CART1");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("CART1");

        Page<Cart> cartPage =
                new PageImpl<>(List.of(cart));

        List<CartDto> cartDtos =
                List.of(cartDto);

        Type listType =
                new org.modelmapper.TypeToken<List<CartDto>>() {
                }.getType();

        Mockito.when(cartRepository.findAll(pageable))
                .thenReturn(cartPage);

        Mockito.when(
                modelMapper.map(
                        cartPage.getContent(),
                        listType
                )
        ).thenReturn(cartDtos);

        List<CartDto> response =
                cartService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.size()
        );

        Assertions.assertEquals(
                "CART1",
                response.get(0).getIdentifier()
        );

        Mockito.verify(cartRepository)
                .findAll(pageable);
    }
}