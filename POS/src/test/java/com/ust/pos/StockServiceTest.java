package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Stock;
import com.ust.pos.modell.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockServiceImpl service;

    @Mock
    private StockRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifierTest() {
        Stock stock = new Stock();
        stock.setIdentifier("STK-P-W");

        when(repository.findByIdentifier("STK-P-W")).thenReturn(stock);

        StockDto result = service.findByIdentifier("STK-P-W");
        assertEquals("STK-P-W", result.getIdentifier());

        when(repository.findByIdentifier("X")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.findByIdentifier("X"));

        assertTrue(ex.getMessage().contains("Stock not found"));
    }

    @Test
    void findByIdTest() {
        Stock stock = new Stock();
        stock.setId(1L);
        stock.setIdentifier("STK-P-W");

        when(repository.findById(1L)).thenReturn(java.util.Optional.of(stock));

        StockDto result = service.findById(1L);
        assertEquals(1L, result.getId());

        when(repository.findById(99L)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findById(99L));
    }

    @Test
    void saveTest() {
        StockDto dto = new StockDto();
        dto.setProductIdentifier("P");
        dto.setWarehouseIdentifier("W");
        dto.setQuantity(20);
        dto.setMinimumStock(10);

        Stock saved = new Stock();
        saved.setIdentifier("STK-P-W");
        saved.setQuantity(20);
        saved.setMinimumStock(10);
        saved.setStatus(true);

        when(repository.save(any())).thenReturn(saved);

        StockDto result = service.save(dto);

        assertEquals("STK-P-W", result.getIdentifier());
        assertTrue(result.getStatus());

        StockDto invalid = new StockDto();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.save(invalid));

        assertTrue(ex.getMessage().contains("Product & Warehouse required"));
    }

    @Test
    void updateTest() {
        Stock existing = new Stock();
        existing.setId(1L);
        existing.setIdentifier("OLD");

        when(repository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        StockDto dto = new StockDto();
        dto.setId(1L);
        dto.setProductIdentifier("P");
        dto.setWarehouseIdentifier("W");
        dto.setQuantity(5);
        dto.setMinimumStock(10);

        StockDto result = service.update(dto);

        assertEquals("STK-P-W", result.getIdentifier());
        assertFalse(result.getStatus());

        when(repository.findById(99L)).thenReturn(java.util.Optional.empty());

        StockDto fail = new StockDto();
        fail.setId(99L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.update(fail));

        assertEquals("Stock not found", ex.getMessage());
    }

    @Test
    void deleteTest() {
        service.delete(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<StockDto>>(){}.getType();

        Stock stock = new Stock();
        stock.setIdentifier("STK-P-W");

        StockDto dto = new StockDto();
        dto.setIdentifier("STK-P-W");

        Page<Stock> page = new PageImpl<>(List.of(stock), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(page.getContent(), type)).thenReturn(List.of(dto));

        WsDto<StockDto> result = service.findAll(pageable);
        assertEquals(1, result.getDtoList().size());

        Page<Stock> empty = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(empty);
        when(mapper.map(empty.getContent(), type)).thenReturn(List.of());

        WsDto<StockDto> emptyResult = service.findAll(pageable);
        assertTrue(emptyResult.getDtoList().isEmpty());
    }
}