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
    void saveTest() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");

        Product product = new Product();

        Mockito.when(productRepository.findByIdentifier("Lays Chili"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(productDto, Product.class))
                .thenReturn(product);

        Mockito.when(productRepository.save(product))
                .thenReturn(product);

        ProductDto response = productService.save(productDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Product created successfully",
                response.getMessage()
        );

        Mockito.verify(productRepository).save(product);
    }

    @Test
    void saveDuplicateTest() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");

        Product existingProduct = new Product();
        existingProduct.setDeleted(false);

        Mockito.when(productRepository.findByIdentifier("Lays Chili"))
                .thenReturn(existingProduct);

        ProductDto response = productService.save(productDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Product with identifier - Lays Chili already exists",
                response.getMessage()
        );

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void saveSoftDeletedProductTest() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");

        Product existingProduct = new Product();
        existingProduct.setDeleted(true);

        Mockito.when(productRepository.findByIdentifier("Lays Chili"))
                .thenReturn(existingProduct);

        ProductDto response = productService.save(productDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Product with identifier Lays Chili has been soft deleted. (Rollback by changing status)",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifierTest() {

        Product product = new Product();
        product.setIdentifier("Lays Chili");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");

        Mockito.when(productRepository.findByIdentifier("Lays Chili"))
                .thenReturn(product);

        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(productDto);

        ProductDto response =
                productService.findByIdentifier("Lays Chili");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "Lays Chili",
                response.getIdentifier()
        );
    }

    @Test
    void updateTest() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("Lays Chili");

        Mockito.when(productRepository.findByIdentifier("Lays Chili"))
                .thenReturn(existingProduct);

        Mockito.when(productRepository.save(existingProduct))
                .thenReturn(existingProduct);

        ProductDto response =
                productService.update(productDto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Product updated successfully",
                response.getMessage()
        );

        Mockito.verify(productRepository)
                .save(existingProduct);
    }

    @Test
    void updateNotFoundTest() {

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("Lays Chili");

        Mockito.when(productRepository.findByIdentifier("Lays Chili"))
                .thenReturn(null);

        ProductDto response =
                productService.update(productDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Product with identifier - Lays Chili not found",
                response.getMessage()
        );
    }

    @Test
    void deleteTest() {

        Product product = new Product();
        product.setIdentifier("Lays Chili");
        product.setDeleted(false);

        Mockito.when(productRepository.findByIdentifier("Lays Chili"))
                .thenReturn(product);

        productService.deleteByIdentifier("Lays Chili");

        Mockito.verify(productRepository)
                .save(product);
    }

    @Test
    void findAllWithPageableTest() {

        Product product = new Product();
        product.setIdentifier("Lays Chili");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("Lays Chili");

        List<Product> products = List.of(product);
        List<ProductDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Product> productPage =
                new PageImpl<>(products);

        Mockito.when(
                productRepository.findByDeletedFalse(pageable)
        ).thenReturn(productPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(products),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<ProductDto> response =
                productService.findAll(pageable);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Lays Chili",
                response.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void findAllWithoutPageableTest() {

        Product product = new Product();
        product.setIdentifier("Lays Chili");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("Lays Chili");

        List<Product> products = List.of(product);
        List<ProductDto> dtos = List.of(dto);

        Page<Product> productPage =
                new PageImpl<>(products);

        Mockito.when(
                productRepository.findByDeletedFalse(null)
        ).thenReturn(productPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(productPage),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<ProductDto> response =
                productService.findAll(null);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getDtoList());

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Lays Chili",
                response.getDtoList().get(0).getIdentifier()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void findAllWithSpecificationTest() {

        Product product = new Product();
        product.setIdentifier("Lays Chili");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("Lays Chili");

        List<Product> products = List.of(product);
        List<ProductDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Product> page = new PageImpl<>(
                products,
                pageable,
                1
        );

        Specification<Product> specification =
                Mockito.mock(Specification.class);

        Mockito.when(
                productRepository.findAll(specification, pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(products),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<ProductDto> response =
                productService.findAll(specification, pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Lays Chili",
                response.getDtoList().get(0).getIdentifier()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );

        Assertions.assertEquals(
                1,
                response.getTotalPages()
        );

        Assertions.assertEquals(
                0,
                response.getPage()
        );

        Assertions.assertEquals(
                5,
                response.getSizePerPage()
        );

        Mockito.verify(productRepository)
                .findAll(specification, pageable);
    }

}