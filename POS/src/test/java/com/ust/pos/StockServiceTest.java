package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
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

import java.lang.reflect.Type;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");

        Mockito.when(stockRepository.findByIdentifier("Lays In-001")).thenReturn(null);
        Stock stock = new Stock();
        Mockito.when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);

        StockDto response = stockService.save(stockDto);

        Assertions.assertEquals("Lays In-001", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");
        Stock stock = new Stock();

        Mockito.when(stockRepository.findByIdentifier("Lays In-001")).thenReturn(stock);
        StockDto response = stockService.save(stockDto);

        Assertions.assertEquals("Lays In-001", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Stock stock = new Stock();
        stock.setIdentifier("Lays In-001");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");

        Mockito.when(stockRepository.findByIdentifier("Lays In-001")).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);

        StockDto response = stockService.findByIdentifier("Lays In-001");

        Assertions.assertEquals("Lays In-001", response.getIdentifier());
    }

    @Test
    void updateTest() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");

        Stock existingStock = new Stock();
        existingStock.setIdentifier("Lays In-001");

        Mockito.when(stockRepository.findByIdentifier("Lays In-001"))
                .thenReturn(existingStock);
        Mockito.when(stockRepository.save(existingStock))
                .thenReturn(existingStock);

        StockDto response = stockService.update(stockDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");

        Mockito.when(stockRepository.findByIdentifier("Lays In-001"))
                .thenReturn(null);

        StockDto response = stockService.update(stockDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(stockRepository)
                .deleteByIdentifier("Lays In-001");

        stockService.delete("Lays In-001");

        Mockito.verify(stockRepository).deleteByIdentifier("Lays In-001");
    }

    @Test
    void findAllWithPageableTest() {
        Stock stock = new Stock();
        stock.setIdentifier("Lays IN-001");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays IN-001");

        List<Stock> stocks = List.of(stock);
        List<StockDto> stockDtos = List.of(stockDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Stock> stockPage = new PageImpl<>(stocks);

        Mockito.when(stockRepository.findAll(pageable))
                .thenReturn(stockPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(stocks),
                Mockito.any(Type.class)
        )).thenReturn(stockDtos);

        List<StockDto> response = stockService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Lays IN-001", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Stock stock = new Stock();
        stock.setIdentifier("Lays IN-001");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays IN-001");

        List<Stock> stocks = List.of(stock);
        List<StockDto> stockDtos = List.of(stockDto);

        Mockito.when(stockRepository.findAll())
                .thenReturn(stocks);

        Mockito.when(modelMapper.map(
                Mockito.eq(stocks),
                Mockito.any(Type.class)
        )).thenReturn(stockDtos);

        List<StockDto> response = stockService.findAll(null);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusSuccessTest() {
        Stock stock = new Stock();
        stock.setIdentifier("Lays In-001");
        stock.setStatus(false);

        Mockito.when(stockRepository.findByIdentifier("Lays In-001"))
                .thenReturn(stock);

        StockDto response = stockService.toggleStatus("Lays In-001", true);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());

        Mockito.verify(stockRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(stockRepository.findByIdentifier("Lays In-001"))
                .thenReturn(null);

        StockDto response = stockService.toggleStatus("Lays In-001", true);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock not found", response.getMessage());
    }

}