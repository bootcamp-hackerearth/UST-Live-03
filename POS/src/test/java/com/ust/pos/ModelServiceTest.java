package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.models.service.impl.ModelServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

    @Mock
    private ModelRepository modelRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private ModelServiceImpl modelService;

    private Model modelEntity;
    private ModelDto modelDto;

    @BeforeEach
    void setUp() {
        modelEntity = new Model();
        modelEntity.setId(1L);
        modelEntity.setIdentifier("MDL-2026");
        modelEntity.setStatus(true);
        modelEntity.setDeleted(false);

        modelDto = new ModelDto();
        modelDto.setIdentifier("MDL-2026");
    }

    @Test
    void testFindByIdentifier_Success() {
        when(modelRepository.findByIdentifier("MDL-2026")).thenReturn(modelEntity);

        ModelDto result = modelService.findByIdentifier("MDL-2026");

        assertNotNull(result);
        assertEquals("MDL-2026", result.getIdentifier());
        verify(modelRepository, times(1)).findByIdentifier("MDL-2026");
    }

    @Test
    void testSave_WhenDtoIsNull_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> modelService.save(null));
    }

    @Test
    void testSave_WhenIdentifierIsNull_ShouldThrowException() {
        modelDto.setIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> modelService.save(modelDto));
    }

    @Test
    void testSave_WhenModelExistsAndNotDeleted_ShouldReturnFailure() {
        modelEntity.setDeleted(false);
        when(modelRepository.findByIdentifier("MDL-2026")).thenReturn(modelEntity);

        ModelDto result = modelService.save(modelDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(modelRepository, never()).save(any(Model.class));
    }

    @Test
    void testSave_WhenModelExistsAndIsDeleted_ShouldReturnFailureWithRestoreMessage() {
        modelEntity.setDeleted(true);
        when(modelRepository.findByIdentifier("MDL-2026")).thenReturn(modelEntity);

        ModelDto result = modelService.save(modelDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
        verify(modelRepository, never()).save(any(Model.class));
    }

    @Test
    void testSave_Success() {
        when(modelRepository.findByIdentifier("MDL-2026")).thenReturn(null);
        when(modelRepository.save(any(Model.class))).thenReturn(modelEntity);

        ModelDto result = modelService.save(modelDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Model created successfully", result.getMessage());
        verify(modelRepository, times(1)).save(any(Model.class));
    }

    @Test
    void testUpdate_WhenModelNotFound_ShouldReturnFailure() {
        when(modelRepository.findByIdentifier("MDL-2026")).thenReturn(null);

        ModelDto result = modelService.update(modelDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(modelRepository, never()).save(any(Model.class));
    }

    @Test
    void testUpdate_WhenModelIsDeleted_ShouldReturnFailure() {
        modelEntity.setDeleted(true);
        when(modelRepository.findByIdentifier("MDL-2026")).thenReturn(modelEntity);

        ModelDto result = modelService.update(modelDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
        verify(modelRepository, never()).save(any(Model.class));
    }

    @Test
    void testUpdate_Success() {
        when(modelRepository.findByIdentifier("MDL-2026")).thenReturn(modelEntity);
        when(modelRepository.save(any(Model.class))).thenReturn(modelEntity);

        ModelDto result = modelService.update(modelDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Model updated successfully", result.getMessage());
        verify(modelRepository, times(1)).save(any(Model.class));
    }

    @Test
    void testDelete_WhenModelNotFound_ShouldDoNothing() {
        when(modelRepository.findByIdentifier("MDL-2026")).thenReturn(null);

        modelService.delete("MDL-2026");

        verify(modelRepository, never()).save(any(Model.class));
    }

    @Test
    void testDelete_Success() {
        when(modelRepository.findByIdentifier("MDL-2026")).thenReturn(modelEntity);
        when(modelRepository.save(any(Model.class))).thenReturn(modelEntity);

        modelService.delete("MDL-2026");

        verify(modelRepository, times(1)).save(modelEntity);
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Model> entityList = Collections.singletonList(modelEntity);
        Page<Model> page = new PageImpl<>(entityList, pageable, 1);

        when(modelRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<ModelDto> result = modelService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());
        assertEquals("MDL-2026", result.getDtoList().get(0).getIdentifier());
    }

    @Test
    void testToggleStatus_WhenModelNotFound_ShouldReturnFailure() {
        when(modelRepository.findByIdentifier("MDL-2026")).thenReturn(null);

        ModelDto result = modelService.toggleStatus("MDL-2026");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(modelRepository, never()).save(any(Model.class));
    }

    @Test
    void testToggleStatus_Success() {
        modelEntity.setStatus(true);
        when(modelRepository.findByIdentifier("MDL-2026")).thenReturn(modelEntity);
        when(modelRepository.save(any(Model.class))).thenReturn(modelEntity);

        ModelDto result = modelService.toggleStatus("MDL-2026");

        assertNotNull(result);
        assertFalse(result.getStatus());
        verify(modelRepository, times(1)).save(modelEntity);
    }

    @Test
    void testFindIfTrue_Success() {
        List<Model> activeModels = Collections.singletonList(modelEntity);
        when(modelRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(activeModels);

        List<ModelDto> result = modelService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("MDL-2026", result.get(0).getIdentifier());
        verify(modelRepository, times(1)).findByStatusIsTrueAndDeletedFalse();
    }
}