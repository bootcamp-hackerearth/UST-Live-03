package com.ust.pos;

import com.ust.pos.dto.ProductDto;
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
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveProductSuccess() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product product = new Product();

        Mockito.when(productRepository.findByIdentifier("P1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Product.class))
                .thenReturn(product);

        ProductDto response = productService.save(dto);

        Assertions.assertNull(response.getMessage());
        Mockito.verify(productRepository).save(product);
    }
    @Test
    void findAll_WithPagination_ShouldReturnProductDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Product> products = List.of(new Product());
        Page<Product> page = new PageImpl<>(products);

        List<ProductDto> productDtos = List.of(new ProductDto());

        Type listType = new TypeToken<List<ProductDto>>() {}.getType();

        Mockito.when(productRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(products, listType))
                .thenReturn(productDtos);

        List<ProductDto> response = productService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(productRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(products, listType);
    }

    @Test
    void saveProductAlreadyExists() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifier("P1"))
                .thenReturn(new Product());

        ProductDto response = productService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Product with identifier - P1 already exists",
                response.getMessage()
        );

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateProductSuccess() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product existingProduct = new Product();
        Product mappedProduct = new Product();

        Mockito.when(productRepository.findByIdentifier("P1"))
                .thenReturn(existingProduct);
        Mockito.when(modelMapper.map(dto, Product.class))
                .thenReturn(mappedProduct);

        ProductDto response = productService.update(dto);

        Assertions.assertNull(response.getMessage());
        Mockito.verify(productRepository).save(mappedProduct);
    }

    @Test
    void updateProductNotFound() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifier("P1"))
                .thenReturn(null);

        ProductDto response = productService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Product with identifier - P1 is not found",
                response.getMessage()
        );

        Mockito.verify(productRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteProductTest() {
        productService.delete("P1");

        Mockito.verify(productRepository)
                .deleteByIdentifier("P1");
    }

    @Test
    void findAllProductsTest() {
        List<Product> products = List.of(new Product(), new Product());
        List<ProductDto> dtoList = List.of(new ProductDto(), new ProductDto());

        Mockito.when(productRepository.findAll())
                .thenReturn(products);
        Mockito.when(modelMapper.map(
                Mockito.eq(products),
                Mockito.any(Type.class))
        ).thenReturn(dtoList);

        List<ProductDto> response = productService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findProductByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("P1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Mockito.when(productRepository.findByIdentifier("P1"))
                .thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto response = productService.findByIdentifier("P1");

        Assertions.assertEquals("P1", response.getIdentifier());
    }

    @Test
    void listOfCategoriesTest() {
        // given
        List<Category> categories = List.of(new Category(), new Category());
        List<ProductDto> dtoList = List.of(new ProductDto(), new ProductDto());

        Mockito.when(categoryRepository.findBySuperCategoryIsNot(""))
                .thenReturn(categories);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class))
        ).thenReturn(dtoList);

        // when
        List<ProductDto> response = productService.listOfCategories();

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.size());
    }
}
