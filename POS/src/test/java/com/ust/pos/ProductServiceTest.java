package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
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

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    // ================= SAVE =================

    @Test
    void saveTest_Success() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Product product = new Product();

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Product.class))
                .thenReturn(product);

        Mockito.when(productRepository.save(product))
                .thenReturn(product);

        ProductDto response = productService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Admin", response.getIdentifier());

        Mockito.verify(productRepository)
                .save(product);
    }

    @Test
    void saveTest_Failure_WhenProductExists() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(new Product());

        ProductDto response = productService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(
                response.getMessage().contains("already exists"));

        Mockito.verify(productRepository,
                Mockito.never()).save(Mockito.any());
    }

    // ================= UPDATE =================

    @Test
    void updateTest_Success() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Product existing = new Product();
        Product mapped = new Product();

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(existing);

        Mockito.when(modelMapper.map(dto, Product.class))
                .thenReturn(mapped);

        Mockito.when(productRepository.save(mapped))
                .thenReturn(mapped);

        ProductDto response = productService.update(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Admin", response.getIdentifier());

        Mockito.verify(productRepository)
                .save(mapped);
    }

    @Test
    void updateTest_Failure_WhenNotFound() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        ProductDto response = productService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage().contains("is not found"));

        Mockito.verify(productRepository,
                Mockito.never()).save(Mockito.any());
    }

    // ================= DELETE =================

    @Test
    void deleteTest() {

        Mockito.doNothing()
                .when(productRepository)
                .deleteByIdentifier("Admin");

        productService.delete("Admin");

        Mockito.verify(productRepository)
                .deleteByIdentifier("Admin");
    }

    // ================= FIND BY IDENTIFIER =================

    @Test
    void findByIdentifierTest_Success() {

        Product product = new Product();
        product.setIdentifier("Admin");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(product);

        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto response =
                productService.findByIdentifier("Admin");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Admin",
                response.getIdentifier());
    }

    @Test
    void findByIdentifierTest_NotFound() {

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(null, ProductDto.class))
                .thenReturn(null);

        ProductDto response =
                productService.findByIdentifier("Admin");

        Assertions.assertNull(response);
    }

    // ================= FIND ALL =================

    @Test
    void findAllTest() {

        List<Product> products =
                List.of(new Product());

        List<ProductDto> productDtos =
                List.of(new ProductDto());

        Type listType =
                new TypeToken<List<ProductDto>>() {
                }.getType();

        Mockito.when(productRepository.findAll())
                .thenReturn(products);

        Mockito.when(modelMapper.map(products, listType))
                .thenReturn(productDtos);

        List<ProductDto> response =
                productService.findAll();

        Assertions.assertEquals(1,
                response.size());
    }

    // ================= PAGINATION =================

    @Test
    void findAll_WithPagination_NoSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();

        Page<Product> page =
                new PageImpl<>(List.of(product));

        ProductDto dto = new ProductDto();

        Mockito.when(productRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.any(Product.class),
                        Mockito.eq(ProductDto.class)))
                .thenReturn(dto);

        Page<ProductDto> response =
                productService.findAll(pageable, null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1,
                response.getContent().size());

        Mockito.verify(productRepository)
                .findAll(pageable);
    }

    @Test
    void findAll_WithPagination_AndSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();

        Page<Product> page =
                new PageImpl<>(List.of(product));

        ProductDto dto = new ProductDto();

        Mockito.when(
                        productRepository
                                .findByIdentifierContainingIgnoreCase(
                                        pageable,
                                        "Admin"))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.any(Product.class),
                        Mockito.eq(ProductDto.class)))
                .thenReturn(dto);

        Page<ProductDto> response =
                productService.findAll(pageable,
                        "Admin");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1,
                response.getContent().size());

        Mockito.verify(productRepository)
                .findByIdentifierContainingIgnoreCase(
                        pageable,
                        "Admin");
    }

    // ================= TOGGLE STATUS =================

    @Test
    void toggleStatus_ShouldToggleToFalse() {

        Product product = new Product();
        product.setIdentifier("Admin");
        product.setStatus(true);

        Mockito.when(productRepository
                        .findByIdentifier("Admin"))
                .thenReturn(product);

        productService.toggleStatus("Admin");

        Assertions.assertFalse(product.getStatus());

        Mockito.verify(productRepository)
                .save(product);
    }

    @Test
    void toggleStatus_ShouldToggleToTrue() {

        Product product = new Product();
        product.setIdentifier("Admin");
        product.setStatus(false);

        Mockito.when(productRepository
                        .findByIdentifier("Admin"))
                .thenReturn(product);

        productService.toggleStatus("Admin");

        Assertions.assertTrue(product.getStatus());

        Mockito.verify(productRepository)
                .save(product);
    }

    @Test
    void toggleStatus_ProductNotFound() {

        Mockito.when(productRepository
                        .findByIdentifier("Admin"))
                .thenReturn(null);

        productService.toggleStatus("Admin");

        Mockito.verify(productRepository,
                        Mockito.never())
                .save(Mockito.any());
    }
}