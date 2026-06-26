package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Stock;
import com.ust.pos.modell.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    public static final String IN_STOCK = "IN_STOCK";
    public static final String STK_P_W = "STK-P-W";
    @InjectMocks
    private StockServiceImpl service;

    @Mock
    private StockRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifierSuccessTest() {

        Stock stock = new Stock();
        stock.setIdentifier(STK_P_W);
        stock.setQuantity(20);
        stock.setMinimumStock(10);

        when(repository.findByIdentifierAndDeletedFalse(STK_P_W))
                .thenReturn(stock);

        StockDto result = service.findByIdentifier(STK_P_W);

        assertEquals(IN_STOCK, result.getStatusLabel());
    }

    @Test
    void findByIdentifierNotFoundTest() {

        when(repository.findByIdentifierAndDeletedFalse("X"))
                .thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.findByIdentifier("X")
        );
    }

    @Test
    void findByIdTest() {

        Stock stock = new Stock();
        stock.setId(1L);
        stock.setIdentifier(STK_P_W);
        stock.setQuantity(20);
        stock.setMinimumStock(10);

        when(repository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(stock));

        assertEquals(
                1L,
                service.findById(1L).getId()
        );

        when(repository.findByIdAndDeletedFalse(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> service.findById(99L)
        );
    }

    @Test
    void saveSuccessTest() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P");
        dto.setWarehouseIdentifier("W");
        dto.setQuantity(20);
        dto.setMinimumStock(10);

        Stock saved = new Stock();
        saved.setIdentifier(STK_P_W);
        saved.setQuantity(20);
        saved.setMinimumStock(10);
        saved.setProductIdentifier("P");
        saved.setWarehouseIdentifier("W");

        when(repository.findByIdentifier(STK_P_W))
                .thenReturn(null);

        when(repository.save(any()))
                .thenReturn(saved);

        StockDto result = service.save(dto);

        assertEquals(IN_STOCK, result.getStatusLabel());
    }

    @Test
    void saveDuplicateTest() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P");
        dto.setWarehouseIdentifier("W");

        Stock stock = new Stock();
        stock.setDeleted(false);

        when(repository.findByIdentifier(STK_P_W))
                .thenReturn(stock);

        StockDto result = service.save(dto);

        assertFalse(result.isSuccess());
    }

    @Test
    void saveSoftDeletedTest() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P");
        dto.setWarehouseIdentifier("W");

        Stock stock = new Stock();
        stock.setDeleted(true);

        when(repository.findByIdentifier(STK_P_W))
                .thenReturn(stock);

        StockDto result = service.save(dto);

        assertFalse(result.isSuccess());
    }

    @Test
    void saveValidationTest() {
        StockDto stockDto = new StockDto();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.save(stockDto)
        );
    }

    @Test
    void updateTest() {

        Stock stock = new Stock();
        stock.setId(1L);
        stock.setCreatedBy("admin");
        stock.setCreatedOn(LocalDateTime.now());

        when(repository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(stock));

        when(repository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        StockDto dto = new StockDto();
        dto.setId(1L);
        dto.setProductIdentifier("P");
        dto.setWarehouseIdentifier("W");
        dto.setQuantity(5);
        dto.setMinimumStock(10);

        StockDto result = service.update(dto);

        assertEquals("LOW_STOCK", result.getStatusLabel());

        when(repository.findByIdAndDeletedFalse(99L))
                .thenReturn(Optional.empty());

        StockDto notFound = new StockDto();
        notFound.setId(99L);

        assertThrows(
                RuntimeException.class,
                () -> service.update(notFound)
        );
    }

    @Test
    void deleteByIdentifierTest() {

        Stock stock = new Stock();

        when(repository.findByIdentifierAndDeletedFalse("STK"))
                .thenReturn(stock)
                .thenReturn(null);

        service.deleteByIdentifier("STK");

        verify(repository).save(stock);

        service.deleteByIdentifier("STK");
    }

    @Test
    void findAllInStockTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Stock stock = new Stock();
        stock.setQuantity(20);
        stock.setMinimumStock(10);

        StockDto dto = new StockDto();

        Page<Stock> page =
                new PageImpl<>(List.of(stock), pageable, 1);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<StockDto> result = service.findAll(pageable);

        assertEquals(
                IN_STOCK,
                result.getDtoList().get(0).getStatusLabel()
        );
    }

    @Test
    void findAllLowAndOutStockTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Stock low = new Stock();
        low.setQuantity(5);
        low.setMinimumStock(10);

        Stock out = new Stock();
        out.setQuantity(0);
        out.setMinimumStock(10);

        StockDto dto1 = new StockDto();
        StockDto dto2 = new StockDto();

        Page<Stock> page =
                new PageImpl<>(List.of(low, out), pageable, 2);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(dto1, dto2));

        WsDto<StockDto> result = service.findAll(pageable);

        assertEquals(
                "LOW_STOCK",
                result.getDtoList().get(0).getStatusLabel()
        );

        assertEquals(
                "OUT_OF_STOCK",
                result.getDtoList().get(1).getStatusLabel()
        );
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Stock> page =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        WsDto<StockDto> result = service.findAll(pageable);

        assertTrue(result.getDtoList().isEmpty());
    }
}