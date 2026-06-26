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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllTest() {

        Cart cart = new Cart();
        cart.setIdentifier("CART-001");

        CartDto dto = new CartDto();
        dto.setIdentifier("CART-001");

        List<Cart> carts = List.of(cart);
        List<CartDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Cart> page = new PageImpl<>(carts);

        Mockito.when(cartRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(carts),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        List<CartDto> response =
                cartService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "CART-001",
                response.get(0).getIdentifier()
        );
    }

    @Test
    void findByIdentifierTest() {

        Cart cart = new Cart();
        cart.setIdentifier("CART-001");

        CartDto dto = new CartDto();
        dto.setIdentifier("CART-001");

        Mockito.when(
                cartRepository.findByIdentifier("CART-001")
        ).thenReturn(cart);

        Mockito.when(
                modelMapper.map(cart, CartDto.class)
        ).thenReturn(dto);

        CartDto response =
                cartService.findByIdentifier("CART-001");

        Assertions.assertEquals(
                "CART-001",
                response.getIdentifier()
        );
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(
                cartRepository.findByIdentifier("CART-001")
        ).thenReturn(null);

        CartDto response =
                cartService.findByIdentifier("CART-001");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Cart not found",
                response.getMessage()
        );
    }

    @Test
    void saveNewCartTest() {

        CartDto dto = new CartDto();

        Mockito.when(
                cartRepository.findByIdentifier("CART-001")
        ).thenReturn(null);

        Cart savedCart = new Cart();
        savedCart.setIdentifier("CART-001");

        Mockito.when(
                cartRepository.save(Mockito.any(Cart.class))
        ).thenReturn(savedCart);

        Mockito.when(
                modelMapper.map(
                        Mockito.any(Cart.class),
                        Mockito.eq(CartDto.class)
                )
        ).thenReturn(dto);

        CartDto response =
                cartService.save("CART-001");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Cart created successfully",
                response.getMessage()
        );
    }

    @Test
    void saveExistingCartTest() {

        Cart cart = new Cart();
        cart.setIdentifier("CART-001");

        CartDto dto = new CartDto();
        dto.setIdentifier("CART-001");

        Mockito.when(
                cartRepository.findByIdentifier("CART-001")
        ).thenReturn(cart);

        Mockito.when(
                modelMapper.map(cart, CartDto.class)
        ).thenReturn(dto);

        CartDto response =
                cartService.save("CART-001");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Cart already exists",
                response.getMessage()
        );
    }

    @Test
    void deleteTest() {

        Mockito.doNothing()
                .when(cartEntryRepository)
                .deleteByCartId("CART-001");

        Mockito.doNothing()
                .when(cartRepository)
                .deleteByIdentifier("CART-001");

        cartService.delete("CART-001");

        Mockito.verify(cartEntryRepository)
                .deleteByCartId("CART-001");

        Mockito.verify(cartRepository)
                .deleteByIdentifier("CART-001");
    }

    @Test
    void recalculateTest() {

        CartEntry entry = new CartEntry();

        entry.setTotalPrice(
                new BigDecimal("100")
        );

        entry.setOriginalPrice(
                new BigDecimal("120")
        );

        entry.setDiscount(
                new BigDecimal("20")
        );

        Cart cart = new Cart();
        cart.setIdentifier("CART-001");

        CartDto dto = new CartDto();

        Mockito.when(
                cartEntryRepository.findByCartId("CART-001")
        ).thenReturn(List.of(entry));

        Mockito.when(
                cartRepository.findByIdentifier("CART-001")
        ).thenReturn(cart);

        Mockito.when(
                cartRepository.save(cart)
        ).thenReturn(cart);

        Mockito.when(
                modelMapper.map(cart, CartDto.class)
        ).thenReturn(dto);

        CartDto response =
                cartService.recalculate("CART-001");

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                new BigDecimal("100"),
                cart.getTotalPrice()
        );

        Assertions.assertEquals(
                new BigDecimal("120"),
                cart.getOriginalPrice()
        );

        Assertions.assertEquals(
                new BigDecimal("20"),
                cart.getDiscount()
        );
    }

    @Test
    void recalculateCartNotFoundTest() {

        Mockito.when(
                cartEntryRepository.findByCartId("CART-001")
        ).thenReturn(List.of());

        Mockito.when(
                cartRepository.findByIdentifier("CART-001")
        ).thenReturn(null);

        CartDto response =
                cartService.recalculate("CART-001");

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Cart not found",
                response.getMessage()
        );
    }
}