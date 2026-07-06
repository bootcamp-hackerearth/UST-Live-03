package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private StockServiceImpl stockService;

    private Stock stock;
    private StockDto stockDto;

    @BeforeEach
    void setUp() {

        stock = new Stock();
        stock.setIdentifier("STK1");
        stock.setStatus(true);
        stock.setDeleted(false);

        stockDto = new StockDto();
        stockDto.setIdentifier("STK1");
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Stock> page =
                new PageImpl<>(Collections.singletonList(stock));

        when(stockRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(stockDto));

        WsDto<StockDto> result = stockService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Stock> specification =
                mock(Specification.class);

        Page<Stock> page =
                new PageImpl<>(Collections.singletonList(stock));

        when(stockRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(stockDto));

        WsDto<StockDto> result =
                stockService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());

        verify(stockRepository)
                .findAll(specification, pageable);
    }

    @Test
    void testSave_NewStock() {

        when(stockRepository.findByIdentifier("STK1"))
                .thenReturn(null);

        when(modelMapper.map(stockDto, Stock.class))
                .thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertNotNull(result);
        assertEquals("STK1", result.getIdentifier());

        verify(stockRepository).save(stock);
    }

    @Test
    void testSave_AlreadyExists() {

        when(stockRepository.findByIdentifier("STK1"))
                .thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_SoftDeleted() {

        stock.setDeleted(true);

        when(stockRepository.findByIdentifier("STK1"))
                .thenReturn(stock);

        StockDto result = stockService.save(stockDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    @Test
    void testDelete() {

        when(stockRepository.findByIdentifier("STK1"))
                .thenReturn(stock);

        stockService.delete("STK1");

        assertTrue(stock.isDeleted());
        assertFalse(stock.isStatus());

        verify(stockRepository).save(stock);
    }

    @Test
    void testFindByIdentifier() {

        when(stockRepository.findByIdentifier("STK1"))
                .thenReturn(stock);

        when(modelMapper.map(stock, StockDto.class))
                .thenReturn(stockDto);

        StockDto result =
                stockService.findByIdentifier("STK1");

        assertNotNull(result);
        assertEquals("STK1", result.getIdentifier());
    }

    @Test
    void testUpdate() {

        when(stockRepository.findByIdentifier("STK1"))
                .thenReturn(stock);

        doNothing().when(modelMapper)
                .map(stockDto, stock);

        StockDto result = stockService.update(stockDto);

        assertNotNull(result);

        verify(stockRepository).save(stock);
    }

    @Test
    void testChangeToggleStatus() {

        when(stockRepository.findByIdentifier("STK1"))
                .thenReturn(stock);

        when(modelMapper.map(stock, StockDto.class))
                .thenReturn(stockDto);

        StockDto result =
                stockService.changeToggleStatus("STK1", false);

        assertNotNull(result);
        assertFalse(stock.isStatus());

        verify(stockRepository).save(stock);
    }

    @Test
    void testChangeToggleStatus_StockNotFound() {

        when(stockRepository.findByIdentifier("STK1"))
                .thenReturn(null);

        when(modelMapper.map(null, StockDto.class))
                .thenReturn(null);

        StockDto result =
                stockService.changeToggleStatus("STK1", false);

        assertNull(result);
    }

    @Test
    void testFindActiveStatus() {

        Stock inactiveStock = new Stock();
        inactiveStock.setStatus(false);

        when(stockRepository.findAll())
                .thenReturn(List.of(stock, inactiveStock));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(stockDto));

        List<StockDto> result =
                stockService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}