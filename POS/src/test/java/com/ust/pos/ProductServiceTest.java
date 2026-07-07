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
import org.springframework.data.jpa.domain.Specification;

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

    // SAVE

    @Test
    void save_Success() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product entity = new Product();

        Mockito.when(productRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Product.class))
                .thenReturn(entity);

        Mockito.when(productRepository.save(entity))
                .thenReturn(entity);

        ProductDto result = productService.save(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(productRepository).save(entity);
    }

    @Test
    void save_WhenExists_ShouldFail() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(new Product());

        ProductDto result = productService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    // UPDATE

    @Test
    void update_Success() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product existing = new Product();
        existing.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(existing);

        Mockito.doNothing().when(modelMapper).map(dto, existing);

        Mockito.when(productRepository.save(existing)).thenReturn(existing);

        ProductDto result = productService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(productRepository).save(existing);
    }

    @Test
    void update_WhenNotFound_ShouldFail() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(null);

        ProductDto result = productService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    // DELETE (SOFT DELETE)

    @Test
    void delete_Success() {

        Product entity = new Product();
        entity.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(entity);

        Mockito.when(productRepository.save(entity)).thenReturn(entity);

        productService.delete("P1");

        Assertions.assertTrue(entity.isDelete());

        Mockito.verify(productRepository).save(entity);
    }

    @Test
    void delete_WhenNotFound_ShouldDoNothing() {

        Mockito.when(productRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(null);

        productService.delete("P1");

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    // TOGGLE STATUS

    @Test
    void toggleStatus_Success() {

        Product entity = new Product();
        entity.setIdentifier("P1");
        entity.setStatus(true);

        Mockito.when(productRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(entity);

        Mockito.when(productRepository.save(entity)).thenReturn(entity);

        productService.toggleStatus("P1");

        Assertions.assertFalse(entity.getStatus());

        Mockito.verify(productRepository).save(entity);
    }

    @Test
    void toggleStatus_WhenNotFound_ShouldDoNothing() {

        Mockito.when(productRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(null);

        productService.toggleStatus("P1");

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    // FIND ALL

    @Test
    void findAll_Success() {

        List<Product> entities = List.of(new Product());
        List<ProductDto> dtos = List.of(new ProductDto());

        Type type = new TypeToken<List<ProductDto>>() {
        }.getType();

        Mockito.when(productRepository.findByIsDeleteFalse())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, type))
                .thenReturn(dtos);

        List<ProductDto> result = productService.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // FIND BY IDENTIFIER

    @Test
    void findByIdentifier_Success() {

        Product entity = new Product();
        entity.setIdentifier("P1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifierAndIsDeleteFalse("P1"))
                .thenReturn(entity);

        Mockito.when(modelMapper.map(entity, ProductDto.class))
                .thenReturn(dto);

        ProductDto result = productService.findByIdentifier("P1");

        Assertions.assertEquals("P1", result.getIdentifier());
    }

    // PAGINATION

    @Test
    void findAll_WithPagination_NoSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Product entity = new Product();
        entity.setIdentifier("P1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Page<Product> page = new PageImpl<>(List.of(entity));

        Mockito.when(productRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(entity, ProductDto.class))
                .thenReturn(dto);

        Page<ProductDto> result = productService.findAll(pageable, null);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void findAllPageableWithSearchTest() {
        Pageable pageable =
                PageRequest.of(0, 10);
        Product product = new Product();
        Page<Product> page =
                new PageImpl<>(List.of(product));
        Mockito.when(productRepository.findAll(
                        Mockito.<Specification<Product>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(page);
        Page<ProductDto> result =
                productService.findAll(pageable, "Admin");
        Assertions.assertEquals(
                1,
                result.getContent().size()
        );
        Mockito.verify(productRepository)
                .findAll(
                        Mockito.<Specification<Product>>any(),
                        Mockito.eq(pageable)
                );
    }

    @Test
    void findAll_WithBlankSearch_ShouldFallback() {

        Pageable pageable = PageRequest.of(0, 10);

        Product entity = new Product();
        entity.setIdentifier("P1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Page<Product> page = new PageImpl<>(List.of(entity));

        Mockito.when(productRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(entity, ProductDto.class))
                .thenReturn(dto);

        Page<ProductDto> result = productService.findAll(pageable, " ");

        Assertions.assertEquals(1, result.getContent().size());
    }
}