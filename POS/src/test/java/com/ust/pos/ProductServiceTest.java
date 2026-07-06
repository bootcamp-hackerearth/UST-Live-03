package com.ust.pos;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.model.StockRepository;
import com.ust.pos.price.service.PriceService;
import com.ust.pos.product.service.impl.ProductServiceImpl;
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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

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

    @Mock
    private PriceService priceService;

    @Test
    void findAllWithPageableTest() {

        Product product = new Product();
        product.setIdentifier("P1");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("P1");

        List<Product> products = List.of(product);
        List<ProductDto> productDtos = List.of(productDto);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Product> productPage =
                new PageImpl<>(products, pageable, products.size());

        Mockito.when(productRepository.findByIsDeletedFalse(pageable))
                .thenReturn(productPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(products),
                Mockito.any(Type.class)
        )).thenReturn(productDtos);

        Mockito.when(priceService.findByIdentifier("P1Selling"))
                .thenReturn(null);

        Mockito.when(priceService.findByIdentifier("P1Mrp"))
                .thenReturn(null);

        Mockito.when(stockRepository.findByProduct("P1"))
                .thenReturn(null);

        PaginationResponseDto<ProductDto> response =
                productService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(
                "P1",
                response.getDtoList().get(0).getIdentifier()
        );

        Assertions.assertEquals(
                0L,
                response.getDtoList().get(0).getStockQuantity()
        );
    }

    @Test
    void findByStatusTrueTest() {
        Product product = new Product();
        ProductDto dto = new ProductDto();

        Mockito.when(productRepository.findByStatusTrue()).thenReturn(List.of(product));
        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));

        List<ProductDto> response = productService.findByStatusTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void saveSuccessTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product product = new Product();

        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Product.class)).thenReturn(product);

        ProductDto response = productService.save(dto);

        Mockito.verify(productRepository).save(product);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveFailureTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifier("P1"))
                .thenReturn(new Product());

        ProductDto response = productService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product P1 already exists", response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        ProductDto dto = new ProductDto();

        Mockito.when(productRepository.findByIdentifier("P1")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        ProductDto response = productService.findByIdentifier("P1");

        Assertions.assertNotNull(response);
    }

    @Test
    void updateProductNotFoundTest() {
        ProductDto dto = new ProductDto();
        dto.setId(1L);
        dto.setIdentifier("P1");

        Mockito.when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        ProductDto response = productService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void updateIdentifierConflictTest() {
        ProductDto dto = new ProductDto();
        dto.setId(1L);
        dto.setIdentifier("NewName");

        Product existing = new Product();
        existing.setIdentifier("OldName");

        Mockito.when(productRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(productRepository.findByIdentifier("NewName"))
                .thenReturn(new Product());

        ProductDto response = productService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product NewName already exists", response.getMessage());
    }

    @Test
    void updateSuccessTest() {
        ProductDto dto = new ProductDto();
        dto.setId(1L);
        dto.setIdentifier("SameName");

        Product existing = new Product();
        existing.setIdentifier("SameName");

        Mockito.when(productRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        ProductDto response = productService.update(dto);

        Mockito.verify(modelMapper).map(dto, existing);
        Mockito.verify(productRepository).save(existing);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateStatusSuccessTest() {
        Product product = new Product();
        product.setStatus(false);

        Mockito.when(productRepository.findByIdentifier("P1"))
                .thenReturn(product);

        ProductDto response = productService.updateStatus("P1", true);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertTrue(product.isStatus());
    }

    @Test
    void updateStatusFailureTest() {
        Mockito.when(productRepository.findByIdentifier("P1"))
                .thenReturn(null);

        ProductDto response = productService.updateStatus("P1", true);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product not found", response.getMessage());
    }

    @Test
    void deleteTest() {

        Product product = new Product();
        product.setIdentifier("P1");
        product.setDeleted(false);

        Mockito.when(productRepository.findByIdentifier("P1"))
                .thenReturn(product);

        Mockito.when(productRepository.save(product))
                .thenReturn(product);

        productService.delete("P1");

        Assertions.assertTrue(product.isDeleted());

        Mockito.verify(productRepository)
                .findByIdentifier("P1");

        Mockito.verify(productRepository)
                .save(product);
    }
}