package com.ust.pos;
import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    private StockDto stockDto;
    private Stock stock;

    @BeforeEach
    void setUp() {

        stockDto = new StockDto();
        stockDto.setProduct("Product1");
        stockDto.setWarehouse("Warehouse1");
        stockDto.setQuantity(20);
        stockDto.setMinimumStock(10);

        stock = new Stock();
        stock.setIdentifier("Product1_Warehouse1");
    }

    @Test
    void save_ShouldSaveSuccessfully_WhenStockDoesNotExist() {

        when(stockRepository.findByIdentifier(
                "Product1_Warehouse1"))
                .thenReturn(null);

        when(modelMapper.map(
                stockDto,
                Stock.class))
                .thenReturn(stock);

        StockDto result =
                stockService.save(stockDto);

        assertEquals(
                "Product1_Warehouse1",
                result.getIdentifier());

        assertEquals(
                "Available",
                result.getStockStatus());

        verify(stockRepository)
                .save(any(Stock.class));
    }

    @Test
    void save_ShouldFail_WhenDuplicateStockExists() {

        when(stockRepository.findByIdentifier(
                "Product1_Warehouse1"))
                .thenReturn(stock);

        StockDto result =
                stockService.save(stockDto);

        assertFalse(result.isSuccess());

        assertTrue(
                result.getMessage()
                        .contains("already exists"));

        verify(stockRepository,
                never())
                .save(any());
    }

    @Test
    void save_ShouldSetLowStockStatus() {

        stockDto.setQuantity(5);
        stockDto.setMinimumStock(10);

        when(stockRepository.findByIdentifier(
                "Product1_Warehouse1"))
                .thenReturn(null);

        when(modelMapper.map(
                stockDto,
                Stock.class))
                .thenReturn(stock);

        StockDto result =
                stockService.save(stockDto);

        assertEquals(
                "Low Stock",
                result.getStockStatus());
    }

    @Test
    void save_ShouldSetOutOfStockStatus() {

        stockDto.setQuantity(0);
        stockDto.setMinimumStock(10);

        when(stockRepository.findByIdentifier(
                "Product1_Warehouse1"))
                .thenReturn(null);

        when(modelMapper.map(
                stockDto,
                Stock.class))
                .thenReturn(stock);

        StockDto result =
                stockService.save(stockDto);

        assertEquals(
                "Out of Stock",
                result.getStockStatus());
    }

    @Test
    void update_ShouldUpdateSuccessfully() {

        stockDto.setIdentifier(
                "Product1_Warehouse1");

        when(stockRepository.findByIdentifier(
                "Product1_Warehouse1"))
                .thenReturn(stock);

        StockDto result =
                stockService.update(stockDto);

        assertEquals(
                "Available",
                result.getStockStatus());

        verify(modelMapper)
                .map(stockDto, stock);

        verify(stockRepository)
                .save(stock);
    }

    @Test
    void update_ShouldFail_WhenStockNotFound() {

        stockDto.setIdentifier(
                "Product1_Warehouse1");

        when(stockRepository.findByIdentifier(
                "Product1_Warehouse1"))
                .thenReturn(null);

        StockDto result =
                stockService.update(stockDto);

        assertFalse(result.isSuccess());

        assertTrue(
                result.getMessage()
                        .contains("not found"));

        verify(stockRepository,
                never())
                .save(any());
    }

    @Test
    void delete_ShouldCallRepository() {

        stockService.delete(
                "Product1_Warehouse1");

        verify(stockRepository)
                .deleteByIdentifier(
                        "Product1_Warehouse1");
    }

    @Test
    void findAll_ShouldReturnDtoList() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Stock> page =
                new PageImpl<>(
                        List.of(stock));

        List<StockDto> dtoList =
                List.of(stockDto);

        when(stockRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()),
                any(java.lang.reflect.Type.class)))
                .thenReturn(dtoList);

        List<StockDto> result =
                stockService.findAll(pageable);

        assertEquals(1, result.size());
    }

    @Test
    void findByIdentifier_ShouldReturnDto() {

        when(stockRepository.findByIdentifier(
                "Product1_Warehouse1"))
                .thenReturn(stock);

        when(modelMapper.map(
                stock,
                StockDto.class))
                .thenReturn(stockDto);

        StockDto result =
                stockService.findByIdentifier(
                        "Product1_Warehouse1");

        assertNotNull(result);

        verify(stockRepository)
                .findByIdentifier(
                        "Product1_Warehouse1");
    }
}