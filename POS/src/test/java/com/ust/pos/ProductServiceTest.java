package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
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
    void findByIdentifierTest() {
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
    void saveTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Product product = new Product();
        product.setIdentifier("P1");

        when(repository.findByIdentifier("P1")).thenReturn(null);
        when(mapper.map(dto, Product.class)).thenReturn(product);

        ProductDto result = service.save(dto);
        verify(repository).save(product);
        assertEquals("P1", result.getIdentifier());

        when(repository.findByIdentifier("P1")).thenReturn(product);

        ProductDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void updateTest() {
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

        ProductDto failure = service.update(dto);
        assertFalse(failure.isSuccess());
        assertTrue(failure.getMessage().contains("not found"));
    }

    @Test
    void deleteTest() {
        service.delete("P1");
        verify(repository).deleteByIdentifier("P1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<ProductDto>>(){}.getType();

        Product product = new Product();
        product.setIdentifier("P1");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(page.getContent(), type)).thenReturn(List.of(dto));

        WsDto<ProductDto> result = service.findAll(pageable);
        assertEquals(1, result.getDtoList().size());

        Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(emptyPage);
        when(mapper.map(emptyPage.getContent(), type)).thenReturn(List.of());

        WsDto<ProductDto> empty = service.findAll(pageable);
        assertTrue(empty.getDtoList().isEmpty());
    }

    @Test
    void toggleAndFindAllActiveTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        product.setStatus(true);

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        when(repository.findByIdentifier("P1")).thenReturn(product);

        when(repository.save(any(Product.class))).thenReturn(product);

        when(mapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(dto);

        ProductDto toggled = service.toggleStatus("P1");

        assertFalse(product.getStatus());
        verify(repository).save(product);
        assertNotNull(toggled);

        when(repository.findByIdentifier("X")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.toggleStatus("X"));

        assertTrue(ex.getMessage().contains("Product not found"));

        when(repository.findByStatusTrue()).thenReturn(List.of(product));

        List<ProductDto> result = service.findAllActive();
        assertEquals(1, result.size());

        when(repository.findByStatusTrue()).thenReturn(List.of());

        List<ProductDto> empty = service.findAllActive();
        assertTrue(empty.isEmpty());
    }
}