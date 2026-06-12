package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Test
    void testSave_NewEntry() {

        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setCart("C1");
        dto.setQuantity(BigDecimal.valueOf(2));

        Price price = new Price();
        price.setMrp(BigDecimal.valueOf(120));
        price.setSellingPrice(BigDecimal.valueOf(100));

        when(cartEntryRepository.findByIdentifier("P1-C1")).thenReturn(null);
        when(priceRepository.findByIdentifier("P1")).thenReturn(price);

        CartEntryDto result = cartEntryService.save(dto);

        assertEquals("P1-C1", result.getIdentifier());
        assertEquals(BigDecimal.valueOf(200), result.getTotalPrice());
        assertEquals(BigDecimal.valueOf(40), result.getDiscount());

        verify(cartEntryRepository).save(any(CartEntry.class));
    }

    @Test
    void testSave_ExistingEntry() {

        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setCart("C1");
        dto.setQuantity(BigDecimal.valueOf(2));

        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.valueOf(3));

        Price price = new Price();
        price.setMrp(BigDecimal.valueOf(120));
        price.setSellingPrice(BigDecimal.valueOf(100));

        when(cartEntryRepository.findByIdentifier("P1-C1")).thenReturn(existing);
        when(priceRepository.findByIdentifier("P1")).thenReturn(price);

        CartEntryDto result = cartEntryService.save(dto);

        // 3(existing) + 2(new) = 5
        assertEquals(BigDecimal.valueOf(5), result.getQuantity());
        assertEquals(BigDecimal.valueOf(500), result.getTotalPrice());

        verify(cartEntryRepository).save(existing);
    }

    @Test
    void testDeleteAllByCart() {

        when(cartEntryRepository.findByCart("C1"))
                .thenReturn(Collections.singletonList(new CartEntry()));

        cartEntryService.deleteAllByCart("C1");

        verify(cartEntryRepository).deleteAll(anyList());
    }

    @Test
    void testDeleteAllByCart_Empty() {

        when(cartEntryRepository.findByCart("C1"))
                .thenReturn(Collections.emptyList());

        cartEntryService.deleteAllByCart("C1");

        verify(cartEntryRepository, never()).deleteAll(any());
    }

    @Test
    void testDeleteByIdentifier() {

        CartEntry entry = new CartEntry();

        when(cartEntryRepository.findByIdentifier("P1-C1")).thenReturn(entry);

        cartEntryService.deleteByIdentifier("P1-C1");

        verify(cartEntryRepository).deleteByIdentifier("P1-C1");
    }

    @Test
    void testDeleteByIdentifier_NotFound() {

        when(cartEntryRepository.findByIdentifier("P1-C1")).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                cartEntryService.deleteByIdentifier("P1-C1"));

        assertEquals("CartEntry not found", ex.getMessage());
    }

    @Test
    void testFindAllCarts() {

        List<CartEntry> entityList = Collections.singletonList(new CartEntry());
        List<CartEntryDto> dtoList = Collections.singletonList(new CartEntryDto());

        when(cartEntryRepository.findByCart("C1")).thenReturn(entityList);

        when(modelMapper.map(
                eq(entityList),
                any(java.lang.reflect.Type.class)
        )).thenReturn(dtoList);

        List<CartEntryDto> result = cartEntryService.findAllCarts("C1");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindActiveStatus() {
        CartEntry active = new CartEntry();
        active.setStatus(true);

        CartEntry inactive = new CartEntry();
        inactive.setStatus(false);

        when(cartEntryRepository.findAll())
                .thenReturn(List.of(active, inactive));

        CartEntryDto dto = new CartEntryDto();
        List<CartEntryDto> expectedDtoList = List.of(dto);

        when(modelMapper.map(
                Mockito.eq(List.of(active)),
                Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(expectedDtoList);

        List<CartEntryDto> result = cartEntryService.findActiveStatus(); // Note: Update variable name to cartEntryService if you renamed it

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}