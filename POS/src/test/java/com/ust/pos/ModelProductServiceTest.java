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

    // ---------------- SAVE ----------------

    @Test
    void saveTest_Success() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("Admin");

        ModelProduct entity = new ModelProduct();

        Mockito.when(modelProductRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, ModelProduct.class))
                .thenReturn(entity);
        Mockito.when(modelProductRepository.save(entity))
                .thenReturn(entity);

        ModelProductDto response = modelProductService.save(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(modelProductRepository).save(entity);
    }

    @Test
    void saveTest_Failure_WhenAlreadyExists() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("Admin");

        Mockito.when(modelProductRepository.findByIdentifier("Admin"))
                .thenReturn(new ModelProduct());

        ModelProductDto response = modelProductService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(modelProductRepository, Mockito.never())
                .save(Mockito.any());
    }

    // ---------------- UPDATE ----------------

    @Test
    void updateTest_Success() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("Admin");

        ModelProduct existing = new ModelProduct();
        ModelProduct mapped = new ModelProduct();

        Mockito.when(modelProductRepository.findByIdentifier("Admin"))
                .thenReturn(existing);
        Mockito.when(modelMapper.map(dto, ModelProduct.class))
                .thenReturn(mapped);
        Mockito.when(modelProductRepository.save(mapped))
                .thenReturn(mapped);

        ModelProductDto response = modelProductService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(modelProductRepository).save(mapped);
    }

    @Test
    void updateTest_Failure_WhenNotFound() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("Admin");

        Mockito.when(modelProductRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        ModelProductDto response = modelProductService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(modelProductRepository, Mockito.never())
                .save(Mockito.any());
    }

    // ---------------- FIND BY IDENTIFIER ----------------

    @Test
    void findByIdentifierTest() {
        ModelProduct entity = new ModelProduct();
        entity.setIdentifier("Admin");

        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("Admin");

        Mockito.when(modelProductRepository.findByIdentifier("Admin"))
                .thenReturn(entity);
        Mockito.when(modelMapper.map(entity, ModelProductDto.class))
                .thenReturn(dto);

        ModelProductDto response = modelProductService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    // ---------------- FIND ALL ----------------

    @Test
    void findAllTest() {
        List<ModelProduct> entities = List.of(new ModelProduct());
        List<ModelProductDto> dtos = List.of(new ModelProductDto());

        Type listType = new TypeToken<List<ModelProductDto>>() {
        }.getType();

        Mockito.when(modelProductRepository.findAll())
                .thenReturn(entities);
        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);

        List<ModelProductDto> response = modelProductService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    // ---------------- UPDATE STATUS ONLY ----------------

    @Test
    void updateStatusOnlyTest() {
        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setStatus(false);

        Mockito.when(modelProductRepository.findByIdentifier("Admin"))
                .thenReturn(modelProduct);
        Mockito.when(modelProductRepository.save(modelProduct))
                .thenReturn(modelProduct);

        modelProductService.updateStatusOnly("Admin", true);

        Assertions.assertTrue(modelProduct.getStatus());
        Mockito.verify(modelProductRepository).save(modelProduct);
    }

    // ---------------- DELETE ----------------

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(modelProductRepository)
                .deleteByIdentifier("Admin");

        modelProductService.delete("Admin");

        Mockito.verify(modelProductRepository)
                .deleteByIdentifier("Admin");
    }

    @Test
    void findAll_WithPagination_ShouldReturnModelProductDtos() {
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