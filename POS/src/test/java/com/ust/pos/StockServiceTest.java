package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.models.Stock;
import com.ust.pos.models.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    void findByIdentifierAndFindByIdTest() {

        Stock stock = new Stock();
        stock.setId(1L);
        stock.setIdentifier("STK-P1-W1");
        stock.setQuantity(10);
        stock.setMinimumStock(5);
        stock.setStatus(true);
        stock.setProductIdentifier("P1");
        stock.setWarehouseIdentifier("W1");
        when(stockRepository.findByIdentifierAndDeletedFalse("STK-P1-W1")).thenReturn(stock);
        StockDto result = stockService.findByIdentifier("STK-P1-W1");
        assertEquals("STK-P1-W1", result.getIdentifier());
        result = stockService.findByIdentifier(" STK-P1-W1 ");
        assertEquals("STK-P1-W1", result.getIdentifier());
        when(stockRepository.findByIdentifierAndDeletedFalse("X")).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> stockService.findByIdentifier("X"));
        when(stockRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(stock));
        StockDto byId = stockService.findById(1L);
        assertTrue(byId.isSuccess());
        when(stockRepository.findByIdAndDeletedFalse(2L)).thenReturn(Optional.empty());
        byId = stockService.findById(2L);
        assertFalse(byId.isSuccess());
        assertEquals("Stock not found", byId.getMessage());
    }

    @Test
    void saveTest() {
        StockDto dto = new StockDto();
        StockDto result = stockService.save(dto);
        assertFalse(result.isSuccess());
        dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        when(stockRepository.findByIdentifier("STK-P1-W1")).thenReturn(new Stock());
        result = stockService.save(dto);
        assertFalse(result.isSuccess());
        Stock deleted = new Stock();
        deleted.setDeleted(true);
        when(stockRepository.findByIdentifier("STK-P1-W1")).thenReturn(deleted);
        result = stockService.save(dto);
        assertFalse(result.isSuccess());
        StockDto dto1 = new StockDto();
        dto1.setProductIdentifier("P1");
        dto1.setWarehouseIdentifier("W1");
        dto1.setQuantity(20);
        dto1.setMinimumStock(10);
        when(stockRepository.findByIdentifier("STK-P1-W1")).thenReturn(null);
        when(stockRepository.save(any(Stock.class))).thenAnswer(i -> i.getArgument(0));
        result = stockService.save(dto1);
        assertTrue(result.isSuccess());
        assertTrue(result.getStatus());
        StockDto dto2 = new StockDto();
        dto2.setProductIdentifier("P1");
        dto2.setWarehouseIdentifier("W1");
        dto2.setQuantity(5);
        dto2.setMinimumStock(10);
        result = stockService.save(dto2);
        assertFalse(result.getStatus());
    }

    @Test
    void updateTest() {
        StockDto dto = new StockDto();
        dto.setIdentifier("STK-P1-W1");
        when(stockRepository.findByIdentifierAndDeletedFalse("STK-P1-W1")).thenReturn(null);
        StockDto result = stockService.update(dto);
        assertFalse(result.isSuccess());
        Stock stock = new Stock();
        when(stockRepository.findByIdentifierAndDeletedFalse("STK-P1-W1")).thenReturn(stock);
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(20);
        dto.setMinimumStock(10);
        result = stockService.update(dto);
        assertTrue(result.isSuccess());
        assertTrue(stock.getStatus());
        stock.setStatus(null);
        dto.setQuantity(5);
        dto.setMinimumStock(10);
        stockService.update(dto);
        assertFalse(stock.getStatus());
        verify(stockRepository, atLeast(2)).save(stock);
    }

    @Test
    void deleteTest() {
        Stock stock = new Stock();
        when(stockRepository.findByIdentifierAndDeletedFalse("STK-A")).thenReturn(stock);
        stockService.delete("STK-A");
        assertTrue(stock.getDeleted());
        verify(stockRepository).save(stock);
        when(stockRepository.findByIdentifierAndDeletedFalse("STK-B")).thenReturn(null);
        stockService.delete("STK-B");
        verify(stockRepository, times(1)).save(any());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Stock> list = List.of(new Stock());
        Page<Stock> page = new PageImpl<>(list, pageable, 1);
        when(stockRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(list), any(Type.class))).thenReturn(List.of(new StockDto()));
        WsDto<StockDto> result = stockService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<Stock> specification = mock(Specification.class);
        List<Stock> stocks = List.of(new Stock());
        Page<Stock> page = new PageImpl<>(stocks, pageable, 1);
        when(stockRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(stocks), any(Type.class))).thenReturn(List.of(new StockDto()));
        WsDto<StockDto> result = stockService.findAll(specification, pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        verify(stockRepository).findAll(specification, pageable);
    }
}