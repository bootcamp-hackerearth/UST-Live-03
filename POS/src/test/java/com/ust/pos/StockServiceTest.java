package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private StockServiceImpl stockService;

    private Stock stock;
    private StockDto stockDto;

    @BeforeEach
    void setUp() {
        stock = new Stock();
        stock.setIdentifier("ST1");
        stock.setStatus(true);
        stock.setDeleted(false);

        stockDto = new StockDto();
        stockDto.setIdentifier("ST1");
    }

    // ✅ FIND ALL (Pagination)
    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> page = new PageImpl<>(Collections.singletonList(stock));

        when(stockRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(stockDto));

        WsDto<StockDto> result = stockService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    // ✅ DELETE (Soft Delete)
    @Test
    void testDelete() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(stock);

        doNothing().when(stockService).softDelete(stock);
        doNothing().when(stockService).setAuditFields(stock, false);

        stockService.delete("ST1");

        verify(stockRepository).save(stock);
    }

    // ✅ FIND BY IDENTIFIER
    @Test
    void testFindByIdentifier() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);

        StockDto result = stockService.findByIdentifier("ST1");

        assertNotNull(result);
    }

    // ✅ UPDATE
    @Test
    void testUpdate() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(stock);

        doNothing().when(modelMapper).map(stockDto, stock);
        doNothing().when(stockService).setAuditFields(stock, false);

        StockDto result = stockService.update(stockDto);

        assertNotNull(result);
        verify(stockRepository).save(stock);
    }

    // ✅ CHANGE TOGGLE STATUS
    @Test
    void testChangeToggleStatus() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);

        StockDto result = stockService.changeToggleStatus("ST1", false);

        assertNotNull(result);
        assertFalse(stock.isStatus());
        verify(stockRepository).save(stock);
    }

    // ✅ FIND ACTIVE STATUS
    @Test
    void testFindActiveStatus() {
        stock.setStatus(true);

        Stock inactive = new Stock();
        inactive.setStatus(false);

        List<Stock> stocks = List.of(stock, inactive);

        when(stockRepository.findAll()).thenReturn(stocks);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(stockDto));

        List<StockDto> result = stockService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ✅ SAVE - NEW STOCK
    @Test
    void testSave_NewStock() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(null);
        when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);

        doNothing().when(stockService).setAuditFields(stock, true);

        StockDto result = stockService.save(stockDto);

        assertNotNull(result);
        verify(stockRepository).save(stock);
    }

    // ✅ SAVE - ALREADY EXISTS
    @Test
    void testSave_AlreadyExists() {
        when(stockRepository.findByIdentifier("ST1")).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    // ✅ SAVE - SOFT DELETED
    @Test
    void testSave_SoftDeleted() {
        stock.setDeleted(true);

        when(stockRepository.findByIdentifier("ST1")).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }
}