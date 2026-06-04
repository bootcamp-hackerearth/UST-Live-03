package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Price;
import com.ust.pos.model.PriceRepository;
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

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccessWithPriceTest() {

        Product product = new Product();
        product.setId(1L);
        product.setIdentifier("SKU001");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("SKU001");

        Price price = new Price();

        PriceDto priceDto = new PriceDto();

        when(productRepository.findByIdentifier("SKU001")).thenReturn(product);

        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        when(priceRepository.findByProductId(1L)).thenReturn(price);

        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        ProductDto response = productService.findByIdentifier("SKU001");

        Assertions.assertEquals("SKU001", response.getIdentifier());

        Assertions.assertNotNull(response.getPrice());
    }

    @Test
    void findByIdentifierWithoutPriceTest() {

        Product product = new Product();
        product.setId(1L);

        ProductDto dto = new ProductDto();

        when(productRepository.findByIdentifier("SKU001")).thenReturn(product);

        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        when(priceRepository.findByProductId(1L)).thenReturn(null);

        ProductDto response = productService.findByIdentifier("SKU001");

        Assertions.assertNotNull(response);

        Assertions.assertNull(response.getPrice());
    }

    @Test
    void findByIdentifierNotFoundTest() {

        when(productRepository.findByIdentifier("SKU001")).thenReturn(null);

        ProductDto response = productService.findByIdentifier("SKU001");

        Assertions.assertNull(response);
    }

    @Test
    void saveSuccessTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier(" SKU001 ");
        dto.setProductName("Samsung");

        Product product = new Product();

        when(productRepository.findByIdentifier("SKU001")).thenReturn(null);

        when(modelMapper.map(dto, Product.class)).thenReturn(product);

        ProductDto response = productService.save(dto);

        Assertions.assertEquals("SKU001", response.getIdentifier());

        verify(productRepository).save(product);
    }

    @Test
    void saveDuplicateIdentifierTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("SKU001");

        Product existingProduct = new Product();

        when(productRepository.findByIdentifier("SKU001")).thenReturn(existingProduct);

        ProductDto response = productService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Product with skuCode - SKU001 already exists", response.getMessage());

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateSuccessTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier(" SKU001 ");
        dto.setProductName("Samsung");

        Product existingProduct = new Product();
        existingProduct.setIdentifier("SKU001");

        when(productRepository.findByIdentifier("SKU001")).thenReturn(existingProduct);

        doAnswer(invocation -> {

            ProductDto source = invocation.getArgument(0);
            Product target = invocation.getArgument(1);

            target.setIdentifier(source.getIdentifier());
            target.setProductName(source.getProductName());

            return null;

        }).when(modelMapper).map(any(ProductDto.class), any(Product.class));

        ProductDto response = productService.update(dto);

        Assertions.assertEquals(" SKU001 ", response.getIdentifier());

        verify(productRepository).save(existingProduct);
    }

    @Test
    void updateNotFoundTest() {

        ProductDto dto = new ProductDto();
        dto.setIdentifier("SKU001");

        when(productRepository.findByIdentifier("SKU001")).thenReturn(null);

        ProductDto response = productService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Product with skuCode - SKU001 not found", response.getMessage());

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteTest() {

        doNothing().when(productRepository).deleteByIdentifier("SKU001");

        boolean response = productService.delete("SKU001");

        Assertions.assertTrue(response);

        verify(productRepository).deleteByIdentifier("SKU001");
    }

    @Test
    void findAllWithPriceTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();
        product.setId(1L);

        ProductDto dto = new ProductDto();

        Price price = new Price();

        PriceDto priceDto = new PriceDto();

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(dto);

        when(priceRepository.findByProductId(1L)).thenReturn(price);

        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        List<ProductDto> response = productService.findAll(pageable);

        Assertions.assertEquals(1, response.size());

        Assertions.assertNotNull(response.get(0).getPrice());
    }

    @Test
    void findAllWithoutPriceTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();
        product.setId(1L);

        ProductDto dto = new ProductDto();

        Page<Product> productPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(dto);

        when(priceRepository.findByProductId(1L)).thenReturn(null);

        List<ProductDto> response = productService.findAll(pageable);

        Assertions.assertEquals(1, response.size());

        Assertions.assertNull(response.get(0).getPrice());
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        List<ProductDto> response = productService.findAll(pageable);

        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void toggleStatusTrueToFalseTest() {

        Product product = new Product();
        product.setIdentifier("SKU001");
        product.setStatus(true);

        ProductDto dto = new ProductDto();

        when(productRepository.findByIdentifier("SKU001")).thenReturn(product);

        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        ProductDto response = productService.toggleStatus("SKU001");

        Assertions.assertNotNull(response);

        Assertions.assertFalse(product.isStatus());

        verify(productRepository).save(product);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Product product = new Product();
        product.setIdentifier("SKU001");
        product.setStatus(false);

        ProductDto dto = new ProductDto();

        when(productRepository.findByIdentifier("SKU001")).thenReturn(product);

        when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        ProductDto response = productService.toggleStatus("SKU001");

        Assertions.assertNotNull(response);

        Assertions.assertTrue(product.isStatus());

        verify(productRepository).save(product);
    }

    @Test
    void findIfTrueTest() {

        Product product = new Product();
        product.setIdentifier("SKU001");
        product.setStatus(true);

        ProductDto dto = new ProductDto();
        dto.setIdentifier("SKU001");

        List<Product> products = List.of(product);

        List<ProductDto> dtos = List.of(dto);

        when(productRepository.findByStatusIsTrue()).thenReturn(products);

        when(modelMapper.map(eq(products), any(Type.class))).thenReturn(dtos);

        List<ProductDto> response = productService.findIfTrue();

        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("SKU001", response.get(0).getIdentifier());
    }

    @Test
    void findIfTrueEmptyTest() {

        when(productRepository.findByStatusIsTrue()).thenReturn(List.of());

        when(modelMapper.map(eq(List.of()), any(Type.class))).thenReturn(List.of());

        List<ProductDto> response = productService.findIfTrue();

        Assertions.assertTrue(response.isEmpty());
    }
}