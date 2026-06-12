package com.ust.pos;

import com.ust.pos.dto.ProductDto;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
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
    void saveTest_Success() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Product entity = new Product();

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Product.class))
                .thenReturn(entity);

        Mockito.when(productRepository.save(entity))
                .thenReturn(entity);

        ProductDto response = productService.save(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(productRepository).save(entity);
    }

    @Test
    void saveTest_Failure_WhenExists() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(new Product());

        ProductDto response = productService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest_Success() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Product existing = new Product();
        Product mapped = new Product();

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(existing);

        Mockito.when(modelMapper.map(dto, Product.class))
                .thenReturn(mapped);

        Mockito.when(productRepository.save(mapped))
                .thenReturn(mapped);

        ProductDto response = productService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(productRepository).save(mapped);
    }

    @Test
    void updateTest_Failure_WhenNotFound() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        ProductDto response = productService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("Admin");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("Admin");

        Mockito.when(productRepository.findByIdentifier("Admin"))
                .thenReturn(product);

        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto response = productService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findByIdentifierWithTrimTest() {
        Product product = new Product();
        product.setIdentifier("PROD001");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        Mockito.when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(product);

        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto response =
                productService.findByIdentifier(" PROD001 ");

        Assertions.assertEquals("PROD001",
                response.getIdentifier());
    }

    @Test
    void findAllTest() {
        List<Product> entities = List.of(new Product());
        List<ProductDto> dtos = List.of(new ProductDto());

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();

        Mockito.when(productRepository.findAll())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);

        List<ProductDto> response = productService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllWithPaginationAndSearchTest() {

        Pageable pageable = PageRequest.of(0, 5);

        Product product = new Product();
        product.setIdentifier("PROD001");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        Page<Product> productPage =
                new PageImpl<>(List.of(product), pageable, 1);

        Mockito.when(productRepository
                        .findByIdentifierContainingIgnoreCase("PROD", pageable))
                .thenReturn(productPage);

        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        Page<ProductDto> response =
                productService.findAll(pageable, "PROD");

        Assertions.assertEquals(1, response.getTotalElements());
        Assertions.assertEquals("PROD001",
                response.getContent().get(0).getIdentifier());
    }

    @Test
    void findAllWithPaginationWithoutSearchTest() {

        Pageable pageable = PageRequest.of(0, 5);

        Product product = new Product();
        product.setIdentifier("PROD001");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD001");

        Page<Product> productPage =
                new PageImpl<>(List.of(product), pageable, 1);

        Mockito.when(productRepository.findAll(pageable))
                .thenReturn(productPage);

        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        Page<ProductDto> response =
                productService.findAll(pageable, "");

        Assertions.assertEquals(1, response.getTotalElements());
        Assertions.assertEquals("PROD001",
                response.getContent().get(0).getIdentifier());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing()
                .when(productRepository)
                .deleteByIdentifier("Admin");

        productService.delete("Admin");

        Mockito.verify(productRepository)
                .deleteByIdentifier("Admin");
    }

    @Test
    void toggleStatusTest_EnableToDisable() {

        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setStatus(true);

        Mockito.when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(product);

        productService.toggleStatus("PROD001");

        Assertions.assertFalse(product.isStatus());

        Mockito.verify(productRepository)
                .save(product);
    }

    @Test
    void toggleStatusTest_DisableToEnable() {

        Product product = new Product();
        product.setIdentifier("PROD001");
        product.setStatus(false);

        Mockito.when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(product);

        productService.toggleStatus("PROD001");

        Assertions.assertTrue(product.isStatus());

        Mockito.verify(productRepository)
                .save(product);
    }

    @Test
    void toggleStatusTest_ProductNotFound() {

        Mockito.when(productRepository.findByIdentifier("PROD001"))
                .thenReturn(null);

        productService.toggleStatus("PROD001");

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }
}