package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
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

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private StockRepository stockRepository;

    @Test
    void testFindByIdentifier_Success() {
        String identifier = "PROD-001";

        Product product = new Product();
        product.setIdentifier(identifier);

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier(identifier);

        when(productRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(product);
        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(productDto);

        ProductDto result = productService.findByIdentifier(identifier);

        assertEquals(identifier, result.getIdentifier());
        verify(productRepository).findByIdentifierAndDeletedFalse(identifier);
    }

    @Test
    void testFindByIdentifier_NotFound() {
        String identifier = "PROD-001";

        when(productRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(null);

        ProductDto result = productService.findByIdentifier(identifier);

        assertFalse(result.isSuccess());
        assertEquals("Product with identifier - PROD-001 not found", result.getMessage());
    }

    @Test
    void testSave_Success() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD-001");

        Product product = new Product();
        product.setIdentifier("PROD-001");

        when(productRepository.findByIdentifier("PROD-001"))
                .thenReturn(null);
        when(modelMapper.map(productDto, Product.class))
                .thenReturn(product);

        ProductDto result = productService.save(productDto);

        assertTrue(result.isSuccess());
        assertEquals("Product created successfully", result.getMessage());

        verify(productRepository).save(product);
    }

    @Test
    void testSave_AlreadyExists() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD-001");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("PROD-001");
        existingProduct.setDeleted(false);

        when(productRepository.findByIdentifier("PROD-001"))
                .thenReturn(existingProduct);

        ProductDto result = productService.save(productDto);

        assertFalse(result.isSuccess());
        assertEquals("Product with identifier - PROD-001 already exists", result.getMessage());

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testSave_DeletedProductExists() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD-001");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("PROD-001");
        existingProduct.setDeleted(true);

        when(productRepository.findByIdentifier("PROD-001"))
                .thenReturn(existingProduct);

        ProductDto result = productService.save(productDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Product with identifier - PROD-001  was deleted and cannot be created again.",
                result.getMessage()
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdate_Success() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD-001");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("PROD-001");

        when(productRepository.findByIdentifierAndDeletedFalse("PROD-001"))
                .thenReturn(existingProduct);

        ProductDto result = productService.update(productDto);

        assertTrue(result.isSuccess());
        assertEquals("Product updated successfully", result.getMessage());

        verify(modelMapper).map(productDto, existingProduct);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void testUpdate_NotFound() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD-001");

        when(productRepository.findByIdentifierAndDeletedFalse("PROD-001"))
                .thenReturn(null);

        ProductDto result = productService.update(productDto);

        assertFalse(result.isSuccess());
        assertEquals("Product with identifier - PROD-001 not found", result.getMessage());

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testDelete_Success() {
        String identifier = "PROD-001";

        Product product = new Product();
        product.setIdentifier(identifier);
        product.setDeleted(false);

        when(productRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(product);

        productService.delete(identifier);

        assertTrue(product.getDeleted());

        verify(productRepository).findByIdentifierAndDeletedFalse(identifier);
        verify(productRepository).save(product);
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();
        product.setIdentifier("PROD-001");

        List<Product> productList = List.of(product);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD-001");

        List<ProductDto> dtoList = List.of(productDto);

        when(productRepository.findByDeletedFalse(pageable))
                .thenReturn(productPage);
        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<ProductDto> result = productService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(productRepository).findByDeletedFalse(pageable);
    }

    @Test
    void testToggleStatus_Success() {
        String identifier = "PROD-001";

        Product product = new Product();
        product.setIdentifier(identifier);
        product.setStatus(true);

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier(identifier);

        when(productRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(product);
        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(productDto);

        ProductDto result = productService.toggleStatus(identifier);

        assertFalse(product.isStatus());
        assertNotNull(result);

        verify(productRepository).save(product);
    }

    @Test
    void testFindActiveProducts_Success() {
        Product product = new Product();
        product.setIdentifier("PROD-001");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PROD-001");

        List<Product> products = List.of(product);
        List<ProductDto> productDtos = List.of(productDto);

        when(productRepository.findByStatusTrueAndDeletedFalse())
                .thenReturn(products);
        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(productDtos);

        List<ProductDto> result = productService.findActiveProducts();

        assertEquals(1, result.size());
        assertEquals("PROD-001", result.get(0).getIdentifier());
    }

    @Test
    void testFindActiveProductsWithStock_Success() {
        List<String> productIdentifiers = List.of("PROD-001", "PROD-002");

        Product product1 = new Product();
        product1.setIdentifier("PROD-001");

        Product product2 = new Product();
        product2.setIdentifier("PROD-002");

        ProductDto productDto1 = new ProductDto();
        productDto1.setIdentifier("PROD-001");

        ProductDto productDto2 = new ProductDto();
        productDto2.setIdentifier("PROD-002");

        when(stockRepository.findProductIdentifiersWithStock())
                .thenReturn(productIdentifiers);

        when(productRepository.findByStatusTrueAndDeletedFalseAndIdentifierIn(productIdentifiers))
                .thenReturn(List.of(product1, product2));

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(productDto1, productDto2));

        List<ProductDto> result = productService.findActiveProductsWithStock();

        assertEquals(2, result.size());
        assertEquals("PROD-001", result.get(0).getIdentifier());
        assertEquals("PROD-002", result.get(1).getIdentifier());

        verify(stockRepository).findProductIdentifiersWithStock();
        verify(productRepository)
                .findByStatusTrueAndDeletedFalseAndIdentifierIn(productIdentifiers);
    }

    @Test
    void testFindActiveProductsWithStock_NoStockProducts() {
        when(stockRepository.findProductIdentifiersWithStock())
                .thenReturn(List.of());

        List<ProductDto> result = productService.findActiveProductsWithStock();

        assertTrue(result.isEmpty());

        verify(stockRepository).findProductIdentifiersWithStock();
        verify(productRepository, never())
                .findByStatusTrueAndDeletedFalseAndIdentifierIn(anyList());
    }

}

