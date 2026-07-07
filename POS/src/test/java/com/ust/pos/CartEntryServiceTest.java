package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Cart;
import com.ust.pos.modell.CartEntry;
import com.ust.pos.modell.CartEntryRepository;
import com.ust.pos.price.service.PriceService;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartEntryServiceTest {

    public static final String CART_1 = "CART1";
    public static final String PROD_1 = "PROD1";
    public static final String PROD_1_MRP = "PROD1-MRP";
    public static final String PROD_1_SELLING = "PROD1-SELLING";
    public static final String PROD_2 = "CART1-PROD1";
    @InjectMocks
    private CartEntryServiceImpl service;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PriceService priceService;

    @Test
    void saveValidationAndRemovalTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier(CART_1);
        dto.setProductIdentifier(null);

        Exception ex =
                assertThrows(
                        RuntimeException.class,
                        () -> service.save(dto)
                );

        assertEquals(
                "Product identifier is missing",
                ex.getMessage()
        );

        dto.setProductIdentifier(" ");

        ex =
                assertThrows(
                        RuntimeException.class,
                        () -> service.save(dto)
                );

        assertEquals(
                "Product identifier is missing",
                ex.getMessage()
        );

        CartEntry deleteEntry = new CartEntry();
        deleteEntry.setDeleted(false);
        deleteEntry.setQuantity(5);

        dto.setProductIdentifier(PROD_1);
        dto.setQuantity(-1000);

        when(cartEntryRepository.findByIdentifier(PROD_2))
                .thenReturn(deleteEntry);

        CartEntryDto result = service.save(dto);

        assertEquals(0, result.getQuantity());

        CartEntry qtyZeroEntry = new CartEntry();
        qtyZeroEntry.setDeleted(false);
        qtyZeroEntry.setQuantity(5);

        dto.setQuantity(-5);

        when(cartEntryRepository.findByIdentifier(PROD_2))
                .thenReturn(qtyZeroEntry);

        result = service.save(dto);

        assertEquals(0, result.getQuantity());

        verify(cartEntryRepository, times(2))
                .save(any(CartEntry.class));
    }

    @Test
    void savePriceValidationAndCreateScenariosTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier(CART_1);
        dto.setProductIdentifier(PROD_1);
        dto.setQuantity(2);

        when(cartEntryRepository.findByIdentifier(PROD_2))
                .thenReturn(null);

        when(priceService.findByIdentifier(anyString()))
                .thenReturn(null);

        Exception ex =
                assertThrows(
                        RuntimeException.class,
                        () -> service.save(dto)
                );

        assertEquals(
                "Price not configured for product: PROD1",
                ex.getMessage()
        );

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        when(priceService.findByIdentifier(PROD_1_SELLING))
                .thenReturn(selling);

        when(priceService.findByIdentifier(PROD_1_MRP))
                .thenReturn(null);

        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class)))
                .thenReturn(new CartEntryDto());

        service.save(dto);

        PriceDto mrp = new PriceDto();
        mrp.setPriceAmount(BigDecimal.valueOf(150));

        when(priceService.findByIdentifier(PROD_1_SELLING))
                .thenReturn(null);

        when(priceService.findByIdentifier(PROD_1_MRP))
                .thenReturn(mrp);

        service.save(dto);

        PriceDto discountSelling = new PriceDto();
        discountSelling.setPriceAmount(BigDecimal.valueOf(80));

        PriceDto discountMrp = new PriceDto();
        discountMrp.setPriceAmount(BigDecimal.valueOf(100));

        when(priceService.findByIdentifier(PROD_1_SELLING))
                .thenReturn(discountSelling);

        when(priceService.findByIdentifier(PROD_1_MRP))
                .thenReturn(discountMrp);

        service.save(dto);

        ArgumentCaptor<CartEntry> captor =
                ArgumentCaptor.forClass(CartEntry.class);

        verify(cartEntryRepository, atLeastOnce())
                .save(captor.capture());

        CartEntry saved =
                captor.getAllValues()
                        .get(captor.getAllValues().size() - 1);

        assertEquals(
                BigDecimal.valueOf(80),
                saved.getUnitPrice()
        );

        assertEquals(
                BigDecimal.valueOf(20),
                saved.getDiscount()
        );
    }

    @Test
    void saveExistingAndSoftDeletedEntryTest() {

        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier(CART_1);
        dto.setProductIdentifier(PROD_1);
        dto.setQuantity(2);

        PriceDto selling = new PriceDto();
        selling.setPriceAmount(BigDecimal.valueOf(100));

        when(priceService.findByIdentifier(anyString()))
                .thenReturn(selling);

        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class)))
                .thenReturn(new CartEntryDto());

        CartEntry existing = new CartEntry();
        existing.setDeleted(false);
        existing.setQuantity(1);
        existing.setStatus(true);

        when(cartEntryRepository.findByIdentifier(PROD_2))
                .thenReturn(existing);

        service.save(dto);

        assertEquals(3, existing.getQuantity());

        CartEntry softDeleted = new CartEntry();
        softDeleted.setDeleted(true);
        softDeleted.setStatus(null);

        when(cartEntryRepository.findByIdentifier(PROD_2))
                .thenReturn(softDeleted);

        service.save(dto);

        assertFalse(softDeleted.getDeleted());
        assertTrue(softDeleted.getStatus());
    }

    @Test
    void findByIdentifierTest() {

        CartEntry entry = new CartEntry();
        CartEntryDto dto = new CartEntryDto();

        when(cartEntryRepository.findByIdentifierAndDeletedFalse("ID"))
                .thenReturn(entry);

        when(modelMapper.map(entry, CartEntryDto.class))
                .thenReturn(dto);

        assertNotNull(service.findByIdentifier("ID"));

        when(cartEntryRepository.findByIdentifierAndDeletedFalse("INVALID"))
                .thenReturn(null);

        ResourceNotFoundException ex =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findByIdentifier("INVALID")
                );

        assertEquals(
                "CartEntry with identifier 'INVALID' not found",
                ex.getMessage()
        );
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<CartEntry> page =
                new PageImpl<>(
                        List.of(new CartEntry()),
                        pageable,
                        1
                );

        when(cartEntryRepository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new CartEntryDto()));

        WsDto<CartEntryDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
    }

    @SuppressWarnings("unchecked")
    @Test
    void specificationFindAllTest() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Cart> page =
                new PageImpl<>(
                        List.of(new Cart()),
                        pageable,
                        1
                );

        when(cartEntryRepository.findAll(
                any(Specification.class),
                eq(pageable)))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new CartEntryDto()));

        Specification specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<CartEntryDto> result =
                service.findAll(specification, pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());

        verify(cartEntryRepository)
                .findAll(any(Specification.class), eq(pageable));
    }
}