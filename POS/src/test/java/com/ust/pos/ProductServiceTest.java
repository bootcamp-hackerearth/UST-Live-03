package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
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

        Product entity = new Product();
        Product saved = new Product();

        ProductDto mappedDto = new ProductDto();

        when(productRepository.findByIdentifier("P1")).thenReturn(null);
        when(modelMapper.map(dto, Product.class)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(saved);
        when(modelMapper.map(saved, ProductDto.class)).thenReturn(mappedDto);

        ProductDto result = productService.save(dto);

        assertTrue(result.isSuccess());
        assertEquals("Product saved successfully", result.getMessage());

        verify(productRepository).save(entity);
    }

    @Test
    void save_failure_duplicate() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product existing = new Product();
        existing.setDeleted(false);

        when(productRepository.findByIdentifier("P1")).thenReturn(existing);
        ProductDto result = productService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Product already exists", result.getMessage());

        verify(productRepository, never()).save(any());
    }

    @Test
    void update_success() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product existing = new Product();
        Product saved = new Product();

        ProductDto mappedDto = new ProductDto();

        when(productRepository.findByIdentifier("P1")).thenReturn(existing);
        doNothing().when(modelMapper).map(dto, existing);
        when(productRepository.save(existing)).thenReturn(saved);
        when(modelMapper.map(saved, ProductDto.class)).thenReturn(mappedDto);
        ProductDto result = productService.update(dto);

        assertTrue(result.isSuccess());
        assertEquals("Product updated successfully", result.getMessage());

        verify(productRepository).save(existing);
    }

    @Test
    void update_notFound() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        when(productRepository.findByIdentifier("P1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> productService.update(dto));
        verify(productRepository, never()).save(any());
    }

    @Test
    void update_deletedRecord_notFound() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product existing = new Product();
        existing.setDeleted(true);

        when(productRepository.findByIdentifier("P1")).thenReturn(existing);

        assertThrows(ResourceNotFoundException.class, () -> productService.update(dto));
        verify(productRepository, never()).save(any());
    }

    @Test
    void findByIdentifier_success() {

        Product product = new Product();

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        when(productRepository.findByIdentifier("P1")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        ProductDto result = productService.findByIdentifier("P1");

        assertNotNull(result);
        assertEquals("P1", result.getIdentifier());
    }

    @Test
    void findByIdentifier_notFound() {

        when(productRepository.findByIdentifier("P1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> productService.findByIdentifier("P1"));
    }

    @Test
    void findAll_success() {

        Product product = new Product();

        List<Product> products = List.of(product);
        Page<Product> page = new PageImpl<>(products);
        Pageable pageable = PageRequest.of(0, 5);

        List<ProductDto> dtoList = List.of(new ProductDto());

        when(productRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(products), ArgumentMatchers.<Type>any())).thenReturn(dtoList);
        WsDto<ProductDto> result = productService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void delete_success() {

        Product product = new Product();

        when(productRepository.findByIdentifier("P1")).thenReturn(product);
        productService.delete("P1");
        assertTrue(product.getDeleted());

        verify(productRepository).save(product);
    }

    @Test
    void delete_notFound() {

        when(productRepository.findByIdentifier("P1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> productService.delete("P1"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void findActiveProducts_success() {

        Product product = new Product();

        when(productRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(product));
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any())).thenReturn(List.of(new ProductDto()));

        List<ProductDto> result = productService.findActiveProducts();

        assertEquals(1, result.size());
    }

    @Test
    void toggleStatus_success() {

        Product product = new Product();
        product.setStatus(true);

        ProductDto dto = new ProductDto();

        when(productRepository.findByIdentifier("P1")).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        ProductDto result = productService.toggleStatus("P1");

        assertTrue(result.isSuccess());
        assertEquals("Status updated successfully", result.getMessage());

        assertFalse(product.getStatus());
    }

    @Test
    void toggleStatus_notFound() {

        when(productRepository.findByIdentifier("P1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> productService.toggleStatus("P1"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void searchProduct_success() {

        Product product = new Product();
        product.setIdentifier("P1");

        when(productRepository.searchActiveProducts("lap")).thenReturn(List.of(product));
        when(priceRepository.countActivePriceTypes("P1")).thenReturn(3L);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(new ProductDto());

        List<ProductDto> result = productService.searchProduct("lap");

        assertEquals(1, result.size());
    }

    @Test
    void searchProduct_filteredOut() {

        Product product = new Product();
        product.setIdentifier("P1");

        when(productRepository.searchActiveProducts("lap")).thenReturn(List.of(product));
        when(priceRepository.countActivePriceTypes("P1")).thenReturn(2L);
        List<ProductDto> result = productService.searchProduct("lap");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchProduct_emptyQuery() {

        List<ProductDto> result = productService.searchProduct("");
        assertTrue(result.isEmpty());
        verify(productRepository, never()).searchActiveProducts(anyString());
    }

    @Test
    void searchProduct_nullQuery() {

        List<ProductDto> result = productService.searchProduct(null);
        assertTrue(result.isEmpty());
        verify(productRepository, never()).searchActiveProducts(anyString());
    }
}