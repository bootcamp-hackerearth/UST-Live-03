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
import org.springframework.data.domain.*;

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

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<CartEntry> list = List.of(new CartEntry());
        Page<CartEntry> page = new PageImpl<>(list);

        when(cartEntryRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(new CartEntryDto()));

        WsDto<CartEntryDto> result = service.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(cartEntryRepository).findAll(pageable);
    }

    @Test
    void findByIdentifierTest() {
        CartEntry entry = new CartEntry();
        CartEntryDto dto = new CartEntryDto();

        when(cartEntryRepository.findByIdentifier("ID")).thenReturn(entry);
        when(modelMapper.map(entry, CartEntryDto.class)).thenReturn(dto);

        assertNotNull(service.findByIdentifier("ID"));
    }

    @Test
    void saveNewTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setCartId("C1");
        dto.setQuantity(BigDecimal.ONE);

        Price price = new Price();
        price.setPriceAmount(new BigDecimal("100"));

        when(priceRepository.findByProductAndPriceType(any(), eq("MRP"))).thenReturn(price);
        when(priceRepository.findByProductAndPriceType(any(), eq("SELLING_PRICE"))).thenReturn(price);
        when(cartEntryRepository.findByIdentifier(any())).thenReturn(null);
        when(modelMapper.map(any(), eq(CartEntry.class))).thenReturn(new CartEntry());

        Cart cart = new Cart();
        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);
        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of());

        CartEntryDto result = service.save(dto);

        assertNotNull(result);
        verify(cartEntryRepository).save(any());
    }

    @Test
    void saveExistingTest() {
        CartEntryDto dto = new CartEntryDto();
        dto.setProduct("P1");
        dto.setCartId("C1");
        dto.setQuantity(BigDecimal.ONE);

        CartEntry existing = new CartEntry();
        existing.setQuantity(BigDecimal.ONE);
        existing.setTotalPrice(new BigDecimal("100"));
        existing.setDiscount(new BigDecimal("10"));

        Price price = new Price();
        price.setPriceAmount(new BigDecimal("100"));

        when(priceRepository.findByProductAndPriceType(any(), eq("MRP"))).thenReturn(price);
        when(priceRepository.findByProductAndPriceType(any(), eq("SELLING_PRICE"))).thenReturn(price);
        when(cartEntryRepository.findByIdentifier(any())).thenReturn(existing);
        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of(existing));

        Cart cart = new Cart();
        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);

        CartEntryDto result = service.save(dto);

        assertNotNull(result);
        verify(cartEntryRepository).save(existing);
    }

    @Test
    void recalculateTest() {
        CartEntry entry = new CartEntry();
        entry.setProduct("P1");
        entry.setQuantity(BigDecimal.ONE);
        entry.setTotalPrice(new BigDecimal("100"));
        entry.setDiscount(new BigDecimal("10"));

        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of(entry));

        Price price = new Price();
        price.setPriceAmount(new BigDecimal("100"));
        when(priceRepository.findByProductAndPriceType("P1", "MRP")).thenReturn(price);

        Cart cart = new Cart();
        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);

        service.recalculate("C1");

        verify(cartRepository).save(cart);
    }

    @Test
    void deleteTest() {

        Cart cart = new Cart();

        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);
        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of());

        service.delete("ID", "C1");

        verify(cartEntryRepository).deleteByIdentifier("ID");
        verify(cartRepository).save(cart);
    }

    @Test
    void deleteAllTest() {

        Cart cart = new Cart();

        when(cartRepository.findByIdentifier("C1")).thenReturn(cart);
        when(cartEntryRepository.findByCartId("C1")).thenReturn(List.of());

        service.deleteAllByCartId("C1");

        verify(cartEntryRepository).deleteAllByCartId("C1");
        verify(cartRepository).save(cart);
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
        dto.setQuantity(BigDecimal.ONE);

        Price mrp = new Price();
        mrp.setPriceAmount(new BigDecimal("100"));
        Price selling = new Price();
        selling.setPriceAmount(new BigDecimal("80"));

        when(priceRepository.findByProductAndPriceType("P1", "MRP")).thenReturn(mrp);
        when(priceRepository.findByProductAndPriceType("P1", "SELLING_PRICE")).thenReturn(selling);

        assertEquals(new BigDecimal("20"), service.getDiscount(dto));
    }
}