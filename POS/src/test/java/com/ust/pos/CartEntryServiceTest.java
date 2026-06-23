package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private PriceRepository priceRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    private Price price;
    private CartEntry cartEntry;
    private CartEntryDto cartEntryDto;

    @BeforeEach
    void setUp() {
        price = new Price();
        price.setMrp(new BigDecimal("100.00"));
        price.setSellingPrice(new BigDecimal("80.00"));

        cartEntry = new CartEntry();
        cartEntry.setId(1L);
        cartEntry.setIdentifier("PROD1-CART1");
        cartEntry.setProduct("PROD1");
        cartEntry.setCart("CART1");
        cartEntry.setQuantity(new BigDecimal("2.00"));

        cartEntryDto = new CartEntryDto();
        cartEntryDto.setProduct("PROD1");
        cartEntryDto.setCart("CART1");
        cartEntryDto.setQuantity(new BigDecimal("3.00"));
        cartEntryDto.setIdentifier("PROD1-CART1");
    }

    @Test
    void testSave_WhenPriceNotFound() {
        when(priceRepository.findByProductIdentifier("PROD1")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> cartEntryService.save(cartEntryDto));
    }

    @Test
    void testSave_WhenCartEntryDoesNotExist() {
        when(priceRepository.findByProductIdentifier("PROD1")).thenReturn(price);
        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(null);
        when(cartEntryRepository.save(any(CartEntry.class))).thenReturn(cartEntry);

        CartEntryDto result = cartEntryService.save(cartEntryDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(new BigDecimal("3.00"), result.getQuantity());
        assertEquals("Cart entry saved successfully", result.getMessage());
        verify(cartEntryRepository, times(1)).save(any(CartEntry.class));
    }

    @Test
    void testSave_WhenCartEntryAlreadyExists() {
        when(priceRepository.findByProductIdentifier("PROD1")).thenReturn(price);
        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(cartEntry);
        when(cartEntryRepository.save(any(CartEntry.class))).thenReturn(cartEntry);

        CartEntryDto result = cartEntryService.save(cartEntryDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(new BigDecimal("5.00"), result.getQuantity());
        verify(cartEntryRepository, times(1)).save(any(CartEntry.class));
    }

    @Test
    void testFindAllEntriesForCart() {
        List<CartEntry> entries = Collections.singletonList(cartEntry);
        when(cartEntryRepository.findByCart("CART1")).thenReturn(entries);

        List<CartEntryDto> result = cartEntryService.findAllEntriesForCart("CART1");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cartEntryRepository, times(1)).findByCart("CART1");
    }

    @Test
    void testUpdate_WhenCartEntryNotFound() {
        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(null);

        CartEntryDto result = cartEntryService.update(cartEntryDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Cart entry not found", result.getMessage());
    }

    @Test
    void testUpdate_WhenPriceNotFound() {
        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(cartEntry);
        when(priceRepository.findByProductIdentifier("PROD1")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> cartEntryService.update(cartEntryDto));
    }

    @Test
    void testUpdate_Success() {
        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(cartEntry);
        when(priceRepository.findByProductIdentifier("PROD1")).thenReturn(price);
        when(cartEntryRepository.save(any(CartEntry.class))).thenReturn(cartEntry);

        CartEntryDto result = cartEntryService.update(cartEntryDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Cart entry updated successfully", result.getMessage());
        verify(cartEntryRepository, times(1)).save(any(CartEntry.class));
    }

    @Test
    void testDeleteByIdentifier_WhenCartEntryNotFound() {
        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> cartEntryService.deleteByIdentifier("PROD1-CART1"));
    }

    @Test
    void testDeleteByIdentifier_Success() {
        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(cartEntry);

        cartEntryService.deleteByIdentifier("PROD1-CART1");

        verify(cartEntryRepository, times(1)).deleteByIdentifier("PROD1-CART1");
    }

    @Test
    void testDeleteAllByCart_WhenListIsEmpty() {
        when(cartEntryRepository.findByCart("CART1")).thenReturn(new ArrayList<>());

        cartEntryService.deleteAllByCart("CART1");

        verify(cartEntryRepository, never()).deleteAll(anyList());
    }

    @Test
    void testDeleteAllByCart_Success() {
        List<CartEntry> entries = Collections.singletonList(cartEntry);
        when(cartEntryRepository.findByCart("CART1")).thenReturn(entries);

        cartEntryService.deleteAllByCart("CART1");

        verify(cartEntryRepository, times(1)).deleteAll(entries);
    }
}