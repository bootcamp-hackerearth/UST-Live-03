package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        models.setDeleted(false);
    }

    @Test
    void testSave_WhenModelAlreadyExists_Active() {
        models.setDeleted(false);
        when(modelsRepository.findByIdentifier("MODEL-001")).thenReturn(models);

        ModelsDto result = modelsService.save(modelsDto);

        assertFalse(result.isSuccess());
        assertEquals("Models with identifier - MODEL-001 already exists", result.getMessage());
        verify(modelsRepository, times(1)).findByIdentifier("MODEL-001");
        verify(modelsRepository, never()).save(any());
    }

    @Test
    void testSave_WhenModelAlreadyExists_SoftDeleted() {
        models.setDeleted(true);
        when(modelsRepository.findByIdentifier("MODEL-001")).thenReturn(models);

        ModelsDto result = modelsService.save(modelsDto);

        assertFalse(result.isSuccess());
        assertEquals("Models identifier - MODEL-001 not available", result.getMessage());
        verify(modelsRepository, times(1)).findByIdentifier("MODEL-001");
        verify(modelsRepository, never()).save(any());
    }

    @Test
    void testSave_NewModel() {
        when(modelsRepository.findByIdentifier("MODEL-001")).thenReturn(null);
        when(modelMapper.map(modelsDto, Models.class)).thenReturn(models);

        ModelsDto result = modelsService.save(modelsDto);

        assertNotNull(result);
        verify(modelsRepository, times(1)).findByIdentifier("MODEL-001");
        verify(modelMapper, times(1)).map(modelsDto, Models.class);
        verify(modelsRepository, times(1)).save(models);
    }

    @Test
    void testUpdate_ModelNotFound() {
        when(modelsRepository.findByIdentifier("MODEL-001")).thenReturn(null);

        ModelsDto result = modelsService.update(modelsDto);

        assertFalse(result.isSuccess());
        assertEquals("models with identifier - MODEL-001 not found", result.getMessage());
        verify(modelsRepository, times(1)).findByIdentifier("MODEL-001");
        verify(modelsRepository, never()).save(any());
    }

    @Test
    void testUpdate_ModelFound() {
        when(modelsRepository.findByIdentifier("MODEL-001")).thenReturn(models);

        ModelsDto result = modelsService.update(modelsDto);

        assertNotNull(result);
        verify(modelsRepository, times(1)).findByIdentifier("MODEL-001");
        verify(modelMapper, times(1)).map(modelsDto, models);
        verify(modelsRepository, times(1)).save(models);
    }

    @Test
    void testDelete_Success() {
        String identifier = "MODEL-001";
        when(modelsRepository.findByIdentifierAndDeletedFalse(identifier)).thenReturn(models);
        assertDoesNotThrow(() -> modelsService.delete(identifier));
        verify(modelsRepository, times(1)).findByIdentifierAndDeletedFalse(identifier);
        assertTrue(models.getDeleted());
    }

    @Test
    void testFindAll_WithData() {
        Pageable pageable = PageRequest.of(0, 50);
        List<Models> modelsList = Collections.singletonList(models);
        List<ModelsDto> modelsDtoList = Collections.singletonList(modelsDto);
        Page<Models> modelsPage = new PageImpl<>(modelsList, pageable, modelsList.size());
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();

        when(modelsRepository.findAllByDeletedFalse(pageable)).thenReturn(modelsPage);
        when(modelMapper.map(modelsPage.getContent(), listType)).thenReturn(modelsDtoList);

        WsDto<ModelsDto> result = modelsService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        verify(modelsRepository, times(1)).findAllByDeletedFalse(pageable);
    }

    @Test
    void testFindAll_EmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Models> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();

        when(modelsRepository.findAllByDeletedFalse(pageable)).thenReturn(emptyPage);
        when(modelMapper.map(emptyPage.getContent(), listType)).thenReturn(Collections.emptyList());

        WsDto<ModelsDto> result = modelsService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getDtoList().isEmpty());
        verify(modelsRepository, times(1)).findAllByDeletedFalse(pageable);
    }

    @Test
    void testFindByIdentifier_Found() {
        when(modelsRepository.findByIdentifierAndDeletedFalse("MODEL-001")).thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);

        ModelsDto result = modelsService.findByIdentifier("MODEL-001");

        assertNotNull(result);
        assertEquals("MODEL-001", result.getIdentifier());
        verify(modelsRepository, times(1)).findByIdentifierAndDeletedFalse("MODEL-001");
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(modelsRepository.findByIdentifierAndDeletedFalse("MODEL-001")).thenReturn(null);
        when(modelMapper.map(null, ModelsDto.class)).thenReturn(null);

        ModelsDto result = modelsService.findByIdentifier("MODEL-001");

        assertNull(result);
        verify(modelsRepository, times(1)).findByIdentifierAndDeletedFalse("MODEL-001");
    }

    @Test
    void testFindActiveModels() {
        when(modelsRepository.findByStatusTrueAndDeletedFalse())
                .thenReturn(Collections.singletonList(models));

        List<Models> result = modelsService.findActiveModels();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(modelsRepository, times(1)).findByStatusTrueAndDeletedFalse();
    }

    @Test
    void testToggleStatus_TrueToFalse() {
        models.setStatus(true);
        when(modelsRepository.findByIdentifier("MODEL-001")).thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);

        ModelsDto result = modelsService.toggleStatus("MODEL-001");

        assertNotNull(result);
        assertFalse(models.isStatus());
        verify(modelsRepository, times(1)).save(models);
    }

    @Test
    void testToggleStatus_FalseToTrue() {
        models.setStatus(false);
        when(modelsRepository.findByIdentifier("MODEL-001")).thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);

        ModelsDto result = modelsService.toggleStatus("MODEL-001");

        assertNotNull(result);
        assertTrue(models.isStatus());
        verify(modelsRepository, times(1)).save(models);
    }

    @Test
    void testToggleStatus_ModelNotFound() {
        when(modelsRepository.findByIdentifier("MODEL-001")).thenReturn(null);

        ModelsDto result = modelsService.toggleStatus("MODEL-001");

        assertNull(result);
        verify(modelsRepository, times(1)).findByIdentifier("MODEL-001");
        verify(modelsRepository, never()).save(any());
    }
}