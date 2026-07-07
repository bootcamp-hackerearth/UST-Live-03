package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.data.jpa.domain.Specification;

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

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {

        product = new Product();
        product.setIdentifier("PROD001");
        product.setStatus(true);

        productDto = new ProductDto();
        productDto.setIdentifier("PROD001");
        productDto.setStatus(true);
    }

    @Test
    void save_WhenProductAlreadyExists_ShouldReturnFailure() {

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("PROD001")
        ).thenReturn(product);

        ProductDto response =
                productService.save(productDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Product with identifier - PROD001 already exists",
                response.getMessage()
        );

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any(Product.class));
    }

    @Test
    void save_ShouldSaveProductSuccessfully() {

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("PROD001")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(productDto, Product.class)
        ).thenReturn(product);

        ProductDto response =
                productService.save(productDto);

        Assertions.assertNotNull(response);

        Mockito.verify(productRepository)
                .save(product);
    }

    @Test
    void update_WhenProductNotFound_ShouldReturnFailure() {

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("PROD001")
        ).thenReturn(null);

        ProductDto response =
                productService.update(productDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Product with identifier - PROD001 is not found",
                response.getMessage()
        );

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any(Product.class));
    }

    @Test
    void update_ShouldUpdateProductSuccessfully() {

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("PROD001")
        ).thenReturn(product);

        Mockito.when(
                modelMapper.map(productDto, Product.class)
        ).thenReturn(product);

        ProductDto response =
                productService.update(productDto);

        Assertions.assertNotNull(response);

        Mockito.verify(productRepository)
                .save(product);
    }

    @Test
    void delete_ShouldSoftDeleteProduct() {

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("PROD001")
        ).thenReturn(product);

        productService.delete("PROD001");

        Assertions.assertTrue(product.isDeleted());

        Mockito.verify(productRepository)
                .save(product);
    }

    @Test
    void delete_WhenProductNotFound_ShouldDoNothing() {

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("PROD001")
        ).thenReturn(null);

        productService.delete("PROD001");

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any(Product.class));
    }

    @Test
    void findAll_ShouldReturnProductDtoList() {

        List<Product> products = List.of(product);
        List<ProductDto> productDtos = List.of(productDto);

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();

        Mockito.when(
                productRepository.findByDeletedFalse()
        ).thenReturn(products);

        Mockito.when(
                modelMapper.map(products, listType)
        ).thenReturn(productDtos);

        List<ProductDto> response =
                productService.findAll();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());

        Mockito.verify(productRepository)
                .findByDeletedFalse();

        Mockito.verify(modelMapper)
                .map(products, listType);
    }

    @Test
    void findByIdentifier_ShouldReturnProductDto() {

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("PROD001")
        ).thenReturn(product);

        Mockito.when(
                modelMapper.map(product, ProductDto.class)
        ).thenReturn(productDto);

        ProductDto response =
                productService.findByIdentifier("PROD001");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "PROD001",
                response.getIdentifier()
        );

        Mockito.verify(productRepository)
                .findByIdentifierAndDeletedFalse("PROD001");
    }

    @Test
    void findAll_WithSearch_ShouldReturnProductDtos() {

        String search = "PROD";
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> productPage =
                new PageImpl<>(List.of(product));

        Mockito.when(
                productRepository.findAll(
                        Mockito.<Specification<Product>>any(),
                        Mockito.eq(pageable)
                )
        ).thenReturn(productPage);

        Mockito.when(
                modelMapper.map(
                        product,
                        ProductDto.class
                )
        ).thenReturn(productDto);

        Page<ProductDto> response =
                productService.findAll(
                        search,
                        pageable
                );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Mockito.verify(productRepository)
                .findAll(
                        Mockito.<Specification<Product>>any(),
                        Mockito.eq(pageable)
                );
    }

    @Test
    void findAll_WithoutSearch_ShouldReturnProductDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> productPage =
                new PageImpl<>(List.of(product));

        Mockito.when(
                productRepository.findByDeletedFalse(pageable)
        ).thenReturn(productPage);

        Mockito.when(
                modelMapper.map(
                        product,
                        ProductDto.class
                )
        ).thenReturn(productDto);

        Page<ProductDto> response =
                productService.findAll(
                        null,
                        pageable
                );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Mockito.verify(productRepository)
                .findByDeletedFalse(pageable);

        Mockito.verify(productRepository, Mockito.never())
                .findAll(
                        Mockito.<Specification<Product>>any(),
                        Mockito.any(Pageable.class)
                );
    }

    @Test
    void toggleStatus_ShouldToggleProductStatus() {

        product.setStatus(true);

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("PROD001")
        ).thenReturn(product);

        productService.toggleStatus("PROD001");

        Assertions.assertFalse(product.getStatus());

        Mockito.verify(productRepository)
                .save(product);
    }

    @Test
    void toggleStatus_WhenProductNotFound_ShouldDoNothing() {

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("PROD001")
        ).thenReturn(null);

        productService.toggleStatus("PROD001");

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any(Product.class));
    }
}