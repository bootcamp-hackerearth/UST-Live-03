package com.ust.pos;

import com.ust.pos.dto.PaginationResponseDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.Optional;

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
    void findAllWithPageableTest() {

        Stock stock = new Stock();
        stock.setIdentifier("STK1");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("STK1");

        List<Stock> stocks = List.of(stock);
        List<StockDto> stockDtos = List.of(stockDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Stock> stockPage = new PageImpl<>(stocks, pageable, stocks.size());

        Mockito.when(stockRepository.findByIsDeletedFalse(pageable))
                .thenReturn(stockPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(stocks),
                Mockito.any(Type.class)
        )).thenReturn(stockDtos);

        PaginationResponseDto<StockDto> result =
                stockService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(
                "STK1",
                result.getDtoList().get(0).getIdentifier()
        );
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(
                5,
                result.getSizePerPage()
        );

        Assertions.assertEquals(
                1,
                result.getTotalPages()
        );

        Assertions.assertEquals(
                1,
                result.getTotalRecords()
        );
    }

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 5);

        Specification<Stock> specification =
                Mockito.mock(Specification.class);


        Stock stock = new Stock();
        stock.setIdentifier("STK1");


        StockDto dto = new StockDto();
        dto.setIdentifier("STK1");

        Page<Stock> page =
                new PageImpl<>(
                        List.of(stock),
                        pageable,
                        1
                );

        Mockito.when(
                stockRepository.findAll(
                        Mockito.eq(specification),
                        Mockito.eq(pageable)
                )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(page.getContent()),
                        Mockito.any(Type.class)
                )
        ).thenReturn(List.of(dto));


        PaginationResponseDto<StockDto> response =
                stockService.findAll(
                        specification,
                        pageable
                );

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "STK1",
                response.getDtoList()
                        .get(0)
                        .getIdentifier()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );

        Assertions.assertEquals(
                1,
                response.getTotalPages()
        );

        Assertions.assertEquals(
                5,
                response.getSizePerPage()
        );

        Assertions.assertEquals(
                0,
                response.getPage()
        );
    }

    @Test
    void save_success() {

        StockDto request = new StockDto();
        request.setProduct("PROD1");
        request.setWarehouse("WH1");

        String identifier = "PROD1WH1";

        Stock stock = new Stock();
        Stock savedStock = new Stock();

        Mockito.when(stockRepository.findByIdentifier(identifier))
                .thenReturn(null);

        Mockito.when(modelMapper.map(request, Stock.class))
                .thenReturn(stock);

        Mockito.when(stockRepository.save(stock))
                .thenReturn(savedStock);

        StockDto response = stockService.save(request);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Successfully added the stock",
                response.getMessage()
        );

        Mockito.verify(stockRepository).save(stock);
        Mockito.verify(modelMapper).map(request, Stock.class);
    }

    @Test
    void save_existingStockTest() {

        StockDto dto = new StockDto();
        dto.setProduct("PROD1");
        dto.setWarehouse("WH1");

        Stock stock = new Stock();
        stock.setIdentifier("PROD1WH1");

        Mockito.when(
                stockRepository.findByIdentifier("PROD1WH1")
        ).thenReturn(stock);

        StockDto response =
                stockService.save(dto);

        Assertions.assertFalse(
                response.isSuccess()
        );

        Assertions.assertEquals(
                "Stock already exists for this Product and Warehouse",
                response.getMessage()
        );

        Mockito.verify(
                stockRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void saveDeletedStockTest() {

        StockDto dto = new StockDto();
        dto.setProduct("PROD1");
        dto.setWarehouse("WH1");

        Stock stock = new Stock();
        stock.setIdentifier("PROD1WH1");
        stock.setDeleted(true);

        Mockito.when(
                stockRepository.findByIdentifier("PROD1WH1")
        ).thenReturn(stock);


        StockDto response =
                stockService.save(dto);

        Assertions.assertFalse(
                response.isSuccess()
        );

        Assertions.assertEquals(
                "Stock PROD1WH1 has been deleted. Please contact the administrator.",
                response.getMessage()
        );

        Mockito.verify(
                stockRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void update_success() {

        StockDto dto = new StockDto();
        dto.setId(1L);

        Stock existingStock = new Stock();
        existingStock.setId(1L);
        existingStock.setDeleted(false);

        Stock updatedStock = new Stock();
        updatedStock.setId(1L);

        Mockito.when(stockRepository.findById(1L))
                .thenReturn(java.util.Optional.of(existingStock));

        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, existingStock);

        Mockito.when(stockRepository.save(existingStock))
                .thenReturn(updatedStock);

        Mockito.when(modelMapper.map(updatedStock, StockDto.class))
                .thenReturn(new StockDto());

        StockDto response = stockService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Stock updated successfully",
                response.getMessage()
        );

        Mockito.verify(stockRepository).save(existingStock);
    }

    @Test
    void updateStockNotFoundTest() {

        StockDto dto = new StockDto();
        dto.setId(10L);

        Mockito.when(
                stockRepository.findById(10L)
        ).thenReturn(Optional.empty());

        StockDto response =
                stockService.update(dto);

        Assertions.assertFalse(
                response.isSuccess()
        );

        Assertions.assertEquals(
                "Stock not found with id: 10",
                response.getMessage()
        );
    }

    @Test
    void updateDeletedStockTest() {

        StockDto dto = new StockDto();
        dto.setId(1L);

        Stock stock = new Stock();
        stock.setIdentifier("STK1");
        stock.setDeleted(true);

        Mockito.when(
                stockRepository.findById(1L)
        ).thenReturn(Optional.of(stock));

        StockDto response =
                stockService.update(dto);

        Assertions.assertFalse(
                response.isSuccess()
        );

        Assertions.assertEquals(
                "Stock STK1 has been deleted. Please contact the administrator.",
                response.getMessage()
        );

        Mockito.verify(
                stockRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void findByIdentifier_success() {

        Stock stock = new Stock();
        stock.setIdentifier("STK1");

        StockDto dto = new StockDto();
        dto.setIdentifier("STK1");

        Mockito.when(stockRepository.findByIdentifier("STK1"))
                .thenReturn(stock);

        Mockito.when(modelMapper.map(stock, StockDto.class))
                .thenReturn(dto);

        StockDto response =
                stockService.findByIdentifier("STK1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("STK1", response.getIdentifier());
    }

    @Test
    void delete_success() {

        Stock stock = new Stock();
        stock.setIdentifier("STK1");

        Mockito.when(stockRepository.findByIdentifier("STK1"))
                .thenReturn(stock);

        Mockito.when(stockRepository.save(stock))
                .thenReturn(stock);

        stockService.delete("STK1");

        Mockito.verify(stockRepository)
                .findByIdentifier("STK1");

        Mockito.verify(stockRepository)
                .save(stock);
    }
}