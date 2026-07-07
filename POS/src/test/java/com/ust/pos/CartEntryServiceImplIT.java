package com.ust.pos;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CartEntryServiceImplIT {

    @Autowired
    private CartEntryService cartEntryService;

    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Autowired
    private PriceRepository priceRepository;

    @BeforeEach
    void cleanUp() {
        cartEntryRepository.deleteAll();
        priceRepository.deleteAll();
    }

    private void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
        assertNotNull(actual, "BigDecimal value should not be null");
        assertEquals(0, expected.compareTo(actual), "Expected " + expected + " but got " + actual);
    }

    @Test
    void save_shouldCreateNewCartEntryWithCalculatedPrices() {
        Price price = new Price();
        price.setIdentifier("PROD01");
        price.setMrp(new BigDecimal("100.00"));
        price.setSellingPrice(new BigDecimal("80.00"));
        priceRepository.save(price);

        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("PROD01");
        dto.setCart("CART01");
        dto.setQuantity(new BigDecimal("2"));

        CartEntryDto response = cartEntryService.save(dto);

        assertNotNull(response);
        assertEquals("PROD01-CART01", response.getIdentifier());
        assertBigDecimalEquals(new BigDecimal("2"), response.getQuantity());
        assertBigDecimalEquals(new BigDecimal("160.00"), response.getTotalPrice());
        assertBigDecimalEquals(new BigDecimal("40.00"), response.getDiscount());
        assertBigDecimalEquals(new BigDecimal("100.00"), response.getPrice());
        assertBigDecimalEquals(new BigDecimal("80.00"), response.getSellingPrice());

        CartEntry saved = cartEntryRepository.findByIdentifier("PROD01-CART01");
        assertNotNull(saved);
        assertBigDecimalEquals(new BigDecimal("2"), saved.getQuantity());
    }

    @Test
    void save_shouldAccumulateQuantityForExistingCartEntry() {
        Price price = new Price();
        price.setIdentifier("PROD01");
        price.setMrp(new BigDecimal("100.00"));
        price.setSellingPrice(new BigDecimal("80.00"));
        priceRepository.save(price);

        CartEntry existing = new CartEntry();
        existing.setIdentifier("PROD01-CART01");
        existing.setProduct("PROD01");
        existing.setCart("CART01");
        existing.setQuantity(new BigDecimal("3"));
        cartEntryRepository.save(existing);

        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("PROD01");
        dto.setCart("CART01");
        dto.setQuantity(new BigDecimal("2"));

        CartEntryDto response = cartEntryService.save(dto);

        assertNotNull(response);
        assertEquals("PROD01-CART01", response.getIdentifier());
        assertBigDecimalEquals(new BigDecimal("5"), response.getQuantity());
        assertBigDecimalEquals(new BigDecimal("400.00"), response.getTotalPrice());
        assertBigDecimalEquals(new BigDecimal("100.00"), response.getDiscount());

        CartEntry updated = cartEntryRepository.findByIdentifier("PROD01-CART01");
        assertBigDecimalEquals(new BigDecimal("5"), updated.getQuantity());
    }

    @Test
    void findByIdentifier_shouldReturnCartEntry() {
        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("PROD01-CART01");
        cartEntry.setProduct("PROD01");
        cartEntry.setCart("CART01");
        cartEntry.setQuantity(new BigDecimal("1"));
        cartEntryRepository.save(cartEntry);

        CartEntryDto result = cartEntryService.findByIdentifier("PROD01-CART01");

        assertNotNull(result);
        assertEquals("PROD01-CART01", result.getIdentifier());
    }

    @Test
    void findAllCarts_shouldReturnEntriesForCart() {
        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("PROD01-CART01");
        cartEntry.setProduct("PROD01");
        cartEntry.setCart("CART01");
        cartEntry.setQuantity(new BigDecimal("1"));
        cartEntryRepository.save(cartEntry);

        List<CartEntryDto> results = cartEntryService.findAllCarts("CART01");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("PROD01-CART01", results.get(0).getIdentifier());
    }

    @Test
    void findAll_shouldReturnPaginatedData() {
        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("PROD01-CART01");
        cartEntry.setProduct("PROD01");
        cartEntry.setCart("CART01");
        cartEntry.setQuantity(new BigDecimal("1"));
        cartEntryRepository.save(cartEntry);

        Pageable pageable = PageRequest.of(0, 50);
        List<CartEntryDto> results = cartEntryService.findAll(pageable);

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    void delete_shouldRemoveEntryWhenExists() {
        CartEntry cartEntry = new CartEntry();
        cartEntry.setIdentifier("PROD01-CART01");
        cartEntry.setProduct("PROD01");
        cartEntry.setCart("CART01");
        cartEntryRepository.save(cartEntry);

        boolean deleted = cartEntryService.delete("PROD01", "CART01");

        assertTrue(deleted);
        assertNull(cartEntryRepository.findByIdentifier("PROD01-CART01"));
    }

    @Test
    void delete_shouldReturnFalseWhenEntryDoesNotExist() {
        boolean deleted = cartEntryService.delete("NON", "EXISTENT");

        assertFalse(deleted);
    }

    @Test
    void deleteAllByCart_shouldRemoveAllEntriesForCart() {
        CartEntry cartEntry1 = new CartEntry();
        cartEntry1.setIdentifier("PROD01-CART01");
        cartEntry1.setCart("CART01");
        cartEntryRepository.save(cartEntry1);

        CartEntry cartEntry2 = new CartEntry();
        cartEntry2.setIdentifier("PROD02-CART01");
        cartEntry2.setCart("CART01");
        cartEntryRepository.save(cartEntry2);

        boolean deleted = cartEntryService.deleteAllByCart("CART01");

        assertTrue(deleted);
        assertTrue(cartEntryRepository.findByCart("CART01").isEmpty());
    }

    @Test
    void deleteAllByCart_shouldReturnFalseWhenNoEntriesExist() {
        boolean deleted = cartEntryService.deleteAllByCart("EMPTY_CART");

        assertFalse(deleted);
    }
}