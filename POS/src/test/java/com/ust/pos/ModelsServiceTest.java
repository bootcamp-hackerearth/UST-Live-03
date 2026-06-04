package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {

    @InjectMocks
    private ModelsServiceImpl modelsService;

    @Mock
    private ModelsRepository modelsRepository;

    @Mock(lenient = true)
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL01");

        Models models = new Models();
        models.setIdentifier("MDL01");

        when(modelsRepository.findByIdentifier("MDL01")).thenReturn(null);
        when(modelMapper.map(dto, Models.class)).thenReturn(models);

        ModelsDto response = modelsService.save(dto);

        assertEquals("MDL01", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        verify(modelsRepository).save(models);
    }

    @Test
    void saveFailureTest() {
        Models models = new Models();
        models.setIdentifier("MDL01");

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL01");

        when(modelsRepository.findByIdentifier("MDL01")).thenReturn(models);

        ModelsDto response = modelsService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(modelsRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        Models models = new Models();
        models.setIdentifier("MDL01");

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL01");

        when(modelsRepository.findByIdentifier("MDL01")).thenReturn(models);

        ModelsDto response = modelsService.update(dto);

        assertEquals("MDL01", response.getIdentifier());
        verify(modelMapper).map(dto, models);
        verify(modelsRepository).save(models);
    }

    @Test
    void updateFailureTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL01");

        when(modelsRepository.findByIdentifier("MDL01")).thenReturn(null);

        ModelsDto response = modelsService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(modelsRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        modelsService.delete("MDL01");
        verify(modelsRepository).deleteByIdentifier("MDL01");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Models models = new Models();
        models.setIdentifier("MDL01");

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL01");

        when(modelsRepository.findByIdentifier("MDL01")).thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class)).thenReturn(dto);

        ModelsDto response = modelsService.findByIdentifier("MDL01");

        assertNotNull(response);
        assertEquals("MDL01", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(modelsRepository.findByIdentifier("MDL01")).thenReturn(null);

        ModelsDto response = modelsService.findByIdentifier("MDL01");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Models> modelsList = List.of(new Models(), new Models());
        Page<Models> page = new PageImpl<>(modelsList);

        List<ModelsDto> dtoList = List.of(new ModelsDto(), new ModelsDto());

        when(modelsRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(modelsList),
                any(Type.class)
        )).thenReturn(dtoList);

        List<ModelsDto> result = modelsService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(modelsRepository).findAll(pageable);
    }

    @Test
    void toggleStatusSuccessTest() {
        Models models = new Models();
        models.setStatus(true);

        when(modelsRepository.findByIdentifier("MDL01")).thenReturn(models);

        modelsService.toggleStatus("MDL01");

        Assertions.assertFalse(models.isStatus());
        verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatusFailureTest() {
        when(modelsRepository.findByIdentifier("MDL01")).thenReturn(null);

        modelsService.toggleStatus("MDL01");

        verify(modelsRepository, never()).save(any());
    }
}