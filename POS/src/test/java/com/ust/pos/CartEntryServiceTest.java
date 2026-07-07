package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.CartEntry;
import com.ust.pos.models.CartEntryRepository;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceService priceService;

    @InjectMocks
    private CartEntryServiceImpl service;

    private CartEntryDto dto;

    @BeforeEach
    void setup() {
        dto = new CartEntryDto();
        dto.setCartIdentifier("C1");
        dto.setProductIdentifier("P1");
        dto.setQuantity(2);
    }

    @Test
    void saveValidationAndRemoveTest() {
        dto.setProductIdentifier(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        assertEquals("Product identifier is missing", ex.getMessage());
        dto.setProductIdentifier("");
        ex = assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        assertEquals("Product identifier is missing", ex.getMessage());
        CartEntry entry = new CartEntry();
        entry.setIdentifier("C1-P1");
        entry.setQuantity(5);
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(entry);
        dto.setProductIdentifier("P1");
        dto.setQuantity(-1000);
        CartEntryDto result = service.save(dto);
        verify(cartEntryRepository).delete(entry);
        assertEquals(0, result.getQuantity());
        CartEntry entry2 = new CartEntry();
        entry2.setIdentifier("C1-P1");
        entry2.setQuantity(2);
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(entry2);
        dto.setQuantity(-2);
        result = service.save(dto);
        assertEquals(0, result.getQuantity());
        verify(cartEntryRepository, atLeast(2)).delete(any());
    }

    @Test
    void savePriceAndQuantityCalculationTest() {
        when(cartEntryRepository.save(any(CartEntry.class))).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(null);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.save(dto));
        assertEquals("Price not configured for product: P1", ex.getMessage());
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(null);
        service.save(dto);
        verify(cartEntryRepository).save(argThat(e -> e.getUnitPrice().compareTo(BigDecimal.valueOf(100)) == 0));
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(200));
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(null);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(mrp);
        service.save(dto);
        verify(cartEntryRepository).save(argThat(e -> e.getUnitPrice().compareTo(BigDecimal.valueOf(200)) == 0));
        selling.setPriceAmount(BigDecimal.valueOf(80));
        mrp.setPriceAmount(BigDecimal.valueOf(100));
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(mrp);
        service.save(dto);
        verify(cartEntryRepository).save(argThat(e -> e.getDiscount().compareTo(BigDecimal.valueOf(20)) == 0));
    }

    @Test
    void saveExistingEntryUpdateTest() {
        CartEntry existing = new CartEntry();
        existing.setIdentifier("C1-P1");
        existing.setQuantity(3);
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(existing);
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(null);
        when(cartEntryRepository.save(any(CartEntry.class))).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());
        service.save(dto);
        verify(cartEntryRepository).save(argThat(e -> e.getQuantity() == 5 && e.getTotalPrice().compareTo(BigDecimal.valueOf(500)) == 0));
    }

    @Test
    void findByIdentifierTest() {
        CartEntry entry = new CartEntry();
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(entry);
        when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(new CartEntryDto());
        CartEntryDto result = service.findByIdentifier("C1-P1");
        assertNotNull(result);
        when(cartEntryRepository.findByIdentifier("EMPTY")).thenReturn(null);
        when(modelMapper.map(null, CartEntryDto.class)).thenReturn(null);
        assertNull(service.findByIdentifier("EMPTY"));
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<CartEntry> page = new PageImpl<>(List.of(new CartEntry()), pageable, 1);
        when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(List.class), any(Type.class))).thenReturn(List.of(new CartEntryDto()));
        WsDto<CartEntryDto> result = service.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        Page<CartEntry> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(cartEntryRepository.findAll(pageable)).thenReturn(emptyPage);
        when(modelMapper.map(any(List.class), any(Type.class))).thenReturn(List.of());
        result = service.findAll(pageable);
        assertEquals(0, result.getTotalRecords());
        assertTrue(result.getDtoList().isEmpty());
    }
}