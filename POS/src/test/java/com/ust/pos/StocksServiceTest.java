package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.StocksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Stocks;
import com.ust.pos.model.StocksRepository;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stocks.service.impl.StocksServiceImpl;
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
class StocksServiceTest {

    @Mock
    private StocksRepository stocksRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ProductService productService;

    @InjectMocks
    private StocksServiceImpl stocksService;

    @Test
    void findByIdentifierTestSuccess() {
        Stocks stocks = new Stocks();
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("STK01");

        Mockito.when(stocksRepository.findByIdentifier("STK01")).thenReturn(stocks);
        Mockito.when(modelMapper.map(stocks, StocksDto.class)).thenReturn(stocksDto);

        StocksDto response = stocksService.findByIdentifier("STK01");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("STK01", response.getIdentifier());
    }

    @Test
    void findByIdentifierTestNotFoundException() {
        Mockito.when(stocksRepository.findByIdentifier("STK01")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            stocksService.findByIdentifier("STK01");
        });
    }

    @Test
    void saveTestSuccess() {
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("STK01");

        Mockito.when(stocksRepository.findByIdentifier("STK01")).thenReturn(null);

        Stocks stocks = new Stocks();
        Mockito.when(modelMapper.map(stocksDto, Stocks.class)).thenReturn(stocks);

        ProductDto productDto = new ProductDto();
        productDto.setName("Test Product");
        Mockito.when(productService.findByIdentifier("STK01")).thenReturn(productDto);
        Mockito.when(stocksRepository.save(stocks)).thenReturn(stocks);

        StocksDto response = stocksService.save(stocksDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("STK01", response.getIdentifier());
    }

    @Test
    void saveTestFailureAlreadyExists() {
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("STK01");

        Stocks existingStocks = new Stocks();
        existingStocks.setIdentifier("STK01");
        existingStocks.setDeleted(false);

        Mockito.when(stocksRepository.findByIdentifier("STK01")).thenReturn(existingStocks);

        StocksDto response = stocksService.save(stocksDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stocks with identifier - STK01 already exists", response.getMessage());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("STK01");

        Stocks existingStocks = new Stocks();
        existingStocks.setIdentifier("STK01");
        existingStocks.setDeleted(true);

        Mockito.when(stocksRepository.findByIdentifier("STK01")).thenReturn(existingStocks);

        StocksDto response = stocksService.save(stocksDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stocks with identifier STK01 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void updateTestSuccess() {
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("STK01");

        Stocks existingStocks = new Stocks();
        existingStocks.setIdentifier("STK01");

        Mockito.when(stocksRepository.findByIdentifier("STK01")).thenReturn(existingStocks);
        Mockito.when(stocksRepository.save(existingStocks)).thenReturn(existingStocks);

        StocksDto response = stocksService.update(stocksDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("STK01", response.getIdentifier());
    }

    @Test
    void updateTestFailure() {
        StocksDto stocksDto = new StocksDto();
        stocksDto.setIdentifier("STK01");

        Mockito.when(stocksRepository.findByIdentifier("STK01")).thenReturn(null);

        StocksDto response = stocksService.update(stocksDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Stocks with identifier - STK01 not found", response.getMessage());
    }

    @Test
    void deleteTestSuccess() {
        Stocks stocks = new Stocks();

        Mockito.when(stocksRepository.findByIdentifier("STK01")).thenReturn(stocks);
        Mockito.when(stocksRepository.save(stocks)).thenReturn(stocks);

        boolean response = stocksService.delete("STK01");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(stocksRepository.findByIdentifier("STK01")).thenReturn(null);

        boolean response = stocksService.delete("STK01");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Stocks stocks = new Stocks();
        List<Stocks> stocksList = List.of(stocks);
        Page<Stocks> stocksPage = new PageImpl<>(stocksList, pageable, stocksList.size());

        StocksDto stocksDto = new StocksDto();
        List<StocksDto> stocksDtos = List.of(stocksDto);

        Mockito.when(stocksRepository.findByDeletedFalse(pageable)).thenReturn(stocksPage);
        Mockito.when(modelMapper.map(Mockito.eq(stocksList), Mockito.any(Type.class))).thenReturn(stocksDtos);

        WsDto<StocksDto> response = stocksService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findIfTrueTest() {
        Stocks stocks = new Stocks();
        List<Stocks> stocksList = List.of(stocks);
        StocksDto stocksDto = new StocksDto();
        List<StocksDto> stocksDtos = List.of(stocksDto);

        Mockito.when(stocksRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(stocksList);
        Mockito.when(modelMapper.map(Mockito.eq(stocksList), Mockito.any(Type.class))).thenReturn(stocksDtos);

        List<StocksDto> response = stocksService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {
        Stocks stocks = new Stocks();
        stocks.setStatus(false);
        StocksDto stocksDto = new StocksDto();
        stocksDto.setStatus(true);

        Mockito.when(stocksRepository.findByIdentifier("STK01")).thenReturn(stocks);
        Mockito.when(stocksRepository.save(stocks)).thenReturn(stocks);
        Mockito.when(modelMapper.map(stocks, StocksDto.class)).thenReturn(stocksDto);

        StocksDto response = stocksService.toggleStatus("STK01");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<Stocks> specification = Mockito.mock(Specification.class);
        Stocks stocks = new Stocks();
        List<Stocks> stocksList = List.of(stocks);
        Page<Stocks> page = new PageImpl<>(stocksList, pageable, stocksList.size());

        StocksDto stocksDto = new StocksDto();
        List<StocksDto> stocksDtos = List.of(stocksDto);

        Mockito.when(stocksRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(stocksList), Mockito.any(Type.class))).thenReturn(stocksDtos);

        WsDto<StocksDto> response = stocksService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}