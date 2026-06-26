package com.ust.pos;

import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.dto.WsDto;
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
    void saveSuccessTest() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("MP1");
        ModelProduct entity = new ModelProduct();
        entity.setIdentifier("MP1");

        Mockito.when(modelProductRepository.findByIdentifier("MP1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, ModelProduct.class)).thenReturn(entity);

        ModelProductDto result = modelProductService.save(dto);

        Assertions.assertEquals("MP1", result.getIdentifier());

        Mockito.verify(modelProductRepository).save(entity);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        ModelProduct existing = new ModelProduct();
        existing.setIdentifier("MP1");
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("MP1");

        Mockito.when(modelProductRepository.findByIdentifier("MP1")).thenReturn(existing);

        ModelProductDto result = modelProductService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Price already exists for identifier: MP1", result.getMessage());
    }

    @Test
    void saveFailureDeletedIdentifierTest() {
        ModelProduct existing = new ModelProduct();
        existing.setIdentifier("MP1");
        existing.setDeleted(true);
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("MP1");

        Mockito.when(modelProductRepository.findByIdentifier("MP1")).thenReturn(existing);

        ModelProductDto result = modelProductService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("ModelProduct identifier - MP1 not available", result.getMessage());

        Mockito.verify(modelProductRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        ModelProduct existing = new ModelProduct();
        existing.setIdentifier("MP2");
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("MP2");

        Mockito.when(modelProductRepository.findByIdentifier("MP2")).thenReturn(existing);

        ModelProductDto result = modelProductService.update(dto);

        Assertions.assertEquals("MP2", result.getIdentifier());

        Mockito.verify(modelMapper).map(dto, existing);
        Mockito.verify(modelProductRepository).save(existing);
    }

    @Test
    void updateFailureNotFoundTest() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("UNKNOWN");

        Mockito.when(modelProductRepository.findByIdentifier("UNKNOWN")).thenReturn(null);

        ModelProductDto result = modelProductService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Price not found for identifier: UNKNOWN", result.getMessage());
    }

    @Test
    void findByIdentifierSuccessTest() {
        ModelProduct entity = new ModelProduct();
        entity.setIdentifier("MP3");

        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("MP3");

        Mockito.when(modelProductRepository.findByIdentifier("MP3")).thenReturn(entity);
        Mockito.when(modelMapper.map(entity, ModelProductDto.class)).thenReturn(dto);

        ModelProductDto result = modelProductService.findByIdentifier("MP3");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("MP3", result.getIdentifier());
    }

    @Test
    void deleteTest() {
        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setIdentifier("MP4");

        Mockito.when(modelProductRepository.findByIdentifier("MP4")).thenReturn(modelProduct);

        modelProductService.delete("MP4");

        Assertions.assertTrue(modelProduct.isDeleted());
    }

    @Test
    void findAllTest() {
        ModelProduct entity = new ModelProduct();
        entity.setIdentifier("MP1");
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("MP1");

        List<ModelProduct> entities = List.of(entity);
        List<ModelProductDto> dtoList = List.of(dto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ModelProduct> page = new PageImpl<>(entities, pageable, entities.size());

        Mockito.when(modelProductRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(entities), Mockito.any(Type.class))).thenReturn(dtoList);

        WsDto<ModelProductDto> result = modelProductService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());

        Mockito.verify(modelProductRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void toggleStatusTest() {
        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setIdentifier("MP1");
        modelProduct.setStatus(true);

        Mockito.when(modelProductRepository.findByIdentifier("MP1")).thenReturn(modelProduct);

        modelProductService.toggleStatus("MP1");

        Assertions.assertFalse(modelProduct.getStatus());

        Mockito.verify(modelProductRepository).save(modelProduct);
    }

    @Test
    void findAllActiveTest() {
        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setIdentifier("MP1");
        modelProduct.setStatus(true);

        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("MP1");

        List<ModelProduct> entities = List.of(modelProduct);
        List<ModelProductDto> dtos = List.of(dto);

        Mockito.when(modelProductRepository.findByStatusTrueAndIsDeletedFalse()).thenReturn(entities);
        Mockito.when(modelMapper.map(Mockito.eq(entities), Mockito.any(Type.class))).thenReturn(dtos);

        List<ModelProductDto> result = modelProductService.findAllActive();

        Assertions.assertEquals(1, result.size());

        Mockito.verify(modelProductRepository).findByStatusTrueAndIsDeletedFalse();
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setIdentifier("MP2");
        modelProduct.setStatus(false);

        Mockito.when(modelProductRepository.findByIdentifier("MP2")).thenReturn(modelProduct);

        modelProductService.toggleStatus("MP2");

        Assertions.assertTrue(modelProduct.getStatus());

        Mockito.verify(modelProductRepository).save(modelProduct);
    }
}