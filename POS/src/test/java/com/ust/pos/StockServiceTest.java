package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.model.*;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Assertions;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void createStockSuccessTest() {

        StockDto dto = new StockDto();
        dto.setProductId(1L);
        dto.setWarehouseId(2L);

        Product product = new Product();
        product.setProductName("Samsung");
        product.setIdentifier("SKU001");

        Warehouse warehouse = new Warehouse();
        warehouse.setName("Main Warehouse");

        Stock stock = new Stock();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));

        when(stockRepository.existsByProductIdAndWarehouseId(1L, 2L)).thenReturn(false);

        when(modelMapper.map(dto, Stock.class)).thenReturn(stock);

        StockDto response = stockService.createStock(dto);

        Assertions.assertEquals("Samsung", response.getProductName());
        Assertions.assertEquals("Main Warehouse", response.getWarehouseName());
        Assertions.assertEquals("SKU001", response.getIdentifier());

        Assertions.assertTrue(stock.isStatus());

        verify(stockRepository).save(stock);
    }

    @Test
    void createStockProductNotFoundTest() {

        StockDto dto = new StockDto();
        dto.setProductId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> stockService.createStock(dto));

        Assertions.assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void createStockWarehouseNotFoundTest() {

        StockDto dto = new StockDto();
        dto.setProductId(1L);
        dto.setWarehouseId(2L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));

        when(warehouseRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> stockService.createStock(dto));

        Assertions.assertEquals("Warehouse not found", exception.getMessage());
    }

    @Test
    void createStockAlreadyExistsTest() {

        StockDto dto = new StockDto();
        dto.setProductId(1L);
        dto.setWarehouseId(2L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));

        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(new Warehouse()));

        when(stockRepository.existsByProductIdAndWarehouseId(1L, 2L)).thenReturn(true);

        StockDto response = stockService.createStock(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Stock already exists", response.getMessage());
    }

    @Test
    void updateStockQuantitySuccessTest() {

        Stock stock = new Stock();
        stock.setProductId(1L);
        stock.setWarehouseId(2L);

        Product product = new Product();
        product.setProductName("Samsung");
        product.setIdentifier("SKU001");

        Warehouse warehouse = new Warehouse();
        warehouse.setName("Main Warehouse");

        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));

        doAnswer(invocation -> {

            Stock source = invocation.getArgument(0);
            StockDto target = invocation.getArgument(1);

            target.setQuantity(source.getQuantity());
            target.setProductName(source.getProductName());
            target.setWarehouseName(source.getWarehouseName());
            target.setIdentifier(source.getIdentifier());

            return null;

        }).when(modelMapper).map(any(Stock.class), any(StockDto.class));

        StockDto response = stockService.updateStockQuantity(1L, 10);

        Assertions.assertEquals(10, response.getQuantity());

        Assertions.assertEquals("Samsung", response.getProductName());

        Assertions.assertEquals("Main Warehouse", response.getWarehouseName());

        Assertions.assertEquals("SKU001", response.getIdentifier());

        verify(stockRepository).save(stock);
    }

    @Test
    void updateStockQuantityWithoutProductAndWarehouseTest() {

        Stock stock = new Stock();
        stock.setProductId(1L);
        stock.setWarehouseId(2L);

        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        when(warehouseRepository.findById(2L)).thenReturn(Optional.empty());

        doNothing().when(modelMapper).map(any(Stock.class), any(StockDto.class));

        StockDto response = stockService.updateStockQuantity(1L, 5);

        Assertions.assertNull(response.getQuantity());

        verify(stockRepository).save(stock);
    }

    @Test
    void updateStockQuantityNotFoundTest() {

        when(stockRepository.findById(1L)).thenReturn(Optional.empty());

        StockDto response = stockService.updateStockQuantity(1L, 10);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Stock not found", response.getMessage());
    }

    @Test
    void getStockSuccessTest() {

        Stock stock = new Stock();
        stock.setProductId(1L);
        stock.setWarehouseId(2L);

        Product product = new Product();
        product.setProductName("Samsung");
        product.setIdentifier("SKU001");

        Warehouse warehouse = new Warehouse();
        warehouse.setName("Main Warehouse");

        when(stockRepository.findByProductIdAndWarehouseId(1L, 2L)).thenReturn(Optional.of(stock));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));

        doAnswer(invocation -> {

            Stock source = invocation.getArgument(0);
            StockDto target = invocation.getArgument(1);

            target.setProductId(source.getProductId());
            target.setWarehouseId(source.getWarehouseId());

            return null;

        }).when(modelMapper).map(any(Stock.class), any(StockDto.class));

        StockDto response = stockService.getStock(1L, 2L);

        Assertions.assertEquals("Samsung", response.getProductName());

        Assertions.assertEquals("Main Warehouse", response.getWarehouseName());

        Assertions.assertEquals("SKU001", response.getIdentifier());
    }

    @Test
    void getStockWithoutProductAndWarehouseTest() {

        Stock stock = new Stock();
        stock.setProductId(1L);
        stock.setWarehouseId(2L);

        when(stockRepository.findByProductIdAndWarehouseId(1L, 2L)).thenReturn(Optional.of(stock));

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        when(warehouseRepository.findById(2L)).thenReturn(Optional.empty());

        doNothing().when(modelMapper).map(any(Stock.class), any(StockDto.class));

        StockDto response = stockService.getStock(1L, 2L);

        Assertions.assertNotNull(response);

        Assertions.assertNull(response.getProductName());

        Assertions.assertNull(response.getWarehouseName());
    }

    @Test
    void getStockNotFoundTest() {

        when(stockRepository.findByProductIdAndWarehouseId(1L, 2L)).thenReturn(Optional.empty());

        StockDto response = stockService.getStock(1L, 2L);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Stock not found", response.getMessage());
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Stock stock = new Stock();
        stock.setProductId(1L);
        stock.setWarehouseId(2L);

        Product product = new Product();
        product.setProductName("Samsung");
        product.setIdentifier("SKU001");

        Warehouse warehouse = new Warehouse();
        warehouse.setName("Main Warehouse");

        Page<Stock> stockPage = new PageImpl<>(List.of(stock));

        when(stockRepository.findAll(pageable)).thenReturn(stockPage);

        when(modelMapper.map(any(Stock.class), eq(StockDto.class))).thenReturn(new StockDto());

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));

        List<StockDto> response = stockService.findAll(pageable);

        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("Samsung", response.get(0).getProductName());

        Assertions.assertEquals("Main Warehouse", response.get(0).getWarehouseName());

        Assertions.assertEquals("SKU001", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutProductAndWarehouseTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Stock stock = new Stock();
        stock.setProductId(1L);
        stock.setWarehouseId(2L);

        Page<Stock> stockPage = new PageImpl<>(List.of(stock));

        when(stockRepository.findAll(pageable)).thenReturn(stockPage);

        when(modelMapper.map(any(Stock.class), eq(StockDto.class))).thenReturn(new StockDto());

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        when(warehouseRepository.findById(2L)).thenReturn(Optional.empty());

        List<StockDto> response = stockService.findAll(pageable);

        Assertions.assertEquals(1, response.size());

        Assertions.assertNull(response.get(0).getProductName());

        Assertions.assertNull(response.get(0).getWarehouseName());
    }

    @Test
    void deleteStockSuccessTest() {

        when(stockRepository.existsById(1L)).thenReturn(true);

        boolean response = stockService.deleteStock(1L);

        Assertions.assertTrue(response);

        verify(stockRepository).deleteById(1L);
    }

    @Test
    void deleteStockFailureTest() {

        when(stockRepository.existsById(1L)).thenReturn(false);

        boolean response = stockService.deleteStock(1L);

        Assertions.assertFalse(response);

        verify(stockRepository, never()).deleteById(anyLong());
    }

    @Test
    void toggleStatusSuccessTest() {

        Stock stock = new Stock();
        stock.setStatus(true);

        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

        stockService.toggleStatus(1L);

        Assertions.assertFalse(stock.isStatus());

        verify(stockRepository).save(stock);
    }

    @Test
    void toggleStatusStockNotFoundTest() {

        when(stockRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> stockService.toggleStatus(1L));

        verify(stockRepository, never()).save(any());
    }
}