package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.impl.ModelsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {

    @Mock
    private ModelsRepository modelsRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ModelsServiceImpl modelsService;

    private ModelsDto modelsDto;
    private Models models;

    @BeforeEach
    void setUp() {
        modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MOD-001");

        models = new Models();
        models.setIdentifier("MOD-001");
        models.setStatus(true);
        models.setDeleted(false);
    }

    @Test
    @DisplayName("Save Models - Success")
    void save_Success() {
        when(modelsRepository.findByIdentifier("MOD-001")).thenReturn(null);
        when(modelMapper.map(modelsDto, Models.class)).thenReturn(models);

        ModelsDto result = modelsService.save(modelsDto);

        Assertions.assertNotNull(result);
        verify(modelsRepository).save(models);
    }

    @Test
    @DisplayName("Save Models - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        when(modelsRepository.findByIdentifier("MOD-001")).thenReturn(models);

        ModelsDto result = modelsService.save(modelsDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(modelsRepository, never()).save(any(Models.class));
    }

    @Test
    @DisplayName("Find All Models - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Models> modelsPage = new PageImpl<>(List.of(models));

        when(modelsRepository.findByDeletedFalse(pageable)).thenReturn(modelsPage);
        when(modelMapper.map(eq(modelsPage.getContent()), any(Type.class))).thenReturn(List.of(modelsDto));

        WsDto<ModelsDto> result = modelsService.findAll(pageable);

        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertFalse(result.getDtoList().isEmpty());
    }

    @Test
    @DisplayName("Find All Active Models - Success")
    void findAllActive_Success() {
        List<Models> activeModels = List.of(models);
        when(modelsRepository.findAllByStatusAndDeletedFalse(true)).thenReturn(activeModels);
        when(modelMapper.map(eq(activeModels), any(Type.class))).thenReturn(List.of(modelsDto));

        List<ModelsDto> result = modelsService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(modelsRepository.findByIdentifier("MOD-001")).thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);

        ModelsDto result = modelsService.findByIdentifier("MOD-001");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("Find By Identifier - Failure: Not Found")
    void findByIdentifier_NotFound() {
        when(modelsRepository.findByIdentifier("MOD-001")).thenReturn(null);

        ModelsDto result = modelsService.findByIdentifier("MOD-001");

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Update Models - Success")
    void update_Success() {
        when(modelsRepository.findByIdentifier("MOD-001")).thenReturn(models);

        ModelsDto result = modelsService.update(modelsDto);

        Assertions.assertNotNull(result);
        verify(modelsRepository).save(models);
    }

    @Test
    @DisplayName("Update Models - Failure: Not Found")
    void update_Failure_NotFound() {
        when(modelsRepository.findByIdentifier("MOD-001")).thenReturn(null);

        ModelsDto result = modelsService.update(modelsDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(modelsRepository, never()).save(any(Models.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(modelsRepository.findByIdentifier("MOD-001")).thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);

        ModelsDto result = modelsService.toggleStatus("MOD-001");

        Assertions.assertFalse(models.isStatus());
        verify(modelsRepository).save(models);
    }

    @Test
    @DisplayName("Toggle Status - Failure: Not Found")
    void toggleStatus_NotFound() {
        when(modelsRepository.findByIdentifier("MOD-001")).thenReturn(null);

        ModelsDto result = modelsService.toggleStatus("MOD-001");

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(modelsRepository, never()).save(any(Models.class));
    }

    @Test
    @DisplayName("Delete Models - Success")
    void delete_Success() {
        when(modelsRepository.findByIdentifier("MOD-001")).thenReturn(models);

        boolean result = modelsService.delete("MOD-001");

        Assertions.assertTrue(result);
        verify(modelsRepository).save(models);
    }

    @Test
    @DisplayName("Delete Models - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(modelsRepository.findByIdentifier("MOD-001")).thenReturn(null);

        boolean result = modelsService.delete("MOD-001");

        Assertions.assertFalse(result);
        verify(modelsRepository, never()).save(any(Models.class));
    }
}