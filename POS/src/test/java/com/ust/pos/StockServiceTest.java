package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private StockServiceImpl stockService;
    private StockDto stockDto;
    private Stock stock;

    @BeforeEach
    void setUp() {
        stockDto = new StockDto();
        stock = new Stock();
        stockDto.setProduct("PROD-001");
        stockDto.setWarehouse("WH1");
        stockDto.setQuantity(20);
        stockDto.setMinimumStock(10);
    }

    @Test
    void findAll_ShouldReturnWsDtoWithStockDtos() {
        Specification<Stock> specification = (root, query, criteriaBuilder) -> null;
        Pageable pageable = PageRequest.of(0, 10);

        Stock stock = new Stock();
        stock.setId(1L);
        stock.setIdentifier("STOCK001");

        List<Stock> stockList = List.of(stock);
        Page<Stock> stockPage =
                new PageImpl<>(stockList, pageable, stockList.size());

        StockDto stockDto = new StockDto();
        stockDto.setId(1L);
        stockDto.setIdentifier("STOCK001");

        List<StockDto> stockDtoList = List.of(stockDto);

        when(stockRepository.findAll(specification, pageable))
                .thenReturn(stockPage);

        when(modelMapper.map(eq(stockList), any(Type.class)))
                .thenReturn(stockDtoList);

        WsDto<StockDto> result = stockService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(stockDtoList, result.getDtoList());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(stockRepository, times(1)).findAll(specification, pageable);
        verify(modelMapper, times(1)).map(eq(stockList), any(Type.class));
    }

    @Test
    void testSave_Success_Available() {
        StockDto stockDto = new StockDto();
        stockDto.setProduct("PROD-001");
        stockDto.setWarehouse("WH-001");
        stockDto.setQuantity(20);
        stockDto.setMinimumStock(10);

        Stock stock = new Stock();

        when(stockRepository.findByIdentifier(null)).thenReturn(null);
        when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertTrue(result.isSuccess());
        assertEquals("Stock created successfully", result.getMessage());
        assertEquals("Available", result.getStockStatus());
        assertEquals("PROD-001_WH-001", result.getIdentifier());

        verify(stockRepository).save(stock);
    }

    @Test
    void testSave_Success_LowStock() {
        StockDto stockDto = new StockDto();
        stockDto.setProduct("PROD-001");
        stockDto.setWarehouse("WH-001");
        stockDto.setQuantity(5);
        stockDto.setMinimumStock(10);

        Stock stock = new Stock();

        when(stockRepository.findByIdentifier(null)).thenReturn(null);
        when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertEquals("Low Stock", result.getStockStatus());
        assertEquals("PROD-001_WH-001", result.getIdentifier());

        verify(stockRepository).save(stock);
    }

    @Test
    void testSave_Success_OutOfStock() {
        StockDto stockDto = new StockDto();
        stockDto.setProduct("PROD-001");
        stockDto.setWarehouse("WH-001");
        stockDto.setQuantity(0);
        stockDto.setMinimumStock(10);

        Stock stock = new Stock();

        when(stockRepository.findByIdentifier(null)).thenReturn(null);
        when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertEquals("Out of Stock", result.getStockStatus());
        assertEquals("PROD-001_WH-001", result.getIdentifier());

        verify(stockRepository).save(stock);
    }

    @Test
    void testSave_AlreadyExists() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("PROD-001_WH-001");

        Stock existingStock = new Stock();
        existingStock.setIdentifier("PROD-001_WH-001");
        existingStock.setDeleted(false);

        when(stockRepository.findByIdentifier("PROD-001_WH-001"))
                .thenReturn(existingStock);

        StockDto result = stockService.save(stockDto);

        assertFalse(result.isSuccess());
        assertEquals("Stock with identifier - PROD-001_WH-001 already exists", result.getMessage());

        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testSave_DeletedStockExists() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("PROD-001_WH-001");

        Stock existingStock = new Stock();
        existingStock.setIdentifier("PROD-001_WH-001");
        existingStock.setDeleted(true);

        when(stockRepository.findByIdentifier("PROD-001_WH-001"))
                .thenReturn(existingStock);

        StockDto result = stockService.save(stockDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Stock with identifier - PROD-001_WH-001 was deleted and cannot be created again.",
                result.getMessage()
        );

        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testUpdate_Success_Available() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("PROD-001_WH-001");
        stockDto.setQuantity(20);
        stockDto.setMinimumStock(10);

        Stock existingStock = new Stock();
        existingStock.setIdentifier("PROD-001_WH-001");

        when(stockRepository.findByIdentifierAndDeletedFalse("PROD-001_WH-001"))
                .thenReturn(existingStock);

        StockDto result = stockService.update(stockDto);

        assertTrue(result.isSuccess());
        assertEquals("Stock updated successfully", result.getMessage());
        assertEquals("Available", result.getStockStatus());

        verify(modelMapper).map(stockDto, existingStock);
        verify(stockRepository).save(existingStock);
    }

    @Test
    void testUpdate_Success_LowStock() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("PROD-001_WH-001");
        stockDto.setQuantity(5);
        stockDto.setMinimumStock(10);

        Stock existingStock = new Stock();

        when(stockRepository.findByIdentifierAndDeletedFalse("PROD-001_WH-001"))
                .thenReturn(existingStock);

        StockDto result = stockService.update(stockDto);

        assertEquals("Low Stock", result.getStockStatus());
        verify(stockRepository).save(existingStock);
    }

    @Test
    void testUpdate_Success_OutOfStock() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("PROD-001_WH-001");
        stockDto.setQuantity(0);
        stockDto.setMinimumStock(10);

        Stock existingStock = new Stock();

        when(stockRepository.findByIdentifierAndDeletedFalse("PROD-001_WH-001"))
                .thenReturn(existingStock);

        StockDto result = stockService.update(stockDto);

        assertEquals("Out of Stock", result.getStockStatus());
        verify(stockRepository).save(existingStock);
    }

    @Test
    void testUpdate_NotFound() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("PROD-001_WH-001");

        when(stockRepository.findByIdentifierAndDeletedFalse("PROD-001_WH-001"))
                .thenReturn(null);

        StockDto result = stockService.update(stockDto);

        assertFalse(result.isSuccess());
        assertEquals("Stock with identifier - PROD-001_WH-001 not found", result.getMessage());

        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    void testDelete_Success() {
        String identifier = "PROD-001_WH-001";

        Stock stock = new Stock();
        stock.setIdentifier(identifier);
        stock.setDeleted(false);

        when(stockRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(stock);

        stockService.delete(identifier);

        assertTrue(stock.getDeleted());

        verify(stockRepository).save(stock);
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Stock stock = new Stock();
        stock.setIdentifier("PROD-001_WH-001");

        Page<Stock> stockPage = new PageImpl<>(List.of(stock), pageable, 1);

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("PROD-001_WH-001");

        when(stockRepository.findAllByDeletedFalse(pageable))
                .thenReturn(stockPage);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(stockDto));

        WsDto<StockDto> result = stockService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void testFindByIdentifier_Success() {
        String identifier = "PROD-001_WH-001";

        Stock stock = new Stock();
        stock.setIdentifier(identifier);

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier(identifier);

        when(stockRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class))
                .thenReturn(stockDto);

        StockDto result = stockService.findByIdentifier(identifier);

        assertEquals(identifier, result.getIdentifier());
    }
}

