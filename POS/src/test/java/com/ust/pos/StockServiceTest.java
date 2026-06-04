package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.modell.*;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockServiceImpl service;

    @Mock
    private StockRepository stockRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private WarehouseRepository warehouseRepository;

    @Test
    void findMethods_shouldHandleBothCases() {
        Stock stock = TestData.stock();
        when(stockRepository.findByIdentifier("STK")).thenReturn(Optional.of(stock));
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stock));
        assertEquals("STK-PRD-WH", service.findByIdentifier("STK").getIdentifier());
        assertEquals(1L, service.findById(1L).getId());
        when(stockRepository.findByIdentifier("X")).thenReturn(Optional.empty());
        when(stockRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.findByIdentifier("X"));
        assertThrows(RuntimeException.class, () -> service.findById(99L));
    }

    @Test
    void save_shouldHandleAllCases() {
        StockDto dto = new StockDto();
        dto.setProductId(1L);
        dto.setWarehouseId(1L);
        dto.setQuantity(20);
        dto.setMinimumStock(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(TestData.product()));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(TestData.warehouse()));
        when(stockRepository.findByProductIdAndWarehouseId(1L, 1L)).thenReturn(Optional.empty());
        when(stockRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        StockDto result = service.save(dto);
        assertEquals("STK-PRD-WH", result.getIdentifier());
        assertTrue(result.isStatus());
        when(stockRepository.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(new Stock()));
        RuntimeException ex1 = assertThrows(RuntimeException.class,
                () -> service.save(dto));
        assertEquals(
                "Stock already exists for this Product and Warehouse",
                ex1.getMessage());
        RuntimeException ex2 = assertThrows(RuntimeException.class,
                () -> service.save(new StockDto()));
        assertEquals("Product and Warehouse are required", ex2.getMessage());
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        StockDto invalidDto = new StockDto();
        invalidDto.setProductId(1L);
        invalidDto.setWarehouseId(1L);
        RuntimeException ex3 = assertThrows(RuntimeException.class,
                () -> service.save(invalidDto));
        assertEquals("Product not found", ex3.getMessage());
        when(productRepository.findById(1L)).thenReturn(Optional.of(TestData.product()));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException ex4 = assertThrows(RuntimeException.class,
                () -> service.save(invalidDto));
        assertEquals("Warehouse not found", ex4.getMessage());
    }

    @Test
    void update_shouldHandleAllCases() {
        Stock existing = TestData.stock();
        when(stockRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(stockRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        StockDto dto = new StockDto();
        dto.setId(1L);
        dto.setQuantity(5);
        dto.setMinimumStock(10);
        StockDto result = service.update(dto);
        assertFalse(result.isStatus());
        assertEquals(5, result.getQuantity());
        when(stockRepository.findById(99L)).thenReturn(Optional.empty());
        StockDto failDto = new StockDto();
        failDto.setId(99L);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.update(failDto));
        assertEquals("Stock not found", ex.getMessage());
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete(1L);
        verify(stockRepository).deleteById(1L);
    }

    @Test
    void update_shouldHandleProductAndWarehouseChange() {
        Stock existing = TestData.stock();
        when(stockRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.findById(2L)).thenReturn(Optional.of(TestData.product2()));
        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(TestData.warehouse2()));
        when(stockRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        StockDto dto = new StockDto();
        dto.setId(1L);
        dto.setProductId(2L);
        dto.setWarehouseId(2L);
        dto.setQuantity(20);
        dto.setMinimumStock(10);
        StockDto result = service.update(dto);
        assertEquals(2L, result.getProductId());
        assertEquals(2L, result.getWarehouseId());
    }

    static class TestData {
        static Product product() {
            Product p = new Product();
            p.setId(1L);
            p.setIdentifier("PRD");
            return p;
        }

        static Product product2() {
            Product p = new Product();
            p.setId(2L);
            p.setIdentifier("PRD2");
            return p;
        }

        static Warehouse warehouse() {
            Warehouse w = new Warehouse();
            w.setId(1L);
            w.setIdentifier("WH");
            return w;
        }

        static Warehouse warehouse2() {
            Warehouse w = new Warehouse();
            w.setId(2L);
            w.setIdentifier("WH2");
            return w;
        }

        static Stock stock() {
            Stock s = new Stock();
            s.setId(1L);
            s.setIdentifier("STK-PRD-WH");
            s.setProductId(1L);
            s.setWarehouseId(1L);
            s.setProductIdentifier("PRD");
            s.setWarehouseIdentifier("WH");
            s.setQuantity(20);
            s.setMinimumStock(10);
            s.setStatus(true);
            return s;
        }
    }
}
