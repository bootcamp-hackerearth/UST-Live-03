package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Product;
import com.ust.pos.models.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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
    void saveTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        Product product = new Product();
        when(productRepository.findByIdentifier("P1")).thenReturn(null);
        when(modelMapper.map(dto, Product.class)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        ProductDto result = productService.save(dto);
        Assertions.assertTrue(result.isSuccess());
        Product duplicate = new Product();
        duplicate.setDeleted(false);
        when(productRepository.findByIdentifier("P2")).thenReturn(duplicate);
        ProductDto duplicateDto = new ProductDto();
        duplicateDto.setIdentifier("P2");
        result = productService.save(duplicateDto);
        Assertions.assertFalse(result.isSuccess());
        Product deleted = new Product();
        deleted.setDeleted(true);
        when(productRepository.findByIdentifier("P3")).thenReturn(deleted);
        ProductDto deletedDto = new ProductDto();
        deletedDto.setIdentifier("P3");
        result = productService.save(deletedDto);
        Assertions.assertFalse(result.isSuccess());
    }

    @Test
    void findByIdentifierUpdateAndDeleteTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        when(productRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        ProductDto result = productService.findByIdentifier("P1");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("P1", result.getIdentifier());
        result = productService.update(dto);
        Assertions.assertTrue(result.isSuccess());
        verify(modelMapper).map(dto, product);
        verify(productRepository).save(product);
        productService.delete("P1");
        Assertions.assertTrue(product.getDeleted());
        verify(productRepository, atLeastOnce()).save(product);
        when(productRepository.findByIdentifierAndDeletedFalse("P2")).thenReturn(null);
        ProductDto notFound = productService.update(new ProductDto() {{setIdentifier("P2");}});
        Assertions.assertFalse(notFound.isSuccess());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = new Product();
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
        when(productRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new ProductDto()));
        WsDto<ProductDto> result = productService.findAll(pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<Product> specification = mock(Specification.class);
        Page<Product> page = new PageImpl<>(List.of(new Product()), pageable, 1);
        when(productRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new ProductDto()));
        WsDto<ProductDto> result = productService.findAll(specification, pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        verify(productRepository).findAll(specification, pageable);
    }

    @Test
    void findAllActiveTest() {
        Product product = new Product();
        product.setIdentifier("P1");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");
        when(productRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(product));
        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        List<ProductDto> result = productService.findAllActive();
        Assertions.assertEquals(1, result.size());
        when(productRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of());
        Assertions.assertTrue(productService.findAllActive().isEmpty());
    }

    @Test
    void toggleStatusTest() {
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(new ProductDto());
        Product product = new Product();
        product.setIdentifier("P1");
        product.setStatus(true);
        when(productRepository.findByIdentifierAndDeletedFalse("P1")).thenReturn(product);
        productService.toggleStatus("P1");
        Assertions.assertFalse(product.getStatus());
        product.setStatus(false);
        when(productRepository.findByIdentifierAndDeletedFalse("P2")).thenReturn(product);
        productService.toggleStatus("P2");
        Assertions.assertTrue(product.getStatus());
        product.setStatus(null);
        when(productRepository.findByIdentifierAndDeletedFalse("P3")).thenReturn(product);
        productService.toggleStatus("P3");
        Assertions.assertTrue(product.getStatus());
        when(productRepository.findByIdentifierAndDeletedFalse("P4")).thenReturn(null);
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, () -> productService.toggleStatus("P4"));
        Assertions.assertEquals("Product not found with identifier: P4", ex.getMessage());
    }
}