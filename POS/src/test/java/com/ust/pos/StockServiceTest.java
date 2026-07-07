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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    // SAVE

    @Test
    void save_Success_AvailableStock() {

        StockDto dto = new StockDto();
        dto.setIdentifier("S1");
        dto.setQuantity(10);

        Stock entity = new Stock();
        entity.setIdentifier("S1");

        when(stockRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(null);

        when(modelMapper.map(any(StockDto.class), eq(Stock.class)))
                .thenReturn(entity);

        when(stockRepository.save(any(Stock.class)))
                .thenReturn(entity);

        StockDto result = stockService.save(dto);

        Assertions.assertEquals("Available", result.getStockStatus());
        Assertions.assertTrue(result.isSuccess());

        verify(stockRepository).save(entity);
    }

    @Test
    void save_Success_NotAvailableStock() {

        StockDto dto = new StockDto();
        dto.setIdentifier("S1");
        dto.setQuantity(0);

        Stock entity = new Stock();
        entity.setIdentifier("S1");

        when(stockRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(null);

        when(modelMapper.map(any(StockDto.class), eq(Stock.class)))
                .thenReturn(entity);

        when(stockRepository.save(any(Stock.class)))
                .thenReturn(entity);

        StockDto result = stockService.save(dto);

        Assertions.assertEquals("Not Available", result.getStockStatus());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_WhenExists_ShouldFail() {

        StockDto dto = new StockDto();
        dto.setIdentifier("S1");
        dto.setQuantity(5);

        Stock mapped = new Stock();
        mapped.setIdentifier("S1");

        when(modelMapper.map(any(StockDto.class), eq(Stock.class)))
                .thenReturn(mapped);

        when(stockRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(new Stock());

        StockDto result = stockService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(stockRepository, never()).save(any());
    }
    // UPDATE

    @Test
    void update_Success() {

        StockDto dto = new StockDto();
        dto.setIdentifier("S1");
        dto.setQuantity(20);

        Stock existing = new Stock();
        existing.setIdentifier("S1");

        when(stockRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(existing);

        doAnswer(invocation -> {
            StockDto src = invocation.getArgument(0);
            Stock target = invocation.getArgument(1);
            target.setQuantity(src.getQuantity());
            target.setIdentifier(src.getIdentifier());
            return null;
        }).when(modelMapper).map(any(StockDto.class), any(Stock.class));

        when(stockRepository.save(any(Stock.class)))
                .thenReturn(existing);

        StockDto result = stockService.update(dto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Available", result.getStockStatus());

        verify(stockRepository).save(existing);
    }

    @Test
    void update_WhenNotFound_ShouldFail() {

        StockDto dto = new StockDto();
        dto.setIdentifier("S1");
        dto.setQuantity(10);

        when(stockRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(null);

        StockDto result = stockService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(stockRepository, never()).save(any());
    }

    // DELETE

    @Test
    void delete_Success() {

        Stock entity = new Stock();
        entity.setIdentifier("S1");

        when(stockRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(entity);

        when(stockRepository.save(any(Stock.class)))
                .thenReturn(entity);

        stockService.delete("S1");

        Assertions.assertTrue(entity.isDelete());

        verify(stockRepository).save(entity);
    }

    @Test
    void delete_WhenNotFound_ShouldDoNothing() {

        when(stockRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(null);

        stockService.delete("S1");

        verify(stockRepository, never()).save(any());
    }

    // STATUS

    @Test
    void updateStatusOnly_Success() {

        Stock entity = new Stock();
        entity.setIdentifier("S1");
        entity.setStatus(false);

        when(stockRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(entity);

        when(stockRepository.save(any(Stock.class)))
                .thenReturn(entity);

        stockService.updateStatusOnly("S1", true);

        Assertions.assertTrue(entity.getStatus());

        verify(stockRepository).save(entity);
    }

    // FIND BY ID

    @Test
    void findByIdentifier_Success() {

        Stock entity = new Stock();
        entity.setIdentifier("S1");

        StockDto dto = new StockDto();
        dto.setIdentifier("S1");

        when(stockRepository.findByIdentifierAndIsDeleteFalse("S1"))
                .thenReturn(entity);

        when(modelMapper.map(entity, StockDto.class))
                .thenReturn(dto);

        StockDto result = stockService.findByIdentifier("S1");

        Assertions.assertEquals("S1", result.getIdentifier());
    }

    // FIND ALL

    @Test
    void findAll_List_Success() {

        List<Stock> entities = List.of(new Stock());
        List<StockDto> dtos = List.of(new StockDto());

        Type type = new TypeToken<List<StockDto>>() {
        }.getType();

        when(stockRepository.findByIsDeleteFalse())
                .thenReturn(entities);

        when(modelMapper.map(entities, type))
                .thenReturn(dtos);

        List<StockDto> result = stockService.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // PAGINATION

    @Test
    void findAll_Pageable_NoSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Stock entity = new Stock();
        entity.setIdentifier("S1");

        StockDto dto = new StockDto();
        dto.setIdentifier("S1");

        Page<Stock> page = new PageImpl<>(List.of(entity));

        when(stockRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(Stock.class), eq(StockDto.class)))
                .thenReturn(dto);

        Page<StockDto> result = stockService.findAll(pageable, null);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void findAllPageableWithSearchTest() {
        Pageable pageable =
                PageRequest.of(0, 10);
        Stock stock = new Stock();
        Page<Stock> page =
                new PageImpl<>(List.of(stock));
        Mockito.when(stockRepository.findAll(
                        Mockito.<Specification<Stock>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(page);
        Page<StockDto> result =
                stockService.findAll(pageable, "Admin");
        Assertions.assertEquals(
                1,
                result.getContent().size()
        );
        Mockito.verify(stockRepository)
                .findAll(
                        Mockito.<Specification<Stock>>any(),
                        Mockito.eq(pageable)
                );
    }

    @Test
    void findAll_WithBlankSearch_ShouldFallback() {

        Pageable pageable = PageRequest.of(0, 10);

        Stock entity = new Stock();
        entity.setIdentifier("S1");

        StockDto dto = new StockDto();
        dto.setIdentifier("S1");

        Page<Stock> page = new PageImpl<>(List.of(entity));

        when(stockRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(Stock.class), eq(StockDto.class)))
                .thenReturn(dto);

        Page<StockDto> result = stockService.findAll(pageable, " ");

        Assertions.assertEquals(1, result.getContent().size());
    }
}