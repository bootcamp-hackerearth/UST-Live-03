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
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("P1");

        ModelProduct modelProduct = new ModelProduct();

        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, ModelProduct.class))
                .thenReturn(modelProduct);

        ModelProductDto response = modelProductService.save(dto);

        Assertions.assertEquals("P1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelProductRepository).save(modelProduct);
    }

    @Test
    void saveDuplicateTest() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("P1");

        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(new ModelProduct());

        ModelProductDto response = modelProductService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(modelProductRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("P1");

        ModelProduct existing = new ModelProduct();
        ModelProduct mapped = new ModelProduct();

        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(existing);
        Mockito.when(modelMapper.map(dto, ModelProduct.class))
                .thenReturn(mapped);

        ModelProductDto response = modelProductService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelProductRepository).save(mapped);
    }

    @Test
    void updateFailureTest() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("P1");

        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);

        ModelProductDto response = modelProductService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(modelProductRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        ModelProduct modelProduct = new ModelProduct();
        ModelProductDto dto = new ModelProductDto();

        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(modelProduct);
        Mockito.when(modelMapper.map(modelProduct, ModelProductDto.class))
                .thenReturn(dto);

        Assertions.assertNotNull(modelProductService.findByIdentifier("P1"));
    }

    @Test
    void deleteTest() {
        ModelProduct modelProduct = new ModelProduct();

        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(modelProduct);

        modelProductService.delete("P1");

        Assertions.assertTrue(modelProduct.isDeleted());

        Mockito.verify(modelProductRepository).save(modelProduct);
    }

    @Test
    void deleteNotFoundTest() {
        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);

        modelProductService.delete("P1");

        Mockito.verify(modelProductRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void toggleStatusTest() {
        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setStatus(false);

        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(modelProduct);

        modelProductService.toggleStatus("P1");

        Assertions.assertTrue(modelProduct.isStatus());

        Mockito.verify(modelProductRepository).save(modelProduct);
    }

    @Test
    void toggleStatusNotFoundTest() {
        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("P1"))
                .thenReturn(null);

        modelProductService.toggleStatus("P1");

        Mockito.verify(modelProductRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllTest() {
        List<ModelProduct> products = List.of(new ModelProduct());
        List<ModelProductDto> dtos = List.of(new ModelProductDto());

        Type type = new TypeToken<List<ModelProductDto>>() {
        }.getType();

        Mockito.when(modelProductRepository.findByDeletedFalse())
                .thenReturn(products);
        Mockito.when(modelMapper.map(products, type))
                .thenReturn(dtos);

        Assertions.assertEquals(1, modelProductService.findAll().size());
    }

    @Test
    void findAllPaginationTest() {
        Pageable pageable = PageRequest.of(0, 10);

        ModelProduct modelProduct = new ModelProduct();
        Page<ModelProduct> page = new PageImpl<>(List.of(modelProduct));

        Mockito.when(modelProductRepository.findByDeletedFalse(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(modelProduct, ModelProductDto.class))
                .thenReturn(new ModelProductDto());

        Page<ModelProductDto> response =
                modelProductService.findAll(pageable, null);

        Assertions.assertEquals(1, response.getContent().size());

        Mockito.verify(modelProductRepository)
                .findByDeletedFalse(pageable);
    }
}