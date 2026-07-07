package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

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
        verify(stockRepository).save(stock);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        StockDto stockDto = new StockDto();
        stockDto.setProduct("PROD1");
        stockDto.setWarehouse("WH1");

        Stock existingStock = new Stock();
        existingStock.setDeleted(false);

        Mockito.when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(existingStock);

        StockDto response = stockService.save(stockDto);

        Assertions.assertEquals("PROD1WH1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock with identifier - PROD1WH1 already exists", response.getMessage());
        Mockito.verify(stockRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureAlreadyDeletedTest() {
        StockDto stockDto = new StockDto();
        stockDto.setProduct("PROD1");
        stockDto.setWarehouse("WH1");

        Stock existingStock = new Stock();
        existingStock.setDeleted(true);

        Mockito.when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(existingStock);

        StockDto response = stockService.save(stockDto);

        Assertions.assertEquals("PROD1WH1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock with identifier - PROD1WH1 was deleted , Please Contact the Administrator to add.", response.getMessage());
        Mockito.verify(stockRepository, Mockito.never()).save(Mockito.any());
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
        verify(modelMapper).map(stockDto, existingStock);
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
        Assertions.assertEquals("Stock with identifier - PROD1WH1 not found", response.getMessage());
        Mockito.verify(stockRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Stock stock = new Stock();
        Mockito.when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(stock);

        stockService.delete("PROD1WH1");

        verify(stockRepository).findByIdentifier("PROD1WH1");
    }

    @Test
    void findAllSuccessTest() {
        Stock stock = new Stock();
        List<Stock> stockList = List.of(stock);

        StockDto dto = new StockDto();
        List<StockDto> stockDtos = List.of(dto);

        Page<Stock> page = new PageImpl<>(stockList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(stockRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(stockList), Mockito.any(Type.class))).thenReturn(stockDtos);

        WsDto<StockDto> result = stockService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Stock stock = new Stock();
        StockDto stockDto = new StockDto();

        Mockito.when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);

        StockDto response = stockService.findByIdentifier("PROD1WH1");

        Assertions.assertNotNull(response);
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(stockRepository.findByIdentifier("PROD1WH1")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            stockService.findByIdentifier("PROD1WH1");
        });
    }

    @Test
    void findAllSpecificationSuccessTest() {
        Stock stock = new Stock();
        List<Stock> stockList = List.of(stock);

        StockDto dto = new StockDto();
        List<StockDto> stockDtos = List.of(dto);

        Page<Stock> page = new PageImpl<>(stockList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Stock> specification = Mockito.mock(Specification.class);

        Mockito.when(stockRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(stockList), Mockito.any(Type.class))).thenReturn(stockDtos);

        WsDto<StockDto> result = stockService.findAll(specification, pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }
}