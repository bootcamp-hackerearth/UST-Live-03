package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.*;
import com.ust.pos.stock.service.impl.StockServiceImpl;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    private static final Long PRODUCT_ID = 1L;
    private static final Long WAREHOUSE_ID = 2L;
    private static final Long STOCK_ID = 10L;
    @Mock
    private StockRepository stockRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private StockServiceImpl service;

    private Product product() {
        Product p = new Product();
        p.setId(PRODUCT_ID);
        p.setProductName("Laptop");
        p.setIdentifier("SKU001");
        return p;
    }

    private Warehouse warehouse() {
        Warehouse w = new Warehouse();
        w.setId(WAREHOUSE_ID);
        w.setName("Warehouse-A");
        return w;
    }

    private Stock stock() {
        Stock s = new Stock();
        s.setId(STOCK_ID);
        s.setProductId(PRODUCT_ID);
        s.setWarehouseId(WAREHOUSE_ID);
        s.setStatus(true);
        return s;
    }

    @Test
    void createStockSuccess() {

        StockDto dto = new StockDto();

        dto.setProductId(PRODUCT_ID);
        dto.setWarehouseId(WAREHOUSE_ID);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product()));

        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse()));

        when(stockRepository.existsByProductIdAndWarehouseId(PRODUCT_ID, WAREHOUSE_ID)).thenReturn(false);

        when(modelMapper.map(dto, Stock.class)).thenReturn(new Stock());

        StockDto result = service.createStock(dto);

        assertEquals("Laptop", result.getProductName());

        assertEquals("Warehouse-A", result.getWarehouseName());

        verify(stockRepository).save(any());

    }

    @Test
    void createStockAlreadyExists() {

        StockDto dto = new StockDto();

        dto.setProductId(PRODUCT_ID);
        dto.setWarehouseId(WAREHOUSE_ID);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product()));

        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse()));

        when(stockRepository.existsByProductIdAndWarehouseId(PRODUCT_ID, WAREHOUSE_ID)).thenReturn(true);

        StockDto result = service.createStock(dto);

        assertFalse(result.isSuccess());

        assertEquals("Stock already exists", result.getMessage());

    }

    @Test
    void createStockProductNotFound() {

        StockDto dto = new StockDto();

        dto.setProductId(PRODUCT_ID);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createStock(dto));

    }

    @Test
    void createStockWarehouseNotFound() {

        StockDto dto = new StockDto();

        dto.setProductId(PRODUCT_ID);
        dto.setWarehouseId(WAREHOUSE_ID);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product()));

        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createStock(dto));

    }

    @Test
    void updateQuantitySuccess() {

        Stock stock = stock();

        when(stockRepository.findById(STOCK_ID)).thenReturn(Optional.of(stock));

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product()));

        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse()));

        service.updateStockQuantity(STOCK_ID, 100);

        verify(stockRepository).save(stock);

        verify(modelMapper).map(eq(stock), any(StockDto.class));

    }

    @Test
    void updateQuantityWithoutProduct() {

        Stock stock = stock();

        when(stockRepository.findById(STOCK_ID)).thenReturn(Optional.of(stock));

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.empty());

        service.updateStockQuantity(STOCK_ID, 50);

        verify(stockRepository).save(stock);

    }

    @Test
    void updateQuantityNotFound() {

        when(stockRepository.findById(STOCK_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateStockQuantity(STOCK_ID, 10));

    }

    @Test
    void getStockSuccess() {

        Stock stock = stock();

        when(stockRepository.findByProductIdAndWarehouseId(PRODUCT_ID, WAREHOUSE_ID)).thenReturn(Optional.of(stock));

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product()));

        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse()));

        service.getStock(PRODUCT_ID, WAREHOUSE_ID);

        verify(modelMapper).map(eq(stock), any(StockDto.class));

    }

    @Test
    void getStockWithoutProductWarehouse() {

        Stock stock = stock();

        when(stockRepository.findByProductIdAndWarehouseId(PRODUCT_ID, WAREHOUSE_ID)).thenReturn(Optional.of(stock));

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.empty());

        StockDto result = service.getStock(PRODUCT_ID, WAREHOUSE_ID);

        assertNotNull(result);

    }

    @Test
    void getStockNotFound() {

        when(stockRepository.findByProductIdAndWarehouseId(PRODUCT_ID, WAREHOUSE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getStock(PRODUCT_ID, WAREHOUSE_ID));

    }

    @Test
    void findAllSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        Stock stock = stock();

        Page<Stock> page = new PageImpl<>(List.of(stock));

        when(stockRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(stock, StockDto.class)).thenReturn(new StockDto());

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product()));

        when(warehouseRepository.findById(WAREHOUSE_ID)).thenReturn(Optional.of(warehouse()));

        WsDto<StockDto> ws = service.findAll(pageable);

        assertEquals(1, ws.getDtoList().size());

    }

    @Test
    void findAllEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Stock> page = new PageImpl<>(Collections.emptyList());

        when(stockRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<StockDto> ws = service.findAll(pageable);

        assertEquals(0, ws.getDtoList().size());

    }

    @Test
    void findAllSpecificationSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Stock> spec = mock(Specification.class);

        Stock stock = stock();

        Page<Stock> page = new PageImpl<>(List.of(stock));

        when(stockRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(stock, StockDto.class)).thenReturn(new StockDto());

        WsDto<StockDto> ws = service.findAll(spec, pageable, "abc");

        assertEquals("abc", ws.getKeyword());

    }

    @Test
    void findAllSpecificationEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Stock> spec = mock(Specification.class);

        Page<Stock> page = new PageImpl<>(Collections.emptyList());

        when(stockRepository.findAll(spec, pageable)).thenReturn(page);

        WsDto<StockDto> ws = service.findAll(spec, pageable, "test");

        assertEquals(0, ws.getDtoList().size());

    }

    @Test
    void deleteStockSuccess() {

        Stock stock = stock();

        when(stockRepository.findById(STOCK_ID)).thenReturn(Optional.of(stock));

        assertTrue(service.deleteStock(STOCK_ID));

        verify(stockRepository).save(stock);

    }

    @Test
    void deleteStockNotFound() {

        when(stockRepository.findById(STOCK_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteStock(STOCK_ID));

    }

    @Test
    void toggleStatusTrueFalse() {

        Stock stock = stock();

        stock.setStatus(true);

        when(stockRepository.findById(STOCK_ID)).thenReturn(Optional.of(stock));

        service.toggleStatus(STOCK_ID);

        assertFalse(stock.isStatus());

    }

    @Test
    void toggleStatusFalseTrue() {

        Stock stock = stock();

        stock.setStatus(false);

        when(stockRepository.findById(STOCK_ID)).thenReturn(Optional.of(stock));

        service.toggleStatus(STOCK_ID);

        assertTrue(stock.isStatus());

        verify(stockRepository).save(stock);

    }

    @Test
    void toggleStatusNotFound() {

        when(stockRepository.findById(STOCK_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.toggleStatus(STOCK_ID));

    }

}