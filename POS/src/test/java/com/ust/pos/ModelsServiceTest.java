package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {

    @Mock
    private ModelsRepository modelsRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ModelsServiceImpl modelsService;

    @Test
    void findByIdentifierTest() {
        Models models = new Models();
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MOD01");

        Mockito.when(modelsRepository.findByIdentifier("MOD01")).thenReturn(models);
        Mockito.when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);

        ModelsDto response = modelsService.findByIdentifier("MOD01");

        Assertions.assertEquals("MOD01", response.getIdentifier());
    }

    @Test
    void findByIdentifierTestFailure() {
        Mockito.when(modelsRepository.findByIdentifier("MOD01")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            modelsService.findByIdentifier("MOD01");
        });
    }

    @Test
    void toggleStatusTest() {
        Models models = new Models();
        models.setStatus(false);
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setStatus(true);

        Mockito.when(modelsRepository.findByIdentifier("MOD01")).thenReturn(models);
        Mockito.when(modelsRepository.save(models)).thenReturn(models);
        Mockito.when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);

        ModelsDto response = modelsService.toggleStatus("MOD01");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void saveTestSuccess() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MOD01 ");

        Mockito.when(modelsRepository.findByIdentifier("MOD01")).thenReturn(null);
        Models models = new Models();
        Mockito.when(modelMapper.map(modelsDto, Models.class)).thenReturn(models);
        Mockito.when(modelsRepository.save(models)).thenReturn(models);

        ModelsDto response = modelsService.save(modelsDto);

        Assertions.assertEquals("MOD01", response.getIdentifier());
    }

    @Test
    void saveTestFailureAlreadyExists() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MOD01");

        Models existingModels = new Models();
        existingModels.setIdentifier("MOD01");

        Mockito.when(modelsRepository.findByIdentifier("MOD01")).thenReturn(existingModels);

        ModelsDto response = modelsService.save(modelsDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Models with identifier - MOD01 already exists", response.getMessage());
    }

    @Test
    void updateTestSuccess() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MOD01");

        Models existingModels = new Models();
        existingModels.setIdentifier("MOD01");

        Mockito.when(modelsRepository.findByIdentifier("MOD01")).thenReturn(existingModels);
        Mockito.when(modelsRepository.save(existingModels)).thenReturn(existingModels);

        ModelsDto response = modelsService.update(modelsDto);

        Assertions.assertEquals("MOD01", response.getIdentifier());
    }

    @Test
    void updateTestFailure() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MOD01");

        Mockito.when(modelsRepository.findByIdentifier("MOD01")).thenReturn(null);

        ModelsDto response = modelsService.update(modelsDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Models with identifier - MOD01 not found", response.getMessage());
    }

    @Test
    void deleteTestSuccess() {
        Models models = new Models();

        Mockito.when(modelsRepository.findByIdentifier("MOD01")).thenReturn(models);
        Mockito.when(modelsRepository.save(models)).thenReturn(models);

        boolean response = modelsService.delete("MOD01");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(modelsRepository.findByIdentifier("MOD01")).thenReturn(null);

        boolean response = modelsService.delete("MOD01");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Models models = new Models();
        List<Models> modelsList = List.of(models);
        Page<Models> modelsPage = new PageImpl<>(modelsList, pageable, modelsList.size());

        ModelsDto modelsDto = new ModelsDto();
        List<ModelsDto> modelsDtos = List.of(modelsDto);

        Mockito.when(modelsRepository.findByDeletedFalse(pageable)).thenReturn(modelsPage);
        Mockito.when(modelMapper.map(Mockito.eq(modelsList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(modelsDtos);

        WsDto<ModelsDto> response = modelsService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findIfTrueTest() {
        Models models = new Models();
        List<Models> modelsList = List.of(models);
        ModelsDto modelsDto = new ModelsDto();
        List<ModelsDto> modelsDtos = List.of(modelsDto);

        Mockito.when(modelsRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(modelsList);
        Mockito.when(modelMapper.map(Mockito.eq(modelsList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(modelsDtos);

        List<ModelsDto> response = modelsService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<Models> specification = Mockito.mock(Specification.class);
        Models models = new Models();
        List<Models> modelsList = List.of(models);
        Page<Models> page = new PageImpl<>(modelsList, pageable, modelsList.size());

        ModelsDto modelsDto = new ModelsDto();
        List<ModelsDto> modelsDtos = List.of(modelsDto);

        Mockito.when(modelsRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(modelsList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(modelsDtos);

        WsDto<ModelsDto> response = modelsService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}