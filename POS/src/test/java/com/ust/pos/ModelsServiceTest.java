package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
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
        modelsDto.setIdentifier("S1");
        Mockito.when(modelsRepository.findByIdentifier("S1")).thenReturn(null);
        Models models = new Models();
        Mockito.when(modelMapper.map(modelsDto, Models.class)).thenReturn(models);
        Mockito.when(modelsRepository.save(models)).thenReturn(models);
        ModelsDto response = modelsService.save(modelsDto);
        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("S1");
        Models existingModels = new Models();
        Mockito.when(modelsRepository.findByIdentifier("S1")).thenReturn(existingModels);
        ModelsDto response = modelsService.save(modelsDto);
        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Models models = new Models();
        models.setIdentifier("S1");
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("S1");
        Mockito.when(modelsRepository.findByIdentifier("S1")).thenReturn(models);
        Mockito.when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);
        ModelsDto response = modelsService.findByIdentifier("S1");
        Assertions.assertEquals("S1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("S1");
        Models existingModels = new Models();
        existingModels.setIdentifier("S1");
        Mockito.when(modelsRepository.findByIdentifier("S1"))
                .thenReturn(existingModels);
        Mockito.when(modelMapper.map(modelsDto, Models.class))
                .thenReturn(existingModels);
        Mockito.when(modelsRepository.save(existingModels))
                .thenReturn(existingModels);
        ModelsDto response = modelsService.update(modelsDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("S1");
        Mockito.when(modelsRepository.findByIdentifier("S1"))
                .thenReturn(null);
        ModelsDto response = modelsService.update(modelsDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(modelsRepository)
                .deleteByIdentifier("S1");
        modelsService.delete("S1");
        Mockito.verify(modelsRepository).deleteByIdentifier("S1");
    }

    @Test
    void findAllTest() {
        Models model = new Models();
        model.setIdentifier("M1");
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("M1");
        List<Models> modelsList = List.of(model);
        List<ModelsDto> modelsDtoList = List.of(modelsDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Models> modelsPage = new PageImpl<>(modelsList);
        Mockito.when(modelsRepository.findAll(pageable))
                .thenReturn(modelsPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(modelsList),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(modelsDtoList);
        WsDto<ModelsDto> response = modelsService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("M1", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void updateStatusTest() {
        Models models = new Models();
        models.setIdentifier("S1");
        Mockito.when(modelsRepository.findByIdentifier("S1"))
                .thenReturn(models);
        Mockito.when(modelsRepository.save(models))
                .thenReturn(models);
        modelsService.updateStatus("S1", true);
        Mockito.verify(modelsRepository).save(models);
    }

    @Test
    void findAllActiveTest() {
        Models models = new Models();
        models.setIdentifier("S1");
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("S1");
        List<Models> shelves = List.of(models);
        List<ModelsDto> modelsDtos = List.of(modelsDto);
        Mockito.when(modelsRepository.findByStatus(true)).thenReturn(shelves);
        Mockito.when(modelMapper.map(
                Mockito.eq(shelves),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(modelsDtos);
        List<ModelsDto> response = modelsService.findAllActive();
        Assertions.assertEquals(1, response.size());
    }
}