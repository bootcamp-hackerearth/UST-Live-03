package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.PriceRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void save_success() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        dto.setCategories(List.of("Electronics", "Mobile"));

        Product entity = new Product();
        entity.setCategories(List.of("Electronics", "Mobile"));

        Product saved = new Product();
        saved.setCategories(List.of("Electronics", "Mobile"));

        ProductDto responseDto = new ProductDto();
        responseDto.setSuccess(true);
        responseDto.setMessage("Product saved successfully");
        responseDto.setCategories(List.of("Electronics", "Mobile"));

        when(productRepository.findByIdentifier("P1")).thenReturn(null);
        when(modelMapper.map(any(ProductDto.class), eq(Product.class))).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(saved);
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(responseDto);

        ProductDto response = productService.save(dto);

        assertTrue(response.isSuccess());
        assertEquals("Product saved successfully", response.getMessage());
        assertEquals(2, response.getCategories().size());
        assertEquals("Electronics", response.getCategories().get(0));

        verify(productRepository).save(entity);
    }

    @Test
    void save_failure_productAlreadyExists() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        when(productRepository.findByIdentifier("P1")).thenReturn(new Product());

        ProductDto response = productService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Product already exists", response.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void save_failure_emptyIdentifier() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("   ");

        ProductDto response = productService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Identifier required", response.getMessage());
        verifyNoInteractions(productRepository);
    }

    @Test
    void save_failure_nullIdentifier() {
        ProductDto dto = new ProductDto();
        ProductDto response = productService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Identifier required", response.getMessage());
        verifyNoInteractions(productRepository);
    }

    @Test
    void update_success() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product existing = new Product();
        existing.setIdentifier("P1");

        Product updated = new Product();

        ProductDto responseDto = new ProductDto();
        responseDto.setSuccess(true);
        responseDto.setMessage("Product updated successfully");

        when(productRepository.findByIdentifier("P1"))
                .thenReturn(existing);

        doNothing().when(modelMapper)
                .map(any(ProductDto.class), any(Product.class));

        when(productRepository.save(existing))
                .thenReturn(updated);

        when(modelMapper.map(any(Product.class), eq(ProductDto.class)))
                .thenReturn(responseDto);

        ProductDto response = productService.update(dto);

        assertTrue(response.isSuccess());
        assertEquals("Product updated successfully", response.getMessage());

        verify(productRepository).save(existing);
    }

    @Test
    void update_failure_productNotFound() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        when(productRepository.findByIdentifier("P1")).thenReturn(null);

        ProductDto response = productService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals("Product not found", response.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void findByIdentifier_success() {
        Product product = new Product();

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        dto.setSuccess(true);
        dto.setCategories(List.of("Electronics", "Accessories"));

        when(productRepository.findByIdentifier("P1")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        ProductDto response = productService.findByIdentifier("P1");

        assertTrue(response.isSuccess());
        assertEquals("P1", response.getIdentifier());
        assertEquals(2, response.getCategories().size());
        assertEquals("Accessories", response.getCategories().get(1));
    }

    @Test
    void findByIdentifier_notFound() {
        when(productRepository.findByIdentifier("P1")).thenReturn(null);

        ProductDto response = productService.findByIdentifier("P1");

        assertFalse(response.isSuccess());
        assertEquals("Product not found", response.getMessage());
    }

    @Test
    void findAll_returnsPaginatedResult() {
        List<Product> productList = List.of(new Product());
        Page<Product> page = new PageImpl<>(productList);

        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(new ProductDto()));

        WsDto<ProductDto> result = productService.findAll(PageRequest.of(0, 5));

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1L, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(5, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findAll_emptyPage() {
        Page<Product> emptyPage = new PageImpl<>(List.of());

        when(productRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of());

        WsDto<ProductDto> result = productService.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertTrue(result.getDtoList().isEmpty());
        assertEquals(0L, result.getTotalRecords());
    }

    @Test
    void findActiveProducts_returnsOnlyActiveOnes() {
        ProductDto active = new ProductDto();
        active.setStatus(true);

        ProductDto inactive = new ProductDto();
        inactive.setStatus(false);

        when(productRepository.findAll()).thenReturn(List.of(new Product(), new Product()));
        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(active, inactive));

        List<ProductDto> result = productService.findActiveProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getStatus());
    }

    @Test
    void findActiveProducts_noActiveProducts() {
        ProductDto inactive = new ProductDto();
        inactive.setStatus(false);

        when(productRepository.findAll()).thenReturn(List.of(new Product()));
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(inactive));

        List<ProductDto> result = productService.findActiveProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findActiveProducts_emptyRepository() {
        when(productRepository.findAll()).thenReturn(List.of());
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of());

        List<ProductDto> result = productService.findActiveProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void delete_callsBothRepositories() {
        doNothing().when(priceRepository).deleteByProductId("P1");
        doNothing().when(productRepository).deleteByIdentifier("P1");

        productService.delete("P1");

        verify(priceRepository).deleteByProductId("P1");
        verify(productRepository).deleteByIdentifier("P1");
    }

    @Test
    void toggleStatus_fromTrueToFalse() {
        Product product = new Product();
        product.setStatus(true);

        ProductDto responseDto = new ProductDto();
        responseDto.setSuccess(true);
        responseDto.setMessage("Status updated successfully");

        when(productRepository.findByIdentifier("P1")).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(responseDto);

        ProductDto response = productService.toggleStatus("P1");

        assertTrue(response.isSuccess());
        assertEquals("Status updated successfully", response.getMessage());
        assertFalse(product.getStatus());
        verify(productRepository).save(product);
    }

    @Test
    void toggleStatus_fromFalseToTrue() {
        Product product = new Product();
        product.setStatus(false);

        ProductDto responseDto = new ProductDto();
        responseDto.setSuccess(true);
        responseDto.setMessage("Status updated successfully");

        when(productRepository.findByIdentifier("P1")).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(responseDto);

        ProductDto response = productService.toggleStatus("P1");

        assertTrue(response.isSuccess());
        assertEquals("Status updated successfully", response.getMessage());
        assertTrue(product.getStatus());
    }

    @Test
    void toggleStatus_fromNullToTrue() {
        Product product = new Product();
        product.setStatus(null);

        ProductDto responseDto = new ProductDto();
        responseDto.setSuccess(true);
        responseDto.setMessage("Status updated successfully");

        when(productRepository.findByIdentifier("P1")).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(responseDto);

        ProductDto response = productService.toggleStatus("P1");

        assertTrue(response.isSuccess());
        assertTrue(product.getStatus());
    }

    @Test
    void toggleStatus_failure_productNotFound() {
        when(productRepository.findByIdentifier("P1")).thenReturn(null);

        ProductDto response = productService.toggleStatus("P1");

        assertFalse(response.isSuccess());
        assertEquals("Product not found", response.getMessage());
        verify(productRepository, never()).save(any());
    }
}