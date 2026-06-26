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
import org.modelmapper.TypeToken;
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

    // ---------------- SAVE ----------------

    @Test
    void saveTest_Success_WhenQuantityGreaterThanZero() {
        StockDto dto = new StockDto();
        dto.setIdentifier("P1");
        dto.setQuantity(10);

        Stock entity = new Stock();
        entity.setIdentifier("P1");

        Mockito.when(modelMapper.map(dto, Stock.class))
                .thenReturn(entity);
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);
        Mockito.when(stockRepository.save(entity))
                .thenReturn(entity);

        StockDto response = stockService.save(dto);

        Assertions.assertEquals("Available", response.getStockStatus());
        Mockito.verify(stockRepository).save(entity);
    }

    @Test
    void saveTest_Success_WhenQuantityZero() {
        StockDto dto = new StockDto();
        dto.setIdentifier("P1");
        dto.setQuantity(0);

        Stock entity = new Stock();
        entity.setIdentifier("P1");

        Mockito.when(modelMapper.map(dto, Stock.class))
                .thenReturn(entity);
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);
        Mockito.when(stockRepository.save(entity))
                .thenReturn(entity);

        StockDto response = stockService.save(dto);

        Assertions.assertEquals("Not Available", response.getStockStatus());
        Mockito.verify(stockRepository).save(entity);
    }

    @Test
    void saveTest_Failure_WhenAlreadyExists() {
        StockDto dto = new StockDto();
        dto.setIdentifier("P1");
        dto.setQuantity(5);

        Stock entity = new Stock();
        entity.setIdentifier("P1");

        Mockito.when(modelMapper.map(dto, Stock.class))
                .thenReturn(entity);
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(new Stock());

        StockDto response = stockService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(stockRepository, Mockito.never())
                .save(Mockito.any());
    }

    // ---------------- UPDATE ----------------

    @Test
    void updateTest_Success_WhenStockExists() {
        StockDto dto = new StockDto();
        dto.setIdentifier("P1");
        dto.setQuantity(15);

        Stock existing = new Stock();
        existing.setIdentifier("P1");

        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(dto, existing);

        Mockito.when(stockRepository.save(existing))
                .thenReturn(existing);

        StockDto response = stockService.update(dto);

        Assertions.assertEquals("Available", response.getStockStatus());
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

    // ---------------- FIND BY IDENTIFIER ----------------

    @Test
    void findByIdentifierTest() {
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

    // ---------------- FIND ALL ----------------

    @Test
    void findAllTest() {

        List<Stock> entities = List.of(new Stock());
        List<StockDto> dtos = List.of(new StockDto());

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();

        Mockito.when(stockRepository.findByDeletedFalse())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);

        List<StockDto> response = stockService.findAll();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());

        Mockito.verify(stockRepository)
                .findByDeletedFalse();
    }

    // ---------------- UPDATE STATUS ONLY ----------------

    @Test
    void updateStatusOnlyTest() {
        Stock stock = new Stock();
        stock.setStatus(false);

        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(stock);
        Mockito.when(stockRepository.save(stock))
                .thenReturn(stock);

        stockService.updateStatusOnly("P1", true);

        Assertions.assertTrue(stock.getStatus());
        Mockito.verify(stockRepository).save(stock);
    }

    // ---------------- DELETE ----------------

    @Test
    void deleteTest() {

        Stock stock = new Stock();
        stock.setIdentifier("P1");
        stock.setDeleted(false);

        Mockito.when(
                        stockRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(stock);

        stockService.delete("P1");

        Assertions.assertTrue(stock.isDeleted());

        Mockito.verify(stockRepository)
                .findByIdentifierAndDeletedFalse("P1");

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void findAll_WithPagination_ShouldReturnStockDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        Stock stock = new Stock();
        stock.setIdentifier("P1");

        Page<Stock> stockPage =
                new PageImpl<>(List.of(stock));

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("P1");

        Mockito.when(
                        stockRepository.findByDeletedFalse(pageable))
                .thenReturn(stockPage);

        Mockito.when(
                        modelMapper.map(stock, StockDto.class))
                .thenReturn(stockDto);

        Page<StockDto> response =
                stockService.findAll("", pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());

        Mockito.verify(stockRepository)
                .findByDeletedFalse(pageable);

        Mockito.verify(modelMapper)
                .map(stock, StockDto.class);
    }

    @Test
    void findAll_WithSearch_ShouldReturnFilteredStockDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        Stock stock = new Stock();
        stock.setIdentifier("P1");

        Page<Stock> stockPage =
                new PageImpl<>(List.of(stock));

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("P1");

        Mockito.when(
                        stockRepository
                                .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                                        "P1",
                                        pageable))
                .thenReturn(stockPage);

        Mockito.when(
                        modelMapper.map(stock, StockDto.class))
                .thenReturn(stockDto);

        Page<StockDto> response =
                stockService.findAll("P1", pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());

        Mockito.verify(stockRepository)
                .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                        "P1",
                        pageable);

        Mockito.verify(modelMapper)
                .map(stock, StockDto.class);
    }
}

