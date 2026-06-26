package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.StocksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stocks;
import com.ust.pos.model.StocksRepository;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stocks.service.impl.StocksServiceImpl;
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
class StocksServiceTest {

    @Mock
    private StocksRepository stocksRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ProductService productService;

    @InjectMocks
    private StocksServiceImpl stocksService;

    @Test
    void findByIdentifierTest() {
        Stocks stocks = new Stocks();
        stocks.setIdentifier("STOCK-01");
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("STOCK-01");

        Mockito.when(stocksRepository.findByIdentifier("STOCK-01")).thenReturn(stocks);
        Mockito.when(modelMapper.map(stocks, StocksDto.class)).thenReturn(stocksDto);

        StocksDto response = stocksService.findByIdentifier("STOCK-01");

        Assertions.assertEquals("STOCK-01", response.getIdentifier());
    }

    @Test
    void saveTest() {
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("STOCK-01");

        ProductDto productDto = new ProductDto();
        productDto.setName("Product Name");

        Stocks stocks = new Stocks();

        Mockito.when(stocksRepository.findByIdentifier("STOCK-01")).thenReturn(null);
        Mockito.when(modelMapper.map(stocksDto, Stocks.class)).thenReturn(stocks);
        Mockito.when(productService.findByIdentifier("STOCK-01")).thenReturn(productDto);
        Mockito.when(stocksRepository.save(stocks)).thenReturn(stocks);

        StocksDto response = stocksService.save(stocksDto);

        Assertions.assertEquals("STOCK-01", response.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("STOCK-01");

        Stocks existingStocks = new Stocks();
        existingStocks.setIdentifier("STOCK-01");
        existingStocks.setDeleted(false);

        Mockito.when(stocksRepository.findByIdentifier("STOCK-01")).thenReturn(existingStocks);

        StocksDto response = stocksService.save(stocksDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("STOCK-01");

        Stocks existingStocks = new Stocks();
        existingStocks.setIdentifier("STOCK-01");
        existingStocks.setDeleted(true);

        Mockito.when(stocksRepository.findByIdentifier("STOCK-01")).thenReturn(existingStocks);

        StocksDto response = stocksService.save(stocksDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTest() {
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("STOCK-01");

        Stocks existingStocks = new Stocks();
        existingStocks.setIdentifier("STOCK-01");

        Mockito.when(stocksRepository.findByIdentifier("STOCK-01")).thenReturn(existingStocks);
        Mockito.when(stocksRepository.save(existingStocks)).thenReturn(existingStocks);

        StocksDto response = stocksService.update(stocksDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("STOCK-01");

        Mockito.when(stocksRepository.findByIdentifier("STOCK-01")).thenReturn(null);

        StocksDto response = stocksService.update(stocksDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Stocks stocks = new Stocks();
        stocks.setIdentifier("STOCK-01");

        Mockito.when(stocksRepository.findByIdentifier("STOCK-01")).thenReturn(stocks);
        Mockito.when(stocksRepository.save(stocks)).thenReturn(stocks);

        boolean response = stocksService.delete("STOCK-01");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(stocksRepository.findByIdentifier("STOCK-01")).thenReturn(null);

        boolean response = stocksService.delete("STOCK-01");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Stocks stocks = new Stocks();
        List<Stocks> stocksList = List.of(stocks);
        Page<Stocks> stocksPage = new PageImpl<>(stocksList, pageable, stocksList.size());

        StocksDto stocksDto = new StocksDto();
        List<StocksDto> stocksDtos = List.of(stocksDto);

        Mockito.when(stocksRepository.findByDeletedFalse(pageable)).thenReturn(stocksPage);
        Mockito.when(modelMapper.map(Mockito.eq(stocksList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(stocksDtos);

        WsDto<StocksDto> response = stocksService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void findByStatusTest() {
        Stocks stocks = new Stocks();
        List<Stocks> stocksList = List.of(stocks);
        StocksDto stocksDto = new StocksDto();
        List<StocksDto> stocksDtos = List.of(stocksDto);

        Mockito.when(stocksRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(stocksList);
        Mockito.when(modelMapper.map(Mockito.eq(stocksList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(stocksDtos);

        List<StocksDto> response = stocksService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleTestActive() {
        Stocks stocks = new Stocks();
        stocks.setStatus(false);
        StocksDto stocksDto = new StocksDto();
        stocksDto.setStatus(true);

        Mockito.when(stocksRepository.findByIdentifier("STOCK-01")).thenReturn(stocks);
        Mockito.when(modelMapper.map(stocks, StocksDto.class)).thenReturn(stocksDto);

        StocksDto response = stocksService.toggleStatus("STOCK-01");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleTestInactive() {
        Stocks stocks = new Stocks();
        stocks.setStatus(true);
        StocksDto stocksDto = new StocksDto();
        stocksDto.setStatus(false);

        Mockito.when(stocksRepository.findByIdentifier("STOCK-01")).thenReturn(stocks);
        Mockito.when(modelMapper.map(stocks, StocksDto.class)).thenReturn(stocksDto);

        StocksDto response = stocksService.toggleStatus("STOCK-01");

        Assertions.assertFalse(response.isStatus());
    }
}