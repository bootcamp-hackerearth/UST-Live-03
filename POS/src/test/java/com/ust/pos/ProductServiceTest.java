package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.modell.Product;
import com.ust.pos.modell.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    void findByIdentifier_shouldHandleBothCases() {
        Product product = new Product();
        product.setIdentifier("P1");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        when(repository.findByIdentifier("P1")).thenReturn(product);
        when(mapper.map(product, ProductDto.class)).thenReturn(dto);
        assertEquals("P1", service.findByIdentifier("P1").getIdentifier());
        when(repository.findByIdentifier("X")).thenReturn(null);
        when(mapper.map(null, ProductDto.class)).thenReturn(null);
        assertNull(service.findByIdentifier("X"));
    }

    @Test
    void save_shouldHandleAllCases() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Product product = new Product();
        product.setIdentifier("P1");
        when(repository.findByIdentifier("P1")).thenReturn(null);
        when(mapper.map(dto, Product.class)).thenReturn(product);
        ProductDto result = service.save(dto);
        verify(mapper).map(dto, Product.class);
        verify(repository).save(product);
        assertEquals("P1", result.getIdentifier());
        when(repository.findByIdentifier("P1")).thenReturn(product);
        ProductDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void update_shouldHandleBothCases() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Product product = new Product();
        product.setIdentifier("P1");
        when(repository.findByIdentifier("P1")).thenReturn(product);
        service.update(dto);
        verify(mapper).map(dto, product);
        verify(repository).save(product);
        when(repository.findByIdentifier("X")).thenReturn(null);
        dto.setIdentifier("X");
        ProductDto fail = service.update(dto);
        assertFalse(fail.isSuccess());
        assertTrue(fail.getMessage().contains("not found"));
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete("P1");
        verify(repository).deleteByIdentifier("P1");
    }

    @Test
    void findAll_shouldHandleDataAndEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<ProductDto>>() {}.getType();
        Product product = new Product();
        product.setIdentifier("P1");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));
        assertEquals(1, service.findAll(pageable).size());
        Page<Product> empty = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(empty);
        when(mapper.map(List.of(), type)).thenReturn(List.of());
        assertTrue(service.findAll(pageable).isEmpty());
    }

    @Test
    void toggleAndFindAllActive_shouldHandleAllCases() {
        Product product = new Product();
        product.setIdentifier("P1");
        product.setStatus(true);
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        when(repository.findByIdentifier("P1")).thenReturn(product);
        service.toggleStatus("P1");
        assertFalse(product.getStatus());
        verify(repository).save(product);
        when(repository.findByIdentifier("X")).thenReturn(null);
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.toggleStatus("X")
        );
        assertEquals("product not found", ex.getMessage());
        when(repository.findByStatusTrue()).thenReturn(List.of(product));
        when(mapper.map(product, ProductDto.class)).thenReturn(dto);
        List<ProductDto> result = service.findAllActive();
        assertEquals(1, result.size());
        when(repository.findByStatusTrue()).thenReturn(List.of());
        assertTrue(service.findAllActive().isEmpty());
    }
}
