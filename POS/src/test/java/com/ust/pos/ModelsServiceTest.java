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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {
    
    @InjectMocks
    private ModelsServiceImpl modelsService;
    @Mock
    private ModelsRepository modelsRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MDL_107");

        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(null);
        Models models = new Models();
        Mockito.when(modelMapper.map(modelsDto, Models.class)).thenReturn(models);
        Mockito.when(modelsRepository.save(models)).thenReturn(models);
        ModelsDto response = modelsService.save(modelsDto);
        Assertions.assertEquals("MDL_107", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MDL_107");
        Models models = new Models();
        
        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(models);
        ModelsDto response = modelsService.save(modelsDto);
        Assertions.assertEquals("MDL_107", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Models models = new Models();
        models.setIdentifier("MDL_107");
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MDL_107");
        
        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(models);
        Mockito.when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);
        ModelsDto response = modelsService.findByIdentifier("MDL_107");
        Assertions.assertEquals("MDL_107", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MDL_107");
        Models existingModels = new Models();
        existingModels.setIdentifier("MDL_107");
        
        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(existingModels);
        Mockito.when(modelsRepository.save(existingModels)).thenReturn(existingModels);
        ModelsDto response = modelsService.update(modelsDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MDL_107");
        
        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(null);
        ModelsDto response = modelsService.update(modelsDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(modelsRepository).deleteByIdentifier("MDL_107");
        modelsService.delete("MDL_107");
        Mockito.verify(modelsRepository).deleteByIdentifier("MDL_107");
    }

    @Test
    void findAllTest() {
        Models models = new Models();
        models.setIdentifier("MDL_107");

        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MDL_107");

        List<Models> modelsList = List.of(models);
        List<ModelsDto> modelsDtos = List.of(modelsDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Models> modelsPage = new PageImpl<>(modelsList, pageable, modelsList.size());
        Mockito.when(modelsRepository.findAll(pageable)).thenReturn(modelsPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(modelsList),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(modelsDtos);
        List<ModelsDto> response = modelsService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("MDL_107", response.get(0).getIdentifier());
    }

    @Test
    void changeModelsStatusSuccessTest() {
        Models models = new Models();
        models.setIdentifier("MDL_107");
        models.setStatus(false);
        
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MDL_107");
        modelsDto.setStatus(true); 
        
        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(models);
        Mockito.when(modelsRepository.save(models)).thenReturn(models);
        Mockito.when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);
        ModelsDto response = modelsService.toggleStatus("MDL_107", true);
        Assertions.assertEquals("MDL_107", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleStatusFailureTest() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MDL_107");
        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(null);
        ModelsDto response = modelsService.toggleStatus("MDL_107", true);
        Assertions.assertNull(response);
        Mockito.verify(modelsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveModelssTest() {
        Models models = new Models();
        models.setIdentifier("MDL_107");
        models.setStatus(true);

        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MDL_107");
        modelsDto.setStatus(true);

        List<Models> activeModelss = List.of(models);
        List<ModelsDto> activeModelsDtos = List.of(modelsDto);
        Mockito.when(modelsRepository.findByStatusTrue()).thenReturn(activeModelss);
        Mockito.when(modelMapper.map(
                Mockito.eq(activeModelss),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(activeModelsDtos);
        List<ModelsDto> response = modelsService.findActiveModel();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("MDL_107", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
    }
}