package com.ust.pos;

import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
        stock.setQuantity(10);
        stock.setStatus(true);

        stockDto = new StockDto();
        stockDto.setIdentifier("STK1");
        stockDto.setQuantity(10);
    }

    @Test
    void save_ShouldSaveStockSuccessfully() {

        Mockito.when(
                        modelMapper.map(stockDto, Stock.class))
                .thenReturn(stock);

        Mockito.when(
                        stockRepository.findByIdentifierAndDeletedFalse("STK1"))
                .thenReturn(null);

        StockDto response =
                stockService.save(stockDto);

        Assertions.assertEquals(
                "IN_STOCK",
                response.getStockStatus());

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void save_WithZeroQuantity_ShouldSetOutOfStock() {

        stockDto.setQuantity(0);
        stock.setQuantity(0);

        Mockito.when(
                        modelMapper.map(stockDto, Stock.class))
                .thenReturn(stock);

        Mockito.when(
                        stockRepository.findByIdentifierAndDeletedFalse("STK1"))
                .thenReturn(null);

        StockDto response =
                stockService.save(stockDto);

        Assertions.assertEquals(
                "OUT_OF_STOCK",
                response.getStockStatus());

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void save_WhenStockAlreadyExists_ShouldReturnFailure() {

        Mockito.when(
                        modelMapper.map(stockDto, Stock.class))
                .thenReturn(stock);

        Mockito.when(
                        stockRepository.findByIdentifierAndDeletedFalse("STK1"))
                .thenReturn(stock);

        StockDto response =
                stockService.save(stockDto);

        Assertions.assertFalse(response.isSuccess());

        Mockito.verify(stockRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void update_WhenStockNotFound_ShouldReturnFailure() {

        Mockito.when(
                        stockRepository.findByIdentifierAndDeletedFalse("STK1"))
                .thenReturn(null);

        StockDto response =
                stockService.update(stockDto);

        Assertions.assertFalse(response.isSuccess());

        Mockito.verify(stockRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void update_ShouldUpdateStockSuccessfully() {

        Mockito.when(
                        stockRepository.findByIdentifierAndDeletedFalse("STK1"))
                .thenReturn(stock);

        Mockito.doNothing().when(modelMapper)
                .map(stockDto, stock);

        StockDto response =
                stockService.update(stockDto);

        Assertions.assertEquals(
                "IN_STOCK",
                response.getStockStatus());

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void update_WithZeroQuantity_ShouldSetOutOfStock() {

        stockDto.setQuantity(0);

        Mockito.when(
                        stockRepository.findByIdentifierAndDeletedFalse("STK1"))
                .thenReturn(stock);

        Mockito.doNothing().when(modelMapper)
                .map(stockDto, stock);

        StockDto response =
                stockService.update(stockDto);

        Assertions.assertEquals(
                "OUT_OF_STOCK",
                response.getStockStatus());

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void findAll_ShouldReturnStockDtos() {

        List<Stock> stocks = List.of(stock);
        List<StockDto> stockDtos = List.of(stockDto);

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();

        Mockito.when(
                stockRepository.findByDeletedFalse()
        ).thenReturn(stocks);

        Mockito.when(
                modelMapper.map(
                        stocks,
                        listType
                )
        ).thenReturn(stockDtos);

        List<StockDto> response =
                stockService.findAll();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.size()
        );

        Mockito.verify(stockRepository)
                .findByDeletedFalse();

        Mockito.verify(modelMapper)
                .map(
                        stocks,
                        listType
                );
    }

    @Test
    void delete_ShouldSoftDeleteStock() {

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(stock);

        stockService.delete("STK1");

        Assertions.assertTrue(stock.isDeleted());

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void delete_WhenStockNotFound_ShouldDoNothing() {

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(null);

        stockService.delete("STK1");

        Mockito.verify(stockRepository, Mockito.never())
                .save(Mockito.any(Stock.class));
    }

    @Test
    void findByIdentifier_ShouldReturnStockDto() {

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(stock);

        Mockito.when(
                modelMapper.map(
                        stock,
                        StockDto.class
                )
        ).thenReturn(stockDto);

        StockDto response =
                stockService.findByIdentifier("STK1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "STK1",
                response.getIdentifier()
        );

        Mockito.verify(stockRepository)
                .findByIdentifierAndDeletedFalse("STK1");
    }

    @Test
    void updateStatusOnly_ShouldUpdateStatus() {

        Mockito.when(
                stockRepository.findByIdentifierAndDeletedFalse("STK1")
        ).thenReturn(stock);

        stockService.updateStatusOnly(
                "STK1",
                false
        );

        Assertions.assertFalse(stock.getStatus());

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void findAll_WithSearch_ShouldReturnStockDtos() {

        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        Page<Stock> stockPage =
                new PageImpl<>(List.of(stock));

        Mockito.when(
                stockRepository.findAll(
                        Mockito.<Specification<Stock>>any(),
                        Mockito.eq(pageable)
                )
        ).thenReturn(stockPage);

        Mockito.when(
                modelMapper.map(
                        stock,
                        StockDto.class
                )
        ).thenReturn(stockDto);

        // Act
        Page<StockDto> response =
                stockService.findAll(
                        "STK",
                        pageable
                );

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Assertions.assertEquals(
                "STK1",
                response.getContent().get(0).getIdentifier()
        );

        Mockito.verify(stockRepository)
                .findAll(
                        Mockito.<Specification<Stock>>any(),
                        Mockito.eq(pageable)
                );

        Mockito.verify(stockRepository, Mockito.never())
                .findByDeletedFalse(Mockito.any(Pageable.class));

        Mockito.verify(modelMapper)
                .map(
                        stock,
                        StockDto.class
                );
    }

    @Test
    void findAll_WithoutSearch_ShouldReturnStockDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Stock> stockPage =
                new PageImpl<>(List.of(stock));

        Mockito.when(
                stockRepository.findByDeletedFalse(pageable)
        ).thenReturn(stockPage);

        Mockito.when(
                modelMapper.map(
                        stock,
                        StockDto.class
                )
        ).thenReturn(stockDto);

        Page<StockDto> response =
                stockService.findAll(
                        null,
                        pageable
                );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Mockito.verify(stockRepository)
                .findByDeletedFalse(pageable);

        Mockito.verify(stockRepository, Mockito.never())
                .findAll(
                        Mockito.<Specification<Stock>>any(),
                        Mockito.any(Pageable.class)
                );

        Mockito.verify(modelMapper)
                .map(
                        stock,
                        StockDto.class
                );
    }
}