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
import org.springframework.data.domain.Pageable;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

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
        Pageable pageable = Mockito.mock(Pageable.class);

        Page<Stock> page = new PageImpl<>(List.of(stock));

        when(stockRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(
                        Mockito.eq(List.of(stock)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<StockDto> result =
                stockService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());    }

    @Test
    void save_success() {

        StockDto input = new StockDto();
        input.setIdentifier("STOCK01");

        when(stockRepository.findByIdentifier("STOCK01"))
                .thenReturn(null);

        Stock entity = new Stock();

        when(modelMapper.map(input, Stock.class))
                .thenReturn(entity);

        when(stockRepository.save(entity))
                .thenReturn(entity);

        StockDto result = stockService.save(input);

        assertEquals("STOCK01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        StockDto input = new StockDto();
        input.setIdentifier("STOCK01");

        when(stockRepository.findByIdentifier("STOCK01"))
                .thenReturn(new Stock());

        StockDto result = stockService.save(input);

        Assertions.assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Stock stock = new Stock();
        stock.setIdentifier("STOCK01");

        StockDto dto = new StockDto();
        dto.setIdentifier("STOCK01");

        when(stockRepository.findByIdentifier("STOCK01"))
                .thenReturn(stock);

        when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto result =
                stockService.findByIdentifier("STOCK01");

        assertEquals("STOCK01", result.getIdentifier());
    }

    @Test
    void update_success() {

        StockDto input = new StockDto();
        input.setIdentifier("STOCK01");

        Stock existing = new Stock();

        when(stockRepository.findByIdentifier("STOCK01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        when(stockRepository.save(existing))
                .thenReturn(existing);

        StockDto result = stockService.update(input);

        assertEquals("STOCK01", result.getIdentifier());
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

        when(stockRepository.findByIdentifier("STOCK01"))
                .thenReturn(stock);

        when(stockRepository.save(stock))
                .thenReturn(stock);

        when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto result =
                stockService.changeToggleStatus("STOCK01", true);

        Assertions.assertTrue(stock.isStatus());
        assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Stock stock = new Stock();
        stock.setStatus(true);

        StockDto dto = new StockDto();

        when(stockRepository.findByIdentifier("STOCK01"))
                .thenReturn(stock);

        when(stockRepository.save(stock))
                .thenReturn(stock);

        when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto result =
                stockService.changeToggleStatus("STOCK01", false);

        Assertions.assertFalse(stock.isStatus());
        assertNotNull(result);
    }

    @Test
    void testFindActiveStatus() {
        // 1. Arrange: Create Stock data
        Stock active = new Stock();
        active.setStatus(true);

        Stock inactive = new Stock();
        inactive.setStatus(false);

        // Stub the repository to return both active and inactive stocks
        when(stockRepository.findAll())
                .thenReturn(List.of(active, inactive));

        // Prepare the expected DTO output list
        StockDto dto = new StockDto();
        List<StockDto> expectedDtoList = List.of(dto);

        // FIX: Stub modelMapper to expect the precisely filtered list and ANY generic Type
        when(modelMapper.map(
                Mockito.eq(List.of(active)),
                Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(expectedDtoList);

        // 2. Act: Call your service layer method
        List<StockDto> result = stockService.findActiveStatus();

        // 3. Assert: Verify the behavior
        assertNotNull(result, "The result list should not be null");
        assertEquals(1, result.size(), "The result list should contain exactly 1 active stock");
    }
}

