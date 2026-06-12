package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.CartEntry;
import com.ust.pos.modell.CartEntryRepository;
import com.ust.pos.price.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceService priceService;

    @Mock
    private CartService cartService;

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
    void Save_ProductIdMissing() {
        dto.setProductIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
    }

    @Test
    void Save_InvalidQuantity() {
        dto.setQuantity(0);
        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
    }

    @Test
    void Save_PriceNotConfigured() {
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(null);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> service.save(dto));
    }

    @Test
    void Save_NewEntry_SellingPriceOnly() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(null);
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        CartEntry saved = new CartEntry();
        when(cartEntryRepository.save(any())).thenReturn(saved);
        when(modelMapper.map(saved, CartEntryDto.class)).thenReturn(new CartEntryDto());
        CartEntryDto result = service.save(dto);
        assertNotNull(result);
        verify(cartEntryRepository).save(any());
    }

    @Test
    void Save_NewEntry_MrpOnly() {
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(200));
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(null);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(mrp);
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        CartEntry saved = new CartEntry();
        when(cartEntryRepository.save(any())).thenReturn(saved);
        when(modelMapper.map(saved, CartEntryDto.class)).thenReturn(new CartEntryDto());
        CartEntryDto result = service.save(dto);
        assertNotNull(result);
    }

    @Test
    void Save_DiscountCalculation() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(80));
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(100));
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(mrp);
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        when(cartEntryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());
        service.save(dto);
        verify(cartEntryRepository).save(argThat(entry -> entry.getDiscount().compareTo(BigDecimal.valueOf(20)) == 0
        ));
    }

    @Test
    void Save_DiscountNeverNegative() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(120));
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(100));
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(mrp);
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        when(cartEntryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());
        service.save(dto);
        verify(cartEntryRepository).save(argThat(entry -> entry.getDiscount().compareTo(BigDecimal.ZERO) == 0
        ));
    }

    @Test
    void Save_UpdateExistingEntry() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(50));
        CartEntry existing = new CartEntry();
        existing.setIdentifier("C1-P1");
        existing.setQuantity(3);
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(null);
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(existing);
        when(cartEntryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());
        service.save(dto);
        verify(cartEntryRepository).save(argThat(entry ->
                entry.getQuantity() == 5 // 3 + 2
        ));
    }

    @Test
    void Save_UnitPriceNullHandling() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(null);
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(null);
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        when(cartEntryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());
        service.save(dto);
        verify(cartEntryRepository).save(argThat(entry -> entry.getUnitPrice().compareTo(BigDecimal.ZERO) == 0
        ));
    }

    @Test
    void FindByIdentifier_Success() {
        CartEntry entry = new CartEntry();
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(entry);
        when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(new CartEntryDto());
        CartEntryDto result = service.findByIdentifier("C1-P1");
        assertNotNull(result);
    }

    @Test
    void FindByIdentifier_NotFound() {
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        CartEntryDto result = service.findByIdentifier("C1-P1");
        assertNull(result);
    }

    @Test
    void FindAll() {
        Pageable pageable = PageRequest.of(0, 2);
        CartEntry entry = new CartEntry();
        List<CartEntry> list = Arrays.asList(entry);
        Page<CartEntry> page = new PageImpl<>(list, pageable, list.size());
        when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(List.class), any(java.lang.reflect.Type.class)
        )).thenReturn(Arrays.asList(new CartEntryDto()));
        WsDto<CartEntryDto> result = service.findAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertNotNull(result.getDtoList());
        assertEquals(1, result.getDtoList().size());
    }
}
