package com.ust.pos;

import com.ust.pos.product.service.impl.ProductServiceImpl;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
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
import org.springframework.data.domain.Pageable;
import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void findAll_success() {

        Product product = new Product();
        ProductDto dto = new ProductDto();

        Page<Product> page = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(product)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<ProductDto> result =
                productService.findAll(Mockito.mock(Pageable.class));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void save_success() {

        ProductDto input = new ProductDto();
        input.setIdentifier("PROD01");

        Mockito.when(productRepository.findByIdentifier("PROD01"))
                .thenReturn(null);

        Product entity = new Product();

        Mockito.when(modelMapper.map(input, Product.class))
                .thenReturn(entity);

        Mockito.when(productRepository.save(entity))
                .thenReturn(entity);

        ProductDto result = productService.save(input);

        Assertions.assertEquals("PROD01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        ProductDto input = new ProductDto();
        input.setIdentifier("PROD01");

        Mockito.when(productRepository.findByIdentifier("PROD01"))
                .thenReturn(new Product());

        ProductDto result = productService.save(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Product product = new Product();
        product.setIdentifier("PROD01");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD01");

        Mockito.when(productRepository.findByIdentifier("PROD01"))
                .thenReturn(product);

        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto result =
                productService.findByIdentifier("PROD01");

        Assertions.assertEquals("PROD01", result.getIdentifier());
    }

    @Test
    void update_success() {

        ProductDto input = new ProductDto();
        input.setIdentifier("PROD01");

        Product existing = new Product();

        Mockito.when(productRepository.findByIdentifier("PROD01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        Mockito.when(productRepository.save(existing))
                .thenReturn(existing);

        ProductDto result = productService.update(input);

        Assertions.assertEquals("PROD01", result.getIdentifier());
    }

    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(productRepository).deleteByIdentifier("PROD01");

        productService.delete("PROD01");

        Mockito.verify(productRepository)
                .deleteByIdentifier("PROD01");
    }

    @Test
    void changeToggleStatus_success() {

        Product product = new Product();
        product.setStatus(false);

        ProductDto dto = new ProductDto();

        Mockito.when(productRepository.findByIdentifier("PROD01"))
                .thenReturn(product);

        Mockito.when(productRepository.save(product))
                .thenReturn(product);

        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto result =
                productService.changeToggleStatus("PROD01", true);

        Assertions.assertTrue(product.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void findActiveStatus_success() {

        Product active = new Product();
        active.setStatus(true);

        Product inactive = new Product();
        inactive.setStatus(false);

        Mockito.when(productRepository.findAll())
                .thenReturn(List.of(active, inactive));

        ProductDto dto = new ProductDto();

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(active)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<ProductDto> result = productService.findActiveStatus();

        Assertions.assertEquals(1, result.size());
    }
}