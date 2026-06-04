package com.ust.pos;

import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.model.ModelProduct;
import com.ust.pos.model.ModelProductRepository;
import com.ust.pos.modelproduct.service.impl.ModelProductServiceImpl;
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
class ModelProductServiceTest {
    @InjectMocks
    private ModelProductServiceImpl modelProductService;

    @Mock
    private ModelProductRepository modelProductRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        ModelProductDto modelProductDto = new ModelProductDto();
        modelProductDto.setIdentifier("M1");

        Mockito.when(modelProductRepository.findByIdentifier("M1")).thenReturn(null);
        ModelProduct modelProduct = new ModelProduct();
        Mockito.when(modelMapper.map(modelProductDto, ModelProduct.class)).thenReturn(modelProduct);
        Mockito.when(modelProductRepository.save(modelProduct)).thenReturn(modelProduct);
        ModelProductDto response = modelProductService.save(modelProductDto);

        Assertions.assertEquals("M1", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());

    }

    @Test
    void saveTestFailure() {
        ModelProductDto modelProductDto = new ModelProductDto();
        modelProductDto.setIdentifier("M1");
        ModelProduct modelProduct = new ModelProduct();

        Mockito.when(modelProductRepository.findByIdentifier("M1")).thenReturn(modelProduct);
        ModelProductDto response = modelProductService.save(modelProductDto);

        Assertions.assertEquals("M1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setIdentifier("M1");

        ModelProductDto modelProductDto = new ModelProductDto();
        modelProductDto.setIdentifier("M1");

        Mockito.when(modelProductRepository.findByIdentifier("M1")).thenReturn(modelProduct);
        Mockito.when(modelMapper.map(modelProduct, ModelProductDto.class)).thenReturn(modelProductDto);

        ModelProductDto response = modelProductService.findByIdentifier("M1");

        Assertions.assertEquals("M1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ModelProductDto modelProductDto = new ModelProductDto();
        modelProductDto.setIdentifier("M1");

        ModelProduct existingModelProduct = new ModelProduct();
        existingModelProduct.setIdentifier("M1");

        ModelProduct mappedProduct = new ModelProduct();
        mappedProduct.setIdentifier("M1");

        Mockito.when(modelProductRepository.findByIdentifier("M1"))
                .thenReturn(existingModelProduct);

        Mockito.when(modelMapper.map(modelProductDto, ModelProduct.class))
                .thenReturn(mappedProduct);

        Mockito.when(modelProductRepository.save(mappedProduct))
                .thenReturn(mappedProduct);

        ModelProductDto response = modelProductService.update(modelProductDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ModelProductDto modelProductDto = new ModelProductDto();
        modelProductDto.setIdentifier("M1");

        Mockito.when(modelProductRepository.findByIdentifier("M1"))
                .thenReturn(null);

        ModelProductDto response = modelProductService.update(modelProductDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(modelProductRepository)
                .deleteByIdentifier("M1");

        modelProductService.delete("M1");

        Mockito.verify(modelProductRepository).deleteByIdentifier("M1");
    }

    @Test
    void findAllTest() {
        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setIdentifier("M1");

        ModelProductDto modelProductDto = new ModelProductDto();
        modelProductDto.setIdentifier("M1");

        List<ModelProduct> modelProducts = List.of(modelProduct);
        List<ModelProductDto> modelProductDtos = List.of(modelProductDto);

        Mockito.when(modelProductRepository.findAll()).thenReturn(modelProducts);
        Mockito.when(modelMapper.map(
                Mockito.eq(modelProducts),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(modelProductDtos);

        List<ModelProductDto> response = modelProductService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {
        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setIdentifier("M1");
        modelProduct.setStatus(false);

        Mockito.when(modelProductRepository.findByIdentifier("M1"))
                .thenReturn(modelProduct);

        modelProductService.toggleStatus("M1");

        Assertions.assertTrue(modelProduct.isStatus());

        Mockito.verify(modelProductRepository).save(modelProduct);
    }

    @Test
    void toggleStatusNotFoundTest() {
        Mockito.when(modelProductRepository.findByIdentifier("M1"))
                .thenReturn(null);

        modelProductService.toggleStatus("M1");

        Mockito.verify(modelProductRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllWithPaginationShouldReturnModelProductDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<ModelProduct> products = List.of(new ModelProduct());
        Page<ModelProduct> page = new PageImpl<>(products);

        List<ModelProductDto> productDtos = List.of(new ModelProductDto());

        Type listType = new TypeToken<List<ModelProductDto>>() {
        }.getType();

        Mockito.when(modelProductRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(products, listType))
                .thenReturn(productDtos);

        List<ModelProductDto> response = modelProductService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(modelProductRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(products, listType);
    }
}