package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void save_success() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        dto.setCategories(List.of("C1"));
        Product entity = new Product();
        Product saved = new Product();
        ProductDto mapped = new ProductDto();
        when(productRepository.findByIdentifier("P1")).thenReturn(null);
        when(modelMapper.map(dto, Product.class)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(saved);
        when(modelMapper.map(saved, ProductDto.class)).thenReturn(mapped);
        ProductDto response = productService.save(dto);
        assertTrue(response.isSuccess());
        assertEquals("Product saved successfully", response.getMessage());
    }

    @Test
    void save_failure_exists() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        when(productRepository.findByIdentifier("P1")).thenReturn(new Product());
        ProductDto response = productService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals("Product already exists", response.getMessage());
    }

    @Test
    void save_failure_empty() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier(" ");
        ProductDto response = productService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals("Identifier required", response.getMessage());
    }

    @Test
    void save_failure_no_categories() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        dto.setCategories(null);
        when(productRepository.findByIdentifier("P1")).thenReturn(null);
        ProductDto response = productService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals("Please select at least one category", response.getMessage());
    }

    @Test
    void save_failure_empty_categories() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        dto.setCategories(List.of());
        when(productRepository.findByIdentifier("P1")).thenReturn(null);
        ProductDto response = productService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals("Please select at least one category", response.getMessage());
    }

    @Test
    void find_success() {
        Product product = new Product();
        ProductDto dto = new ProductDto();
        when(productRepository.findByIdentifier("P1")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        ProductDto response = productService.findByIdentifier("P1");
        assertTrue(response.isSuccess());
    }

    @Test
    void find_not_found() {
        when(productRepository.findByIdentifier("P1")).thenReturn(null);
        ProductDto response = productService.findByIdentifier("P1");
        assertFalse(response.isSuccess());
        assertEquals("Product not found", response.getMessage());
    }

    @Test
    void update_success() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        dto.setCategories(List.of("C1"));
        Product existing = new Product();
        Product saved = new Product();
        ProductDto mapped = new ProductDto();
        when(productRepository.findByIdentifier("P1")).thenReturn(existing);
        doNothing().when(modelMapper).map(any(ProductDto.class), any(Product.class));
        when(productRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, ProductDto.class)).thenReturn(mapped);
        ProductDto response = productService.update(dto);
        assertTrue(response.isSuccess());
        assertEquals("Product updated successfully", response.getMessage());
    }

    @Test
    void update_failure() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        dto.setCategories(List.of("C1"));
        when(productRepository.findByIdentifier("P1")).thenReturn(null);
        ProductDto response = productService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals("Product not found", response.getMessage());
    }

    @Test
    void update_failure_no_categories() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        dto.setCategories(null);
        ProductDto response = productService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals("Please select at least one category", response.getMessage());
    }

    @Test
    void update_failure_empty_categories() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        dto.setCategories(List.of());
        ProductDto response = productService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals("Please select at least one category", response.getMessage());
    }

    @Test
    void delete_test() {
        productService.delete("P1");
        verify(productRepository).deleteByIdentifier("P1");
    }

    @Test
    void find_all_test() {
        List<Product> list = List.of(new Product());
        Page<Product> page = new PageImpl<>(list);
        List<ProductDto> mappedList = List.of(new ProductDto());
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(eq(list), any(Type.class))).thenReturn(mappedList);
        List<ProductDto> result = productService.findAll(Pageable.unpaged());
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void active_products_test() {
        List<Product> list = List.of(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(list);
        ProductDto active = new ProductDto();
        active.setStatus(true);
        ProductDto inactive = new ProductDto();
        inactive.setStatus(false);
        when(modelMapper.map(eq(list), any(Type.class))).thenReturn(List.of(active, inactive));
        List<ProductDto> result = productService.findActiveProducts();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getStatus());
    }

    @Test
    void toggle_success() {
        Product product = new Product();
        product.setStatus(true);
        ProductDto mapped = new ProductDto();
        when(productRepository.findByIdentifier("P1")).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(mapped);
        ProductDto response = productService.toggleStatus("P1");
        assertTrue(response.isSuccess());
        assertEquals("Status updated successfully", response.getMessage());
    }

    @Test
    void toggle_failure() {
        when(productRepository.findByIdentifier("P1")).thenReturn(null);
        ProductDto response = productService.toggleStatus("P1");
        assertFalse(response.isSuccess());
        assertEquals("Product not found", response.getMessage());
    }
}