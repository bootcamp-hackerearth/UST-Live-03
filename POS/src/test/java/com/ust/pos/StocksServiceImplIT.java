package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.StocksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Stocks;
import com.ust.pos.model.StocksRepository;
import com.ust.pos.product.service.ProductService;
import com.ust.pos.stocks.service.StocksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class StocksServiceImplIT {

    @Autowired
    private StocksService stocksService;

    @Autowired
    private StocksRepository stocksRepository;

    @MockitoBean
    private ProductService productService;

    @BeforeEach
    void cleanUp() {
        stocksRepository.deleteAll();
    }

    @Test
    void save_shouldCreateStocks() {
        StocksDto dto = new StocksDto();
        dto.setIdentifier("STK001");
        dto.setStatus(true);

        ProductDto mockProduct = new ProductDto();
        mockProduct.setIdentifier("STK001");
        mockProduct.setName("Test Product");
        Mockito.when(productService.findByIdentifier("STK001")).thenReturn(mockProduct);

        Stocks saved = stocksRepository.findByIdentifier("STK001");

        assertNotNull(saved);
        assertEquals("STK001", saved.getIdentifier());
        assertEquals("Test Product", saved.getName());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Stocks stocks = new Stocks();
        stocks.setIdentifier("STK001");
        stocks.setDeleted(false);
        stocksRepository.save(stocks);

        StocksDto dto = new StocksDto();
        dto.setIdentifier("STK001");

        StocksDto response = stocksService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Stocks with identifier - STK001 already exists",
                response.getMessage()
        );
    }

    @Test
    void save_shouldFailWhenPreviouslyDeleted() {
        Stocks stocks = new Stocks();
        stocks.setIdentifier("STK001");
        stocks.setDeleted(true);
        stocksRepository.save(stocks);

        StocksDto dto = new StocksDto();
        dto.setIdentifier("STK001");

        StocksDto response = stocksService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Stocks with identifier STK001 was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateStocksDetails() {
        Stocks stocks = new Stocks();
        stocks.setIdentifier("STK001");
        stocks.setStatus(true);
        stocks.setDeleted(false);
        stocksRepository.save(stocks);

        StocksDto dto = new StocksDto();
        dto.setIdentifier("STK001");
        dto.setStatus(false);

        StocksDto response = stocksService.update(dto);

        assertTrue(response.isSuccess());

        Stocks updated = stocksRepository.findByIdentifier("STK001");

        assertFalse(updated.isStatus());
    }

    @Test
    void findByIdentifier_shouldReturnStocks() {
        Stocks stocks = new Stocks();
        stocks.setIdentifier("STK001");
        stocksRepository.save(stocks);

        StocksDto result = stocksService.findByIdentifier("STK001");

        assertEquals("STK001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            stocksService.findByIdentifier("NON-EXISTENT");
        });
    }

    @Test
    void toggleStatus_shouldToggleValue() {
        Stocks stocks = new Stocks();
        stocks.setIdentifier("STK001");
        stocks.setStatus(true);
        stocksRepository.save(stocks);

        stocksService.toggleStatus("STK001");

        Stocks updated = stocksRepository.findByIdentifier("STK001");

        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {
        Stocks stocks = new Stocks();
        stocks.setIdentifier("STK001");
        stocks.setDeleted(false);
        stocksRepository.save(stocks);

        boolean isDeleted = stocksService.delete("STK001");

        assertTrue(isDeleted);

        Stocks deleted = stocksRepository.findByIdentifier("STK001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void findAll_shouldReturnPaginatedData() {
        Stocks stocks1 = new Stocks();
        stocks1.setIdentifier("STK001");
        stocks1.setDeleted(false);
        stocksRepository.save(stocks1);

        Stocks stocks2 = new Stocks();
        stocks2.setIdentifier("STK002");
        stocks2.setDeleted(false);
        stocksRepository.save(stocks2);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<StocksDto> response = stocksService.findAll(pageable);

        assertNotNull(response);
        assertEquals(2, response.getTotalRecords());
        assertEquals(2, response.getDtoList().size());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedRecords() {
        Stocks activeStocks = new Stocks();
        activeStocks.setIdentifier("STK001");
        activeStocks.setStatus(true);
        activeStocks.setDeleted(false);
        stocksRepository.save(activeStocks);

        Stocks inactiveStocks = new Stocks();
        inactiveStocks.setIdentifier("STK002");
        inactiveStocks.setStatus(false);
        inactiveStocks.setDeleted(false);
        stocksRepository.save(inactiveStocks);

        List<StocksDto> activeList = stocksService.findIfTrue();

        assertEquals(1, activeList.size());
        assertEquals("STK001", activeList.get(0).getIdentifier());
    }
}