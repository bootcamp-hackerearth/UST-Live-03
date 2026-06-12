package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.impl.ModelServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {
    @InjectMocks
    private ModelServiceImpl modelsService;
    @Mock
    private ModelsRepository modelsRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("Admin");

        Models models = new Models();
        when(modelsRepository.findByIdentifier("Admin")).thenReturn(null);
        when(modelMapper.map(modelsDto, Models.class)).thenReturn(models);
        ModelsDto response = modelsService.save(modelsDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());

        assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("Admin");

        Models models = new Models();
        when(modelsRepository.findByIdentifier("Admin")).thenReturn(models);
        ModelsDto response = modelsService.save(modelsDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());

        assertFalse(response.isSuccess());

    }

    @Test
    void findByIdentifierSuccessTest() {
        Models models = new Models();
        ModelsDto dto = new ModelsDto();

        when(modelsRepository.findByIdentifier("M1")).thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class)).thenReturn(dto);

        ModelsDto result = modelsService.findByIdentifier("M1");

        assertNotNull(result);

        verify(modelsRepository).findByIdentifier("M1");
        verify(modelMapper).map(models, ModelsDto.class);
    }

    @Test
    void findByIdentifierNullTest() {
        when(modelsRepository.findByIdentifier("M1")).thenReturn(null);

        ModelsDto result = modelsService.findByIdentifier("M1");

        assertNull(result);

        verify(modelsRepository).findByIdentifier("M1");
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void updateTest() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("Admin");

        Models existingModels = new Models();
        existingModels.setIdentifier("Admin");

        when(modelsRepository.findByIdentifier("Admin"))
                .thenReturn(existingModels);
        when(modelsRepository.save(existingModels))
                .thenReturn(existingModels);

        ModelsDto response = modelsService.update(modelsDto);

        assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("Admin");

        when(modelsRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        ModelsDto response = modelsService.update(modelsDto);

        assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(modelsRepository)
                .deleteByIdentifier("Admin");

        modelsService.delete("Admin");

        verify(modelsRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Models models = new Models();
        models.setIdentifier("Admin");

        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("Admin");

        List<Models> modelsList = List.of(models);
        Page<Models> page = new PageImpl<>(modelsList);
        List<ModelsDto> modelsDtos = List.of(modelsDto);

        when(modelsRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(
                Mockito.any(),
                any(java.lang.reflect.Type.class)
        )).thenReturn(modelsDtos);

        WsDto<ModelsDto> response = modelsService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getContent());
        Assertions.assertEquals(1, response.getContent().size());

        verify(modelsRepository).findAll(pageable);
    }

    @Test
    void toggleStatusTest_TrueToFalse() {
        Models models = new Models();
        models.setIdentifier("M1");
        models.setStatus(true);

        when(modelsRepository.findByIdentifier("M1"))
                .thenReturn(models);

        modelsService.toggleStatus("M1");

        assertFalse(models.isStatus()); // toggled
        verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatusTest_FalseToTrue() {
        Models models = new Models();
        models.setIdentifier("M1");
        models.setStatus(false);

        when(modelsRepository.findByIdentifier("M1"))
                .thenReturn(models);

        modelsService.toggleStatus("M1");

        assertTrue(models.isStatus()); // toggled
        verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatusTest_NotFound() {
        when(modelsRepository.findByIdentifier("M1"))
                .thenReturn(null);

        modelsService.toggleStatus("M1");

        verify(modelsRepository, never())
                .save(any());
    }

    @Test
    void findActiveModelsTest() {
        Models model = new Models();
        model.setIdentifier("M1");
        model.setStatus(true);

        List<Models> modelsList = List.of(model);
        when(modelsRepository.findByStatus(true))
                .thenReturn(modelsList);

        List<Models> response = modelsService.findActiveModels();

        assertEquals(1, response.size());
        assertEquals("M1", response.get(0).getIdentifier());

        verify(modelsRepository).findByStatus(true);
    }
}