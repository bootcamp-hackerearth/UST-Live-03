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

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        modelsDto.setIdentifier("Admin");
        Models models = new Models();

        Mockito.when(modelsRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(modelsDto, Models.class)).thenReturn(models);

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

        Mockito.when(modelsRepository.findByIdentifier("Admin")).thenReturn(models);

        ModelsDto response = modelsService.save(modelsDto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Models models = new Models();
        models.setIdentifier("Admin");
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("Admin");

        Mockito.when(modelsRepository.findByIdentifier("Admin")).thenReturn(models);
        Mockito.when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);

        ModelsDto response = modelsService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("Admin");
        Models existingModels = new Models();
        existingModels.setIdentifier("Admin");

        Mockito.when(modelsRepository.findByIdentifier("Admin")).thenReturn(existingModels);
        Mockito.when(modelsRepository.save(existingModels)).thenReturn(existingModels);

        ModelsDto response = modelsService.update(modelsDto);
        assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("Admin");

        Mockito.when(modelsRepository.findByIdentifier("Admin")).thenReturn(null);

        ModelsDto response = modelsService.update(modelsDto);
        assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(modelsRepository).deleteByIdentifier("Admin");

        modelsService.delete("Admin");
        Mockito.verify(modelsRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Models models = new Models();
        models.setIdentifier("M1");
        List<Models> modelsList = List.of(models);
        Page<Models> modelsPage = new PageImpl<>(modelsList);

        Mockito.when(modelsRepository.findAll(pageable)).thenReturn(modelsPage);

        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("M1");
        List<ModelsDto> modelsDtoList = List.of(modelsDto);

        Mockito.when(modelMapper.map(Mockito.eq(modelsPage.getContent()), Mockito.any(Type.class))).thenReturn(modelsDtoList);

        WsDto<ModelsDto> result = modelsService.findAll(pageable);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals("M1", result.getDtoList().get(0).getIdentifier());
        Mockito.verify(modelsRepository).findAll(pageable);
    }

    @Test
    void toggleStatusTest_TrueToFalse() {
        Models models = new Models();
        models.setIdentifier("M1");
        models.setStatus(true);

        Mockito.when(modelsRepository.findByIdentifier("M1")).thenReturn(models);

        modelsService.toggleStatus("M1");
        assertFalse(models.isStatus());
        Mockito.verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatusTest_FalseToTrue() {
        Models models = new Models();
        models.setIdentifier("M1");
        models.setStatus(false);

        Mockito.when(modelsRepository.findByIdentifier("M1")).thenReturn(models);

        modelsService.toggleStatus("M1");
        assertTrue(models.isStatus());
        Mockito.verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatusTest_NotFound() {
        Mockito.when(modelsRepository.findByIdentifier("M1")).thenReturn(null);
        assertThrows(NullPointerException.class,() -> modelsService.toggleStatus("M1"));
    }

    @Test
    void findActiveModelsTest() {
        Models model = new Models();
        model.setIdentifier("M1");
        model.setStatus(true);
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");
        List<Models> modelsList = List.of(model);
        List<ModelsDto> dtoList = List.of(dto);

        Mockito.when(modelsRepository.findByStatusTrue()).thenReturn(modelsList);
        Mockito.when(modelMapper.map(Mockito.eq(modelsList),Mockito.any(java.lang.reflect.Type.class))).thenReturn(dtoList);

        List<ModelsDto> response = modelsService.findActiveModels();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("M1", response.get(0).getIdentifier());
        Mockito.verify(modelsRepository).findByStatusTrue();
    }

}