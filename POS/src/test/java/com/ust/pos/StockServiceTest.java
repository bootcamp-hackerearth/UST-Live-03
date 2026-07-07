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
    void saveTestSuccess() {
        StockDto dto = new StockDto();
        dto.setIdentifier("Admin");

        Stock stock = new Stock();

        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Stock.class))
                .thenReturn(stock);
        Mockito.when(stockRepository.save(stock))
                .thenReturn(stock);

        StockDto response = stockService.save(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void saveTestFailureWhenAlreadyExists() {
        StockDto dto = new StockDto();
        dto.setIdentifier("Admin");

        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(new Stock());

        StockDto response = stockService.save(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(stockRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Stock stock = new Stock();
        stock.setIdentifier("Admin");

        StockDto dto = new StockDto();
        dto.setIdentifier("Admin");

        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto response = stockService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTestSuccess() {
        StockDto dto = new StockDto();
        dto.setIdentifier("Admin");

        Stock existingStock = new Stock();
        existingStock.setIdentifier("Admin");

        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(existingStock);
        Mockito.when(stockRepository.save(existingStock))
                .thenReturn(existingStock);

        StockDto response = stockService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper)
                .map(dto, existingStock);

        Mockito.verify(stockRepository)
                .save(existingStock);
    }

    @Test
    void updateTestFailureWhenNotFound() {
        StockDto dto = new StockDto();
        dto.setIdentifier("Admin");

        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(null);

        StockDto response = stockService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Mockito.verify(stockRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Stock stock = new Stock();

        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(stock);

        stockService.delete("Admin");

        Assertions.assertTrue(stock.isDeleted());

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void findAllTest() {
        List<Stock> stocks = List.of(new Stock());
        List<StockDto> dtos = List.of(new StockDto());

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();

        Mockito.when(stockRepository.findByDeletedFalse())
                .thenReturn(stocks);
        Mockito.when(modelMapper.map(stocks, listType))
                .thenReturn(dtos);

        List<StockDto> response = stockService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {
        Stock stock = new Stock();
        stock.setStatus(true);

        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(stock);

        stockService.toggleStatus("Admin");

        Assertions.assertFalse(stock.isStatus());

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void toggleStatusNullTest() {
        Mockito.when(stockRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(null);

        stockService.toggleStatus("Admin");

        Mockito.verify(stockRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllWithPaginationTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Stock stock = new Stock();
        stock.setIdentifier("Admin");

        StockDto dto = new StockDto();
        dto.setIdentifier("Admin");

        Page<Stock> page = new PageImpl<>(List.of(stock));

        Mockito.when(stockRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        Page<StockDto> response = stockService.findAll(pageable, null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals(
                "Admin",
                response.getContent().get(0).getIdentifier()
        );

        Mockito.verify(stockRepository)
                .findByDeletedFalse(pageable);
    }

    @Test
    void findAllWithSearchTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Stock stock = new Stock();
        stock.setIdentifier("Admin");
        stock.setDeleted(false);

        StockDto dto = new StockDto();
        dto.setIdentifier("Admin");

        Page<Stock> page = new PageImpl<>(List.of(stock));

        Mockito.when(
                stockRepository.findAll(
                        Mockito.<Specification<Stock>>any(),
                        Mockito.eq(pageable)
                )
        ).thenReturn(page);

        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        Page<StockDto> response = stockService.findAll(pageable, "Admin");

        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals(
                "Admin",
                response.getContent().get(0).getIdentifier()
        );

        Mockito.verify(stockRepository)
                .findAll(
                        Mockito.<Specification<Stock>>any(),
                        Mockito.eq(pageable)
                );
    }
}