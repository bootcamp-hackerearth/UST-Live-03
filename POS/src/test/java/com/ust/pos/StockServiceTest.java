package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    private final String expectedIdentifier = "Laptop_WH1";
    @Mock
    private StockRepository stockRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private StockServiceImpl stockService;
    private Stock stock;
    private StockDto stockDto;
    private Product product;

    @BeforeEach
    void setUp() {
        stockDto = new StockDto();
        stockDto.setProduct("PROD-001");
        stockDto.setWarehouse("WH1");
        stockDto.setQuantity(50);
        stockDto.setMinimumStock(20);
        stockDto.setSuccess(true);

        product = new Product();
        product.setIdentifier("PROD-001");
        product.setName("Laptop");

        stock = new Stock();
        stock.setIdentifier(expectedIdentifier);
        stock.setQuantity(50);
        stock.setMinimumStock(20);
        stock.setStockStatus("Available");
        stock.setDeleted(false);
    }

    @Test
    void save_existingStock_active() {
        stock.setDeleted(false);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(stockRepository, never()).save(any());
    }

    @Test
    void save_existingStock_softDeleted() {
        stock.setDeleted(true);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not available"));
        verify(stockRepository, never()).save(any());
    }

    @Test
    void save_available() {
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(null);
        when(modelMapper.map(any(StockDto.class), eq(Stock.class))).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertEquals("Available", result.getStockStatus());
        assertEquals(expectedIdentifier, result.getIdentifier());
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void save_lowStock() {
        stockDto.setQuantity(10);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(null);
        when(modelMapper.map(any(StockDto.class), eq(Stock.class))).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertEquals("Low Stock", result.getStockStatus());
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void save_outOfStock() {
        stockDto.setQuantity(0);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(null);
        when(modelMapper.map(any(StockDto.class), eq(Stock.class))).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertEquals("Out of Stock", result.getStockStatus());
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void save_negativeStock_fallsToOutOfStock() {
        stockDto.setQuantity(-5);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(null);
        when(modelMapper.map(any(StockDto.class), eq(Stock.class))).thenReturn(stock);
        StockDto result = stockService.save(stockDto);
        assertEquals("Out of Stock", result.getStockStatus());
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void update_negativeStock_fallsToOutOfStock() {
        stockDto.setIdentifier(expectedIdentifier);
        stockDto.setQuantity(-10);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stock);
        StockDto result = stockService.update(stockDto);
        assertEquals("Out of Stock", result.getStockStatus());
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void save_boundary_equalMinimumStock() {
        stockDto.setQuantity(20);
        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(null);
        when(modelMapper.map(any(StockDto.class), eq(Stock.class))).thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertEquals("Low Stock", result.getStockStatus());
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void save_quantityGreaterThanMinimumStock_coversElseIfBranchEvaluation() {
        stockDto.setQuantity(50);
        stockDto.setMinimumStock(20);

        when(productRepository.findByIdentifier("PROD-001")).thenReturn(product);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(null);
        when(modelMapper.map(any(StockDto.class), eq(Stock.class))).thenReturn(stock);
        StockDto result = stockService.save(stockDto);
        assertEquals("Available", result.getStockStatus());
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void update_quantityGreaterThanMinimumStock_coversElseIfBranchEvaluation() {
        stockDto.setIdentifier(expectedIdentifier);
        stockDto.setQuantity(50);
        stockDto.setMinimumStock(20);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stock);
        StockDto result = stockService.update(stockDto);
        assertEquals("Available", result.getStockStatus());
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void update_notFound() {
        stockDto.setIdentifier(expectedIdentifier);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(null);

        StockDto result = stockService.update(stockDto);

        assertFalse(result.isSuccess());
        verify(stockRepository, never()).save(any());
    }

    @Test
    void update_available() {
        stockDto.setIdentifier(expectedIdentifier);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        assertEquals("Available", result.getStockStatus());
        verify(modelMapper, times(1)).map(stockDto, stock);
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void update_lowStock() {
        stockDto.setIdentifier(expectedIdentifier);
        stockDto.setQuantity(10);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        assertEquals("Low Stock", result.getStockStatus());
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void update_outOfStock() {
        stockDto.setIdentifier(expectedIdentifier);
        stockDto.setQuantity(0);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        assertEquals("Out of Stock", result.getStockStatus());
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void update_boundary_equalMinimumStock() {
        stockDto.setIdentifier(expectedIdentifier);
        stockDto.setQuantity(20);
        when(stockRepository.findByIdentifier(expectedIdentifier)).thenReturn(stock);

        StockDto result = stockService.update(stockDto);

        assertEquals("Low Stock", result.getStockStatus());
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void delete_test() {
        when(stockRepository.findByIdentifierAndDeletedFalse(expectedIdentifier)).thenReturn(stock);

        assertDoesNotThrow(() -> stockService.delete(expectedIdentifier));

        verify(stockRepository, times(1)).findByIdentifierAndDeletedFalse(expectedIdentifier);
        assertTrue(stock.getDeleted());
    }

    @Test
    void findAll_test() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> page = new PageImpl<>(List.of(stock));
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();

        when(stockRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(page.getContent(), listType)).thenReturn(List.of(stockDto));

        WsDto<StockDto> result = stockService.findAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findAll_emptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> page = new PageImpl<>(List.of(), pageable, 0);
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();

        when(stockRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(page.getContent(), listType)).thenReturn(List.of());

        WsDto<StockDto> result = stockService.findAll(pageable);
        assertNotNull(result);
        assertTrue(result.getDtoList().isEmpty());
    }

    @Test
    void findById_found() {
        when(stockRepository.findByIdentifierAndDeletedFalse(expectedIdentifier)).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);

        StockDto result = stockService.findByIdentifier(expectedIdentifier);

        assertNotNull(result);
        verify(stockRepository, times(1)).findByIdentifierAndDeletedFalse(expectedIdentifier);
    }

    @Test
    void findById_notFound() {
        when(stockRepository.findByIdentifierAndDeletedFalse(expectedIdentifier)).thenReturn(null);
        when(modelMapper.map(null, StockDto.class)).thenReturn(null);

        StockDto result = stockService.findByIdentifier(expectedIdentifier);

        assertNull(result);
        verify(stockRepository, times(1)).findByIdentifierAndDeletedFalse(expectedIdentifier);
    }
}