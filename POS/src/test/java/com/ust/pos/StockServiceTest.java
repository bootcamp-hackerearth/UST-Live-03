package com.ust.pos;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.StockDto;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

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
    void saveTest_success() {
        StockDto dto = new StockDto();
        dto.setIdentifier("Stock1");

        Mockito.when(stockRepository.findByIdentifier("Stock1")).thenReturn(null);

        Stock stock = new Stock();
        Mockito.when(modelMapper.map(dto, Stock.class)).thenReturn(stock);

        Mockito.when(stockRepository.save(stock)).thenReturn(stock);

        StockDto response = stockService.save(dto);

        Assertions.assertEquals("Stock1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void saveTest_duplicate() {
        StockDto dto = new StockDto();
        dto.setIdentifier("Stock1");

        Stock existing = new Stock();
        existing.setDeleted(false);

        Mockito.when(stockRepository.findByIdentifier("Stock1")).thenReturn(existing);

        StockDto response = stockService.save(dto);

        Assertions.assertEquals("Stock1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock with identifier - Stock1 already exists", response.getMessage());

        Mockito.verify(stockRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveTest_softDeleted() {
        StockDto dto = new StockDto();
        dto.setIdentifier("Stock1");

        Stock existing = new Stock();
        existing.setDeleted(true);

        Mockito.when(stockRepository.findByIdentifier("Stock1")).thenReturn(existing);

        StockDto response = stockService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock with identifier - Stock1 has been soft deleted. Restore it by changing status.", response.getMessage());

        Mockito.verify(stockRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void findByIdentifierTest() {
        Stock stock = new Stock();
        stock.setIdentifier("Stock1");

        StockDto dto = new StockDto();
        dto.setIdentifier("Stock1");

        Mockito.when(stockRepository.findByIdentifier("Stock1")).thenReturn(stock);

        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);

        StockDto response = stockService.findByIdentifier("Stock1");

        Assertions.assertEquals("Stock1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(stockRepository.findByIdentifier("Stock1")).thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> stockService.findByIdentifier("Stock1"));

        Assertions.assertEquals("Stock with identifier 'Stock1' not found", exception.getMessage());

        Mockito.verify(modelMapper, Mockito.never()).map(Mockito.any(), Mockito.eq(StockDto.class));
    }


    @Test
    void updateTest_success() {
        StockDto dto = new StockDto();
        dto.setIdentifier("Stock1");

        Stock existing = new Stock();
        existing.setIdentifier("Stock1");

        Mockito.when(stockRepository.findByIdentifier("Stock1")).thenReturn(existing);

        Mockito.when(stockRepository.save(existing)).thenReturn(existing);

        StockDto response = stockService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper).map(dto, existing);
        Mockito.verify(stockRepository).save(existing);
    }

    @Test
    void updateTest_notFound() {
        StockDto dto = new StockDto();
        dto.setIdentifier("Stock1");

        Mockito.when(stockRepository.findByIdentifier("Stock1")).thenReturn(null);

        StockDto response = stockService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock with identifier - Stock1 not found", response.getMessage());

        Mockito.verify(stockRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void deleteTest_success() {
        Stock stock = new Stock();
        stock.setIdentifier("Stock1");
        stock.setDeleted(false);
        stock.setStatus(true);

        Mockito.when(stockRepository.findByIdentifier("Stock1")).thenReturn(stock);

        Mockito.when(stockRepository.save(stock)).thenReturn(stock);

        boolean result = stockService.delete("Stock1");

        Assertions.assertTrue(result);
        Assertions.assertTrue(stock.getDeleted());
        Assertions.assertFalse(stock.getStatus());

        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void deleteTest_notFound() {
        Mockito.when(stockRepository.findByIdentifier("Stock1")).thenReturn(null);

        boolean result = stockService.delete("Stock1");

        Assertions.assertFalse(result);

        Mockito.verify(stockRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void toggleStatusTest() {
        Stock stock = new Stock();
        stock.setIdentifier("Stock1");
        stock.setStatus(true);

        Mockito.when(stockRepository.findByIdentifier("Stock1")).thenReturn(stock);

        Mockito.when(stockRepository.save(stock)).thenReturn(stock);

        stockService.toggleStatus("Stock1");

        Assertions.assertFalse(stock.getStatus());

        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void toggleStatusTest_notFound() {
        Mockito.when(stockRepository.findByIdentifier("Stock1")).thenReturn(null);

        stockService.toggleStatus("Stock1");

        Mockito.verify(stockRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void findAllPaginationTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Stock1");

        StockDto dto = new StockDto();
        dto.setIdentifier("Stock1");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Stock> page = new PageImpl<>(List.of(stock), pageable, 1);

        Mockito.when(stockRepository.findByDeletedFalse(pageable)).thenReturn(page);

        Type listType = new TypeToken<List<StockDto>>() {}.getType();

        Mockito.when(modelMapper.map(page.getContent(), listType)).thenReturn(List.of(dto));

        PageDto<StockDto> response = stockService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Stock1", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAllWithSpecificationTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Stock1");

        StockDto dto = new StockDto();
        dto.setIdentifier("Stock1");

        Pageable pageable = PageRequest.of(0, 10);
        Specification<Stock> spec = Mockito.mock(Specification.class);

        Page<Stock> page = new PageImpl<>(List.of(stock), pageable, 1);

        Mockito.when(stockRepository.findAll(spec, pageable)).thenReturn(page);

        Type listType = new TypeToken<List<StockDto>>() {}.getType();

        Mockito.when(modelMapper.map(page.getContent(), listType)).thenReturn(List.of(dto));

        PageDto<StockDto> response = stockService.findAll(spec, pageable, "Stock1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Stock1", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Assertions.assertEquals("Stock1", response.getKeyword());
    }

    @Test
    void findActiveStocksTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Stock1");
        stock.setStatus(true);

        StockDto dto = new StockDto();
        dto.setIdentifier("Stock1");

        List<Stock> list = List.of(stock);

        Type listType = new TypeToken<List<StockDto>>() {}.getType();

        Mockito.when(stockRepository.findByStatusTrue()).thenReturn(list);

        Mockito.when(modelMapper.map(list, listType)).thenReturn(List.of(dto));

        List<StockDto> result = stockService.findActiveStocks();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Stock1", result.get(0).getIdentifier());

        Mockito.verify(stockRepository).findByStatusTrue();
    }

    @Test
    void findActiveStocksTest_empty() {

        List<Stock> emptyList = List.of();

        Type listType = new TypeToken<List<StockDto>>() {}.getType();

        Mockito.when(stockRepository.findByStatusTrue()).thenReturn(emptyList);

        Mockito.when(modelMapper.map(emptyList, listType)).thenReturn(List.of());

        List<StockDto> result = stockService.findActiveStocks();

        Assertions.assertTrue(result.isEmpty());

        Mockito.verify(stockRepository).findByStatusTrue();
    }
}