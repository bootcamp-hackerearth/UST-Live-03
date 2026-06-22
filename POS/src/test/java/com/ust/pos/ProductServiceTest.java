package com.ust.pos;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
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
import static org.mockito.ArgumentMatchers.eq;

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
        priceDto.setIdentifier("PRICE001");

        Mockito.when(productRepository.findByIdentifier("SKU001")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        Mockito.when(priceRepository.findByProductId(1L)).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        ProductDto response = productService.findByIdentifier("SKU001");

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getPrice());
        Assertions.assertEquals("PRICE001", response.getPrice().getIdentifier());
    }

    @Test
    void findByIdentifierWithoutPriceTest() {
        Product product = new Product();
        product.setId(1L);

        ProductDto dto = new ProductDto();

        Mockito.when(productRepository.findByIdentifier("SKU001")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);
        Mockito.when(priceRepository.findByProductId(1L)).thenReturn(null);

        ProductDto response = productService.findByIdentifier("SKU001");

        Assertions.assertNotNull(response);
        Assertions.assertNull(response.getPrice());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(productRepository.findByIdentifier("SKU001")).thenReturn(null);

        ProductDto response = productService.findByIdentifier("SKU001");

        Assertions.assertNull(response);
    }

    @Test
    void saveSuccessTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier(" SKU001 ");

        Product product = new Product();

        Mockito.when(productRepository.findByIdentifier("SKU001")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Product.class)).thenReturn(product);

        ProductDto response = productService.save(dto);

        Assertions.assertEquals("SKU001", response.getIdentifier());

        Mockito.verify(productRepository).save(product);
    }

    @Test
    void saveDuplicateIdentifierTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("SKU001");

        Product existingProduct = new Product();

        Mockito.when(productRepository.findByIdentifier("SKU001")).thenReturn(existingProduct);

        ProductDto response = productService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product with skuCode - SKU001 already exists", response.getMessage());

        Mockito.verify(productRepository, Mockito.never()).save(any(Product.class));
    }

    @Test
    void saveSoftDeletedTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("SKU001");

        Product existingProduct = new Product();
        existingProduct.setDeleted(true);

        Mockito.when(productRepository.findByIdentifier("SKU001")).thenReturn(existingProduct);

        ProductDto response = productService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product with skuCode - SKU001 has been soft deleted.(Rollback by changing status", response.getMessage());

        Mockito.verify(productRepository, Mockito.never()).save(any(Product.class));
    }

    @Test
    void updateSuccessTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier(" SKU001 ");

        Product existingProduct = new Product();

        Mockito.when(productRepository.findByIdentifier("SKU001")).thenReturn(existingProduct);

        Mockito.doNothing().when(modelMapper).map(dto, existingProduct);

        ProductDto response = productService.update(dto);

        Assertions.assertEquals(" SKU001 ", response.getIdentifier());

        Mockito.verify(productRepository).save(existingProduct);
    }

    @Test
    void updateNotFoundTest() {
        ProductDto dto = new ProductDto();
        dto.setIdentifier("SKU001");

        Mockito.when(productRepository.findByIdentifier("SKU001")).thenReturn(null);

        ProductDto response = productService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Product with skuCode - SKU001 not found", response.getMessage());

        Mockito.verify(productRepository, Mockito.never()).save(any(Product.class));
    }

    @Test
    void deleteTest() {
        Product product = new Product();

        Mockito.when(productRepository.findByIdentifier("SKU001")).thenReturn(product);

        boolean response = productService.delete("SKU001");

        Assertions.assertTrue(response);

        Mockito.verify(productRepository).save(product);
    }

    @Test
    void deleteNotFoundTest() {
        Mockito.when(productRepository.findByIdentifier("SKU001")).thenReturn(null);

        boolean response = productService.delete("SKU001");

        Assertions.assertFalse(response);

        Mockito.verify(productRepository, Mockito.never()).save(any(Product.class));
    }

    @Test
    void findAllWithPriceTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();
        product.setId(1L);

        ProductDto dto = new ProductDto();

        Price price = new Price();
        PriceDto priceDto = new PriceDto();

        Page<Product> page = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(dto);
        Mockito.when(priceRepository.findByProductId(1L)).thenReturn(price);
        Mockito.when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        WsDto<ProductDto> response = productService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertNotNull(response.getDtoList().get(0).getPrice());
    }

    @Test
    void findAllWithoutPriceTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Product product = new Product();
        product.setId(1L);

        ProductDto dto = new ProductDto();

        Page<Product> page = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(any(Product.class), eq(ProductDto.class))).thenReturn(dto);
        Mockito.when(priceRepository.findByProductId(1L)).thenReturn(null);

        WsDto<ProductDto> response = productService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertNull(response.getDtoList().get(0).getPrice());
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(productRepository.findByDeletedFalse(pageable)).thenReturn(new PageImpl<>(List.of()));

        WsDto<ProductDto> response = productService.findAll(pageable);

        Assertions.assertTrue(response.getDtoList().isEmpty());
        Assertions.assertEquals(0L, response.getTotalRecords());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Product product = new Product();
        product.setStatus(true);

        ProductDto dto = new ProductDto();

        Mockito.when(productRepository.findByIdentifier("SKU001")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        ProductDto response = productService.toggleStatus("SKU001");

        Assertions.assertNotNull(response);
        Assertions.assertFalse(product.isStatus());

        Mockito.verify(productRepository).save(product);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Product product = new Product();
        product.setStatus(false);

        ProductDto dto = new ProductDto();

        Mockito.when(productRepository.findByIdentifier("SKU001")).thenReturn(product);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(dto);

        ProductDto response = productService.toggleStatus("SKU001");

        Assertions.assertNotNull(response);
        Assertions.assertTrue(product.isStatus());

        Mockito.verify(productRepository).save(product);
    }

    @Test
    void findIfTrueTest() {
        Product product = new Product();
        product.setIdentifier("SKU001");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("SKU001");

        List<Product> products = List.of(product);

        Mockito.when(productRepository.findByStatusIsTrue()).thenReturn(products);
        Mockito.when(modelMapper.map(eq(products), Mockito.any(Type.class))).thenReturn(List.of(dto));

        List<ProductDto> response = productService.findIfTrue();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("SKU001", response.get(0).getIdentifier());
    }

    @Test
    void findIfTrueEmptyTest() {
        Mockito.when(productRepository.findByStatusIsTrue()).thenReturn(List.of());
        Mockito.when(modelMapper.map(eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());

        List<ProductDto> response = productService.findIfTrue();

        Assertions.assertTrue(response.isEmpty());
    }

}
