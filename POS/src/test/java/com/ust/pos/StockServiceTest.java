package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
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

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {

        StockDto stockDto = new StockDto();
        stockDto.setProduct("PROD1");
        stockDto.setWarehouse("WH1");

        Stock stock = new Stock();

        Mockito.when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(null);
        Mockito.when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);

        StockDto response = stockService.save(stockDto);

        Assertions.assertEquals("PROD1WH1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(stockRepository).save(stock);
    }

    @Test
    void saveFailureTest() {

        StockDto stockDto = new StockDto();
        stockDto.setProduct("PROD1");
        stockDto.setWarehouse("WH1");

        Mockito.when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(new Stock());

        StockDto response = stockService.save(stockDto);

        Assertions.assertEquals("PROD1WH1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateSuccessTest() {

        StockDto stockDto = new StockDto();
        stockDto.setProduct("PROD1");
        stockDto.setWarehouse("WH1");

        Stock existingStock = new Stock();

        Mockito.when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(existingStock);

        StockDto response = stockService.update(stockDto);

        Assertions.assertEquals("PROD1WH1", response.getIdentifier());
        verify(stockRepository).save(existingStock);
    }

    @Test
    void updateFailureTest() {

        StockDto stockDto = new StockDto();
        stockDto.setProduct("PROD1");
        stockDto.setWarehouse("WH1");

        Mockito.when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(null);

        StockDto response = stockService.update(stockDto);

        Assertions.assertEquals("PROD1WH1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteSuccessTest() {

        stockService.delete("PROD1WH1");

        verify(stockRepository).deleteByIdentifier("PROD1WH1");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Stock stock = new Stock();
        stock.setIdentifier("PROD1WH1");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("PROD1WH1");

        Mockito.when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);

        StockDto response = stockService.findByIdentifier("PROD1WH1");

        Assertions.assertEquals("PROD1WH1", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(null);

        StockDto response = stockService.findByIdentifier("PROD1WH1");

        Assertions.assertNull(response);
    }

    @Test
    void findAllSuccessTest() {

        Stock s1 = new Stock();
        s1.setIdentifier("PROD1WH1");

        Stock s2 = new Stock();
        s2.setIdentifier("PROD2WH2");

        List<Stock> stocks = List.of(s1, s2);

        StockDto d1 = new StockDto();
        d1.setIdentifier("PROD1WH1");

        StockDto d2 = new StockDto();
        d2.setIdentifier("PROD2WH2");

        List<StockDto> stockDtos = List.of(d1, d2);

        Page<Stock> page = new PageImpl<>(stocks);
        Pageable pageable = PageRequest.of(0, 20);

        Mockito.when(stockRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(stocks), Mockito.any(Type.class))).thenReturn(stockDtos);

        WsDto<StockDto> result = stockService.findAll(pageable);

        Assertions.assertEquals(2, result.getDtoList().size());
    }
}