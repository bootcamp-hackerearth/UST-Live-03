package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Product;
import com.ust.pos.modell.ProductRepository;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    public static final String INVALID = "INVALID";
    @InjectMocks
    private ProductServiceImpl service;

    @Mock
    private ProductRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifierTest() {

        Product product = new Product();
        ProductDto dto = new ProductDto();

        when(repository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(product);

        when(mapper.map(product, ProductDto.class))
                .thenReturn(dto);

        assertNotNull(service.findByIdentifier("P1"));

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findByIdentifier(INVALID)
                );

        assertEquals(
                "Product with identifier 'INVALID' not found",
                exception.getMessage()
        );
    }

    @Test
    void saveTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product product = new Product();
        product.setStatus(null);

        when(repository.findByIdentifier("P1"))
                .thenReturn(null);

        when(mapper.map(dto, Product.class))
                .thenReturn(product);

        ProductDto result = service.save(dto);

        verify(repository).save(product);

        assertTrue(product.getStatus());

        Product duplicate = new Product();
        duplicate.setDeleted(false);

        when(repository.findByIdentifier("P1"))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Product with identifier - P1 already exists",
                result.getMessage()
        );

        duplicate.setDeleted(true);

        when(repository.findByIdentifier("P1"))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Product with Identifier P1 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateAndDeleteTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product product = new Product();
        product.setIdentifier("P1");
        product.setCreatedBy("admin");
        product.setCreatedOn(LocalDateTime.now());

        when(repository.findByIdentifier("P1"))
                .thenReturn(product);

        ProductDto result = service.update(dto);

        verify(mapper).map(dto, product);
        verify(repository).save(product);

        assertNotNull(result);

        ProductDto invalidDto = new ProductDto();
        invalidDto.setIdentifier(INVALID);

        when(repository.findByIdentifier(INVALID))
                .thenReturn(null);

        result = service.update(invalidDto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Shelf with identifier - INVALID not found",
                result.getMessage()
        );

        when(repository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(product)
                .thenReturn(null);

        service.delete("P1");
        service.delete("P1");

        verify(repository, atLeast(2))
                .save(any(Product.class));
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Product product = new Product();
        product.setStatus(false);

        ProductDto dto = new ProductDto();

        when(repository.findByIdentifierAndDeletedFalse("P2"))
                .thenReturn(product);

        when(repository.save(product))
                .thenReturn(product);

        when(mapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto result = service.toggleStatus("P2");

        assertNotNull(result);
        assertTrue(product.getStatus());

        verify(repository).save(product);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> page =
                new PageImpl<>(
                        List.of(new Product()),
                        pageable,
                        1
                );

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new ProductDto()));

        WsDto<ProductDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());

        Specification<Product> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<ProductDto> specResult =
                service.findAll(specification, pageable);

        assertEquals(1, specResult.getDtoList().size());
        assertEquals(1, specResult.getTotalRecords());

        verify(repository)
                .findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void toggleStatusTest() {

        Product product = new Product();
        product.setStatus(true);

        ProductDto dto = new ProductDto();

        when(repository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(product);

        when(repository.save(product))
                .thenReturn(product);

        when(mapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto result = service.toggleStatus("P1");

        assertNotNull(result);
        assertFalse(product.getStatus());

        Product nullStatusProduct = new Product();
        nullStatusProduct.setStatus(null);

        when(repository.findByIdentifierAndDeletedFalse("P2"))
                .thenReturn(nullStatusProduct);

        when(repository.save(nullStatusProduct))
                .thenReturn(nullStatusProduct);

        when(mapper.map(nullStatusProduct, ProductDto.class))
                .thenReturn(dto);

        service.toggleStatus("P2");

        assertTrue(nullStatusProduct.getStatus());

        when(repository.findByIdentifierAndDeletedFalse("P3"))
                .thenReturn(null);

        NoSuchElementException exception =
                assertThrows(
                        NoSuchElementException.class,
                        () -> service.toggleStatus("P3")
                );

        assertEquals(
                "Product not found with identifier: P3",
                exception.getMessage()
        );

        assertEquals(
                "Product not found",
                ProductServiceImpl.PRODUCT_NOT_FOUND.getMessage()
        );
    }

    @Test
    void findAllActiveTest() {

        Product product = new Product();
        ProductDto dto = new ProductDto();

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(product));

        when(mapper.map(product, ProductDto.class))
                .thenReturn(dto);

        List<ProductDto> result =
                service.findAllActive();

        assertEquals(1, result.size());

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(Collections.emptyList());

        result = service.findAllActive();

        assertTrue(result.isEmpty());
    }
}