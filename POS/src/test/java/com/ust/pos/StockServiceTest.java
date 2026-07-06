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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");

        Stock stock = new Stock();

        Mockito.when(
                stockRepository.findByIdentifier("Lays In-001")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(stockDto, Stock.class)
        ).thenReturn(stock);

        Mockito.when(
                stockRepository.save(stock)
        ).thenReturn(stock);

        StockDto response = stockService.save(stockDto);

        Assertions.assertEquals(
                "Lays In-001",
                response.getIdentifier()
        );

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");

        Stock existingStock = new Stock();

        Mockito.when(
                stockRepository.findByIdentifier("Lays In-001")
        ).thenReturn(existingStock);

        StockDto response = stockService.save(stockDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveSoftDeletedStockTest() {

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");

        Stock existingStock = new Stock();
        existingStock.setDeleted(true);

        Mockito.when(
                stockRepository.findByIdentifier("Lays In-001")
        ).thenReturn(existingStock);

        StockDto response = stockService.save(stockDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage().contains("soft deleted")
        );
    }

    @Test
    void findByIdentifierTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Lays In-001");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");

        Mockito.when(
                stockRepository.findByIdentifier("Lays In-001")
        ).thenReturn(stock);

        Mockito.when(
                modelMapper.map(stock, StockDto.class)
        ).thenReturn(stockDto);

        StockDto response =
                stockService.findByIdentifier("Lays In-001");

        Assertions.assertEquals(
                "Lays In-001",
                response.getIdentifier()
        );
    }

    @Test
    void updateTest() {

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");

        Stock stock = new Stock();
        stock.setIdentifier("Lays In-001");

        Mockito.when(
                stockRepository.findByIdentifier("Lays In-001")
        ).thenReturn(stock);

        Mockito.when(
                stockRepository.save(stock)
        ).thenReturn(stock);

        StockDto response =
                stockService.update(stockDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Stock updated successfully",
                response.getMessage()
        );
    }

    @Test
    void updateTestFailure() {

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");

        Mockito.when(
                stockRepository.findByIdentifier("Lays In-001")
        ).thenReturn(null);

        StockDto response =
                stockService.update(stockDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(
                response.getMessage().contains("not found")
        );
    }

    @Test
    void findAllWithPageableTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Lays In-001");

        StockDto dto = new StockDto();
        dto.setIdentifier("Lays In-001");

        List<Stock> stocks = List.of(stock);
        List<StockDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Stock> stockPage =
                new PageImpl<>(stocks);

        Mockito.when(
                stockRepository.findByDeletedFalse(pageable)
        ).thenReturn(stockPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(stocks),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<StockDto> response =
                stockService.findAll(pageable);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Lays In-001",
                response.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void findAllWithoutPageableTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Lays In-001");

        StockDto dto = new StockDto();
        dto.setIdentifier("Lays In-001");

        List<Stock> stocks = List.of(stock);
        List<StockDto> dtos = List.of(dto);

        Page<Stock> stockPage =
                new PageImpl<>(stocks);

        Mockito.when(
                stockRepository.findByDeletedFalse(null)
        ).thenReturn(stockPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(stockPage),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<StockDto> response =
                stockService.findAll(null);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );
    }

    @Test
    void deleteTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Lays In-001");

        Mockito.when(
                stockRepository.findByIdentifier("Lays In-001")
        ).thenReturn(stock);

        stockService.delete("Lays In-001");

        Assertions.assertTrue(stock.isDeleted());

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void toggleStatusSuccessTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Lays In-001");
        stock.setStatus(false);

        StockDto mappedDto = new StockDto();
        mappedDto.setIdentifier("Lays In-001");

        Mockito.when(
                stockRepository.findByIdentifier("Lays In-001")
        ).thenReturn(stock);

        Mockito.when(
                modelMapper.map(stock, StockDto.class)
        ).thenReturn(mappedDto);

        StockDto response =
                stockService.toggleStatus(
                        "Lays In-001",
                        true
                );

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Status updated successfully",
                response.getMessage()
        );

        Mockito.verify(stockRepository)
                .save(stock);
    }

    @Test
    void toggleStatusFailureTest() {

        Mockito.when(
                stockRepository.findByIdentifier("Lays In-001")
        ).thenReturn(null);

        StockDto response =
                stockService.toggleStatus(
                        "Lays In-001",
                        true
                );

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Stock not found",
                response.getMessage()
        );
    }

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable =
                PageRequest.of(0, 5);

        Specification<Stock> specification =
                Mockito.mock(Specification.class);

        Stock stock = new Stock();
        stock.setIdentifier("Lays In-001");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Lays In-001");

        List<Stock> stocks =
                List.of(stock);

        List<StockDto> stockDtos =
                List.of(stockDto);

        Page<Stock> stockPage =
                new PageImpl<>(stocks, pageable, 1);

        Mockito.when(
                stockRepository.findAll(specification, pageable)
        ).thenReturn(stockPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(stockPage.getContent()),
                        Mockito.any(Type.class)
                )
        ).thenReturn(stockDtos);

        WsDto<StockDto> response =
                stockService.findAll(specification, pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Lays In-001",
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
                0,
                response.getPage()
        );

        Assertions.assertEquals(
                5,
                response.getSizePerPage()
        );
    }
}