package com.ust.pos;

import com.ust.pos.cartentry.service.impl.CartentryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartentryServiceTest {

    @InjectMocks
    private CartentryServiceImpl service;

    @Mock
    private CartEntryRepository cartEntryRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ModelMapper modelMapper;

    private Price mockPrice() {
        Price price = new Price();
        price.setPriceAmount(new BigDecimal("50"));
        when(priceRepository.findByProductAndPriceType(any(), any()))
                .thenReturn(price);
        return price;
    }

    private CartEntry mockCartEntry() {
        CartEntry entry = new CartEntry();
        entry.setProduct("P1");
        entry.setQuantity(new BigDecimal("2"));
        entry.setTotalPrice(new BigDecimal("100"));
        entry.setDiscount(new BigDecimal("10"));
        return entry;
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<CartEntry> page = new PageImpl<>(List.of(new CartEntry()), pageable, 1);

        when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(new CartEntryDto()));

        WsDto<CartEntryDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findByIdentifierTest() {
        CartEntry entry = new CartEntry();
        CartEntryDto dto = new CartEntryDto();

        when(cartEntryRepository.findByIdentifier("C1")).thenReturn(entry);
        when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(dto);

        assertNotNull(service.findByIdentifier("C1"));
    }

    @Test
    void saveNewTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setCartId("C1");
        dto.setQuantity(new BigDecimal("2"));

        mockPrice();
        when(cartEntryRepository.findByIdentifier("P1_C1")).thenReturn(null);
        when(modelMapper.map(any(), eq(CartEntry.class))).thenReturn(new CartEntry());
        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of());

        when(cartRepository.findByIdentifier("C1")).thenReturn(new Cart());

        CartEntryDto result = service.save(dto);

        assertEquals(new BigDecimal("100"), result.getTotalPrice());
        verify(cartEntryRepository).save(any());
    }

    @Test
    void saveExistingUpdateTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setCartId("C1");
        dto.setQuantity(new BigDecimal("2"));

        CartEntry existing = mockCartEntry();
        existing.setDeleted(false);

        mockPrice();

        when(cartEntryRepository.findByIdentifier("P1_C1")).thenReturn(existing);
        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of(existing));
        when(cartRepository.findByIdentifier("C1")).thenReturn(new Cart());

        service.save(dto);

        verify(cartEntryRepository).save(existing);
    }

    @Test
    void saveExistingDeletedTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setCartId("C1");
        dto.setQuantity(new BigDecimal("1"));

        CartEntry existing = new CartEntry();
        existing.setDeleted(true);

        mockPrice();
        when(cartEntryRepository.findByIdentifier("P1_C1")).thenReturn(existing);

        CartEntryDto result = service.save(dto);

        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void recalculateTest() {
        CartEntry entry = mockCartEntry();

        mockPrice();
        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of(entry));
        when(cartRepository.findByIdentifier("C1")).thenReturn(new Cart());

        service.recalculate("C1");

        Cart cart = cartRepository.findByIdentifier("C1");

        assertEquals(new BigDecimal("100"), cart.getTotalPrice());
        assertEquals(new BigDecimal("10"), cart.getDiscount());
    }

    @Test
    void deleteTest() {
        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of());
        when(cartRepository.findByIdentifier("C1")).thenReturn(new Cart());

        service.delete("ID1", "C1");

        verify(cartEntryRepository).deleteByIdentifier("ID1");
    }

    @Test
    void deleteAllByCartIdTest() {
        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of());
        when(cartRepository.findByIdentifier("C1")).thenReturn(new Cart());

        service.deleteAllByCartId("C1");

        verify(cartEntryRepository).deleteAllByCartId("C1");
    }

    @Test
    void getMrpPriceTest() {
        Price price = new Price();
        price.setPriceAmount(new BigDecimal("100"));

        when(priceRepository.findByProductAndPriceType("P1", "MRP")).thenReturn(price);

        assertEquals(new BigDecimal("100"), service.getMrpPrice("P1"));
    }

    @Test
    void getSellingPriceTest() {
        Price price = new Price();
        price.setPriceAmount(new BigDecimal("80"));

        when(priceRepository.findByProductAndPriceType("P1", "SELLING_PRICE")).thenReturn(price);

        assertEquals(new BigDecimal("80"), service.getSellingPrice("P1"));
    }

    @Test
    void getDiscountTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setQuantity(new BigDecimal("2"));

        Price mrp = new Price();
        mrp.setPriceAmount(new BigDecimal("100"));
        Price sp = new Price();
        sp.setPriceAmount(new BigDecimal("80"));

        when(priceRepository.findByProductAndPriceType("P1", "MRP")).thenReturn(mrp);
        when(priceRepository.findByProductAndPriceType("P1", "SELLING_PRICE")).thenReturn(sp);

        BigDecimal result = service.getDiscount(dto);

        assertEquals(new BigDecimal("40"), result);
    }

    @Test
    void getTotalPriceTest() {
        Price sp = new Price();
        sp.setPriceAmount(new BigDecimal("50"));

        when(priceRepository.findByProductAndPriceType("P1", "SELLING_PRICE")).thenReturn(sp);

        BigDecimal result = service.getTotalPrice("P1", new BigDecimal("2"));

        assertEquals(new BigDecimal("100"), result);
    }

    @Test
    void updateQuantityExistingTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setCartId("C1");
        dto.setQuantity(new BigDecimal("2"));

        CartEntry existing = mockCartEntry();

        mockPrice();

        when(cartEntryRepository.findByIdentifier("P1_C1")).thenReturn(existing);
        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of(existing));
        when(cartRepository.findByIdentifier("C1")).thenReturn(new Cart());

        service.updateQuantity(dto);

        verify(cartEntryRepository).save(existing);
    }

    @Test
    void updateQuantityNewTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setCartId("C1");
        dto.setQuantity(new BigDecimal("2"));

        mockPrice();

        when(cartEntryRepository.findByIdentifier("P1_C1")).thenReturn(null);
        when(modelMapper.map(any(), eq(CartEntry.class))).thenReturn(new CartEntry());
        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of());
        when(cartRepository.findByIdentifier("C1")).thenReturn(new Cart());

        service.updateQuantity(dto);

        verify(cartEntryRepository).save(any());
    }

    @Test
    void findByCartIdTest() {
        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of(new CartEntry()));
        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(new CartEntryDto()));

        List<CartEntryDto> result = service.findByCartId("C1");

        assertEquals(1, result.size());
    }
}