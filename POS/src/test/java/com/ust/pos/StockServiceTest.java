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
class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Stock1");

        Stock stock = new Stock();

        Mockito.when(stockRepository.findByIdentifier("Stock1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(stockDto, Stock.class))
                .thenReturn(stock);
        Mockito.when(stockRepository.save(stock))
                .thenReturn(stock);

        StockDto response = stockService.save(stockDto);

        Assertions.assertEquals("Stock1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Stock1");

        Mockito.when(stockRepository.findByIdentifier("Stock1"))
                .thenReturn(new Stock());

        StockDto response = stockService.save(stockDto);

        Assertions.assertEquals("Stock1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Stock stock = new Stock();
        stock.setIdentifier("Stock1");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Stock1");

        Mockito.when(stockRepository.findByIdentifier("Stock1"))
                .thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(stockDto);

        StockDto response = stockService.findByIdentifier("Stock1");

        Assertions.assertEquals("Stock1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Stock1");

        Stock existingStock = new Stock();
        existingStock.setIdentifier("Stock1");

        Mockito.when(stockRepository.findByIdentifier("Stock1"))
                .thenReturn(existingStock);
        Mockito.when(stockRepository.save(existingStock))
                .thenReturn(existingStock);

        StockDto response = stockService.update(stockDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Stock1");

        Mockito.when(stockRepository.findByIdentifier("Stock1"))
                .thenReturn(null);

        StockDto response = stockService.update(stockDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(stockRepository)
                .deleteByIdentifier("Stock1");

        stockService.delete("Stock1");

        Mockito.verify(stockRepository)
                .deleteByIdentifier("Stock1");
    }

    @Test
    void toggleStatusTest() {
        Stock stock = new Stock();
        stock.setIdentifier("Stock1");
        stock.setStatus(true);

        Mockito.when(stockRepository.findByIdentifier("Stock1"))
                .thenReturn(stock);
        Mockito.when(stockRepository.save(stock))
                .thenReturn(stock);

        stockService.toggleStatus("Stock1");

        Assertions.assertFalse(stock.getStatus());
        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void toggleStatusTestFailure() {
        Mockito.when(stockRepository.findByIdentifier("Stock1"))
                .thenReturn(null);

        stockService.toggleStatus("Stock1");

        Mockito.verify(stockRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllPaginationTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Stock1");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Stock1");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Stock> stockPage =
                new PageImpl<>(List.of(stock), pageable, 1);

        Mockito.when(stockRepository.findAll(pageable))
                .thenReturn(stockPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(stockPage.getContent()),
                Mockito.any(Type.class)
        )).thenReturn(List.of(stockDto));

        List<StockDto> response = stockService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }
}