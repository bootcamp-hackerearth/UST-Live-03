package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.impl.ModelsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {

    @InjectMocks
    private ModelsServiceImpl modelsService;

    @Mock
    private ModelsRepository modelsRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierTest() {
        Models models = new Models();
        models.setIdentifier("MODEL001");

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MODEL001");

        when(modelsRepository.findByIdentifier("MODEL001"))
                .thenReturn(models);

        when(modelMapper.map(models, ModelsDto.class))
                .thenReturn(dto);

        ModelsDto result = modelsService.findByIdentifier("MODEL001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("MODEL001", result.getIdentifier());
    }

    @Test
    void saveSuccessTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MODEL001");

        Models models = new Models();

        when(modelsRepository.findByIdentifier("MODEL001"))
                .thenReturn(null);

        when(modelMapper.map(dto, Models.class))
                .thenReturn(models);

        ModelsDto result = modelsService.save(dto);

        Assertions.assertEquals("MODEL001", result.getIdentifier());

        verify(modelsRepository).save(models);
    }

    @Test
    void saveAlreadyExistsTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MODEL001");

        Models existingModels = new Models();
        existingModels.setDeleted(false);

        when(modelsRepository.findByIdentifier("MODEL001"))
                .thenReturn(existingModels);

        ModelsDto result = modelsService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Models with identifier - MODEL001 already exists",
                result.getMessage()
        );

        verify(modelsRepository, never()).save(any());
    }

    @Test
    void saveDeletedModelsTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MODEL001");

        Models existingModels = new Models();
        existingModels.setDeleted(true);

        when(modelsRepository.findByIdentifier("MODEL001"))
                .thenReturn(existingModels);

        ModelsDto result = modelsService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Models with identifier - MODEL001 was deleted , Please Contact the Administrator to add.",
                result.getMessage()
        );

        verify(modelsRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MODEL001");

        Models existingModels = new Models();
        existingModels.setIdentifier("MODEL001");

        when(modelsRepository.findByIdentifier("MODEL001"))
                .thenReturn(existingModels);

        ModelsDto result = modelsService.update(dto);

        Assertions.assertEquals("MODEL001", result.getIdentifier());

        verify(modelMapper).map(dto, existingModels);
        verify(modelsRepository).save(existingModels);
    }

    @Test
    void updateFailureTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MODEL001");

        when(modelsRepository.findByIdentifier("MODEL001"))
                .thenReturn(null);

        ModelsDto result = modelsService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Models with identifier - MODEL001 not found",
                result.getMessage()
        );

        verify(modelsRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Models models = new Models();

        when(modelsRepository.findByIdentifier("MODEL001"))
                .thenReturn(models);

        modelsService.delete("MODEL001");

        verify(modelsRepository).findByIdentifier("MODEL001");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Models models1 = new Models();
        Models models2 = new Models();

        List<Models> modelsList = List.of(
                models1,
                models2
        );

        Page<Models> page = new PageImpl<>(
                modelsList,
                pageable,
                2
        );

        List<ModelsDto> dtoList = List.of(
                new ModelsDto(),
                new ModelsDto()
        );

        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();

        when(modelsRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(modelsList, listType))
                .thenReturn(dtoList);

        WsDto<ModelsDto> result = modelsService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Models models = new Models();
        models.setStatus(true);

        when(modelsRepository.findByIdentifier("MODEL001"))
                .thenReturn(models);

        modelsService.toggleStatus("MODEL001");

        Assertions.assertFalse(models.isStatus());

        verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Models models = new Models();
        models.setStatus(false);

        when(modelsRepository.findByIdentifier("MODEL001"))
                .thenReturn(models);

        modelsService.toggleStatus("MODEL001");

        Assertions.assertTrue(models.isStatus());

        verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatusModelsNotFoundTest() {
        when(modelsRepository.findByIdentifier("MODEL001"))
                .thenReturn(null);

        modelsService.toggleStatus("MODEL001");

        verify(modelsRepository, never()).save(any());
    }
}