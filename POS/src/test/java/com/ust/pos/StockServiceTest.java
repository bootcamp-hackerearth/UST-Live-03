package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    public static final String INVALID = "STK-P1-W1";
    public static final String INVALID1 = "LOW_STOCK";
    public static final String INVALID2 = "IN_STOCK";
    public static final String STK_P_W = "STK-P-W";
    @InjectMocks
    private StockServiceImpl service;

    @Mock
    private StockRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findMethodsTest() {

        Stock stock = new Stock();
        stock.setId(1L);
        stock.setIdentifier(STK_P_W);
        stock.setQuantity(20);
        stock.setMinimumStock(10);

        when(repository.findByIdentifierAndDeletedFalse(STK_P_W))
                .thenReturn(stock);

        StockDto dto = service.findByIdentifier(STK_P_W);

        assertEquals(INVALID2, dto.getStatusLabel());

        when(repository.findByIdentifierAndDeletedFalse("INVALID"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.findByIdentifier("INVALID")
        );

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
    void saveTest() {

        StockDto invalid = new StockDto();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.save(invalid)
        );

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P");
        dto.setWarehouseIdentifier("W");
        dto.setQuantity(20);
        dto.setMinimumStock(10);

        when(repository.findByIdentifier(STK_P_W))
                .thenReturn(null);

        Stock saved = new Stock();
        saved.setIdentifier(STK_P_W);
        saved.setProductIdentifier("P");
        saved.setWarehouseIdentifier("W");
        saved.setQuantity(20);
        saved.setMinimumStock(10);

        when(repository.save(any()))
                .thenReturn(saved);

        StockDto result = service.save(dto);

        assertEquals(INVALID2, result.getStatusLabel());

        Stock duplicate = new Stock();
        duplicate.setDeleted(false);

        when(repository.findByIdentifier(STK_P_W))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Stock record already exists with identifier: STK-P-W",
                result.getMessage()
        );

        duplicate.setDeleted(true);

        when(repository.findByIdentifier(STK_P_W))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Stock record with Identifier STK-P-W already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void saveValidationWarehouseNullTest() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P");

        assertThrows(
                IllegalArgumentException.class,
                () -> service.save(dto)
        );
    }

    @Test
    void saveStatusFalseBranchTest() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(5);
        dto.setMinimumStock(10);

        when(repository.findByIdentifier(INVALID))
                .thenReturn(null);

        Stock saved = new Stock();
        saved.setIdentifier(INVALID);
        saved.setProductIdentifier("P1");
        saved.setWarehouseIdentifier("W1");
        saved.setQuantity(5);
        saved.setMinimumStock(10);

        when(repository.save(any(Stock.class)))
                .thenReturn(saved);

        StockDto result = service.save(dto);

        assertNotNull(result);
        assertEquals(INVALID1, result.getStatusLabel());

        verify(repository).save(any(Stock.class));
    }

    @Test
    void updateStatusFalseBranchTest() {

        Stock stock = new Stock();
        stock.setId(1L);

        when(repository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(stock));

        when(repository.save(any(Stock.class)))
                .thenAnswer(i -> i.getArgument(0));

        StockDto dto = new StockDto();
        dto.setId(1L);
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(5);
        dto.setMinimumStock(10);

        StockDto result = service.update(dto);

        assertEquals(INVALID1, result.getStatusLabel());
    }

    @Test
    void saveStatusTrueCoverageTest() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P1");
        dto.setWarehouseIdentifier("W1");
        dto.setQuantity(20);
        dto.setMinimumStock(10);

        when(repository.findByIdentifier(INVALID))
                .thenReturn(null);

        Stock saved = new Stock();
        saved.setQuantity(20);
        saved.setMinimumStock(10);

        when(repository.save(any(Stock.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.save(dto);

        verify(repository).save(argThat(stock ->
                stock.getStatus() != null &&
                        stock.getStatus()
        ));
    }

    @Test
    void saveStatusFalseCoverageTest() {

        StockDto dto = new StockDto();
        dto.setProductIdentifier("P");
        dto.setWarehouseIdentifier("W");
        dto.setQuantity(5);
        dto.setMinimumStock(10);

        when(repository.findByIdentifier(STK_P_W))
                .thenReturn(null);

        Stock saved = new Stock();
        saved.setIdentifier(STK_P_W);
        saved.setProductIdentifier("P");
        saved.setWarehouseIdentifier("W");
        saved.setQuantity(5);
        saved.setMinimumStock(10);

        when(repository.save(any()))
                .thenReturn(saved);

        StockDto result = service.save(dto);

        assertEquals(INVALID1, result.getStatusLabel());
    }

    @Test
    void mapToDtoOutOfStockCoverageTest() {

        Stock stock = new Stock();
        stock.setId(1L);
        stock.setIdentifier(STK_P_W);
        stock.setQuantity(0);
        stock.setMinimumStock(10);

        when(repository.findByIdentifierAndDeletedFalse(STK_P_W))
                .thenReturn(stock);

        StockDto dto = service.findByIdentifier(STK_P_W);

        assertEquals(
                "OUT_OF_STOCK",
                dto.getStatusLabel()
        );
    }

    @Test
    void updateStatusTrueAndFalseCoverageTest() {

        Stock stock = new Stock();
        stock.setId(1L);

        when(repository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(stock));

        when(repository.save(any(Stock.class)))
                .thenAnswer(i -> i.getArgument(0));

        StockDto dto = new StockDto();
        dto.setId(1L);
        dto.setProductIdentifier("P");
        dto.setWarehouseIdentifier("W");

        dto.setQuantity(20);
        dto.setMinimumStock(10);

        service.update(dto);

        assertTrue(stock.getStatus());

        dto.setQuantity(5);
        dto.setMinimumStock(10);

        service.update(dto);

        assertFalse(stock.getStatus());
    }

    @Test
    void updateAndDeleteTest() {

        Stock stock = new Stock();
        stock.setId(1L);
        stock.setCreatedBy("admin");
        stock.setCreatedOn(LocalDateTime.now());

        when(repository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(stock));

        when(repository.save(any(Stock.class)))
                .thenAnswer(i -> i.getArgument(0));

        StockDto dto = new StockDto();
        dto.setId(1L);
        dto.setProductIdentifier("P");
        dto.setWarehouseIdentifier("W");
        dto.setQuantity(5);
        dto.setMinimumStock(10);

        StockDto result = service.update(dto);

        assertEquals(INVALID1, result.getStatusLabel());

        when(repository.findByIdAndDeletedFalse(99L))
                .thenReturn(Optional.empty());

        StockDto missing = new StockDto();
        missing.setId(99L);

        assertThrows(
                RuntimeException.class,
                () -> service.update(missing)
        );

        when(repository.findByIdentifierAndDeletedFalse("STK"))
                .thenReturn(stock)
                .thenReturn(null);

        service.deleteByIdentifier("STK");

        service.deleteByIdentifier("STK");

        verify(repository, atLeast(2))
                .save(any(Stock.class));
    }

    @Test
    void findAllStatusCoverageTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Stock inStock = new Stock();
        inStock.setQuantity(20);
        inStock.setMinimumStock(10);

        Stock lowStock = new Stock();
        lowStock.setQuantity(5);
        lowStock.setMinimumStock(10);

        Stock outStock = new Stock();
        outStock.setQuantity(0);
        outStock.setMinimumStock(10);

        Page<Stock> page =
                new PageImpl<>(
                        List.of(inStock, lowStock, outStock),
                        pageable,
                        3
                );

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(
                        List.of(
                                new StockDto(),
                                new StockDto(),
                                new StockDto()
                        )
                );

        WsDto<StockDto> result =
                service.findAll(pageable);

        assertEquals(
                INVALID2,
                result.getDtoList().get(0).getStatusLabel()
        );

        assertEquals(
                INVALID1,
                result.getDtoList().get(1).getStatusLabel()
        );

        assertEquals(
                "OUT_OF_STOCK",
                result.getDtoList().get(2).getStatusLabel()
        );
    }

    @Test
    void findAllSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Stock> page =
                new PageImpl<>(
                        List.of(new Stock()),
                        pageable,
                        1
                );

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new StockDto()));

        Specification<Stock> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<StockDto> result =
                service.findAll(specification, pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());

        verify(repository)
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(
                        new PageImpl<>(
                                Collections.emptyList(),
                                pageable,
                                0
                        )
                );

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        WsDto<StockDto> result =
                service.findAll(pageable);

        assertTrue(result.getDtoList().isEmpty());
    }
}