package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockServiceImpl service;

    @Mock
    private StockRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Stock> page = new PageImpl<>(
                List.of(new Stock()),
                pageable,
                1
        );

        when(repository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(new StockDto()));

        WsDto<StockDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findByIdentifierTest() {
        Stock stock = new Stock();
        StockDto dto = new StockDto();

        when(repository.findByIdentifier("S1")).thenReturn(stock);
        when(modelMapper.map(stock, StockDto.class)).thenReturn(dto);

        assertNotNull(service.findByIdentifier("S1"));
    }

    @Test
    void saveNewAvailableTest() {
        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");
        dto.setQuantity(10L);

        when(repository.findByIdentifier("P1W1")).thenReturn(null);
        when(modelMapper.map(any(), eq(Stock.class))).thenReturn(new Stock());

        StockDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(repository).save(any());
    }

    @Test
    void saveNewLimitedTest() {
        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");
        dto.setQuantity(5L);

        when(repository.findByIdentifier("P1W1")).thenReturn(null);
        when(modelMapper.map(any(), eq(Stock.class))).thenReturn(new Stock());

        StockDto result = service.save(dto);

        assertEquals("LIMITED_STOCK", dto.getStockStatus());
    }

    @Test
    void saveNewOutOfStockTest() {
        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");
        dto.setQuantity(0L);

        when(repository.findByIdentifier("P1W1")).thenReturn(null);
        when(modelMapper.map(any(), eq(Stock.class))).thenReturn(new Stock());

        StockDto result = service.save(dto);

        assertEquals("OUT_OF_STOCK", dto.getStockStatus());
    }

    @Test
    void saveExistingTest() {
        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");

        Stock existing = new Stock();
        existing.setDeleted(false);

        when(repository.findByIdentifier("P1W1")).thenReturn(existing);

        StockDto result = service.save(dto);

        assertFalse(result.isSuccess());
    }

    @Test
    void saveDeletedStockTest() {
        StockDto dto = new StockDto();
        dto.setProduct("P1");
        dto.setWarehouse("W1");

        Stock existing = new Stock();
        existing.setDeleted(true);

        when(repository.findByIdentifier("P1W1")).thenReturn(existing);

        StockDto result = service.save(dto);

        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void updateSuccessTest() {
        StockDto dto = new StockDto();
        dto.setIdentifier("P1W1");
        dto.setQuantity(10L);

        Stock stock = new Stock();

        when(repository.findByIdentifier("P1W1")).thenReturn(stock);

        service.update(dto);

        verify(repository).save(stock);
    }

    @Test
    void updateNotFoundTest() {
        StockDto dto = new StockDto();
        dto.setIdentifier("P1W1");

        when(repository.findByIdentifier("P1W1")).thenReturn(null);

        StockDto result = service.update(dto);

        assertFalse(result.isSuccess());
    }

    @Test
    void deleteTest() {
        Stock stock = new Stock();

        when(repository.findByIdentifier("P1W1")).thenReturn(stock);

        assertDoesNotThrow(() -> service.delete("P1W1"));

        verify(repository).findByIdentifier("P1W1");
    }

    @Test
    void toggleStatusTest() {
        Stock stock = new Stock();
        stock.setStatus(true);

        when(repository.findByIdentifier("P1W1")).thenReturn(stock);

        service.toggleStatus("P1W1");

        assertFalse(stock.isStatus());
        verify(repository).save(stock);
    }

    @Test
    void toggleStatusNullTest() {
        when(repository.findByIdentifier("P1W1")).thenReturn(null);

        service.toggleStatus("P1W1");

        verify(repository, never()).save(any());
    }
}