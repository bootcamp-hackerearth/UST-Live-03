package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
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
import static org.mockito.ArgumentMatchers.any;
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

        Models existing = new Models();
        existing.setDeleted(false);

        when(repository.findByIdentifier("Test"))
                .thenReturn(existing);

        ModelsDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Model already exists", result.getMessage());

        verify(repository, never()).save(any());
    }

    @Test
    void save_shouldSuccess() {

        ModelsDto dto = new ModelsDto();
        dto.setModelName("Test");
        dto.setStatus(true);

        when(repository.findByIdentifier("Test"))
                .thenReturn(null);

        ModelsDto result = service.save(dto);

        assertTrue(result.isSuccess());
        assertEquals("Model saved successfully", result.getMessage());
        assertEquals("Test", result.getIdentifier());

        verify(repository).save(any(Models.class));
    }

    @Test
    void save_shouldHandleNullStatus() {

        ModelsDto dto = new ModelsDto();
        dto.setModelName("Test");
        dto.setStatus(null);

        when(repository.findByIdentifier("Test"))
                .thenReturn(null);

        ModelsDto result = service.save(dto);

        assertTrue(result.isSuccess());
        assertNull(result.getStatus());

        verify(repository).save(any(Models.class));
    }

    @Test
    void update_shouldFail_whenNotFound() {

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("id");

        when(repository.findByIdentifier("id"))
                .thenReturn(null);

        ModelsDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertEquals("Model not found", result.getMessage());
    }

    @Test
    void update_shouldFail_whenDeleted() {

        Models model = new Models();
        model.setDeleted(true);

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("id");

        when(repository.findByIdentifier("id"))
                .thenReturn(model);

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
        dto.setModelName("Updated");
        dto.setStatus(true);

        when(repository.findByIdentifier("id"))
                .thenReturn(model);

        ModelsDto result = service.update(dto);

        assertTrue(result.isSuccess());
        assertEquals("Model updated successfully", result.getMessage());
        assertEquals("Updated", model.getModelName());
        assertTrue(model.getStatus());

        verify(repository).save(model);
    }

    @Test
    void findAll_shouldReturnList() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Models> list =
                Arrays.asList(new Models(), new Models());

        Page<Models> page = new PageImpl<>(list);

        List<ModelsDto> mapped =
                Arrays.asList(new ModelsDto(), new ModelsDto());

        when(repository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(list),
                ArgumentMatchers.<Type>any()))
                .thenReturn(mapped);

        WsDto<ModelsDto> result =
                service.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getDtoList().size());
    }

    @Test
    void findByIdentifier_shouldReturnNotFound() {

        when(repository.findByIdentifier("id"))
                .thenReturn(null);

        ModelsDto result = service.findByIdentifier("id");

        assertFalse(result.isSuccess());
        assertEquals("Model not found", result.getMessage());
    }

    @Test
    void findByIdentifier_shouldReturnDeletedNotFound() {

        Models model = new Models();
        model.setDeleted(true);

        when(repository.findByIdentifier("id"))
                .thenReturn(model);

        ModelsDto result = service.findByIdentifier("id");

        assertFalse(result.isSuccess());
        assertEquals("Model not found", result.getMessage());
    }

    @Test
    void findByIdentifier_shouldReturnDto() {

        Models model = new Models();
        model.setIdentifier("id");
        model.setModelName("name");
        model.setStatus(true);
        model.setDeleted(false);

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("id");
        dto.setModelName("name");
        dto.setStatus(true);

        when(repository.findByIdentifier("id"))
                .thenReturn(model);

        when(modelMapper.map(model, ModelsDto.class))
                .thenReturn(dto);

        ModelsDto result = service.findByIdentifier("id");

        assertNotNull(result);
        assertEquals("id", result.getIdentifier());
        assertEquals("name", result.getModelName());
        assertTrue(result.getStatus());
    }

    @Test
    void delete_shouldSoftDelete() {

        Models model = new Models();
        model.setIdentifier("id");
        model.setDeleted(false);

        when(repository.findByIdentifier("id"))
                .thenReturn(model);

        service.delete("id");

        assertTrue(model.getDeleted());

        verify(repository).save(model);
    }

    @Test
    void delete_shouldIgnoreWhenNotFound() {

        when(repository.findByIdentifier("id"))
                .thenReturn(null);

        service.delete("id");

        verify(repository, never()).save(any());
    }

    @Test
    void toggleStatus_shouldFail_whenNotFound() {

        when(repository.findByIdentifier("id"))
                .thenReturn(null);

        ModelsDto result = service.toggleStatus("id");

        assertFalse(result.isSuccess());
        assertEquals("Model not found", result.getMessage());
    }

    @Test
    void toggleStatus_shouldFail_whenDeleted() {

        Models model = new Models();
        model.setDeleted(true);

        when(repository.findByIdentifier("id"))
                .thenReturn(model);

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

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("id");
        dto.setModelName("name");
        dto.setStatus(false);

        when(repository.findByIdentifier("id"))
                .thenReturn(model);

        when(modelMapper.map(model, ModelsDto.class))
                .thenReturn(dto);

        ModelsDto result = service.toggleStatus("id");

        assertFalse(result.getStatus());
        assertEquals("Status updated successfully", result.getMessage());

        verify(repository).save(model);
    }

    @Test
    void toggleStatus_shouldToggleFalseToTrue() {

        Models model = new Models();
        model.setIdentifier("id");
        model.setStatus(false);

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("id");
        dto.setStatus(true);

        when(repository.findByIdentifier("id"))
                .thenReturn(model);

        when(modelMapper.map(model, ModelsDto.class))
                .thenReturn(dto);

        ModelsDto result = service.toggleStatus("id");

        assertTrue(result.getStatus());

        verify(repository).save(model);
    }

    @Test
    void toggleStatus_shouldHandleNullStatus() {

        Models model = new Models();
        model.setIdentifier("id");
        model.setStatus(null);

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("id");
        dto.setStatus(true);

        when(repository.findByIdentifier("id"))
                .thenReturn(model);

        when(modelMapper.map(model, ModelsDto.class))
                .thenReturn(dto);

        ModelsDto result = service.toggleStatus("id");

        assertTrue(result.getStatus());

        verify(repository).save(model);
    }

    @Test
    void findActiveModels_shouldReturnOnlyActive() {

        Models active = new Models();
        active.setIdentifier("1");
        active.setModelName("A");
        active.setStatus(true);

        List<Models> models = List.of(active);

        List<ModelsDto> dtos = List.of(new ModelsDto());

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(models);

        when(modelMapper.map(
                eq(models),
                ArgumentMatchers.<Type>any()))
                .thenReturn(dtos);

        List<ModelsDto> result = service.findActiveModels();

        assertEquals(1, result.size());
    }

    @Test
    void findActiveModels_shouldReturnEmpty() {

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(Collections.emptyList());

        when(modelMapper.map(
                eq(Collections.emptyList()),
                ArgumentMatchers.<Type>any()))
                .thenReturn(Collections.emptyList());

        List<ModelsDto> result = service.findActiveModels();

        assertTrue(result.isEmpty());
    }
}