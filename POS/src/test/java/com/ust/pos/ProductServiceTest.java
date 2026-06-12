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
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
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
    void saveTestSuccess() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product product = new Product();

        when(productRepository.findByIdentifier("P1")).thenReturn(null);
        when(modelMapper.map(dto, Product.class)).thenReturn(product);

        ProductDto result = productService.save(dto);

        Assertions.assertEquals("P1", result.getIdentifier());
        verify(productRepository).save(product);
    }

    @Test
    void saveTestFailure() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product product = new Product();
        product.setIdentifier("P1");

        when(productRepository.findByIdentifier("P1")).thenReturn(product);

        ProductDto result = productService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        Product product = new Product();

        when(productRepository.findByIdentifier("P1")).thenReturn(product);

        ProductDto result = productService.update(dto);

        Assertions.assertEquals("P1", result.getIdentifier());
        verify(modelMapper).map(dto, product);
        verify(productRepository).save(product);
    }

    @Test
    void updateTestFailure() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        when(productRepository.findByIdentifier("P1")).thenReturn(null);

        ProductDto result = productService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        productService.delete("P1");
        verify(productRepository).deleteByIdentifier("P1");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Product product = new Product();
        product.setIdentifier("P1");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("P1");

        when(productRepository.findByIdentifier("P1")).thenReturn(product);
        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        ProductDto result = productService.findByIdentifier("P1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("P1", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(productRepository.findByIdentifier("P1")).thenReturn(null);

        ProductDto result = productService.findByIdentifier("P1");

        Assertions.assertNull(result);
    }

    @Test
    void findAllTest() {
        Pageable pageable = mock(Pageable.class);
        Page<Product> page = mock(Page.class);

        List<Product> products = List.of(new Product(), new Product());
        List<ProductDto> dtoList = List.of(new ProductDto(), new ProductDto());

        when(productRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(products);
        when(modelMapper.map(eq(products), any(Type.class))).thenReturn(dtoList);

        WsDto<ProductDto> result = productService.findAll(pageable);

        Assertions.assertEquals(2, result.getDtoList().size());

        verify(productRepository).findAll(pageable);
        verify(page).getContent();
        verify(modelMapper).map(eq(products), any(Type.class));
    }
}