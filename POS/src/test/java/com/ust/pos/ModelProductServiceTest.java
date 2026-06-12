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
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;

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

        verify(modelProductRepository).save(entity);
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
        Assertions.assertTrue(result.getMessage().contains("already exists"));
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

        verify(modelMapper).map(dto, existing);
        verify(modelProductRepository).save(existing);
    }

    @Test
    void updateFailureNotFoundTest() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("UNKNOWN");

        Mockito.when(modelProductRepository.findByIdentifier("UNKNOWN")).thenReturn(null);

        ModelProductDto result = modelProductService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
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
        modelProductService.delete("MP4");
        verify(modelProductRepository).deleteByIdentifier("MP4");
    }

    @Test
    void findAllTest() {
        List<ModelProduct> entities = Arrays.asList(new ModelProduct(), new ModelProduct());
        List<ModelProductDto> dtoList = Arrays.asList(new ModelProductDto(), new ModelProductDto());

        Pageable pageable = PageRequest.of(0, 10);
        Page<ModelProduct> modelProductPage = new PageImpl<>(entities, pageable, entities.size());

        Mockito.when(modelProductRepository.findAll(pageable)).thenReturn(modelProductPage);
        Mockito.when(modelMapper.map(Mockito.eq(entities), Mockito.any(Type.class))).thenReturn(dtoList);

        WsDto<ModelProductDto> result = modelProductService.findAll(pageable);

        Assertions.assertEquals(2, result.getDtoList().size());

        Mockito.verify(modelProductRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(Mockito.eq(entities), Mockito.any(Type.class));
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
}