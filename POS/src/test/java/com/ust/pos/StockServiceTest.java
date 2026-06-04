package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.model.ProductRepository;
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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductRepository productRepository;

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
        request.setProduct("P1");
        request.setWarehouse("W1");

        Stock stock = new Stock();
        Stock savedStock = new Stock();
        StockDto mappedResponse = new StockDto();

        Mockito.when(productRepository.existsByIdentifier(Mockito.any()))
                .thenReturn(true);

        Mockito.when(stockRepository.findByIdentifier(Mockito.any()))
                .thenReturn(null);

        Mockito.when(modelMapper.map(request, Stock.class))
                .thenReturn(stock);

        Mockito.when(stockRepository.save(stock))
                .thenReturn(savedStock);

        Mockito.when(modelMapper.map(savedStock, StockDto.class))
                .thenReturn(mappedResponse);

        StockDto response = stockService.save(request);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void update_success() {
        StockDto dto = new StockDto();
        dto.setId(1L);
        dto.setIdentifier("STK1");

        Stock existingStock = new Stock();
        existingStock.setIdentifier("OLD");

        Mockito.when(stockRepository.findById(1L))
                .thenReturn(Optional.of(existingStock));

        Mockito.when(productRepository.findByIdentifier("STK1"))
                .thenReturn(null);

        StockDto response = stockService.update(dto);

        Mockito.verify(modelMapper).map(dto, existingStock);
        Mockito.verify(stockRepository).save(existingStock);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Stock successfully edited", response.getMessage());
    }

    @Test
    void findById_success() {
        Stock stock = new Stock();
        StockDto dto = new StockDto();

        Mockito.when(stockRepository.findById(1L))
                .thenReturn(Optional.of(stock));

        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto response = stockService.findById(1L);

        Assertions.assertNotNull(response);
    }

    @Test
    void delete_success() {
        Mockito.doNothing()
                .when(stockRepository)
                .deleteByIdentifier("STK1");

        stockService.delete("STK1");

        Mockito.verify(stockRepository)
                .deleteByIdentifier("STK1");
    }
}