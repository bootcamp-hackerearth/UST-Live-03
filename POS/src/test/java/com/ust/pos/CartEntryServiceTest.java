package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.CartEntry;
import com.ust.pos.model.CartEntryRepository;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Type;
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

    private CartEntryDto cartEntryDto;
    private CartEntry cartEntry;
    private Price price;

    @BeforeEach
    void setUp() {

        cartEntryDto = new CartEntryDto();
        cartEntryDto.setProduct("P1");
        cartEntryDto.setCart("C1");
        cartEntryDto.setQuantity(BigDecimal.ONE);

        cartEntry = new CartEntry();
        cartEntry.setIdentifier("P1-C1");
        cartEntry.setQuantity(BigDecimal.ONE);
        cartEntry.setStatus(true);

        price = new Price();
        price.setIdentifier("P1");
        price.setMrp(BigDecimal.valueOf(120));
        price.setSellingPrice(BigDecimal.valueOf(100));
    }

    @Test
    void testSave_NewCartEntry() {

        when(cartEntryRepository.findByIdentifier("P1-C1"))
                .thenReturn(null);

        when(priceRepository.findByIdentifier("P1"))
                .thenReturn(price);

        doNothing().when(modelMapper)
                .map(eq(cartEntryDto), any(CartEntry.class));

        CartEntryDto result = cartEntryService.save(cartEntryDto);

        assertNotNull(result);
        assertEquals(BigDecimal.ONE, result.getQuantity());
        assertEquals(BigDecimal.valueOf(120), result.getPrice());
        assertEquals(BigDecimal.valueOf(100), result.getSellingPrice());
        assertEquals(BigDecimal.valueOf(100), result.getTotalPrice());
        assertEquals(BigDecimal.valueOf(20), result.getDiscount());

        verify(cartEntryRepository).save(any(CartEntry.class));
    }

    @Test
    void testSave_ExistingCartEntry() {

        cartEntry.setQuantity(BigDecimal.valueOf(2));

        when(cartEntryRepository.findByIdentifier("P1-C1"))
                .thenReturn(cartEntry);

        when(priceRepository.findByIdentifier("P1"))
                .thenReturn(price);

        doNothing().when(modelMapper)
                .map(cartEntryDto, cartEntry);

        CartEntryDto result = cartEntryService.save(cartEntryDto);

        assertEquals(BigDecimal.valueOf(3), result.getQuantity());
        assertEquals(BigDecimal.valueOf(300), result.getTotalPrice());
        assertEquals(BigDecimal.valueOf(60), result.getDiscount());

        verify(cartEntryRepository).save(cartEntry);
    }

    @Test
    void testDeleteAllByCart_WithEntries() {

        List<CartEntry> entries = List.of(cartEntry);

        when(cartEntryRepository.findByCart("C1"))
                .thenReturn(entries);

        cartEntryService.deleteAllByCart("C1");

        verify(cartEntryRepository).deleteAll(entries);
    }

    @Test
    void testDeleteAllByCart_NoEntries() {

        when(cartEntryRepository.findByCart("C1"))
                .thenReturn(Collections.emptyList());

        cartEntryService.deleteAllByCart("C1");

        verify(cartEntryRepository, never()).deleteAll(any());
    }

    @Test
    void testDeleteByIdentifier() {

        when(cartEntryRepository.findByIdentifier("P1-C1"))
                .thenReturn(cartEntry);

        cartEntryService.deleteByIdentifier("P1-C1");

        verify(cartEntryRepository).deleteByIdentifier("P1-C1");
    }

    @Test
    void testDeleteByIdentifier_NotFound() {

        when(cartEntryRepository.findByIdentifier("P1-C1"))
                .thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> cartEntryService.deleteByIdentifier("P1-C1")
        );
    }

    @Test
    void testFindAllCarts() {

        List<CartEntry> entries = List.of(cartEntry);
        List<CartEntryDto> dtoList = List.of(cartEntryDto);

        when(cartEntryRepository.findByCart("C1"))
                .thenReturn(entries);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(dtoList);

        List<CartEntryDto> result =
                cartEntryService.findAllCarts("C1");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindActiveStatus() {

        CartEntry inactive = new CartEntry();
        inactive.setStatus(false);

        when(cartEntryRepository.findAll())
                .thenReturn(List.of(cartEntry, inactive));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(cartEntryDto));

        List<CartEntryDto> result =
                cartEntryService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}