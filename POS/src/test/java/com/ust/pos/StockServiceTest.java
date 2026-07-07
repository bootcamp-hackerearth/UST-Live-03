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
    void saveTest_Success() {
        StockDto dto = new StockDto();
        dto.setIdentifier("P1");
        Stock entity = new Stock();
        entity.setIdentifier("P1");
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Stock.class))
                .thenReturn(entity);
        Mockito.when(stockRepository.save(entity))
                .thenReturn(entity);
        StockDto response = stockService.save(dto);
        Assertions.assertEquals("P1", response.getIdentifier());
        Mockito.verify(stockRepository).save(entity);
    }

    @Test
    void saveTest_Failure_WhenAlreadyExists() {
        StockDto dto = new StockDto();
        dto.setIdentifier("P1");
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(new Stock());
        StockDto response = stockService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(stockRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest_Success() {
        StockDto dto = new StockDto();
        dto.setIdentifier("P1");
        Stock existing = new Stock();
        existing.setIdentifier("P1");
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(existing);
        Mockito.doNothing()
                .when(modelMapper).map(dto, existing);
        Mockito.when(stockRepository.save(existing))
                .thenReturn(existing);
        StockDto response = stockService.update(dto);
        Assertions.assertEquals("P1", response.getIdentifier());
        Mockito.verify(stockRepository).save(existing);
    }

    @Test
    void updateTest_Failure_WhenNotFound() {
        StockDto dto = new StockDto();
        dto.setIdentifier("P1");
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);
        StockDto response = stockService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(stockRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest_Success() {
        Stock stock = new Stock();
        stock.setIdentifier("P1");
        StockDto dto = new StockDto();
        dto.setIdentifier("P1");
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);
        StockDto response = stockService.findByIdentifier("P1");
        Assertions.assertEquals("P1", response.getIdentifier());
    }

    @Test
    void findAllTest() {
        List<Stock> entities = List.of(new Stock());
        List<StockDto> dtos = List.of(new StockDto());
        Mockito.when(stockRepository.findByDeletedFalse()).thenReturn(entities);
        Mockito.when(modelMapper.map(
                Mockito.eq(entities),
                Mockito.any(Type.class))).thenReturn(dtos);
        List<StockDto> response = stockService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {
        Stock stock = new Stock();
        stock.setStatus(false);
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);
        stockService.toggleStatus("P1");
        Assertions.assertTrue(stock.isStatus());
        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void deleteTest() {
        Stock stock = new Stock();
        stock.setIdentifier("P1");
        stock.setDeleted(false);
        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(stock);
        stockService.delete("P1");
        Assertions.assertTrue(stock.isDeleted());
        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void deleteTest_WhenStockNotFound() {
        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(null);
        stockService.delete("P1");
        Mockito.verify(
                stockRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void findAllPageableWithSearchTest() {
        Pageable pageable =
                PageRequest.of(0, 10);
        Stock stock = new Stock();
        Page<Stock> page =
                new PageImpl<>(List.of(stock));
        Mockito.when(stockRepository.findAll(
                        Mockito.<Specification<Stock>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(page);
        Page<StockDto> result =
                stockService.findAll(pageable, "P1");
        Assertions.assertEquals(
                1,
                result.getContent().size()
        );
        Mockito.verify(stockRepository)
                .findAll(
                        Mockito.<Specification<Stock>>any(),
                        Mockito.eq(pageable)
                );
    }

    @Test
    void findAllPageableWithoutSearchTest() {
        Pageable pageable =
                PageRequest.of(0, 10);
        Stock stock = new Stock();
        Page<Stock> page =
                new PageImpl<>(List.of(stock));
        Mockito.when(
                stockRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);
        Page<StockDto> result =
                stockService.findAll(pageable, "");
        Assertions.assertEquals(
                1,
                result.getContent().size()
        );
        Mockito.verify(stockRepository)
                .findByDeletedFalse(pageable);
    }

    @Test
    void toggleStatus_NotFound() {
        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(null);
        stockService.toggleStatus("P1");
        Mockito.verify(
                stockRepository,
                Mockito.never()
        ).save(Mockito.any());
    }
}