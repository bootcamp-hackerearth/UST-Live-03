package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
    private ModelMapper modelMapper;

    // ---------------- SAVE SUCCESS ----------------
    @Test
    void save_success() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setAvailableQuantity(10);

        Stock stock = new Stock();

        Stock saved = new Stock();
        saved.setAvailableQuantity(10);
        saved.setStatus(true);

        StockDto mapped = new StockDto();

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(null);

        when(modelMapper.map(any(StockDto.class), eq(Stock.class)))
                .thenReturn(stock);

        when(stockRepository.save(stock))
                .thenReturn(saved);

        when(modelMapper.map(saved, StockDto.class))
                .thenReturn(mapped);

        StockDto response = stockService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Stock saved successfully", response.getMessage());

        verify(stockRepository).save(stock);
    }

    // ---------------- SAVE FAILURE ----------------
    @Test
    void save_failure_alreadyExists() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");

        Stock existing = new Stock();
        existing.setDeleted(false);

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(existing);

        StockDto response = stockService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product already exists in this warehouse", response.getMessage());

        verify(stockRepository, never()).save(any());
    }

    // ---------------- UPDATE SUCCESS ----------------
    @Test
    void update_success() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setAvailableQuantity(5);
        dto.setReorderLevel(10);
        dto.setStatus(true);

        Stock existing = new Stock();
        existing.setDeleted(false);

        StockDto mapped = new StockDto();

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(existing);

        when(stockRepository.save(existing))
                .thenReturn(existing);

        when(modelMapper.map(existing, StockDto.class))
                .thenReturn(mapped);

        StockDto response = stockService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Stock updated successfully", response.getMessage());

        verify(stockRepository).save(existing);
    }

    // ---------------- UPDATE FAIL NOT FOUND ----------------
    @Test
    void update_failure_notFound() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(null);

        StockDto response = stockService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock not found", response.getMessage());
    }

    // ---------------- FIND SUCCESS ----------------
    @Test
    void find_success() {

        Stock stock = new Stock();
        stock.setAvailableQuantity(10);
        stock.setStatus(true);
        stock.setDeleted(false);

        StockDto dto = new StockDto();

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(stock);

        when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto response = stockService.findByIdentifier("P1_W1");

        Assertions.assertTrue(response.isSuccess());
    }

    // ---------------- FIND NOT FOUND ----------------
    @Test
    void find_notFound() {

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(null);

        StockDto response = stockService.findByIdentifier("P1_W1");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock not found", response.getMessage());
    }

    // ---------------- FIND DELETED ----------------
    @Test
    void find_deleted() {

        Stock stock = new Stock();
        stock.setDeleted(true);

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(stock);

        StockDto response = stockService.findByIdentifier("P1_W1");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock not found", response.getMessage());
    }

    // ---------------- FIND ALL FIXED ----------------
    @Test
    void findAllTest() {

        Stock stock = new Stock();
        stock.setAvailableQuantity(10);
        stock.setStatus(true);
        stock.setDeleted(false);

        List<Stock> list = List.of(stock);
        Page<Stock> page = new PageImpl<>(list);

        Pageable pageable = PageRequest.of(0, 5);

        StockDto dto = new StockDto();

        when(stockRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        WsDto<StockDto> result = stockService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
    }

    // ---------------- DELETE SUCCESS ----------------
    @Test
    void delete_success() {

        Stock stock = new Stock();
        stock.setDeleted(false);

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(stock);

        stockService.delete("P1_W1");

        Assertions.assertTrue(stock.getDeleted());

        verify(stockRepository).save(stock);
    }

    // ---------------- DELETE NOT FOUND ----------------
    @Test
    void delete_notFound() {

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(null);

        stockService.delete("P1_W1");

        verify(stockRepository, never()).save(any());
    }

    // ---------------- TOGGLE SUCCESS (FIXED) ----------------
    @Test
    void toggle_success() {

        Stock stock = new Stock();
        stock.setStatus(true);
        stock.setAvailableQuantity(10);
        stock.setDeleted(false);

        StockDto dto = new StockDto();

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(stock);

        when(stockRepository.save(stock))
                .thenReturn(stock);

        when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto response = stockService.toggleStatus("P1_W1");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Stock status updated successfully", response.getMessage());

        // IMPORTANT: verify toggle happened
        Assertions.assertFalse(stock.getStatus());
    }

    // ---------------- TOGGLE FAIL ----------------
    @Test
    void toggle_notFound() {

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(null);

        StockDto response = stockService.toggleStatus("P1_W1");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock not found", response.getMessage());
    }
}