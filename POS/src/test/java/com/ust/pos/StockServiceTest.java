package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void saveTestSuccess() {
        StockDto stockDto = new StockDto();
        stockDto.setQuantity(10);

        Stock stock = new Stock();
        stock.setIdentifier("STOCK-12345678");

        StockDto mappedDto = new StockDto();
        mappedDto.setIdentifier("STOCK-12345678");

        Mockito.when(modelMapper.map(stockDto, Stock.class))
                .thenReturn(stock);
        Mockito.when(stockRepository.save(stock))
                .thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(mappedDto);

        StockDto response = stockService.save(stockDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Stock created successfully", response.getMessage());
        Assertions.assertNotNull(response.getIdentifier());
    }

    @Test
    void saveTestFailure_whenStockAlreadyExists() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STOCK-123");

        Stock existingStock = new Stock();
        existingStock.setIdentifier("STOCK-123");

        Mockito.when(stockRepository.findByIdentifier("STOCK-123"))
                .thenReturn(existingStock);

        StockDto response = stockService.save(stockDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Stock with Identifier STOCK-123 already exists!",
                response.getMessage()
        );

        Mockito.verify(stockRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTestSuccess() {
        StockDto stockDto = new StockDto();
        stockDto.setId(1L);
        stockDto.setQuantity(20);

        Stock stock = new Stock();
        stock.setId(1L);
        stock.setQuantity(10);

        Mockito.when(stockRepository.findById(1L))
                .thenReturn(Optional.of(stock));

        Mockito.doNothing()
                .when(modelMapper)
                .map(stockDto, stock);

        Mockito.when(stockRepository.save(stock))
                .thenReturn(stock);

        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(stockDto);

        StockDto response = stockService.update(stockDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Stock updated successfully", response.getMessage());
    }

    @Test
    void updateTestFailure_NotFound() {
        StockDto stockDto = new StockDto();
        stockDto.setId(99L);

        Mockito.when(stockRepository.findById(99L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class,
                () -> stockService.update(stockDto));
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(stockRepository)
                .deleteById(1L);

        stockService.delete(1L);

        Mockito.verify(stockRepository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    void findAllTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STOCK-1");

        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK-1");

        Page<Stock> page = new PageImpl<>(List.of(stock));

        Mockito.when(stockRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(dto));

        Pageable pageable = PageRequest.of(0, 50, Sort.unsorted());
        List<StockDto> response = stockService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("STOCK-1", response.get(0).getIdentifier());
    }

    @Test
    void findByIdTest() {
        Stock stock = new Stock();
        stock.setId(1L);

        StockDto dto = new StockDto();
        dto.setId(1L);

        Mockito.when(stockRepository.findById(1L))
                .thenReturn(Optional.of(stock));
        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto response = stockService.findById(1L);

        Assertions.assertEquals(1L, response.getId());
    }

    @Test
    void findByIdFailure_shouldThrowException() {

        Mockito.when(stockRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> stockService.findById(99L)
        );

        Assertions.assertEquals("Stock not found", exception.getMessage());
    }

    @Test
    void changeStockStatusTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STOCK-1");
        stock.setStatus(false);

        StockDto dto = new StockDto();
        dto.setStatus(true);

        Mockito.when(stockRepository.findByIdentifier("STOCK-1"))
                .thenReturn(stock);
        Mockito.when(stockRepository.save(stock))
                .thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto response = stockService.changeStockStatus("STOCK-1", true);

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void findByIdentifierTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STOCK-1");

        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK-1");

        Mockito.when(stockRepository.findByIdentifier("STOCK-1"))
                .thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto response = stockService.findByIdentifier("STOCK-1");

        Assertions.assertEquals("STOCK-1", response.getIdentifier());
    }
}
