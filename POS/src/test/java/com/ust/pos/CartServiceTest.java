package com.ust.pos;

import com.ust.pos.cart.service.impl.CartServiceImpl;
import com.ust.pos.dto.CartDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
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
    private CartEntryRepository cartEntryRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierTest() {
        Cart cart = new Cart();
        cart.setIdentifier("cart123");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("cart123");

        Mockito.when(cartRepository.findByIdentifier("cart123")).thenReturn(cart);
        Mockito.when(modelMapper.map(cart, CartDto.class)).thenReturn(cartDto);
        CartDto response = cartService.findByIdentifier("cart123");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("cart123", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(cartRepository.findByIdentifier("cart123")).thenReturn(null);
        CartDto response = cartService.findByIdentifier("cart123");
        Assertions.assertNull(response);
    }

    @Test
    void saveTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("cart123");

        Cart cart = new Cart();
        Mockito.when(cartRepository.findByIdentifier("cart123")).thenReturn(null);
        Mockito.when(modelMapper.map(cartDto, Cart.class)).thenReturn(cart);
        Mockito.when(cartRepository.save(cart)).thenReturn(cart);
        CartDto response = cartService.save(cartDto);
        Assertions.assertEquals("cart123", response.getIdentifier());
        Mockito.verify(cartRepository).save(cart);
    }

    @Test
    void saveFailureTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("cart123");
        Cart existing = new Cart();
        Mockito.when(cartRepository.findByIdentifier("cart123")).thenReturn(existing);
        CartDto response = cartService.save(cartDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void reCalculateTest() {
        String identifier = "cart123";

        Cart cart = new Cart();
        cart.setIdentifier(identifier);

        CartEntry entry = new CartEntry();
        entry.setOriginalPrice(new BigDecimal("100"));
        entry.setDiscount(new BigDecimal("10"));
        entry.setTotalPrice(new BigDecimal("90"));
        List<CartEntry> entries = List.of(entry);

        Cart savedCart = new Cart();
        savedCart.setIdentifier(identifier);

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier(identifier);
        Mockito.when(cartRepository.findByIdentifier(identifier)).thenReturn(cart);
        Mockito.when(cartEntryRepository.findByCartIdentifier(identifier)).thenReturn(entries);
        Mockito.when(cartRepository.save(cart)).thenReturn(savedCart);
        Mockito.when(modelMapper.map(savedCart, CartDto.class)).thenReturn(cartDto);

        CartDto response = cartService.reCalculate(identifier);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(identifier, response.getIdentifier());
        Assertions.assertEquals(new BigDecimal("100"), cart.getTotalOriginalPrice());
        Assertions.assertEquals(new BigDecimal("10"), cart.getTotalDiscount());
        Assertions.assertEquals(new BigDecimal("90"), cart.getTotalPrice());
    }

    @Test
    void reCalculateWhenCartNotExistsTest() {
        String identifier = "cart123";
        Cart mappedCart = new Cart();
        mappedCart.setIdentifier(identifier);

        Cart savedCart = new Cart();
        savedCart.setIdentifier(identifier);

        CartDto responseDto = new CartDto();
        responseDto.setIdentifier(identifier);
        Mockito.when(cartRepository.findByIdentifier(identifier))
                .thenReturn(null)
                .thenReturn(null)
                .thenReturn(savedCart);

        Mockito.when(modelMapper.map(Mockito.any(CartDto.class), Mockito.eq(Cart.class))).thenReturn(mappedCart);
        Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenReturn(savedCart);
        Mockito.when(cartEntryRepository.findByCartIdentifier(identifier)).thenReturn(List.of());
        Mockito.when(modelMapper.map(savedCart, CartDto.class)).thenReturn(responseDto);
        CartDto response = cartService.reCalculate(identifier);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(identifier, response.getIdentifier());
        Mockito.verify(cartRepository, Mockito.times(2)).save(Mockito.any());
    }

    @Test
    void updateTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("cart123");

        Cart existing = new Cart();
        Mockito.when(cartRepository.findByIdentifier("cart123")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(cartDto, existing);
        Mockito.when(cartRepository.save(existing)).thenReturn(existing);
        CartDto response = cartService.update(cartDto);
        Assertions.assertNotNull(response);
        Mockito.verify(cartRepository).save(existing);
    }

    @Test
    void updateFailureTest() {
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("cart123");
        Mockito.when(cartRepository.findByIdentifier("cart123")).thenReturn(null);
        CartDto response = cartService.update(cartDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(cartRepository).deleteByIdentifier("cart123");
        Mockito.doNothing().when(cartEntryRepository).deleteByCartIdentifier("cart123");
        cartService.delete("cart123");
        Mockito.verify(cartRepository).deleteByIdentifier("cart123");
        Mockito.verify(cartEntryRepository).deleteByCartIdentifier("cart123");
    }

    @Test
    void findAllTest() {
        Cart cart = new Cart();
        cart.setIdentifier("cart123");

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier("cart123");

        List<Cart> cartList = List.of(cart);
        List<CartDto> cartDtoList = List.of(cartDto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Cart> cartPage = new PageImpl<>(cartList, pageable, cartList.size());
        Mockito.when(cartRepository.findAll(pageable)).thenReturn(cartPage);
        Mockito.when(modelMapper.map(Mockito.eq(cartList), Mockito.any(Type.class)
        )).thenReturn(cartDtoList);
        List<CartDto> response = cartService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("cart123", response.get(0).getIdentifier());
    }
}