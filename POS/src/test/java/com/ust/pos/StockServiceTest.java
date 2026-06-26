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
        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");
        Mockito.when(stockRepository.findByIdentifier("P1_W1")).thenReturn(null);
        Stock stock = new Stock();
        Mockito.when(modelMapper.map(dto, Stock.class)).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);
        StockDto response = stockService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("P1_W1", response.getIdentifier());
    }

    @Test
    void saveTestAlreadyExists() {
        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");
        Stock existing = new Stock();
        existing.setDeleted(false);
        Mockito.when(stockRepository.findByIdentifier("P1_W1")).thenReturn(existing);
        StockDto response = stockService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestDeletedExists() {
        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");
        Stock existing = new Stock();
        existing.setDeleted(true);
        Mockito.when(stockRepository.findByIdentifier("P1_W1")).thenReturn(existing);
        StockDto response = stockService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTest() {
        StockDto dto = new StockDto();
        dto.setIdentifier("S1");
        Stock existing = new Stock();
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(stockRepository.save(existing)).thenReturn(existing);
        StockDto response = stockService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        StockDto dto = new StockDto();
        dto.setIdentifier("S1");
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        StockDto response = stockService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Stock stock = new Stock();
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);
        stockService.delete("S1");
        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void deleteNullTest() {
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        stockService.delete("S1");
        Mockito.verify(stockRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllTest() {
        Stock stock = new Stock();
        StockDto dto = new StockDto();
        List<Stock> list = List.of(stock);
        List<StockDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Stock> page = new PageImpl<>(list);
        Mockito.when(stockRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        WsDto<StockDto> response = stockService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findByIdentifierTest() {
        Stock stock = new Stock();
        StockDto dto = new StockDto();
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);
        StockDto response = stockService.findByIdentifier("S1");
        Assertions.assertNotNull(response);
    }
}