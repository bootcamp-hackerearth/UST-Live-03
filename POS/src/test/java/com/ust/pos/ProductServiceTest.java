package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccessTest() {

        Product product = new Product();
        ProductDto dto = new ProductDto();

        when(productRepository.findByIdentifier("P1"))
                .thenReturn(product);

        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto response =
                productService.findByIdentifier("P1");

        assertNotNull(response);
    }

    @Test
    void findByIdentifierFailureTest() {

        when(productRepository.findByIdentifier("P1"))
                .thenReturn(null);

        ProductDto response =
                productService.findByIdentifier("P1");

        assertNull(response);
    }

    @Test
    void saveSuccessTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product product = new Product();

        when(productRepository.findByIdentifier("P1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Product.class))
                .thenReturn(product);

        ProductDto response =
                productService.save(dto);

        assertEquals("P1", response.getIdentifier());
        assertTrue(response.isSuccess());
        assertNull(response.getMessage());

        verify(productRepository).save(product);
    }

    @Test
    void saveFailureTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        when(productRepository.findByIdentifier("P1"))
                .thenReturn(new Product());

        ProductDto response =
                productService.save(dto);

        assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());

        verify(productRepository, never())
                .save(any());
    }

    @Test
    void saveFailureDeletedProductTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product existing = new Product();
        existing.setDeleted(true);

        when(productRepository.findByIdentifier("P1"))
                .thenReturn(existing);

        ProductDto response =
                productService.save(dto);

        assertFalse(response.isSuccess());

        assertTrue(
                response.getMessage()
                        .contains("already exists but was deleted")
        );

        verify(productRepository, never())
                .save(any());
    }

    @Test
    void updateSuccessTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product existing = new Product();

        when(productRepository.findByIdentifier("P1"))
                .thenReturn(existing);

        ProductDto response =
                productService.update(dto);

        assertEquals("P1", response.getIdentifier());
        assertTrue(response.isSuccess());

        verify(modelMapper).map(dto, existing);
        verify(productRepository).save(existing);
    }

    @Test
    void updateFailureTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        when(productRepository.findByIdentifier("P1"))
                .thenReturn(null);

        ProductDto response =
                productService.update(dto);

        assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());

        verify(productRepository, never())
                .save(any());
    }

    @Test
    void deleteTest() {

        Product product = new Product();

        when(productRepository.findByIdentifier("P1"))
                .thenReturn(product);

        productService.delete("P1");

        assertTrue(product.isDeleted());

        verify(productRepository)
                .findByIdentifier("P1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();
        product.setIdentifier("P1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Page<Product> page =
                new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        WsDto<ProductDto> result =
                productService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("P1",
                result.getContent().getFirst().getIdentifier());

        assertEquals(0, result.getPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalRecords());

        verify(productRepository)
                .findByIsDeletedFalse(pageable);
    }

    @Test
    void toggleStatusTrueToFalseTest() {

        Product product = new Product();
        product.setStatus(true);

        when(productRepository.findByIdentifier("P1"))
                .thenReturn(product);

        productService.toggleStatus("P1");

        assertFalse(product.isStatus());

        verify(productRepository).save(product);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Product product = new Product();
        product.setStatus(false);

        when(productRepository.findByIdentifier("P1"))
                .thenReturn(product);

        productService.toggleStatus("P1");

        assertTrue(product.isStatus());

        verify(productRepository).save(product);
    }

    @Test
    void toggleStatusNotFoundTest() {

        when(productRepository.findByIdentifier("P1"))
                .thenReturn(null);

        productService.toggleStatus("P1");

        verify(productRepository, never())
                .save(any());
    }

    @Test
    void findActiveShelfTest() {

        Product product = new Product();

        ProductDto dto = new ProductDto();

        when(productRepository.findByStatus(true))
                .thenReturn(List.of(product));

        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        List<ProductDto> result =
                productService.findActiveShelf();

        assertEquals(1, result.size());

        verify(productRepository).findByStatus(true);
    }

    @Test
    void findAllWithQuantityStockExistsTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();
        product.setIdentifier("P1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Stock stock = new Stock();
        stock.setQuantity(25L);

        Page<Product> page =
                new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        when(stockRepository.findByProduct("P1"))
                .thenReturn(stock);

        WsDto<ProductDto> result =
                productService.findAllWithQuantity(pageable);

        assertEquals(25L,
                result.getContent().getFirst().getStockQuantity());

        verify(stockRepository)
                .findByProduct("P1");
    }

    @Test
    void findAllWithQuantityStockNotExistsTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();
        product.setIdentifier("P1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Page<Product> page =
                new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        when(stockRepository.findByProduct("P1"))
                .thenReturn(null);

        WsDto<ProductDto> result =
                productService.findAllWithQuantity(pageable);

        assertNull(
                result.getContent().getFirst().getStockQuantity()
        );

        verify(stockRepository)
                .findByProduct("P1");
    }

    @Test
    void findAllWithQuantityEmptyPageTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> page =
                new PageImpl<>(List.of(), pageable, 0);

        when(productRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        WsDto<ProductDto> result =
                productService.findAllWithQuantity(pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalRecords());
    }
}