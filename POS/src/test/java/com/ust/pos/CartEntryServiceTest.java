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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    public static final String PROD_1_MRP = "PROD1-MRP";
    public static final String PROD_1_MRP1 = PROD_1_MRP;
    public static final String PROD_1_SELLING = "PROD1-SELLING";
    public static final String CART_1_PROD_1 = "CART1-PROD1";
    @InjectMocks
    private CartEntryServiceImpl service;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceService priceService;

    @Mock
    private CartService cartService;

    private CartEntryDto dto;
    private CartEntry entry;

    @BeforeEach
    void setUp() {
        dto = new CartEntryDto();
        dto.setCartIdentifier("CART1");
        dto.setProductIdentifier("PROD1");
        dto.setQuantity(2);

        entry = new CartEntry();
        entry.setIdentifier(CART_1_PROD_1);
        entry.setProductIdentifier("PROD1");
        entry.setCartIdentifier("CART1");
        entry.setQuantity(1);
        entry.setDeleted(false);
        entry.setStatus(true);
    }

    @Test
    void saveNewEntryWithSellingPrice() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        when(cartEntryRepository.findByIdentifier(CART_1_PROD_1)).thenReturn(null);

        when(priceService.findByIdentifier(PROD_1_SELLING)).thenReturn(selling);

        when(priceService.findByIdentifier(PROD_1_MRP1)).thenReturn(null);

        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());

        CartEntryDto result = service.save(dto);

        assertNotNull(result);

        verify(cartEntryRepository).save(any(CartEntry.class));
    }

    @Test
    void saveNewEntryWithMrpPriceOnly() {
        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(150));

        when(cartEntryRepository.findByIdentifier(CART_1_PROD_1)).thenReturn(null);

        when(priceService.findByIdentifier(PROD_1_SELLING)).thenReturn(null);

        when(priceService.findByIdentifier(PROD_1_MRP1)).thenReturn(mrp);

        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());

        service.save(dto);

        verify(cartEntryRepository).save(any(CartEntry.class));
    }

    @Test
    void saveWithDiscountCalculation() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(80));

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(100));

        when(cartEntryRepository.findByIdentifier(CART_1_PROD_1)).thenReturn(null);

        when(priceService.findByIdentifier(PROD_1_SELLING)).thenReturn(selling);

        when(priceService.findByIdentifier(PROD_1_MRP1)).thenReturn(mrp);

        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());

        service.save(dto);

        ArgumentCaptor<CartEntry> captor = ArgumentCaptor.forClass(CartEntry.class);

        verify(cartEntryRepository).save(captor.capture());

        CartEntry saved = captor.getValue();

        assertEquals(BigDecimal.valueOf(80), saved.getUnitPrice());
        assertEquals(BigDecimal.valueOf(20), saved.getDiscount());
        assertEquals(BigDecimal.valueOf(160), saved.getTotalPrice());
    }

    @Test
    void saveExistingEntry() {
        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        when(cartEntryRepository.findByIdentifier(CART_1_PROD_1)).thenReturn(entry);

        when(priceService.findByIdentifier(anyString())).thenReturn(selling);

        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());

        service.save(dto);

        verify(cartEntryRepository).save(entry);

        assertEquals(3, entry.getQuantity());
    }

    @Test
    void saveResurrectSoftDeletedEntry() {
        entry.setDeleted(true);

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        when(cartEntryRepository.findByIdentifier(CART_1_PROD_1)).thenReturn(entry);

        when(priceService.findByIdentifier(anyString())).thenReturn(selling);

        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class))).thenReturn(new CartEntryDto());

        service.save(dto);

        verify(cartEntryRepository).save(entry);

        assertFalse(entry.getDeleted());
    }

    @Test
    void saveDeleteFlag() {
        dto.setQuantity(-1000);

        when(cartEntryRepository.findByIdentifier(CART_1_PROD_1)).thenReturn(entry);

        CartEntryDto result = service.save(dto);

        assertEquals(0, result.getQuantity());

        verify(cartEntryRepository).save(entry);
    }

    @Test
    void saveQuantityBecomesZero() {
        dto.setQuantity(-1);

        when(cartEntryRepository.findByIdentifier(CART_1_PROD_1)).thenReturn(entry);

        CartEntryDto result = service.save(dto);

        assertEquals(0, result.getQuantity());

        verify(cartEntryRepository).save(entry);
    }

    @Test
    void saveProductIdentifierMissing() {
        dto.setProductIdentifier(null);

        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> service.save(dto));

        assertEquals(
                "Product identifier is missing",
                exception.getMessage()
        );
    }

    @Test
    void saveBlankProductIdentifier() {
        dto.setProductIdentifier(" ");

        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> service.save(dto));

        assertEquals(
                "Product identifier is missing",
                exception.getMessage()
        );
    }

    @Test
    void savePriceNotConfigured() {
        when(cartEntryRepository.findByIdentifier(CART_1_PROD_1))
                .thenReturn(null);

        when(priceService.findByIdentifier(anyString()))
                .thenReturn(null);

        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> service.save(dto));

        assertEquals(
                "Price not configured for product: PROD1",
                exception.getMessage()
        );
    }

    @Test
    void findByIdentifier() {
        CartEntryDto response = new CartEntryDto();

        when(cartEntryRepository.findByIdentifierAndDeletedFalse("ID"))
                .thenReturn(entry);

        when(modelMapper.map(entry, CartEntryDto.class))
                .thenReturn(response);

        CartEntryDto result = service.findByIdentifier("ID");

        assertNotNull(result);

        verify(cartEntryRepository)
                .findByIdentifierAndDeletedFalse("ID");
    }

    @Test
    void findAll() {
        Pageable pageable = PageRequest.of(0, 5);

        Page<CartEntry> page =
                new PageImpl<>(List.of(entry), pageable, 1);

        when(cartEntryRepository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new CartEntryDto()));

        WsDto<CartEntryDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(5, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findAllEmpty() {
        Pageable pageable = PageRequest.of(0, 5);

        Page<CartEntry> page =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(cartEntryRepository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        WsDto<CartEntryDto> result =
                service.findAll(pageable);

        assertTrue(result.getDtoList().isEmpty());
        assertEquals(0, result.getTotalRecords());
    }
}