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
    void findAllWithPageableTest() {

        Stock stock = new Stock();
        stock.setIdentifier("STK1");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STK1");

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

        List<StockDto> result = stockService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("STK1", result.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STK1");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STK1");

        List<Stock> stocks = List.of(stock);
        List<StockDto> stockDtos = List.of(stockDto);

        Mockito.when(stockRepository.findAll())
                .thenReturn(stocks);

        Mockito.when(modelMapper.map(
                Mockito.eq(stocks),
                Mockito.any(Type.class)
        )).thenReturn(stockDtos);

        List<StockDto> result = stockService.findAll(null);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void save_success() {
        StockDto request = new StockDto();
        request.setIdentifier("STK1");

        Stock stock = new Stock();
        Stock savedStock = new Stock();

        StockDto mappedResponse = new StockDto();

        Mockito.when(modelMapper.map(request, Stock.class))
                .thenReturn(stock);

        Mockito.when(stockRepository.save(stock))
                .thenReturn(savedStock);

        Mockito.when(modelMapper.map(savedStock, StockDto.class))
                .thenReturn(mappedResponse);

        StockDto response = stockService.save(request);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Successfully added the stock", response.getMessage());

        Mockito.verify(stockRepository).save(stock);
        Mockito.verify(modelMapper).map(request, Stock.class);
        Mockito.verify(modelMapper).map(savedStock, StockDto.class);
    }

    @Test
    void update_success() {
        StockDto dto = new StockDto();
        dto.setId(1L);

        Stock existingStock = new Stock();
        existingStock.setId(1L);

        Stock updatedStock = new Stock();

        Mockito.when(stockRepository.findById(1L))
                .thenReturn(java.util.Optional.of(existingStock));

        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, existingStock);

        Mockito.when(stockRepository.save(existingStock))
                .thenReturn(updatedStock);

        Mockito.when(modelMapper.map(updatedStock, StockDto.class))
                .thenReturn(new StockDto());

        StockDto response = stockService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Stock updated successfully", response.getMessage());
    }

    @Test
    void findById_success() {
        Stock stock = new Stock();
        StockDto dto = new StockDto();

        Mockito.when(stockRepository.findById(1L))
                .thenReturn(java.util.Optional.of(stock));

        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto response = stockService.findById(1L);

        Assertions.assertNotNull(response);
    }

    @Test
    void delete_success() {
        Mockito.doNothing()
                .when(stockRepository)
                .deleteById(1L);

        stockService.delete(1L);

        Mockito.verify(stockRepository)
                .deleteById(1L);
    }
}