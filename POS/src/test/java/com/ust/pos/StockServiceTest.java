package com.ust.pos;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {

        StockDto stockDto = new StockDto();
        stockDto.setProduct("P1");
        stockDto.setWarehouse("W1");

        String identifier = "P1_W1";

        Mockito.when(stockRepository.findByIdentifier(identifier)).thenReturn(null);

        Stock stock = new Stock();
        Mockito.when(modelMapper.map(stockDto, Stock.class)).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);

        StockDto response = stockService.save(stockDto);

        Assertions.assertEquals(identifier, response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertFalse(stock.getIsDeleted());
    }

    @Test
    void saveTestFailureAlreadyExists() {

        StockDto stockDto = new StockDto();
        stockDto.setProduct("P1");
        stockDto.setWarehouse("W1");

        Stock stock = new Stock();
        stock.setIsDeleted(false);

        Mockito.when(stockRepository.findByIdentifier("P1_W1")).thenReturn(stock);

        StockDto response = stockService.save(stockDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));
    }

    @Test
    void saveTestFailureDeletedStock() {

        StockDto stockDto = new StockDto();
        stockDto.setProduct("P1");
        stockDto.setWarehouse("W1");

        Stock stock = new Stock();
        stock.setIsDeleted(true);

        Mockito.when(stockRepository.findByIdentifier("P1_W1")).thenReturn(stock);

        StockDto response = stockService.save(stockDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("was deleted"));
    }

    @Test
    void findByIdentifierTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Admin");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Admin");

        Mockito.when(stockRepository.findByIdentifier("Admin")).thenReturn(stock);
        Mockito.when(modelMapper.map(stock, StockDto.class)).thenReturn(stockDto);

        StockDto response = stockService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Admin");

        Stock existingStock = new Stock();
        existingStock.setIdentifier("Admin");

        Mockito.when(stockRepository.findByIdentifier("Admin")).thenReturn(existingStock);
        Mockito.when(stockRepository.save(existingStock)).thenReturn(existingStock);

        StockDto response = stockService.update(stockDto);

        Assertions.assertEquals("Admin", response.getIdentifier());

        Mockito.verify(modelMapper).map(stockDto, existingStock);
        Mockito.verify(stockRepository).save(existingStock);
    }

    @Test
    void updateTestFailure() {

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Admin");

        Mockito.when(stockRepository.findByIdentifier("Admin")).thenReturn(null);

        StockDto response = stockService.update(stockDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock with identifier - Admin not found", response.getMessage());
    }

    @Test
    void deleteTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Admin");
        stock.setStatus(true);
        stock.setIsDeleted(false);

        Mockito.when(stockRepository.findByIdentifier("Admin")).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);

        StockDto response = stockService.delete("Admin");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Stock deleted successfully", response.getMessage());
        Assertions.assertTrue(stock.getIsDeleted());
        Assertions.assertFalse(stock.getStatus());
    }

    @Test
    void deleteTestFailure() {

        Mockito.when(stockRepository.findByIdentifier("Admin")).thenReturn(null);

        StockDto response = stockService.delete("Admin");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stock with identifier - Admin not found", response.getMessage());
    }

    @Test
    void findAllTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Admin");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Admin");

        List<Stock> stocks = List.of(stock);
        List<StockDto> stockDtos = List.of(stockDto);

        Page<Stock> stockPage = new PageImpl<>(stocks);

        Mockito.when(stockRepository.findByIsDeleted(
                Mockito.eq(false),
                Mockito.any(Pageable.class)
        )).thenReturn(stockPage);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(stockDtos);

        PaginatedResponseDto<StockDto> response = stockService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAllActiveTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Admin");
        stock.setStatus(true);

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Admin");

        List<Stock> stocks = List.of(stock);
        List<StockDto> stockDtos = List.of(stockDto);

        Mockito.when(stockRepository.findByStatusAndIsDeleted(true, false)).thenReturn(stocks);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(stockDtos);

        List<StockDto> response = stockService.findAllActive();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Admin", response.get(0).getIdentifier());
    }

    @Test
    void changeStatusTrueTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Admin");
        stock.setStatus(false);

        Mockito.when(stockRepository.findByIdentifier("Admin")).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);

        stockService.changeStatus("Admin", true);

        Assertions.assertTrue(stock.getStatus());
        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void changeStatusFalseTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Admin");
        stock.setStatus(true);

        Mockito.when(stockRepository.findByIdentifier("Admin")).thenReturn(stock);
        Mockito.when(stockRepository.save(stock)).thenReturn(stock);

        stockService.changeStatus("Admin", false);

        Assertions.assertFalse(stock.getStatus());
        Mockito.verify(stockRepository).save(stock);
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(stockRepository.findByIdentifier("Admin")).thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> stockService.findByIdentifier("Admin")
        );

        Assertions.assertEquals(
                "Stock with identifier - Admin not found",
                exception.getMessage()
        );
    }

    @Test
    void findAllWithSpecificationTest() {

        Stock stock = new Stock();
        stock.setIdentifier("Admin");

        StockDto stockDto = new StockDto();
        stockDto.setIdentifier("Admin");

        List<Stock> stocks = List.of(stock);
        List<StockDto> stockDtos = List.of(stockDto);

        Page<Stock> page = new PageImpl<>(stocks);
        Specification<Stock> specification = Mockito.mock(Specification.class);

        Mockito.when(stockRepository.findAll(
                Mockito.eq(specification),
                Mockito.any(Pageable.class)
        )).thenReturn(page);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(stockDtos);

        PaginatedResponseDto<StockDto> response =
                stockService.findAll(specification, PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}