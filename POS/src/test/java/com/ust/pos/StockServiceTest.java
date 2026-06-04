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
import org.springframework.data.domain.Pageable;
import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void findAll_success() {

        Stock stock = new Stock();
        StockDto dto = new StockDto();

        Page<Stock> page = new PageImpl<>(List.of(stock));

        Mockito.when(stockRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(stock)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<StockDto> result =
                stockService.findAll(Mockito.mock(Pageable.class));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void save_success() {

        StockDto input = new StockDto();
        input.setIdentifier("STOCK01");

        Mockito.when(stockRepository.findByIdentifier("STOCK01"))
                .thenReturn(null);

        Stock entity = new Stock();

        Mockito.when(modelMapper.map(input, Stock.class))
                .thenReturn(entity);

        Mockito.when(stockRepository.save(entity))
                .thenReturn(entity);

        StockDto result = stockService.save(input);

        Assertions.assertEquals("STOCK01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        StockDto input = new StockDto();
        input.setIdentifier("STOCK01");

        Mockito.when(stockRepository.findByIdentifier("STOCK01"))
                .thenReturn(new Stock());

        StockDto result = stockService.save(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Stock stock = new Stock();
        stock.setIdentifier("STOCK01");

        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK01");

        Mockito.when(stockRepository.findByIdentifier("STOCK01"))
                .thenReturn(stock);

        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto result =
                stockService.findByIdentifier("STOCK01");

        Assertions.assertEquals("STOCK01", result.getIdentifier());
    }

    @Test
    void update_success() {

        StockDto input = new StockDto();
        input.setIdentifier("STOCK01");

        Stock existing = new Stock();

        Mockito.when(stockRepository.findByIdentifier("STOCK01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        Mockito.when(stockRepository.save(existing))
                .thenReturn(existing);

        StockDto result = stockService.update(input);

        Assertions.assertEquals("STOCK01", result.getIdentifier());
    }

    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(stockRepository).deleteByIdentifier("STOCK01");

        stockService.delete("STOCK01");

        Mockito.verify(stockRepository)
                .deleteByIdentifier("STOCK01");
    }

    @Test
    void changeToggleStatus_enable() {

        Stock stock = new Stock();
        stock.setStatus(false);

        StockDto dto = new StockDto();

        Mockito.when(stockRepository.findByIdentifier("STOCK01"))
                .thenReturn(stock);

        Mockito.when(stockRepository.save(stock))
                .thenReturn(stock);

        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto result =
                stockService.changeToggleStatus("STOCK01", true);

        Assertions.assertTrue(stock.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Stock stock = new Stock();
        stock.setStatus(true);

        StockDto dto = new StockDto();

        Mockito.when(stockRepository.findByIdentifier("STOCK01"))
                .thenReturn(stock);

        Mockito.when(stockRepository.save(stock))
                .thenReturn(stock);

        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto result =
                stockService.changeToggleStatus("STOCK01", false);

        Assertions.assertFalse(stock.isStatus());
        Assertions.assertNotNull(result);
    }
}
