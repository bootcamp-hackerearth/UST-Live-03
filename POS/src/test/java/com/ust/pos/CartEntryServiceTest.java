package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.impl.CartEntryServiceImpl;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.ObjectProvider;

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
    private StockRepository stockRepository;
    @Mock
    private PriceRepository priceRepository;
    @Mock
    private ObjectProvider<CartService> cartServiceProvider;
    @Mock
    private CartService cartService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private CartEntryServiceImpl cartEntryService;

    @Test
    void testSave_NewEntry_Success() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("CUST-001");
        dto.setProductIdentifier("PROD-001");
        dto.setQuantity(BigDecimal.valueOf(2));

        Stock stock = new Stock();
        stock.setQuantity(10);

        Price sellingPrice = new Price();
        sellingPrice.setPriceAmount(BigDecimal.valueOf(100));

        Price mrp = new Price();
        mrp.setPriceAmount(BigDecimal.valueOf(120));

        CartEntryDto responseDto = new CartEntryDto();
        responseDto.setIdentifier("CUST-001-PROD-001");

        when(cartEntryRepository.findByIdentifier("CUST-001-PROD-001")).thenReturn(null);
        when(stockRepository.findByProduct("PROD-001")).thenReturn(stock);
        when(priceRepository.findByProductIdentifierAndPriceType("PROD-001", "SELLING_PRICE"))
                .thenReturn(sellingPrice);
        when(priceRepository.findByProductIdentifierAndPriceType("PROD-001", "MRP"))
                .thenReturn(mrp);
        when(cartServiceProvider.getObject()).thenReturn(cartService);
        when(modelMapper.map(any(CartEntry.class), eq(CartEntryDto.class))).thenReturn(responseDto);

        CartEntryDto result = cartEntryService.save(dto);

        assertTrue(result.isSuccess());

        verify(cartEntryRepository).save(any(CartEntry.class));
        verify(cartService).recalculate("CUST-001");
    }

    @Test
    void testSave_ExistingEntry_Success() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("CUST-001");
        dto.setProductIdentifier("PROD-001");
        dto.setQuantity(BigDecimal.valueOf(2));

        CartEntry existingEntry = new CartEntry();
        existingEntry.setIdentifier("CUST-001-PROD-001");
        existingEntry.setCartIdentifier("CUST-001");
        existingEntry.setProductIdentifier("PROD-001");
        existingEntry.setQuantity(BigDecimal.valueOf(3));

        Stock stock = new Stock();
        stock.setQuantity(10);

        Price sellingPrice = new Price();
        sellingPrice.setPriceAmount(BigDecimal.valueOf(100));

        Price mrp = new Price();
        mrp.setPriceAmount(BigDecimal.valueOf(120));

        CartEntryDto responseDto = new CartEntryDto();

        when(cartEntryRepository.findByIdentifier("CUST-001-PROD-001")).thenReturn(existingEntry);
        when(stockRepository.findByProduct("PROD-001")).thenReturn(stock);
        when(priceRepository.findByProductIdentifierAndPriceType("PROD-001", "SELLING_PRICE"))
                .thenReturn(sellingPrice);
        when(priceRepository.findByProductIdentifierAndPriceType("PROD-001", "MRP"))
                .thenReturn(mrp);
        when(cartServiceProvider.getObject()).thenReturn(cartService);
        when(modelMapper.map(existingEntry, CartEntryDto.class)).thenReturn(responseDto);

        CartEntryDto result = cartEntryService.save(dto);

        assertTrue(result.isSuccess());
        assertEquals(BigDecimal.valueOf(5), existingEntry.getQuantity());
        assertEquals(BigDecimal.valueOf(100), existingEntry.getUnitPrice());
        assertEquals(BigDecimal.valueOf(120), existingEntry.getMrp());
        assertEquals(BigDecimal.valueOf(600), existingEntry.getOriginalPrice());
        assertEquals(BigDecimal.valueOf(20), existingEntry.getUnitDiscount());
        assertEquals(BigDecimal.valueOf(100), existingEntry.getDiscount());
        assertEquals(BigDecimal.valueOf(500), existingEntry.getTotalPrice());

        verify(cartEntryRepository).save(existingEntry);
        verify(cartService).recalculate("CUST-001");
    }

    @Test
    void testSave_StockNotFound() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("CUST-001");
        dto.setProductIdentifier("PROD-001");
        dto.setQuantity(BigDecimal.ONE);

        when(cartEntryRepository.findByIdentifier("CUST-001-PROD-001")).thenReturn(null);
        when(stockRepository.findByProduct("PROD-001")).thenReturn(null);

        CartEntryDto result = cartEntryService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Stock not found for this product", result.getMessage());

        verify(cartEntryRepository, never()).save(any(CartEntry.class));
    }

    @Test
    void testSave_RequestedQuantityGreaterThanStock() {
        CartEntryDto dto = new CartEntryDto();
        dto.setCartIdentifier("CUST-001");
        dto.setProductIdentifier("PROD-001");
        dto.setQuantity(BigDecimal.valueOf(15));

        Stock stock = new Stock();
        stock.setQuantity(10);

        when(cartEntryRepository.findByIdentifier("CUST-001-PROD-001")).thenReturn(null);
        when(stockRepository.findByProduct("PROD-001")).thenReturn(stock);

        CartEntryDto result = cartEntryService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Only 10 quantity available in stock", result.getMessage());

        verify(cartEntryRepository, never()).save(any(CartEntry.class));
    }

    @Test
    void testReduceQuantity_QuantityGreaterThanOne() {
        String cartIdentifier = "CUST-001";
        String productIdentifier = "PROD-001";

        CartEntry entry = new CartEntry();
        entry.setIdentifier("CUST-001-PROD-001");
        entry.setCartIdentifier(cartIdentifier);
        entry.setProductIdentifier(productIdentifier);
        entry.setQuantity(BigDecimal.valueOf(3));

        Price sellingPrice = new Price();
        sellingPrice.setPriceAmount(BigDecimal.valueOf(100));

        Price mrp = new Price();
        mrp.setPriceAmount(BigDecimal.valueOf(120));

        when(cartEntryRepository.findByIdentifier("CUST-001-PROD-001")).thenReturn(entry);
        when(priceRepository.findByProductIdentifierAndPriceType(productIdentifier, "SELLING_PRICE"))
                .thenReturn(sellingPrice);
        when(priceRepository.findByProductIdentifierAndPriceType(productIdentifier, "MRP"))
                .thenReturn(mrp);
        when(cartServiceProvider.getObject()).thenReturn(cartService);

        cartEntryService.reduceQuantity(cartIdentifier, productIdentifier);

        assertEquals(BigDecimal.valueOf(2), entry.getQuantity());
        assertEquals(BigDecimal.valueOf(100), entry.getUnitPrice());
        assertEquals(BigDecimal.valueOf(240), entry.getOriginalPrice());
        assertEquals(BigDecimal.valueOf(40), entry.getDiscount());
        assertEquals(BigDecimal.valueOf(200), entry.getTotalPrice());

        verify(cartEntryRepository).save(entry);
        verify(cartService).recalculate(cartIdentifier);
    }

    @Test
    void testReduceQuantity_ToZero_DeleteEntry() {
        String cartIdentifier = "CUST-001";
        String productIdentifier = "PROD-001";

        CartEntry entry = new CartEntry();
        entry.setQuantity(BigDecimal.ONE);

        when(cartEntryRepository.findByIdentifier("CUST-001-PROD-001")).thenReturn(entry);
        when(cartServiceProvider.getObject()).thenReturn(cartService);

        cartEntryService.reduceQuantity(cartIdentifier, productIdentifier);

        verify(cartEntryRepository).deleteByIdentifier("CUST-001-PROD-001");
        verify(cartService).recalculate(cartIdentifier);
        verify(cartEntryRepository, never()).save(any(CartEntry.class));
    }

    @Test
    void testReduceQuantity_EntryNotFound() {
        when(cartEntryRepository.findByIdentifier("CUST-001-PROD-001")).thenReturn(null);

        cartEntryService.reduceQuantity("CUST-001", "PROD-001");

        verify(cartEntryRepository, never()).save(any(CartEntry.class));
        verify(cartEntryRepository, never()).deleteByIdentifier(anyString());
    }

    @Test
    void testDelete_Success() {
        cartEntryService.delete("CUST-001-PROD-001");

        verify(cartEntryRepository).deleteByIdentifier("CUST-001-PROD-001");
    }

    @Test
    void testFindAllEntriesForCart_Success() {
        CartEntry entry = new CartEntry();
        entry.setIdentifier("CUST-001-PROD-001");

        CartEntryDto dto = new CartEntryDto();
        dto.setIdentifier("CUST-001-PROD-001");

        when(cartEntryRepository.findByCartIdentifier("CUST-001"))
                .thenReturn(List.of(entry));

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(dto));

        List<CartEntryDto> result = cartEntryService.findAllEntriesForCart("CUST-001");

        assertEquals(1, result.size());
        assertEquals("CUST-001-PROD-001", result.get(0).getIdentifier());
    }

    @Test
    void testDeleteAllByCartIdentifier_Success() {
        cartEntryService.deleteAllByCartIdentifier("CUST-001");

        verify(cartEntryRepository).deleteAllByCartIdentifier("CUST-001");
    }

}