package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.impl.ModelsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModelsServiceTest {

    @InjectMocks
    private ModelsServiceImpl service;

    @Mock
    private ModelsRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void save_shouldFail_whenNameNull() {
        ModelsDto dto = new ModelsDto();
        ModelsDto result = service.save(dto);
        assertFalse(result.isSuccess());
        assertEquals("Model name is required", result.getMessage());
    }

    @Test
    void save_shouldFail_whenNameEmpty() {
        ModelsDto dto = new ModelsDto();
        dto.setModelName("   ");
        ModelsDto result = service.save(dto);
        assertFalse(result.isSuccess());
        assertEquals("Model name is required", result.getMessage());
    }

    @Test
    void save_shouldFail_whenDuplicate() {
        ModelsDto dto = new ModelsDto();
        dto.setModelName("Test");
        when(repository.findByIdentifier("Test")).thenReturn(new Models());
        ModelsDto result = service.save(dto);
        assertFalse(result.isSuccess());
        assertEquals("Model already exists", result.getMessage());
    }

    @Test
    void save_shouldSuccess() {
        ModelsDto dto = new ModelsDto();
        dto.setModelName("Test");
        dto.setStatus(true);
        Models saved = new Models();
        saved.setIdentifier("Test");
        saved.setModelName("Test");
        saved.setStatus(true);
        ModelsDto mapped = new ModelsDto();
        mapped.setIdentifier("Test");
        mapped.setModelName("Test");
        mapped.setStatus(true);
        when(repository.findByIdentifier("Test")).thenReturn(null);
        when(repository.save(any())).thenReturn(saved);
        when(modelMapper.map(saved, ModelsDto.class)).thenReturn(mapped);
        ModelsDto result = service.save(dto);
        assertTrue(result.isSuccess());
        assertEquals("Model added successfully", result.getMessage());
        assertEquals("Test", result.getIdentifier());
        assertEquals("Test", result.getModelName());
        assertTrue(result.getStatus());
        verify(repository).save(any(Models.class));
    }

    @Test
    void save_shouldHandleNullStatus_defaultTrue() {
        ModelsDto dto = new ModelsDto();
        dto.setModelName("Test");
        dto.setStatus(null);
        Models saved = new Models();
        saved.setIdentifier("Test");
        saved.setModelName("Test");
        saved.setStatus(true);
        ModelsDto mapped = new ModelsDto();
        mapped.setIdentifier("Test");
        mapped.setModelName("Test");
        mapped.setStatus(true);
        when(repository.findByIdentifier("Test")).thenReturn(null);
        when(repository.save(any())).thenReturn(saved);
        when(modelMapper.map(saved, ModelsDto.class)).thenReturn(mapped);
        ModelsDto result = service.save(dto);
        assertTrue(result.getStatus());
    }

    @Test
    void update_shouldFail_whenNotFound() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("id");
        when(repository.findByIdentifier("id")).thenReturn(null);
        ModelsDto result = service.update(dto);
        assertFalse(result.isSuccess());
        assertEquals("Model not found", result.getMessage());
    }

    @Test
    void update_shouldSuccess() {
        Models model = new Models();
        model.setIdentifier("id");
        model.setStatus(false);
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("id");
        dto.setStatus(true);
        when(repository.findByIdentifier("id")).thenReturn(model);
        ModelsDto result = service.update(dto);
        assertTrue(result.isSuccess());
        assertEquals("Model updated successfully", result.getMessage());
        assertTrue(model.getStatus());
        verify(repository).save(model);
    }

    @Test
    void findAll_shouldReturnList() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Models> list = Arrays.asList(new Models(), new Models());
        Page<Models> page = new PageImpl<>(list);
        List<ModelsDto> mapped = Arrays.asList(new ModelsDto(), new ModelsDto());
        when(repository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(list), ArgumentMatchers.<Type>any())).thenReturn(mapped);
        List<ModelsDto> result = service.findAll(pageable);
        assertEquals(2, result.size());
    }

    @Test
    void findByIdentifier_shouldReturnNull_whenNotFound() {
        when(repository.findByIdentifier("id")).thenReturn(null);
        ModelsDto result = service.findByIdentifier("id");
        assertNull(result);
    }

    @Test
    void findByIdentifier_shouldReturnDto() {
        Models model = new Models();
        model.setIdentifier("id");
        model.setModelName("name");
        model.setStatus(true);
        when(repository.findByIdentifier("id")).thenReturn(model);
        ModelsDto result = service.findByIdentifier("id");
        assertNotNull(result);
        assertEquals("id", result.getIdentifier());
        assertEquals("name", result.getModelName());
        assertTrue(result.getStatus());
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete("id");
        verify(repository).deleteByIdentifier("id");
    }

    @Test
    void toggleStatus_shouldFail_whenNotFound() {
        when(repository.findByIdentifier("id")).thenReturn(null);
        ModelsDto result = service.toggleStatus("id");
        assertFalse(result.isSuccess());
        assertEquals("Model not found", result.getMessage());
    }

    @Test
    void toggleStatus_shouldToggleTrueToFalse() {
        Models model = new Models();
        model.setIdentifier("id");
        model.setModelName("name");
        model.setStatus(true);
        Models saved = new Models();
        saved.setIdentifier("id");
        saved.setModelName("name");
        saved.setStatus(false);
        when(repository.findByIdentifier("id")).thenReturn(model);
        when(repository.save(model)).thenReturn(saved);
        ModelsDto result = service.toggleStatus("id");
        assertFalse(result.getStatus());
        assertEquals("Status updated successfully", result.getMessage());
    }

    @Test
    void toggleStatus_shouldToggleFalseToTrue() {
        Models model = new Models();
        model.setIdentifier("id");
        model.setModelName("name");
        model.setStatus(false);
        Models saved = new Models();
        saved.setIdentifier("id");
        saved.setModelName("name");
        saved.setStatus(true);
        when(repository.findByIdentifier("id")).thenReturn(model);
        when(repository.save(model)).thenReturn(saved);
        ModelsDto result = service.toggleStatus("id");
        assertTrue(result.getStatus());
    }

    @Test
    void toggleStatus_shouldHandleNullStatus() {
        Models model = new Models();
        model.setIdentifier("id");
        model.setModelName("name");
        model.setStatus(null);
        Models saved = new Models();
        saved.setIdentifier("id");
        saved.setModelName("name");
        saved.setStatus(true);
        when(repository.findByIdentifier("id")).thenReturn(model);
        when(repository.save(model)).thenReturn(saved);
        ModelsDto result = service.toggleStatus("id");
        assertTrue(result.getStatus());
    }

    @Test
    void findActiveModels_shouldReturnOnlyActive() {
        Models active = new Models();
        active.setIdentifier("1");
        active.setModelName("A");
        active.setStatus(true);
        Models inactive = new Models();
        inactive.setStatus(false);
        Models nullModel = new Models();
        nullModel.setStatus(null);
        when(repository.findAll()).thenReturn(Arrays.asList(active, inactive, nullModel));
        List<ModelsDto> result = service.findActiveModels();
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getIdentifier());
    }

    @Test
    void findActiveModels_shouldReturnEmpty() {
        Models m = new Models();
        m.setStatus(false);
        when(repository.findAll()).thenReturn(Collections.singletonList(m));
        List<ModelsDto> result = service.findActiveModels();
        assertTrue(result.isEmpty());
    }
}