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

    @Test
    void saveTest_Success() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");
        Product entity = new Product();
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Product.class))
                .thenReturn(entity);
        Mockito.when(productRepository.save(entity))
                .thenReturn(entity);
        ProductDto response = productService.save(dto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(productRepository).save(entity);
    }

    @Test
    void saveTest_Failure_WhenExists() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(new Product());
        ProductDto response = productService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertEquals("Product with identifier - Admin already exists",
                response.getMessage());
        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest_Success() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Product existing = new Product();

        Mockito.when(productRepository
                        .findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(existing);

        ProductDto response = productService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Updated successfully",
                response.getMessage()
        );

        Mockito.verify(productRepository)
                .save(existing);

        Mockito.verify(modelMapper, Mockito.never())
                .map(Mockito.any(), Mockito.eq(Product.class));
    }

    @Test
    void updateTest_Failure_WhenNotFound() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(null);
        ProductDto response = productService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertEquals("Product not found",
                response.getMessage());
        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("Admin");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);
        ProductDto response = productService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findAllTest() {
        List<Product> entities = List.of(new Product());
        List<ProductDto> dtos = List.of(new ProductDto());
        Type listType = new TypeToken<List<ProductDto>>() {}.getType();
        Mockito.when(productRepository.findByDeletedFalse())
                .thenReturn(entities);
        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);
        List<ProductDto> response = productService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteTest() {
        Product product = new Product();
        product.setIdentifier("Admin");
        product.setDeleted(false);
        Mockito.when(productRepository.findByIdentifierAndDeletedFalse("Admin")).thenReturn(product);
        productService.delete("Admin");
        Assertions.assertTrue(product.isDeleted());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void findAll_WithPagination_ShouldReturnProductDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();
        ProductDto productDto = new ProductDto();

        Page<Product> page = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(productDto);

        Page<ProductDto> response =
                productService.findAll(pageable, null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());

        Mockito.verify(productRepository)
                .findByDeletedFalse(pageable);

        Mockito.verify(modelMapper)
                .map(product, ProductDto.class);
    }
    @Test
    void findAll_WithSearch_ShouldReturnProductDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = new Product();
        ProductDto dto = new ProductDto();
        Page<Product> page = new PageImpl<>(List.of(product));
        Mockito.when(productRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse("ABC", pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);
        Page<ProductDto> response =
                productService.findAll(pageable, "ABC");
        Assertions.assertEquals(1, response.getContent().size());
        Mockito.verify(productRepository)
                .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                        "ABC",
                        pageable
                );
    }
}