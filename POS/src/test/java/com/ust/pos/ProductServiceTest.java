package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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
    void saveTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD 106");
        Product product = new Product();
        Mockito.when(productRepository.findByIdentifier("PROD 106")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Product.class)).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        ProductDto response = productService.save(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("PROD 106", response.getIdentifier());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void saveFailure_existingActive() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD 106");
        Product existing = new Product();
        existing.setDeleted(false);
        Mockito.when(productRepository.findByIdentifier("PROD 106")).thenReturn(existing);

        ProductDto response = productService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveFailure_softDeleted() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD 106");
        Product existing = new Product();
        existing.setDeleted(true);
        Mockito.when(productRepository.findByIdentifier("PROD 106")).thenReturn(existing);

        ProductDto response = productService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("PROD 106");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD 106");
        Mockito.when(productRepository.findByIdentifier("PROD 106")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        ProductDto response = productService.findByIdentifier("PROD 106");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("PROD 106", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD 106");
        Product existing = new Product();
        Mockito.when(productRepository.findByIdentifier("PROD 106")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(productRepository.save(existing)).thenReturn(existing);

        ProductDto response = productService.update(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("PROD 106", response.getIdentifier());
        Mockito.verify(productRepository).save(existing);
    }

    @Test
    void updateFailure() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD 106");
        Mockito.when(productRepository.findByIdentifier("PROD 106")).thenReturn(null);
        ProductDto response = productService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Product product = new Product();
        product.setDeleted(false);
        Mockito.when(productRepository.findByIdentifier("PROD 106")).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        productService.delete("PROD 106");
        Mockito.verify(productRepository).findByIdentifier("PROD 106");
        Mockito.verify(productRepository).save(product);
        Assertions.assertTrue(product.isDeleted());
    }

    @Test
    void findAllTest() {
        Product product = new Product();
        product.setIdentifier("PROD 106");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD 106");
        List<Product> products = List.of(product);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> page = new PageImpl<>(products, pageable, 1);
        Mockito.when(productRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of(dto));

        WsDto<ProductDto> response = productService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("PROD 106", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findAllWithSpecificationTest() {
        Product product = new Product();
        product.setIdentifier("PROD 106");
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD 106");
        List<Product> products = List.of(product);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(products, pageable, 1);
        @SuppressWarnings("unchecked")
        Specification<Product> specification = Mockito.mock(Specification.class);
        Mockito.when(productRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of(dto));

        WsDto<ProductDto> response = productService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("PROD 106", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());

        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void toggleStatusSuccessTest() {
        Product product = new Product();
        product.setIdentifier("Admin");
        product.setStatus(false);
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");
        dto.setStatus(true);
        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(product);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        ProductDto response = productService.toggleStatus("Admin", true);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
        Mockito.verify(productRepository).save(product);
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(productRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(Mockito.isNull(), Mockito.eq(ProductDto.class))).thenReturn(null);
        ProductDto response = productService.toggleStatus("Admin", true);
        Assertions.assertNull(response);
        Mockito.verify(productRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveProductsTest() {
        Product product = new Product();
        product.setIdentifier("PROD_01");
        product.setStatus(true);
        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD_01");
        dto.setStatus(true);

        List<Product> products = List.of(product);
        Mockito.when(productRepository.findByStatusTrue()).thenReturn(products);
        Mockito.when(modelMapper.map(Mockito.eq(products), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of(dto));

        List<ProductDto> response = productService.findActiveProducts();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("PROD_01", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
        Mockito.verify(productRepository).findByStatusTrue();
    }
}