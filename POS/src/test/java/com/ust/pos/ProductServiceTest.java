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

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTestSuccess() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Product product = new Product();

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Product.class))
                .thenReturn(product);

        ProductDto response = productService.save(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(productRepository).save(product);
    }

    @Test
    void saveTestFailure() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Product existing = new Product();

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(existing);

        ProductDto response = productService.save(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestSuccess() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Product existing = new Product();

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(existing);

        Mockito.when(productRepository.save(any(Product.class)))
                .thenReturn(existing);

        ProductDto response = productService.update(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(productRepository).save(existing);
    }

    @Test
    void updateTestFailure() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        ProductDto response = productService.update(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {

        productService.delete("Admin");

        verify(productRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Product product = new Product();
        product.setIdentifier("Admin");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(product);

        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto response = productService.findByIdentifier("Admin");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        ProductDto response = productService.findByIdentifier("Admin");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Product> products = List.of(new Product());
        Page<Product> page = new PageImpl<>(products);
        List<ProductDto> dtos = List.of(new ProductDto());

        Mockito.when(productRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                Mockito.any(),
                Mockito.any(Type.class)
        )).thenReturn(dtos);

        WsDto<ProductDto> response = productService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getContent());
        Assertions.assertEquals(1, response.getContent().size());

        Mockito.verify(productRepository).findAll(pageable);
    }
}