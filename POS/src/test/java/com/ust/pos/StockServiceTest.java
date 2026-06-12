package com.ust.pos;

import com.ust.pos.dto.PaginatedResponseDto;
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
        stockDto.setProduct("P1");
        stockDto.setWarehouse("W1");

        String identifier = "P1_W1";

        Mockito.when(stockRepository.findByIdentifier(identifier)).thenReturn(null);

        Stock stock = new Stock();
        Mockito.when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);

        StockDto response = stockService.save(stockDto);

        Assertions.assertEquals(identifier, response.getIdentifier());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void saveTestFailure() {

        StockDto stockDto = new StockDto();
        stockDto.setProduct("P1");
        stockDto.setWarehouse("W1");

        String identifier = "P1_W1";

        Stock stock = new Stock();

        Mockito.when(stockRepository.findByIdentifier(identifier)).thenReturn(stock);

        StockDto response = stockService.save(stockDto);

        Assertions.assertEquals(identifier, response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Admin");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Admin");

        Mockito.when(stockRepository.findByIdentifier("Admin")).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);

        StockDto response = stockService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Admin");

        Stock existingStock = new Stock();
        existingStock.setIdentifier("Admin");

        Mockito.when(stockRepository.findByIdentifier("Admin"))
                .thenReturn(existingStock);
        Mockito.when(stockRepository.save(existingStock))
                .thenReturn(existingStock);

        StockDto response = stockService.update(stockDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Admin");

        Mockito.when(stockRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        StockDto response = stockService.update(stockDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(stockRepository)
                .deleteByIdentifier("Admin");

        stockService.delete("Admin");

        Mockito.verify(stockRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Admin");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Admin");

        List<Stock> stocks = List.of(stock);
        List<StockDto> stockDtos = List.of(stockDto);

        Page<Stock> stockPage = new PageImpl<>(stocks);

        Mockito.when(stockRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(stockPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(stocks),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(stockDtos);

        PaginatedResponseDto<StockDto> response = stockService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
    }

    @Test
    void findAllActiveTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Admin");
        stock.setStatus(true);

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Admin");

        List<Stock> stocks = List.of(stock);
        List<StockDto> stockDtos = List.of(stockDto);

        Mockito.when(stockRepository.findByStatus(true)).thenReturn(stocks);
        Mockito.when(modelMapper.map(
                Mockito.eq(stocks),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(stockDtos);

        List<StockDto> response = stockService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Admin");
        stock.setStatus(false);

        Mockito.when(stockRepository.findByIdentifier("Admin"))
                .thenReturn(stock);

        Mockito.when(stockRepository.save(stock))
                .thenReturn(stock);

        stockService.changeStatus("Admin", true);

        Assertions.assertTrue(stock.getStatus());

        Mockito.verify(stockRepository).save(stock);
    }
}