package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void save_whenAlreadyExists_savesAnyway() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setAvailableQuantity(10);

        Stock existing = new Stock();
        existing.setDeleted(false);

        Stock stock = new Stock();

        Stock saved = new Stock();
        saved.setAvailableQuantity(10);
        saved.setStatus(true);

        StockDto mapped = new StockDto();

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(existing);

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

    @Test
    void save_failure_softDeleted() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");

        Stock existing = new Stock();
        existing.setDeleted(true);

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(existing);

        assertThrows(
                ResourceNotFoundException.class,
                () -> stockService.save(dto));

        verify(stockRepository, never()).save(any());
    }

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

    @Test
    void update_failure_notFound() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> stockService.update(dto));

        verify(stockRepository, never()).save(any());
    }

    @Test
    void update_failure_deleted() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");

        Stock existing = new Stock();
        existing.setDeleted(true);

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(existing);

        assertThrows(
                ResourceNotFoundException.class,
                () -> stockService.update(dto));

        verify(stockRepository, never()).save(any());
    }

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

    @Test
    void find_notFound() {

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> stockService.findByIdentifier("P1_W1"));
    }

    @Test
    void find_deleted() {

        Stock stock = new Stock();
        stock.setDeleted(true);

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(stock);

        assertThrows(
                ResourceNotFoundException.class,
                () -> stockService.findByIdentifier("P1_W1"));
    }

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

    @Test
    void findAll_withSpecification() {

        Stock stock = new Stock();
        stock.setAvailableQuantity(10);
        stock.setStatus(true);
        stock.setDeleted(false);

        List<Stock> list = List.of(stock);
        Page<Stock> page = new PageImpl<>(list);

        Pageable pageable = PageRequest.of(0, 5);

        @SuppressWarnings("unchecked")
        Specification<Stock> spec = mock(Specification.class);

        List<StockDto> mappedList = List.of(new StockDto());

        when(stockRepository.findAll(spec, pageable))
                .thenReturn(page);

        doReturn(mappedList)
                .when(modelMapper)
                .map(eq(list), any(Type.class));

        WsDto<StockDto> result = stockService.findAll(spec, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }

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

    @Test
    void delete_notFound() {

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> stockService.delete("P1_W1"));

        verify(stockRepository, never()).save(any());
    }

    @Test
    void delete_alreadyDeleted() {

        Stock stock = new Stock();
        stock.setDeleted(true);

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(stock);

        assertThrows(
                ResourceNotFoundException.class,
                () -> stockService.delete("P1_W1"));

        verify(stockRepository, never()).save(any());
    }

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

        Assertions.assertFalse(stock.getStatus());
    }

    @Test
    void toggle_notFound() {

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> stockService.toggleStatus("P1_W1"));
    }

    @Test
    void toggle_deleted() {

        Stock stock = new Stock();
        stock.setDeleted(true);

        when(stockRepository.findByIdentifier("P1_W1"))
                .thenReturn(stock);

        assertThrows(
                ResourceNotFoundException.class,
                () -> stockService.toggleStatus("P1_W1"));

        verify(stockRepository, never()).save(any());
    }

    @Test
    void findActiveStock_returnsOnlyActiveNonDeleted() {

        Stock stock = new Stock();
        stock.setAvailableQuantity(10);
        stock.setStatus(true);
        stock.setDeleted(false);

        StockDto dto = new StockDto();

        when(stockRepository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(stock));

        when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        List<StockDto> result = stockService.findActiveStock();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void findActiveStock_empty() {

        when(stockRepository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of());

        List<StockDto> result = stockService.findActiveStock();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void isStockAvailable_true_singleWarehouse() {

        Stock stock = new Stock();
        stock.setStatus(true);
        stock.setAvailableQuantity(10);
        stock.setDeleted(false);

        when(stockRepository.findByProductIdentifierAndDeletedFalse("P1"))
                .thenReturn(List.of(stock));

        boolean result = stockService.isStockAvailable("P1", 5);

        Assertions.assertTrue(result);
    }

    @Test
    void isStockAvailable_true_aggregatedAcrossWarehouses() {

        Stock stock1 = new Stock();
        stock1.setStatus(true);
        stock1.setAvailableQuantity(3);

        Stock stock2 = new Stock();
        stock2.setStatus(true);
        stock2.setAvailableQuantity(4);

        when(stockRepository.findByProductIdentifierAndDeletedFalse("P1"))
                .thenReturn(List.of(stock1, stock2));

        boolean result = stockService.isStockAvailable("P1", 7);

        Assertions.assertTrue(result);
    }

    @Test
    void isStockAvailable_false_insufficientQuantity() {

        Stock stock = new Stock();
        stock.setStatus(true);
        stock.setAvailableQuantity(2);

        when(stockRepository.findByProductIdentifierAndDeletedFalse("P1"))
                .thenReturn(List.of(stock));

        boolean result = stockService.isStockAvailable("P1", 5);

        Assertions.assertFalse(result);
    }

    @Test
    void isStockAvailable_false_ignoresInactiveStock() {

        Stock stock = new Stock();
        stock.setStatus(false);
        stock.setAvailableQuantity(100);

        when(stockRepository.findByProductIdentifierAndDeletedFalse("P1"))
                .thenReturn(List.of(stock));

        boolean result = stockService.isStockAvailable("P1", 5);

        Assertions.assertFalse(result);
    }

    @Test
    void reduceStock_success_singleWarehouse() {

        Stock stock = new Stock();
        stock.setStatus(true);
        stock.setAvailableQuantity(10);
        stock.setDeleted(false);

        when(stockRepository.findByProductIdentifierAndDeletedFalse("P1"))
                .thenReturn(List.of(stock));

        StockDto response = stockService.reduceStock("P1", 6);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Stock reduced successfully", response.getMessage());
        Assertions.assertEquals("P1", response.getProductIdentifier());
        Assertions.assertEquals(4, stock.getAvailableQuantity());

        verify(stockRepository).save(stock);
    }

    @Test
    void reduceStock_success_acrossMultipleWarehouses_takesFromLargestFirst() {

        Stock small = new Stock();
        small.setStatus(true);
        small.setAvailableQuantity(3);

        Stock large = new Stock();
        large.setStatus(true);
        large.setAvailableQuantity(8);

        when(stockRepository.findByProductIdentifierAndDeletedFalse("P1"))
                .thenReturn(List.of(small, large));

        StockDto response = stockService.reduceStock("P1", 10);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(0, large.getAvailableQuantity());
        Assertions.assertEquals(1, small.getAvailableQuantity());

        verify(stockRepository).save(large);
        verify(stockRepository).save(small);
    }

    @Test
    void reduceStock_failure_insufficientStock() {

        Stock stock = new Stock();
        stock.setStatus(true);
        stock.setAvailableQuantity(2);

        when(stockRepository.findByProductIdentifierAndDeletedFalse("P1"))
                .thenReturn(List.of(stock));

        StockDto response = stockService.reduceStock("P1", 5);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Insufficient stock for product: P1", response.getMessage());

        verify(stockRepository, never()).save(any());
    }

    @Test
    void reduceStock_ignoresInactiveAndZeroQuantityStock() {

        Stock inactive = new Stock();
        inactive.setStatus(false);
        inactive.setAvailableQuantity(100);

        Stock zeroQty = new Stock();
        zeroQty.setStatus(true);
        zeroQty.setAvailableQuantity(0);

        when(stockRepository.findByProductIdentifierAndDeletedFalse("P1"))
                .thenReturn(List.of(inactive, zeroQty));

        StockDto response = stockService.reduceStock("P1", 1);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Insufficient stock for product: P1", response.getMessage());
    }
}