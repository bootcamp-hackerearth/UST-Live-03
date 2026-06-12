package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;


    @Test
    void save_newEntry() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("PROD1");
        dto.setCart("CART1");
        dto.setQuantity(new BigDecimal("2"));

        Price price = new Price();
        price.setMrp(new BigDecimal("100"));
        price.setSellingPrice(new BigDecimal("80"));

        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(null);
        when(priceRepository.findByProductIdentifier("PROD1")).thenReturn(price);

        CartEntryDto result = cartEntryService.save(dto);

        assertEquals("PROD1-CART1", result.getIdentifier());
        assertEquals(new BigDecimal("2"), result.getQuantity());
        assertEquals(new BigDecimal("160"), result.getTotalPrice());
        assertEquals(new BigDecimal("40"), result.getDiscount());
        assertEquals(new BigDecimal("100"), result.getPrice());
        assertEquals(new BigDecimal("80"), result.getSellingPrice());
        verify(cartEntryRepository).save(any(CartEntry.class));
    }

    @Test
    void save_existingEntry_quantityAccumulates() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("PROD1");
        dto.setCart("CART1");
        dto.setQuantity(new BigDecimal("2"));

        CartEntry existing = new CartEntry();
        existing.setQuantity(new BigDecimal("3"));

        Price price = new Price();
        price.setMrp(new BigDecimal("100"));
        price.setSellingPrice(new BigDecimal("80"));

        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(existing);
        when(priceRepository.findByProductIdentifier("PROD1")).thenReturn(price);

        CartEntryDto result = cartEntryService.save(dto);

        assertEquals(new BigDecimal("5"), result.getQuantity());
        assertEquals(new BigDecimal("400"), result.getTotalPrice());
        verify(cartEntryRepository).save(existing);
    }

    @Test
    void findAllEntriesForCart_success() {
        List<CartEntry> entries = List.of(new CartEntry());
        List<CartEntryDto> dtos = List.of(new CartEntryDto());

        when(cartEntryRepository.findByCart("CART1")).thenReturn(entries);
        when(modelMapper.map(Mockito.eq(entries), Mockito.any(java.lang.reflect.Type.class))).thenReturn(dtos);

        List<CartEntryDto> result = cartEntryService.findAllEntriesForCart("CART1");

        assertEquals(1, result.size());
    }

    @Test
    void update_success() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("PROD1-CART1");
        dto.setProduct("PROD1");
        dto.setQuantity(new BigDecimal("3"));

        CartEntry existing = new CartEntry();

        Price price = new Price();
        price.setCostPrice(new BigDecimal("120"));
        price.setSellingPrice(new BigDecimal("90"));

        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(existing);
        when(priceRepository.findByIdentifier("PROD1")).thenReturn(price);

        CartEntryDto result = cartEntryService.update(dto);

        assertTrue(result.isSuccess());
        assertEquals(new BigDecimal("270"), result.getTotalPrice());
        assertEquals(new BigDecimal("90"), result.getDiscount());
        assertEquals(new BigDecimal("120"), result.getPrice());
        assertEquals(new BigDecimal("90"), result.getSellingPrice());
        verify(cartEntryRepository).save(existing);
    }

    @Test
    void update_notFound() {
        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("PROD1-CART1");

        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(null);

        CartEntryDto result = cartEntryService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals("Cart entry not found", result.getMessage());
    }

    @Test
    void deleteByIdentifier_success() {
        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(new CartEntry());

        cartEntryService.deleteByIdentifier("PROD1-CART1");

        verify(cartEntryRepository).deleteByIdentifier("PROD1-CART1");
    }

    @Test
    void deleteByIdentifier_notFound() {
        when(cartEntryRepository.findByIdentifier("PROD1-CART1")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> cartEntryService.deleteByIdentifier("PROD1-CART1"));
    }


    @Test
    void deleteAllByCart_success() {
        List<CartEntry> entries = List.of(new CartEntry());

        when(cartEntryRepository.findByCart("CART1")).thenReturn(entries);

        cartEntryService.deleteAllByCart("CART1");

        verify(cartEntryRepository).deleteAll(entries);
    }

    @Test
    void deleteAllByCart_emptyCart_doesNothing() {
        when(cartEntryRepository.findByCart("CART1")).thenReturn(List.of());

        cartEntryService.deleteAllByCart("CART1");

        verify(cartEntryRepository, never()).deleteAll(any());
    }
}