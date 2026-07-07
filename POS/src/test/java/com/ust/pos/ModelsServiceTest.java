package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.impl.ModelsServiceImpl;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {

    @InjectMocks
    @Spy
    private ModelsServiceImpl modelsService;

    @Mock
    private ModelsRepository modelsRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierTest() {

        Models models = new Models();
        models.setIdentifier("M1");

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        when(modelsRepository.findByIdentifier("M1")).thenReturn(models);

        when(modelMapper.map(models, ModelsDto.class)).thenReturn(dto);

        ModelsDto result = modelsService.findByIdentifier("M1");

        assertNotNull(result);
        assertEquals("M1", result.getIdentifier());

        verify(modelsRepository).findByIdentifier("M1");

    }

    @Test
    void findByIdentifierNotFoundTest() {

        when(modelsRepository.findByIdentifier("M1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> modelsService.findByIdentifier("M1"));

    }

    @Test
    void toggleStatusTrueToFalseTest() {

        Models models = new Models();
        models.setStatus(true);

        ModelsDto dto = new ModelsDto();

        when(modelsRepository.findByIdentifier("M1")).thenReturn(models);

        when(modelMapper.map(models, ModelsDto.class)).thenReturn(dto);

        ModelsDto result = modelsService.toggleStatus("M1");

        assertNotNull(result);

        assertFalse(models.isStatus());

        verify(modelsRepository).save(models);

    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Models models = new Models();
        models.setStatus(false);

        ModelsDto dto = new ModelsDto();

        when(modelsRepository.findByIdentifier("M1")).thenReturn(models);

        when(modelMapper.map(models, ModelsDto.class)).thenReturn(dto);

        modelsService.toggleStatus("M1");

        assertTrue(models.isStatus());

        verify(modelsRepository).save(models);

    }

    @Test
    void toggleStatusNotFoundTest() {

        when(modelsRepository.findByIdentifier("M1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> modelsService.toggleStatus("M1"));

    }

    @Test
    void saveTest() {

        ModelsDto dto = new ModelsDto();

        dto.setIdentifier(" MODEL1 ");

        Models models = new Models();

        when(modelsRepository.findByIdentifier("MODEL1")).thenReturn(null);

        when(modelMapper.map(dto, Models.class)).thenReturn(models);

        ModelsDto result = modelsService.save(dto);

        assertEquals("MODEL1", result.getIdentifier());

        verify(modelsRepository).save(models);

    }

    @Test
    void saveAlreadyExistsTest() {

        ModelsDto dto = new ModelsDto();

        dto.setIdentifier("MODEL1");

        Models existing = new Models();

        when(modelsRepository.findByIdentifier("MODEL1")).thenReturn(existing);

        ModelsDto result = modelsService.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Models with identifier - MODEL1 already exists", result.getMessage());

        verify(modelsRepository, never()).save(any());

    }

    @Test
    void saveSoftDeletedTest() {

        ModelsDto dto = new ModelsDto();

        dto.setIdentifier("MODEL1");

        Models existing = new Models();

        existing.setDeleted(true);

        when(modelsRepository.findByIdentifier("MODEL1")).thenReturn(existing);

        ModelsDto result = modelsService.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Models with identifier - MODEL1 has been soft deleted.(Rollback by changing status", result.getMessage());

        verify(modelsRepository, never()).save(any());

    }

    @Test
    void updateTest() {

        ModelsDto dto = new ModelsDto();

        dto.setIdentifier("MODEL1");

        Models existing = new Models();

        existing.setIdentifier("MODEL1");

        doNothing().when(modelMapper).map(dto, existing);

        when(modelsRepository.findByIdentifier("MODEL1")).thenReturn(existing);

        ModelsDto result = modelsService.update(dto);

        assertEquals("MODEL1", result.getIdentifier());

        verify(modelMapper).map(dto, existing);

        verify(modelsRepository).save(existing);

    }

    @Test
    void updateNotFoundTest() {

        ModelsDto dto = new ModelsDto();

        dto.setIdentifier("MODEL1");

        when(modelsRepository.findByIdentifier("MODEL1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> modelsService.update(dto));

    }

    @Test
    void deleteTest() {

        Models models = new Models();

        when(modelsRepository.findByIdentifier("MODEL1")).thenReturn(models);

        boolean result = modelsService.delete("MODEL1");

        assertTrue(result);

        assertTrue(models.isDeleted());

        verify(modelsRepository).save(models);

    }

    @Test
    void deleteNotFoundTest() {

        when(modelsRepository.findByIdentifier("MODEL1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> modelsService.delete("MODEL1"));

    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Models models = new Models();

        ModelsDto dto = new ModelsDto();

        Page<Models> page = new PageImpl<>(List.of(models));

        when(modelsRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(dto));

        WsDto<ModelsDto> result = modelsService.findAll(pageable);

        assertEquals(1, result.getDtoList().size());

        assertEquals(0, result.getPage());

        assertEquals(10, result.getSizePerPage());

    }

    @Test
    void findAllSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Models> spec = (root, query, cb) -> null;

        Models models = new Models();

        ModelsDto dto = new ModelsDto();

        Page<Models> page = new PageImpl<>(List.of(models));

        when(modelsRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(dto));

        WsDto<ModelsDto> result = modelsService.findAll(spec, pageable, "abc");

        assertEquals("abc", result.getKeyword());

        assertEquals(1, result.getDtoList().size());

    }

    @Test
    void findIfTrueTest() {

        List<Models> models = List.of(new Models());

        List<ModelsDto> dtos = List.of(new ModelsDto());

        when(modelsRepository.findByStatusIsTrue()).thenReturn(models);

        when(modelMapper.map(eq(models), any(Type.class))).thenReturn(dtos);

        List<ModelsDto> result = modelsService.findIfTrue();

        assertEquals(1, result.size());

        verify(modelsRepository).findByStatusIsTrue();

    }

}