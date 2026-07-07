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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;

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
    void findAllStocksTest() {

        List<Stock> stocks = List.of(new Stock(), new Stock());

        List<StockDto> dtoList =
                List.of(new StockDto(), new StockDto());

        Mockito.when(
                stockRepository.findByDeletedFalse()
        ).thenReturn(stocks);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(stocks),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<StockDto> response = stockService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Stock> stocks = List.of(new Stock());

        Page<Stock> stockPage =
                new PageImpl<>(stocks, pageable, 1);

        List<StockDto> dtoList =
                List.of(new StockDto());

        Type listType =
                new TypeToken<List<StockDto>>(){}.getType();

        Mockito.when(
                stockRepository.findByDeletedFalse(pageable)
        ).thenReturn(stockPage);

        Mockito.when(
                modelMapper.map(stocks, listType)
        ).thenReturn(dtoList);

        WsDto<StockDto> response =
                stockService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );
        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void saveStockSuccess() {

        StockDto dto = new StockDto();
        dto.setIdentifier("STK1");

        Stock stock = new Stock();

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(dto, Stock.class)
        ).thenReturn(stock);

        StockDto response = stockService.save(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void saveStockAlreadyExists() {

        StockDto dto = new StockDto();
        dto.setIdentifier("STK1");

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(new Stock());

        StockDto response = stockService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Stock with identifier - STK1 already exists",
                response.getMessage()
        );

        Mockito.verify(
                stockRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void updateStockSuccess() {

        StockDto dto = new StockDto();
        dto.setIdentifier("STK1");

        Stock existingStock = new Stock();

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(existingStock);

        StockDto response = stockService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper)
                .map(dto, existingStock);

        Mockito.verify(stockRepository)
                .save(existingStock);
    }

    @Test
    void updateStockNotFound() {

        StockDto dto = new StockDto();
        dto.setIdentifier("STK1");

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(null);

        StockDto response = stockService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Stock with identifier - STK1 not found",
                response.getMessage()
        );

        Mockito.verify(
                stockRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void findStockByIdentifierTest() {

        Stock stock = new Stock();
        stock.setIdentifier("STK1");

        StockDto dto = new StockDto();
        dto.setIdentifier("STK1");

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(stock);

        Mockito.when(
                modelMapper.map(stock, StockDto.class)
        ).thenReturn(dto);

        StockDto response =
                stockService.findByIdentifier("STK1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "STK1",
                response.getIdentifier()
        );
    }

    @Test
    void deleteStockSuccess() {

        Stock stock = new Stock();
        stock.setDeleted(false);

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(stock);

        stockService.delete("STK1");

        Assertions.assertTrue(stock.getDeleted());

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void deleteStockNotFound() {

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(null);

        stockService.delete("STK1");

        Mockito.verify(
                stockRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void toggleStockStatusSuccess() {

        Stock stock = new Stock();
        stock.setStatus(true);

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(stock);

        stockService.toggleStatus("STK1");

        Mockito.verify(stockRepository).save(stock);

        Assertions.assertFalse(stock.getStatus());
    }

    @Test
    void toggleStockStatusNotFound() {

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(null);

        stockService.toggleStatus("STK1");

        Mockito.verify(
                stockRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void updateStatusOnlySuccess() {

        Stock stock = new Stock();

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(stock);

        stockService.updateStatusOnly(
                "STK1",
                true
        );

        Assertions.assertTrue(stock.getStatus());

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void updateStatusOnlyNotFound() {

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(null);

        stockService.updateStatusOnly(
                "STK1",
                true
        );

        Mockito.verify(
                stockRepository,
                Mockito.never()
        ).save(Mockito.any());
    }
    @Test
    void findAllWithSearchTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Stock stock = new Stock();
        stock.setIdentifier("STK1");

        Example<Stock> example = Example.of(
                stock,
                ExampleMatcher.matching()
                        .withMatcher(
                                "identifier",
                                ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase()
                        )
        );

        Page<Stock> page =
                new PageImpl<>(List.of(stock));

        Mockito.when(
                stockRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(stock, StockDto.class)
        ).thenReturn(new StockDto());

        Page<StockDto> response =
                stockService.findAll(example, pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void findAllWithoutSearchTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Stock stock = new Stock();

        Example<Stock> example = Example.of(new Stock());

        Page<Stock> page =
                new PageImpl<>(List.of(stock));

        Mockito.when(
                stockRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(stock, StockDto.class)
        ).thenReturn(new StockDto());

        Page<StockDto> response =
                stockService.findAll(example, pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }
}