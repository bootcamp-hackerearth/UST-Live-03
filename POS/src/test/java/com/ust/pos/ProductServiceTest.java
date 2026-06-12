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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void saveTest_Success() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PRD001");

        Product product = new Product();
        product.setIdentifier("PRD001");

        when(productRepository.findByIdentifier("PRD001")).thenReturn(null);

        when(modelMapper.map(productDto, Product.class)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);

        ProductDto response = productService.save(productDto);

        Assertions.assertEquals("PRD001", response.getIdentifier());

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void saveTest_Failure_ProductAlreadyExists() {
        Product existingProduct = new Product();
        existingProduct.setIdentifier("PRD001");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PRD001");

        when(productRepository.findByIdentifier("PRD001")).thenReturn(existingProduct);

        ProductDto response = productService.save(productDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product with identifier - PRD001 already exists", response.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateTest_Success() {
        Product existingProduct = new Product();
        existingProduct.setIdentifier("PRD002");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PRD002");

        when(productRepository.findByIdentifier("PRD002")).thenReturn(existingProduct);
        doNothing().when(modelMapper).map(productDto, existingProduct);
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        ProductDto response = productService.update(productDto);

        Assertions.assertNotNull(response);

        verify(productRepository, times(1)).findByIdentifier("PRD002");
        verify(modelMapper, times(1)).map(productDto, existingProduct);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void updateTest_Failure_ProductNotFound() {
        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PRD999");

        when(productRepository.findByIdentifier("PRD999")).thenReturn(null);

        ProductDto response = productService.update(productDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product with identifier - PRD999 is not found", response.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        productService.delete("PRD003");
        verify(productRepository, times(1)).deleteByIdentifier("PRD003");
    }

    @Test
    void findByIdentifierTest() {
        Product product = new Product();
        product.setIdentifier("PRD004");

        ProductDto productDto = new ProductDto();
        productDto.setIdentifier("PRD004");

        when(productRepository.findByIdentifier("PRD004")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);

        ProductDto response = productService.findByIdentifier("PRD004");

        Assertions.assertEquals("PRD004", response.getIdentifier());
    }

    @Test
    void findAllSuccessTest() {
        Product p1 = new Product();
        p1.setIdentifier("PROD1");

        Product p2 = new Product();
        p2.setIdentifier("PROD2");

        List<Product> products = List.of(p1, p2);

        ProductDto d1 = new ProductDto();
        d1.setIdentifier("PROD1");

        ProductDto d2 = new ProductDto();
        d2.setIdentifier("PROD2");

        List<ProductDto> productDtos = List.of(d1, d2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(pageable)).thenReturn(productPage);
        when(modelMapper.map(Mockito.eq(products), Mockito.any(Type.class))).thenReturn(productDtos);

        WsDto<ProductDto> result = productService.findAll(pageable);

        Assertions.assertEquals(2, result.getDtoList().size());

        verify(productRepository).findAll(pageable);
        verify(modelMapper).map(Mockito.eq(products), Mockito.any(Type.class));
    }
}