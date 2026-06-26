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
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.Arrays;
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
    void save_ProductIdentifierNull() {
        dto.setProductIdentifier(null);
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.save(dto));
        assertEquals("Product identifier is missing", ex.getMessage());
    }

    @Test
    void save_ProductIdentifierBlank() {
        dto.setProductIdentifier("");
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.save(dto));
        assertEquals("Product identifier is missing", ex.getMessage());
    }

    @Test
    void save_RemoveUsingMinus1000_ExistingEntry() {
        CartEntry existing = new CartEntry();
        existing.setIdentifier("C1-P1");
        existing.setQuantity(5);
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(existing);
        dto.setQuantity(-1000);
        CartEntryDto result = service.save(dto);
        verify(cartEntryRepository).delete(existing);
        assertEquals(0, result.getQuantity());
        assertEquals("P1", result.getProductIdentifier());
        assertEquals("C1", result.getCartIdentifier());
    }

    @Test
    void save_RemoveUsingMinus1000_NoExistingEntry() {
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        dto.setQuantity(-1000);
        CartEntryDto result = service.save(dto);
        verify(cartEntryRepository, never()).delete(any());
        assertEquals(0, result.getQuantity());
    }

    @Test
    void save_RemoveWhenQuantityBecomesZero() {
        CartEntry existing = new CartEntry();
        existing.setIdentifier("C1-P1");
        existing.setQuantity(2);
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(existing);
        dto.setQuantity(-2);
        CartEntryDto result = service.save(dto);
        verify(cartEntryRepository).delete(existing);
        assertEquals(0, result.getQuantity());
    }

    @Test
    void save_RemoveWhenQuantityBecomesNegative() {
        CartEntry existing = new CartEntry();
        existing.setIdentifier("C1-P1");
        existing.setQuantity(2);
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(existing);
        dto.setQuantity(-3);
        CartEntryDto result = service.save(dto);
        verify(cartEntryRepository).delete(existing);
        assertEquals(0, result.getQuantity());
    }

    @Test
    void save_PriceNotConfigured() {
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(null);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(null);
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.save(dto));
        assertEquals("Price not configured for product: P1", ex.getMessage());
    }

    @Test
    void save_NewEntry_SellingPriceOnly() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(null);
        when(cartEntryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());
        CartEntryDto result = service.save(dto);
        assertNotNull(result);
        verify(cartEntryRepository).save(argThat(entry -> entry.getUnitPrice().compareTo(BigDecimal.valueOf(100)) == 0)
        );
    }

    @Test
    void save_NewEntry_MrpOnly() {
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(200));
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(null);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(mrp);
        when(cartEntryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());
        CartEntryDto result = service.save(dto);
        assertNotNull(result);
        verify(cartEntryRepository).save(argThat(entry -> entry.getUnitPrice().compareTo(BigDecimal.valueOf(200)) == 0)
        );
    }

    @Test
    void save_SellingAndMrp_DiscountApplied() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(80));
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(100));
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(mrp);
        when(cartEntryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());
        service.save(dto);
        verify(cartEntryRepository).save(argThat(entry -> entry.getDiscount().compareTo(BigDecimal.valueOf(20)) == 0)
        );
    }

    @Test
    void save_ExistingEntryQuantityUpdated() {
        CartEntry existing = new CartEntry();
        existing.setIdentifier("C1-P1");
        existing.setQuantity(3);
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(existing);
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(null);
        when(cartEntryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());
        service.save(dto);
        verify(cartEntryRepository).save(argThat(entry -> entry.getQuantity() == 5 && entry.getTotalPrice().compareTo(BigDecimal.valueOf(500)) == 0)
        );
    }

    @Test
    void save_NewEntry_TotalPriceCalculated() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(50));
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        when(priceService.findByIdentifier("P1-SELLING")).thenReturn(selling);
        when(priceService.findByIdentifier("P1-MRP")).thenReturn(null);
        when(cartEntryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(modelMapper.map(any(), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());
        service.save(dto);
        verify(cartEntryRepository).save(argThat(entry -> entry.getTotalPrice().compareTo(BigDecimal.valueOf(100)) == 0)
        );
    }

    @Test
    void findByIdentifier_Success() {
        CartEntry entry = new CartEntry();
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(entry);
        when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(new CartEntryDto());
        CartEntryDto result = service.findByIdentifier("C1-P1");
        assertNotNull(result);
    }

    @Test
    void findByIdentifier_NullEntity() {
        when(cartEntryRepository.findByIdentifier("C1-P1")).thenReturn(null);
        when(modelMapper.map(null, CartEntryDto.class)).thenReturn(null);
        CartEntryDto result = service.findByIdentifier("C1-P1");
        assertNull(result);
    }

    @Test
    void findAll_Success() {
        Pageable pageable = PageRequest.of(0, 2);
        CartEntry entry = new CartEntry();
        List<CartEntry> list = Arrays.asList(entry);
        Page<CartEntry> page = new PageImpl<>(list, pageable, list.size());
        when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(List.class), any(java.lang.reflect.Type.class))).thenReturn(List.of(new CartEntryDto()));
        WsDto<CartEntryDto> result = service.findAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertEquals(1, result.getDtoList().size());
    }

    @Test
    void findAll_EmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CartEntry> page = new PageImpl<>(List.of(), pageable, 0);
        when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(List.class), any(java.lang.reflect.Type.class))).thenReturn(List.of());
        WsDto<CartEntryDto> result = service.findAll(pageable);
        assertNotNull(result);
        assertEquals(0, result.getTotalRecords());
        assertEquals(0, result.getDtoList().size());
        assertEquals(0, result.getTotalPage());
    }
}