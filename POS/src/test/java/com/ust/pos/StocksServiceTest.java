package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.StocksDto;
import com.ust.pos.model.Stocks;
import com.ust.pos.model.StocksRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import com.ust.pos.stocks.service.impl.StocksServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class StocksServiceTest {

    @Mock
    ProductServiceImpl productService;
    @Mock
    private StocksRepository stocksRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private StocksServiceImpl stocksService;

    @Test
    void saveTest() {
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("Admin");
        ProductDto productDto = new ProductDto();
        productDto.setName("123");
        Mockito.when(stocksRepository.findByIdentifier("Admin")).thenReturn(null);
        Stocks stocks = new Stocks();
        Mockito.when(modelMapper.map(stocksDto, Stocks.class)).thenReturn(stocks);
        Mockito.when(stocksRepository.save(stocks)).thenReturn(stocks);
        Mockito.when(productService.findByIdentifier(stocksDto.getIdentifier())).thenReturn(productDto);
        StocksDto response = stocksService.save(stocksDto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("Admin");
        Stocks existingStocks = new Stocks();
        existingStocks.setIdentifier("Admin");
        Mockito.when(stocksRepository.findByIdentifier("Admin")).thenReturn(existingStocks);
        StocksDto response = stocksService.save(stocksDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Stocks stocks = new Stocks();
        stocks.setIdentifier("Admin");
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("Admin");
        Mockito.when(stocksRepository.findByIdentifier("Admin")).thenReturn(stocks);
        Mockito.when(modelMapper.map(stocks, StocksDto.class)).thenReturn(stocksDto);
        StocksDto response = stocksService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("Admin");
        Stocks existingStocks = new Stocks();
        existingStocks.setIdentifier("Admin");
        Mockito.when(stocksRepository.findByIdentifier("Admin")).thenReturn(existingStocks);
        Mockito.when(stocksRepository.save(existingStocks)).thenReturn(existingStocks);
        StocksDto response = stocksService.update(stocksDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("Admin");
        Mockito.when(stocksRepository.findByIdentifier("Admin")).thenReturn(null);
        StocksDto response = stocksService.update(stocksDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(stocksRepository).deleteByIdentifier("Admin");
        boolean response = stocksService.delete("Admin");
        Assertions.assertEquals(true, response);
    }

    @Test
    void findAllTest() {
        Stocks stocks = new Stocks();
        stocks.setIdentifier("Admin");
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("Admin");
        List<Stocks> stockss = List.of(stocks);
        List<StocksDto> stocksDtos = List.of(stocksDto);
        Page<Stocks> stocksPage = new PageImpl<>(stockss, PageRequest.of(0, 2), stockss.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(stocksRepository.findAll(pageable)).thenReturn(stocksPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(stockss),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(stocksDtos);
        List<StocksDto> response = stocksService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }


    @Test
    void findByStatusTest() {
        Stocks stocks = new Stocks();
        stocks.setIdentifier("Admin");
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("Admin");
        List<Stocks> stockss = List.of(stocks);
        List<StocksDto> stocksDtos = List.of(stocksDto);
        Mockito.when(stocksRepository.findByStatusIsTrue()).thenReturn(stockss);
        Mockito.when(modelMapper.map(
                Mockito.eq(stockss),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(stocksDtos);
        List<StocksDto> response = stocksService.findIfTrue();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleTestActive() {
        Stocks stocks = new Stocks();
        stocks.setStatus(false);
        StocksDto stocksDto = new StocksDto();
        stocksDto.setStatus(true);
        Mockito.when(stocksRepository.findByIdentifier("Admin")).thenReturn(stocks);
        Mockito.when(modelMapper.map(stocks, StocksDto.class)).thenReturn(stocksDto);
        StocksDto response = stocksService.toggleStatus("Admin");
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleTestInactive() {
        Stocks stocks = new Stocks();
        stocks.setStatus(true);
        StocksDto stocksDto = new StocksDto();
        stocksDto.setStatus(false);
        Mockito.when(stocksRepository.findByIdentifier("Admin")).thenReturn(stocks);
        Mockito.when(modelMapper.map(stocks, StocksDto.class)).thenReturn(stocksDto);
        StocksDto response = stocksService.toggleStatus("Admin");
        Assertions.assertFalse(response.isStatus());
    }

}
