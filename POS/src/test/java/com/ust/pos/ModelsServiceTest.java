package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.impl.ModelsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {

    @Mock
    private ModelsRepository modelsRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ModelsServiceImpl modelsService;

    private Models models;
    private ModelsDto modelsDto;

    @BeforeEach
    void setUp() {

        modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MODEL-001");
        modelsDto.setSuccess(true);
        models = new Models();
        models.setIdentifier("MODEL-001");
        models.setStatus(true);
    }

    @Test
    void testSave_WhenModelAlreadyExists() {

        when(modelsRepository.findByIdentifier("MODEL-001"))
                .thenReturn(models);
        ModelsDto result = modelsService.save(modelsDto);
        assertFalse(result.isSuccess());
        assertEquals(
                "Models with identifier - MODEL-001 already exists",
                result.getMessage()
        );
        verify(modelsRepository, times(1))
                .findByIdentifier("MODEL-001");
        verify(modelsRepository, never())
                .save(any());
        verifyNoMoreInteractions(modelsRepository);
    }

    @Test
    void testSave_NewModel() {

        when(modelsRepository.findByIdentifier("MODEL-001"))
                .thenReturn(null);
        when(modelMapper.map(modelsDto, Models.class))
                .thenReturn(models);
        ModelsDto result = modelsService.save(modelsDto);
        assertNotNull(result);
        verify(modelsRepository, times(1))
                .findByIdentifier("MODEL-001");
        verify(modelMapper, times(1))
                .map(modelsDto, Models.class);
        verify(modelsRepository, times(1))
                .save(models);
    }
    @Test
    void testUpdate_ModelNotFound() {

        when(modelsRepository.findByIdentifier("MODEL-001"))
                .thenReturn(null);
        ModelsDto result = modelsService.update(modelsDto);
        assertFalse(result.isSuccess());
        assertEquals(
                "models with identifier - MODEL-001 not found",
                result.getMessage()
        );
        verify(modelsRepository, times(1))
                .findByIdentifier("MODEL-001");
        verify(modelsRepository, never())
                .save(any());
        verify(modelMapper, never())
                .map(any(), any());
    }

    @Test
    void testUpdate_ModelFound() {

        when(modelsRepository.findByIdentifier("MODEL-001"))
                .thenReturn(models);
        ModelsDto result = modelsService.update(modelsDto);
        assertNotNull(result);
        verify(modelsRepository, times(1))
                .findByIdentifier("MODEL-001");
        verify(modelMapper, times(1))
                .map(modelsDto, models);
        verify(modelsRepository, times(1))
                .save(models);
    }

    @Test
    void testDelete() {

        doNothing().when(modelsRepository)
                .deleteByIdentifier("MODEL-001");
        modelsService.delete("MODEL-001");
        verify(modelsRepository, times(1))
                .deleteByIdentifier("MODEL-001");
    }

    @Test
    void testFindAll_WithData() {

        Pageable pageable = PageRequest.of(0, 50);
        List<Models> modelsList =
                Collections.singletonList(models);
        List<ModelsDto> modelsDtoList =
                Collections.singletonList(modelsDto);
        Page<Models> modelsPage =
                new PageImpl<>(modelsList, pageable, modelsList.size());
        Type listType =
                new TypeToken<List<ModelsDto>>() {
                }.getType();

        when(modelsRepository.findAll(pageable))
                .thenReturn(modelsPage);
        when(modelMapper.map(modelsPage.getContent(), listType))
                .thenReturn(modelsDtoList);
        List<ModelsDto> result =
                modelsService.findAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(modelsRepository, times(1))
                .findAll(pageable);
        verify(modelMapper, times(1))
                .map(modelsPage.getContent(), listType);
    }

    @Test
    void testFindAll_EmptyList() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Models> emptyPage =
                new PageImpl<>(Collections.emptyList());
        Type listType =
                new TypeToken<List<ModelsDto>>() {
                }.getType();
        when(modelsRepository.findAll(pageable))
                .thenReturn(emptyPage);
        when(modelMapper.map(emptyPage.getContent(), listType))
                .thenReturn(Collections.emptyList());
        List<ModelsDto> result =
                modelsService.findAll(pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(modelsRepository, times(1))
                .findAll(pageable);
        verify(modelMapper, times(1))
                .map(emptyPage.getContent(), listType);
    }

    @Test
    void testFindByIdentifier_Found() {

        when(modelsRepository.findByIdentifier("MODEL-001"))
                .thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class))
                .thenReturn(modelsDto);
        ModelsDto result =
                modelsService.findByIdentifier("MODEL-001");
        assertNotNull(result);
        assertEquals("MODEL-001", result.getIdentifier());

        verify(modelsRepository, times(1))
                .findByIdentifier("MODEL-001");
        verify(modelMapper, times(1))
                .map(models, ModelsDto.class);
    }

    @Test
    void testFindByIdentifier_NotFound() {

        when(modelsRepository.findByIdentifier("MODEL-001"))
                .thenReturn(null);
        when(modelMapper.map(null, ModelsDto.class))
                .thenReturn(null);
        ModelsDto result =
                modelsService.findByIdentifier("MODEL-001");
        assertNull(result);
        verify(modelsRepository, times(1))
                .findByIdentifier("MODEL-001");
        verify(modelMapper, times(1))
                .map(null, ModelsDto.class);
    }

    @Test
    void testFindActiveModels() {

        when(modelsRepository.findByStatus(true))
                .thenReturn(Collections.singletonList(models));

        List<Models> result =
                modelsService.findActiveModels();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(modelsRepository, times(1))
                .findByStatus(true);
    }

    @Test
    void testToggleStatus_TrueToFalse() {

        models.setStatus(true);
        when(modelsRepository.findByIdentifier("MODEL-001"))
                .thenReturn(models);
        modelsService.toggleStatus("MODEL-001");
        assertFalse(models.isStatus());
        verify(modelsRepository, times(1))
                .save(models);
    }

    @Test
    void testToggleStatus_FalseToTrue() {

        models.setStatus(false);
        when(modelsRepository.findByIdentifier("MODEL-001"))
                .thenReturn(models);
        modelsService.toggleStatus("MODEL-001");
        assertTrue(models.isStatus());
        verify(modelsRepository, times(1))
                .save(models);
    }

    @Test
    void testToggleStatus_ModelNotFound() {

        when(modelsRepository.findByIdentifier("MODEL-001"))
                .thenReturn(null);
        modelsService.toggleStatus("MODEL-001");
        verify(modelsRepository, never())
                .save(any());
    }
}