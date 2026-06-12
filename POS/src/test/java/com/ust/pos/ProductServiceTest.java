package com.ust.pos;

import com.ust.pos.dto.WsDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

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
        Pageable pageable = Mockito.mock(Pageable.class);

        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(
                        Mockito.eq(List.of(product)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<ProductDto> result =
                productService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());    }

    @Test
    void save_success() {

        ProductDto input = new ProductDto();
        input.setIdentifier("PROD01");

        when(productRepository.findByIdentifier("PROD01"))
                .thenReturn(null);

        Product entity = new Product();

        when(modelMapper.map(input, Product.class))
                .thenReturn(entity);

        when(productRepository.save(entity))
                .thenReturn(entity);

        ProductDto result = productService.save(input);

        assertEquals("PROD01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        ProductDto input = new ProductDto();
        input.setIdentifier("PROD01");

        when(productRepository.findByIdentifier("PROD01"))
                .thenReturn(new Product());

        ProductDto result = productService.save(input);

        Assertions.assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Product product = new Product();
        product.setIdentifier("PROD01");

        ProductDto dto = new ProductDto();
        dto.setIdentifier("PROD01");

        when(productRepository.findByIdentifier("PROD01"))
                .thenReturn(product);

        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto result =
                productService.findByIdentifier("PROD01");

        assertEquals("PROD01", result.getIdentifier());
    }

    @Test
    void update_success() {

        ProductDto input = new ProductDto();
        input.setIdentifier("PROD01");

        Product existing = new Product();

        when(productRepository.findByIdentifier("PROD01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        when(productRepository.save(existing))
                .thenReturn(existing);

        ProductDto result = productService.update(input);

        assertEquals("PROD01", result.getIdentifier());
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

        when(productRepository.findByIdentifier("PROD01"))
                .thenReturn(product);

        when(productRepository.save(product))
                .thenReturn(product);

        when(modelMapper.map(product, ProductDto.class))
                .thenReturn(dto);

        ProductDto result =
                productService.changeToggleStatus("PROD01", true);

        Assertions.assertTrue(product.isStatus());
        assertNotNull(result);
    }

    @Test
    void findActiveStatus_success() {

        Product active = new Product();
        active.setStatus(true);

        Product inactive = new Product();
        inactive.setStatus(false);

        when(productRepository.findAll())
                .thenReturn(List.of(active, inactive));

        ProductDto dto = new ProductDto();

        when(modelMapper.map(
                        Mockito.eq(List.of(active)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<ProductDto> result = productService.findActiveStatus();

        assertEquals(1, result.size());
    }

    @Test
    void testFindActiveStatus() {
        // 1. Arrange: Create Product data
        Product active = new Product();
        active.setStatus(true);

        Product inactive = new Product();
        inactive.setStatus(false);

        // Stub the repository to return both active and inactive products
        when(productRepository.findAll())
                .thenReturn(List.of(active, inactive));

        // Prepare the expected DTO output list
        ProductDto dto = new ProductDto();
        List<ProductDto> expectedDtoList = List.of(dto);

        // FIX: Stub modelMapper to expect the precisely filtered list and ANY generic Type
        when(modelMapper.map(
                Mockito.eq(List.of(active)),
                Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(expectedDtoList);

        // 2. Act: Call your service layer method
        List<ProductDto> result = productService.findActiveStatus();

        // 3. Assert: Verify the behavior
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}