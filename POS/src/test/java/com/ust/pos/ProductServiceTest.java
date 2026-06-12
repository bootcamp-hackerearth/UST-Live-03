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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
    void findByIdentifier_Found() {

        Product product = new Product();
        product.setIdentifier("PROD001");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(product);

        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto result =
                productService.findByIdentifier("PROD001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("PROD001",
                result.getIdentifier());
    }

    @Test
    void save_NewProduct() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        Product product = new Product();

        when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(null);

        when(modelMapper.map(dto, Product.class))
                .thenReturn(product);

        when(productRepository.save(product))
                .thenReturn(product);

        ProductDto result = productService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("PROD001",
                result.getIdentifier());

        verify(productRepository).save(product);
    }

    @Test
    void save_ProductAlreadyExists() {

        Product existing = new Product();

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(existing);

        ProductDto result = productService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(productRepository, never()).save(any());
    }

    @Test
    void update_ProductExists() {

        Product existing = new Product();

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(existing);

        when(productRepository.save(existing))
                .thenReturn(existing);

        ProductDto result = productService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("PROD001",
                result.getIdentifier());

        verify(modelMapper).map(dto, existing);
        verify(productRepository).save(existing);
    }

    @Test
    void update_ProductNotFound() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(null);

        ProductDto result = productService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        doNothing().when(productRepository)
                .deleteByIdentifier("PROD001");

        productService.delete("PROD001");

        verify(productRepository)
                .deleteByIdentifier("PROD001");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Product product1 = new Product();
        Product product2 = new Product();

        Page<Product> page = new PageImpl<>(
                List.of(product1, product2),
                pageable,
                2
        );

        ProductDto dto1 = new ProductDto();
        ProductDto dto2 = new ProductDto();

        when(productRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(product1, ProductDto.class))
                .thenReturn(dto1);

        when(modelMapper.map(product2, ProductDto.class))
                .thenReturn(dto2);

        WsDto<ProductDto> result =
                productService.findAll(pageable);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                2,
                result.getContent().size());

        Assertions.assertEquals(
                dto1,
                result.getContent().get(0));

        Assertions.assertEquals(
                dto2,
                result.getContent().get(1));

        Assertions.assertEquals(
                0,
                result.getPage());

        Assertions.assertEquals(
                10,
                result.getSizePerPage());

        Assertions.assertEquals(
                1,
                result.getTotalPages());

        Assertions.assertEquals(
                2,
                result.getTotalRecords());

        verify(productRepository)
                .findAll(pageable);

        verify(modelMapper)
                .map(product1, ProductDto.class);

        verify(modelMapper)
                .map(product2, ProductDto.class);
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> page =
                new PageImpl<>(List.of());

        when(productRepository.findAll(pageable))
                .thenReturn(page);

        WsDto<ProductDto> result =
                productService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(
                result.getContent().isEmpty());

        Assertions.assertEquals(0,
                result.getTotalRecords());

        verify(productRepository)
                .findAll(pageable);

        verify(modelMapper, never())
                .map(any(Product.class),
                        eq(ProductDto.class));
    }
}