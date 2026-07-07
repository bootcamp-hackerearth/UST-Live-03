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

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {

    @InjectMocks
    private ModelsServiceImpl modelsService;

    @Mock
    private ModelsRepository modelsRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MOD1");

        Models models = new Models();

        Mockito.when(modelsRepository.findByIdentifier("MOD1")).thenReturn(null);
        Mockito.when(modelMapper.map(modelsDto, Models.class)).thenReturn(models);

        ModelsDto response = modelsService.save(modelsDto);

        Assertions.assertEquals("MOD1", response.getIdentifier());
        verify(modelsRepository).save(models);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MOD1");

        Models existingModels = new Models();
        existingModels.setDeleted(false);

        Mockito.when(modelsRepository.findByIdentifier("MOD1")).thenReturn(existingModels);

        ModelsDto response = modelsService.save(modelsDto);

        Assertions.assertEquals("MOD1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Models with identifier - MOD1 already exists", response.getMessage());
        Mockito.verify(modelsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureAlreadyDeletedTest() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MOD1");

        Models existingModels = new Models();
        existingModels.setDeleted(true);

        Mockito.when(modelsRepository.findByIdentifier("MOD1")).thenReturn(existingModels);

        ModelsDto response = modelsService.save(modelsDto);

        Assertions.assertEquals("MOD1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Models with identifier - MOD1 was deleted , Please Contact the Administrator to add.", response.getMessage());
        Mockito.verify(modelsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MOD1");

        Models existingModels = new Models();
        existingModels.setIdentifier("MOD1");

        Mockito.when(modelsRepository.findByIdentifier("MOD1")).thenReturn(existingModels);

        ModelsDto response = modelsService.update(modelsDto);

        Assertions.assertEquals("MOD1", response.getIdentifier());
        verify(modelMapper).map(modelsDto, existingModels);
        verify(modelsRepository).save(existingModels);
    }

    @Test
    void updateFailureTest() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MOD1");

        Mockito.when(modelsRepository.findByIdentifier("MOD1")).thenReturn(null);

        ModelsDto response = modelsService.update(modelsDto);

        Assertions.assertEquals("MOD1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Models with identifier - MOD1 not found", response.getMessage());
        Mockito.verify(modelsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Models models = new Models();
        models.setIdentifier("MOD1");

        Mockito.when(modelsRepository.findByIdentifier("MOD1")).thenReturn(models);

        modelsService.delete("MOD1");

        verify(modelsRepository).findByIdentifier("MOD1");
    }

    @Test
    void findAllSuccessTest() {
        Models m1 = new Models();
        m1.setIdentifier("MOD1");
        List<Models> modelsList = List.of(m1);

        ModelsDto d1 = new ModelsDto();
        d1.setIdentifier("MOD1");
        List<ModelsDto> modelsDtos = List.of(d1);

        Page<Models> page = new PageImpl<>(modelsList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(modelsRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(modelsList), Mockito.any(Type.class))).thenReturn(modelsDtos);

        WsDto<ModelsDto> result = modelsService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Models models = new Models();
        models.setIdentifier("MOD1");

        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MOD1");

        Mockito.when(modelsRepository.findByIdentifierAndIsDeletedFalse("MOD1")).thenReturn(models);
        Mockito.when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);

        ModelsDto response = modelsService.findByIdentifier("MOD1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("MOD1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(modelsRepository.findByIdentifierAndIsDeletedFalse("MOD1")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            modelsService.findByIdentifier("MOD1");
        });
    }

    @Test
    void findAllActiveSuccessTest() {
        Models m1 = new Models();
        List<Models> activeModels = List.of(m1);

        ModelsDto d1 = new ModelsDto();
        List<ModelsDto> modelsDtos = List.of(d1);

        Mockito.when(modelsRepository.findByStatusTrueAndIsDeletedFalse()).thenReturn(activeModels);
        Mockito.when(modelMapper.map(Mockito.eq(activeModels), Mockito.any(Type.class))).thenReturn(modelsDtos);

        List<ModelsDto> result = modelsService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void toggleStatusSuccessTest() {
        Models models = new Models();
        models.setStatus(true);

        Mockito.when(modelsRepository.findByIdentifier("MOD1")).thenReturn(models);

        modelsService.toggleStatus("MOD1");

        Assertions.assertFalse(models.isStatus());
        verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatusModelsNotFoundTest() {
        Mockito.when(modelsRepository.findByIdentifier("MOD1")).thenReturn(null);

        modelsService.toggleStatus("MOD1");

        Mockito.verify(modelsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllSpecificationSuccessTest() {
        Models models = new Models();
        List<Models> modelsList = List.of(models);

        ModelsDto dto = new ModelsDto();
        List<ModelsDto> modelsDtos = List.of(dto);

        Page<Models> page = new PageImpl<>(modelsList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Models> specification = Mockito.mock(Specification.class);

        Mockito.when(modelsRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(modelsList), Mockito.any(Type.class))).thenReturn(modelsDtos);

        WsDto<ModelsDto> result = modelsService.findAll(specification, pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }
}