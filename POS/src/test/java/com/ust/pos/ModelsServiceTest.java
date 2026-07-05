package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

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
        modelsDto.setIdentifier("MDL-001");

        models = new Models();
        models.setIdentifier("MDL-001");
        models.setStatus(true);
        models.setDeleted(false);
    }

    @Test
    @DisplayName("Save Models - Success")
    void save_Success() {
        when(modelsRepository.findByIdentifier("MDL-001")).thenReturn(null);
        when(modelMapper.map(modelsDto, Models.class)).thenReturn(models);

        ModelsDto result = modelsService.save(modelsDto);

        Assertions.assertNotNull(result);
        verify(modelsRepository).save(models);
    }

    @Test
    @DisplayName("Save Models - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        when(modelsRepository.findByIdentifier("MDL-001")).thenReturn(models);

        ModelsDto result = modelsService.save(modelsDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(modelsRepository, never()).save(any(Models.class));
    }

    @Test
    @DisplayName("Find All Models - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Models> modelsPage = new PageImpl<>(List.of(models), pageable, 1);

        when(modelsRepository.findByDeletedFalse(pageable)).thenReturn(modelsPage);
        when(modelMapper.map(eq(modelsPage.getContent()), any(Type.class))).thenReturn(List.of(modelsDto));

        WsDto<ModelsDto> result = modelsService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    @DisplayName("Find All Models with Specification - Success")
    void findAll_WithSpecification_Success() {
        Specification<Models> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Models> modelsPage = new PageImpl<>(List.of(models), pageable, 1);

        when(modelsRepository.findAll(spec, pageable)).thenReturn(modelsPage);
        when(modelMapper.map(eq(modelsPage.getContent()), any(Type.class))).thenReturn(List.of(modelsDto));

        WsDto<ModelsDto> result = modelsService.findAll(spec, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
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
        when(modelsRepository.findByIdentifier("MDL-001")).thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);

        ModelsDto result = modelsService.findByIdentifier("MDL-001");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("Find By Identifier - Failure: Not Found Exception")
    void findByIdentifier_Failure_NotFound() {
        when(modelsRepository.findByIdentifier("MDL-001")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> modelsService.findByIdentifier("MDL-001"));
    }

    @Test
    @DisplayName("Update Models - Success")
    void update_Success() {
        when(modelsRepository.findByIdentifier("MDL-001")).thenReturn(models);

        ModelsDto result = modelsService.update(modelsDto);

        Assertions.assertNotNull(result);
        verify(modelsRepository).save(models);
        verify(modelMapper).map(modelsDto, models);
    }

    @Test
    @DisplayName("Update Models - Failure: Not Found")
    void update_Failure_NotFound() {
        when(modelsRepository.findByIdentifier("MDL-001")).thenReturn(null);

        ModelsDto result = modelsService.update(modelsDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(modelsRepository, never()).save(any(Models.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(modelsRepository.findByIdentifier("MDL-001")).thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);

        ModelsDto result = modelsService.toggleStatus("MDL-001");

        Assertions.assertFalse(models.isStatus());
        verify(modelsRepository, times(2)).findByIdentifier("MDL-001");
        verify(modelsRepository).save(models);
    }

    @Test
    @DisplayName("Toggle Status - Failure: Not Found")
    void toggleStatus_Failure_NotFound() {
        when(modelsRepository.findByIdentifier("MDL-001")).thenReturn(null);

        ModelsDto result = modelsService.toggleStatus("MDL-001");

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(modelsRepository, never()).save(any(Models.class));
    }

    @Test
    @DisplayName("Delete Models - Success")
    void delete_Success() {
        when(modelsRepository.findByIdentifier("MDL-001")).thenReturn(models);

        boolean result = modelsService.delete("MDL-001");

        Assertions.assertTrue(result);
        verify(modelsRepository).save(models);
    }

    @Test
    @DisplayName("Delete Models - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(modelsRepository.findByIdentifier("MDL-001")).thenReturn(null);

        boolean result = modelsService.delete("MDL-001");

        Assertions.assertFalse(result);
        verify(modelsRepository, never()).save(any(Models.class));
    }
}