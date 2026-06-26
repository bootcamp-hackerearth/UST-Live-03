package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.impl.ModelServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {

    @InjectMocks
    private ModelServiceImpl service;

    @Mock
    private ModelsRepository modelsRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierFoundTest() {
        Models models = new Models();
        ModelsDto dto = new ModelsDto();

        when(modelsRepository.findByIdentifier("M1")).thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class)).thenReturn(dto);

        ModelsDto result = service.findByIdentifier("M1");

        assertNotNull(result);
    }

    @Test
    void findByIdentifierNotFoundTest() {
        when(modelsRepository.findByIdentifier("M1")).thenReturn(null);

        ModelsDto result = service.findByIdentifier("M1");

        assertNull(result);
    }

    @Test
    void saveSuccessTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        when(modelsRepository.findByIdentifier("M1")).thenReturn(null);
        when(modelMapper.map(dto, Models.class)).thenReturn(new Models());

        ModelsDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(modelsRepository).save(any());
    }

    @Test
    void saveDuplicateActiveTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        Models existing = new Models();
        existing.setDeleted(false);

        when(modelsRepository.findByIdentifier("M1")).thenReturn(existing);

        ModelsDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(modelsRepository, never()).save(any());
    }

    @Test
    void saveDuplicateDeletedTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        Models existing = new Models();
        existing.setDeleted(true);

        when(modelsRepository.findByIdentifier("M1")).thenReturn(existing);

        ModelsDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void updateSuccessTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        Models existing = new Models();

        when(modelsRepository.findByIdentifier("M1")).thenReturn(existing);

        ModelsDto result = service.update(dto);

        assertTrue(result.isSuccess());
        verify(modelsRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        when(modelsRepository.findByIdentifier("M1")).thenReturn(null);

        ModelsDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(modelsRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Models models = new Models();
        models.setDeleted(false);

        when(modelsRepository.findByIdentifier("M1")).thenReturn(models);

        service.delete("M1");

        assertTrue(models.isDeleted());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Models> page = new PageImpl<>(List.of(new Models()), pageable, 1);

        when(modelsRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new ModelsDto()));

        WsDto<ModelsDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Models models = new Models();
        models.setStatus(true);

        when(modelsRepository.findByIdentifier("M1")).thenReturn(models);

        service.toggleStatus("M1");

        assertFalse(models.isStatus());
        verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Models models = new Models();
        models.setStatus(false);

        when(modelsRepository.findByIdentifier("M1")).thenReturn(models);

        service.toggleStatus("M1");

        assertTrue(models.isStatus());
        verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(modelsRepository.findByIdentifier("M1")).thenReturn(null);

        service.toggleStatus("M1");

        verify(modelsRepository, never()).save(any());
    }

    @Test
    void findActiveModelsTest() {
        when(modelsRepository.findByStatus(true)).thenReturn(List.of(new Models(), new Models()));

        List<Models> result = service.findActiveModels();

        assertEquals(2, result.size());
    }
}