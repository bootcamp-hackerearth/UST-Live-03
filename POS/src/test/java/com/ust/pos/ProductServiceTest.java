package com.ust.pos;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
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
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveProductSuccess() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product product = new Product();

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(dto, Product.class)
        ).thenReturn(product);

        ProductDto response = productService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(productRepository).save(product);
        Assertions.assertFalse(product.getDeleted());
    }

    @Test
    void saveProductAlreadyExists() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(new Product());

        ProductDto response = productService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Product with identifier - P1 already exists",
                response.getMessage()
        );

        Mockito.verify(
                productRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void updateProductSuccess() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product existingProduct = new Product();
        Product mappedProduct = new Product();

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(existingProduct);

        Mockito.when(
                modelMapper.map(dto, Product.class)
        ).thenReturn(mappedProduct);

        ProductDto response = productService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(productRepository)
                .save(mappedProduct);
    }

    @Test
    void updateProductNotFound() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(null);

        ProductDto response = productService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Product with identifier - P1 is not found",
                response.getMessage()
        );

        Mockito.verify(
                productRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void deleteProductTest() {

        Product product = new Product();
        product.setDeleted(false);

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(product);

        productService.delete("P1");

        Assertions.assertTrue(product.getDeleted());

        Mockito.verify(productRepository)
                .save(product);
    }

    @Test
    void deleteProductNotFoundTest() {

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(null);

        productService.delete("P1");

        Mockito.verify(
                productRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void findAllProductsTest() {

        List<Product> products =
                List.of(new Product(), new Product());

        List<ProductDto> dtoList =
                List.of(new ProductDto(), new ProductDto());

        Mockito.when(
                productRepository.findByDeletedFalse()
        ).thenReturn(products);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(products),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<ProductDto> response =
                productService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findProductByIdentifierTest() {

        Product product = new Product();
        product.setIdentifier("P1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Mockito.when(
                productRepository.findByIdentifierAndDeletedFalse("P1")
        ).thenReturn(product);

        Mockito.when(
                modelMapper.map(product, ProductDto.class)
        ).thenReturn(dto);

        ProductDto response =
                productService.findByIdentifier("P1");

        Assertions.assertEquals(
                "P1",
                response.getIdentifier()
        );
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Product> products =
                List.of(new Product());

        Page<Product> page =
                new PageImpl<>(products, pageable, 1);

        List<ProductDto> dtoList =
                List.of(new ProductDto());

        Type listType =
                new TypeToken<List<ProductDto>>() {}.getType();

        Mockito.when(
                productRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(products, listType)
        ).thenReturn(dtoList);

        WsDto<ProductDto> response =
                productService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );
        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void findAllSearchTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Product product = new Product();
        product.setIdentifier("P1");

        Example<Product> example = Example.of(
                product,
                ExampleMatcher.matching()
                        .withMatcher(
                                "identifier",
                                ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase()
                        )
        );

        Page<Product> page =
                new PageImpl<>(List.of(product));

        Mockito.when(
                productRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(product, ProductDto.class)
        ).thenReturn(new ProductDto());

        Page<ProductDto> response =
                productService.findAll(example, pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void findAllWithoutSearchTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Product product = new Product();

        Example<Product> example = Example.of(new Product());

        Page<Product> page =
                new PageImpl<>(List.of(product));

        Mockito.when(
                productRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(product, ProductDto.class)
        ).thenReturn(new ProductDto());

        Page<ProductDto> response =
                productService.findAll(example, pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void listOfCategoriesTest() {

        List<Category> categories =
                List.of(new Category(), new Category());

        List<ProductDto> dtoList =
                List.of(new ProductDto(), new ProductDto());

        Mockito.when(
                categoryRepository
                        .findBySuperCategoryIsNotAndDeletedFalse("")
        ).thenReturn(categories);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(categories),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<ProductDto> response =
                productService.listOfCategories();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.size());
    }
}