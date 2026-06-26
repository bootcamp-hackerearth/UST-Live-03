package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
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

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

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

        ProductDto result = service.findByIdentifier("P1");

        assertNotNull(result);
    }

    @Test
    void saveSuccessTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product product = new Product();

        when(repository.findByIdentifier("P1"))
                .thenReturn(null);

        when(mapper.map(dto, Product.class))
                .thenReturn(product);

        ProductDto result = service.save(dto);

        verify(repository).save(product);

        assertEquals("P1", result.getIdentifier());
    }

    @Test
    void saveDuplicateTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product product = new Product();
        product.setDeleted(false);

        when(repository.findByIdentifier("P1"))
                .thenReturn(product);

        ProductDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Product with identifier - P1 already exists",
                result.getMessage()
        );
    }

    @Test
    void saveSoftDeletedTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product product = new Product();
        product.setDeleted(true);

        when(repository.findByIdentifier("P1"))
                .thenReturn(product);

        ProductDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Product with Identifier P1 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateSuccessTest() {

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
    }

    @Test
    void updateNotFoundTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        when(repository.findByIdentifier("P1"))
                .thenReturn(null);

        ProductDto result = service.update(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Shelf with identifier - P1 not found",
                result.getMessage()
        );
    }

    @Test
    void deleteSuccessTest() {

        Product product = new Product();

        when(repository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(product);

        service.delete("P1");

        verify(repository).save(product);
    }

    @Test
    void deleteNotFoundTest() {

        when(repository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);

        service.delete("P1");

        verify(repository, never()).save(any());
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();
        ProductDto dto = new ProductDto();

        Page<Product> page =
                new PageImpl<>(List.of(product), pageable, 1);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<ProductDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> page =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        WsDto<ProductDto> result =
                service.findAll(pageable);

        assertTrue(result.getDtoList().isEmpty());
    }

    @Test
    void toggleStatusSuccessTest() {

        Product product = new Product();
        product.setStatus(true);

        ProductDto dto = new ProductDto();

        when(repository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(product);

        when(repository.save(product))
                .thenReturn(product);

        when(mapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto result =
                service.toggleStatus("P1");

        assertNotNull(result);
        assertFalse(product.getStatus());

        verify(repository).save(product);
    }

    @Test
    void toggleStatusNullStatusTest() {

        Product product = new Product();
        product.setStatus(null);

        ProductDto dto = new ProductDto();

        when(repository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(product);

        when(repository.save(product))
                .thenReturn(product);

        when(mapper.map(product, ProductDto.class))
                .thenReturn(dto);

        service.toggleStatus("P1");

        assertTrue(product.getStatus());
    }

    @Test
    void toggleStatusNotFoundTest() {

        when(repository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.toggleStatus("P1")
                );

        assertEquals(
                "Product not found with identifier: P1",
                exception.getMessage()
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
    }

    @Test
    void findAllActiveEmptyTest() {

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(Collections.emptyList());

        List<ProductDto> result =
                service.findAllActive();

        assertTrue(result.isEmpty());
    }
}