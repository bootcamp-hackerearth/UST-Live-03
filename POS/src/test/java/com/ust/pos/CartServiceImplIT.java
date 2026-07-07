package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.Cart;
import com.ust.pos.model.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CartServiceImplIT {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @MockitoBean
    private CartEntryService cartEntryService;

    @BeforeEach
    void cleanUp() {
        cartRepository.deleteAll();
    }

    private void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
        assertNotNull(actual, "BigDecimal value should not be null");
        assertEquals(0, expected.compareTo(actual), "Expected " + expected + " but got " + actual);
    }

    @Test
    void save_shouldCreateCart() {
        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        CartDto response = cartService.save(dto);

        assertNotNull(response);
        Cart saved = cartRepository.findByIdentifier("CART001");
        assertNotNull(saved);
        assertEquals("CART001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        cartRepository.save(cart);

        CartDto dto = new CartDto();
        dto.setIdentifier("CART001");

        CartDto response = cartService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Already exists", response.getMessage());
    }

    @Test
    void recalculate_shouldAggregatePricesAndDiscounts() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setTotalDiscount(BigDecimal.ZERO);
        cartRepository.save(cart);

        CartEntryDto entry1 = new CartEntryDto();
        entry1.setIdentifier("PROD01-CART001");
        entry1.setTotalPrice(new BigDecimal("160.00"));
        entry1.setDiscount(new BigDecimal("40.00"));

        CartEntryDto entry2 = new CartEntryDto();
        entry2.setIdentifier("PROD02-CART001");
        entry2.setTotalPrice(new BigDecimal("240.00"));
        entry2.setDiscount(new BigDecimal("60.00"));

        List<CartEntryDto> mockEntries = new ArrayList<>();
        mockEntries.add(entry1);
        mockEntries.add(entry2);

        Mockito.when(cartEntryService.findAllCarts("CART001")).thenReturn(mockEntries);

        CartDto response = cartService.recalculate("CART001");

        assertNotNull(response);
        assertBigDecimalEquals(new BigDecimal("400.00"), response.getTotalPrice());
        assertBigDecimalEquals(new BigDecimal("100.00"), response.getTotalDiscount());

        Cart updated = cartRepository.findByIdentifier("CART001");
        assertBigDecimalEquals(new BigDecimal("400.00"), updated.getTotalPrice());
        assertBigDecimalEquals(new BigDecimal("100.00"), updated.getTotalDiscount());
    }

    @Test
    void findByIdentifier_shouldReturnCartWithEntries() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        cartRepository.save(cart);

        CartEntryDto entry = new CartEntryDto();
        entry.setIdentifier("PROD01-CART001");
        List<CartEntryDto> mockEntries = new ArrayList<>();
        mockEntries.add(entry);

        Mockito.when(cartEntryService.findAllCarts("CART001")).thenReturn(mockEntries);

        CartDto result = cartService.findByIdentifier("CART001");

        assertNotNull(result);
        assertEquals("CART001", result.getIdentifier());
        assertNotNull(result.getEntryDtoList());
        assertEquals(1, result.getEntryDtoList().size());
    }

    @Test
    void delete_shouldRemoveCartAndTriggerCascadeCleanUp() {
        Cart cart = new Cart();
        cart.setIdentifier("CART001");
        cartRepository.save(cart);

        Mockito.when(cartEntryService.deleteAllByCart("CART001")).thenReturn(true);

        boolean response = cartService.delete("CART001");

        assertTrue(response);
        assertNull(cartRepository.findByIdentifier("CART001"));
        Mockito.verify(cartEntryService, Mockito.times(1)).deleteAllByCart("CART001");
    }
}