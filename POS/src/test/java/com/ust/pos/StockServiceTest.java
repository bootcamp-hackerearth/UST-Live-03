package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Stock;
import com.ust.pos.modell.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByIdentifier_Success() {
        Stock stock = new Stock();
        stock.setIdentifier("STK-P1-W1");
        when(stockRepository.findByIdentifierAndDeletedFalse("STK-P1-W1")).thenReturn(stock);
        StockDto result = stockService.findByIdentifier("STK-P1-W1");
        assertEquals("STK-P1-W1", result.getIdentifier());
        assertTrue(result.isSuccess());
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(stockRepository.findByIdentifierAndDeletedFalse("X")).thenReturn(null);
        StockDto result = stockService.findByIdentifier("X");
        assertFalse(result.isSuccess());
        assertEquals("Stock not found", result.getMessage());
    }

    @Test
    void testFindById_Success() {
        Stock stock = new Stock();
        stock.setId(1L);
        when(stockRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(stock));
        StockDto result = stockService.findById(1L);
        assertEquals(1L, result.getId());
        assertTrue(result.isSuccess());
    }

    @Test
    void testFindById_NotFound() {
        when(stockRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        StockDto result = stockService.findById(1L);
        assertFalse(result.isSuccess());
        assertEquals("Stock not found", result.getMessage());
    }

    @Test
    void testSave_MissingFields() {
        StockDto dto = new StockDto();
        StockDto result = stockService.save(dto);
        assertFalse(result.isSuccess());
        assertEquals("Product & Warehouse required", result.getMessage());
    }

    @Test
    void testSave_AlreadyExists() {
        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        when(stockRepository.findByIdentifier("STK-P1-W1")).thenReturn(new Stock());
        StockDto result = stockService.save(dto);
        assertFalse(result.isSuccess());
    }

    @Test
    void testSave_DeletedExists() {
        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        Stock deleted = new Stock();
        deleted.setDeleted(true);
        when(stockRepository.findByIdentifier("STK-P1-W1")).thenReturn(deleted);
        StockDto result = stockService.save(dto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("was deleted"));
    }
    @Test
    void testSave_Success_StatusTrue() {
        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(10);
        dto.setMinimumStock(5);
        when(stockRepository.findByIdentifier("STK-P1-W1")).thenReturn(null);
        when(stockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        StockDto result = stockService.save(dto);
        assertTrue(result.isSuccess());
        assertEquals("STK-P1-W1", result.getIdentifier());
        assertTrue(result.getStatus());
    }

    @Test
    void testSave_Success_StatusFalse() {
        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(5);
        dto.setMinimumStock(10);
        when(stockRepository.findByIdentifier("STK-P1-W1")).thenReturn(null);
        when(stockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        StockDto result = stockService.save(dto);
        assertFalse(result.getStatus());
    }

    @Test
    void testUpdate_NotFound() {
        StockDto dto = new StockDto();
        dto.setIdentifier("STK-X-Y");
        when(stockRepository.findByIdentifierAndDeletedFalse("STK-X-Y")).thenReturn(null);
        StockDto result = stockService.update(dto);
        assertFalse(result.isSuccess());
    }

    @Test
    void testUpdate_Success() {
        Stock stock = new Stock();
        when(stockRepository.findByIdentifierAndDeletedFalse("STK-P1-W1")).thenReturn(stock);
        StockDto dto = new StockDto();
        dto.setIdentifier("STK-P1-W1");
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(20);
        dto.setMinimumStock(10);
        StockDto result = stockService.update(dto);
        assertTrue(result.isSuccess());
        verify(stockRepository).save(stock);
    }

    @Test
    void testDelete_Exists() {
        Stock stock = new Stock();
        when(stockRepository.findByIdentifierAndDeletedFalse("STK-A")).thenReturn(stock);
        stockService.delete("STK-A");
        verify(stockRepository).save(stock);
    }

    @Test
    void testDelete_NotFound() {
        when(stockRepository.findByIdentifierAndDeletedFalse("STK-A")).thenReturn(null);
        stockService.delete("STK-A");
        verify(stockRepository, never()).save(any());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Stock> list = List.of(new Stock());
        Page<Stock> page = new PageImpl<>(list, pageable, 1);
        List<StockDto> dtoList = List.of(new StockDto());
        when(stockRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(list), any(Type.class))).thenReturn(dtoList);
        WsDto<StockDto> result = stockService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }
}
