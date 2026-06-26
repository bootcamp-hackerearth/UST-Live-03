package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.*;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));
        Mockito.when(stockRepository.existsByProductIdAndWarehouseId(1L, 2L)).thenReturn(false);
        Mockito.when(modelMapper.map(dto, Stock.class)).thenReturn(stock);

        StockDto response = stockService.createStock(dto);

        Assertions.assertEquals("Samsung", response.getProductName());
        Assertions.assertEquals("Main Warehouse", response.getWarehouseName());
        Assertions.assertEquals("SKU001", response.getIdentifier());

        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void createStockProductNotFoundTest() {
        StockDto dto = new StockDto();
        dto.setProductId(1L);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(ResourceNotFoundException.class, () -> stockService.createStock(dto));

        Assertions.assertEquals("Product with id '1' not found", ex.getMessage());
    }

    @Test
    void createStockWarehouseNotFoundTest() {
        StockDto dto = new StockDto();
        dto.setProductId(1L);
        dto.setWarehouseId(2L);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        Mockito.when(warehouseRepository.findById(2L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(ResourceNotFoundException.class, () -> stockService.createStock(dto));

        Assertions.assertEquals("Warehouse with id '2' not found", ex.getMessage());
    }

    @Test
    void createStockAlreadyExistsTest() {
        StockDto dto = new StockDto();
        dto.setProductId(1L);
        dto.setWarehouseId(2L);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        Mockito.when(warehouseRepository.findById(2L)).thenReturn(Optional.of(new Warehouse()));
        Mockito.when(stockRepository.existsByProductIdAndWarehouseId(1L, 2L)).thenReturn(true);

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

        Mockito.when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));

        Mockito.doAnswer(i -> {
            Stock s = i.getArgument(0);
            StockDto d = i.getArgument(1);
            d.setQuantity(s.getQuantity());
            d.setProductName(s.getProductName());
            d.setWarehouseName(s.getWarehouseName());
            d.setIdentifier(s.getIdentifier());
            return null;
        }).when(modelMapper).map(any(Stock.class), any(StockDto.class));

        StockDto response = stockService.updateStockQuantity(1L, 10);

        Assertions.assertEquals(10, response.getQuantity());
        Assertions.assertEquals("Samsung", response.getProductName());
        Assertions.assertEquals("Main Warehouse", response.getWarehouseName());
        Assertions.assertEquals("SKU001", response.getIdentifier());

        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void updateStockQuantityWithoutProductWarehouseTest() {
        Stock stock = new Stock();
        stock.setProductId(1L);
        stock.setWarehouseId(2L);

        Mockito.when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(warehouseRepository.findById(2L)).thenReturn(Optional.empty());

        StockDto response = stockService.updateStockQuantity(1L, 5);

        Assertions.assertNotNull(response);

        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void updateStockQuantityNotFoundTest() {
        Mockito.when(stockRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(ResourceNotFoundException.class, () -> stockService.updateStockQuantity(1L, 10));

        Assertions.assertEquals("Stock with id '1' not found", ex.getMessage());
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

        Mockito.when(stockRepository.findByProductIdAndWarehouseId(1L, 2L)).thenReturn(Optional.of(stock));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));
        Mockito.doNothing().when(modelMapper).map(any(Stock.class), any(StockDto.class));

        StockDto response = stockService.getStock(1L, 2L);

        Assertions.assertEquals("Samsung", response.getProductName());
        Assertions.assertEquals("Main Warehouse", response.getWarehouseName());
        Assertions.assertEquals("SKU001", response.getIdentifier());
    }

    @Test
    void getStockNotFoundTest() {
        Mockito.when(stockRepository.findByProductIdAndWarehouseId(1L, 2L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(ResourceNotFoundException.class, () -> stockService.getStock(1L, 2L));

        Assertions.assertEquals("Stock with productId '1' and warehouseId '2' not found", ex.getMessage());
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

        Page<Stock> page = new PageImpl<>(List.of(stock));

        Mockito.when(stockRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(any(Stock.class), eq(StockDto.class))).thenReturn(new StockDto());
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));

        List<StockDto> response = stockService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Samsung", response.get(0).getProductName());
        Assertions.assertEquals("Main Warehouse", response.get(0).getWarehouseName());
        Assertions.assertEquals("SKU001", response.get(0).getIdentifier());
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(stockRepository.findByDeletedFalse(pageable)).thenReturn(new PageImpl<>(List.of()));

        List<StockDto> response = stockService.findAll(pageable);

        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void deleteStockSuccessTest() {
        Stock stock = new Stock();

        Mockito.when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

        boolean response = stockService.deleteStock(1L);

        Assertions.assertTrue(response);

        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void deleteStockNotFoundTest() {
        Mockito.when(stockRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(ResourceNotFoundException.class, () -> stockService.deleteStock(1L));

        Assertions.assertEquals("Stock with id '1' not found", ex.getMessage());

        Mockito.verify(stockRepository, Mockito.never()).save(any());
    }

    @Test
    void toggleStatusSuccessTest() {
        Stock stock = new Stock();
        stock.setStatus(true);

        Mockito.when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));

        stockService.toggleStatus(1L);

        Assertions.assertFalse(stock.isStatus());

        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void toggleStatusNotFoundTest() {
        Mockito.when(stockRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = Assertions.assertThrows(ResourceNotFoundException.class, () -> stockService.toggleStatus(1L));

        Assertions.assertEquals("Stock with id '1' not found", ex.getMessage());

        Mockito.verify(stockRepository, Mockito.never()).save(any());
    }
}