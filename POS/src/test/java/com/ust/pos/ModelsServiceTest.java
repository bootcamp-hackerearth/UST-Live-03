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
        modelsDto.setIdentifier("iPhone14");

        Models models = new Models();

        Mockito.when(modelsRepository.findByIdentifier("iPhone14")).thenReturn(null);
        Mockito.when(modelMapper.map(modelsDto, Models.class)).thenReturn(models);

        ModelsDto response = modelsService.save(modelsDto);

        Assertions.assertEquals("iPhone14", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(modelsRepository).save(models);
    }

    @Test
    void saveFailureTest() {

        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("iPhone14");

        Models models = new Models();
        models.setIdentifier("iPhone14");

        Mockito.when(modelsRepository.findByIdentifier("iPhone14")).thenReturn(models);

        ModelsDto response = modelsService.save(modelsDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateSuccessTest() {

        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("iPhone14");

        Models models = new Models();
        models.setIdentifier("iPhone14");

        Mockito.when(modelsRepository.findByIdentifier("iPhone14")).thenReturn(models);

        ModelsDto response = modelsService.update(modelsDto);

        Assertions.assertEquals("iPhone14", response.getIdentifier());
        verify(modelsRepository).save(models);
    }

    @Test
    void updateFailureTest() {

        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("iPhone14");

        Mockito.when(modelsRepository.findByIdentifier("iPhone14")).thenReturn(null);

        ModelsDto response = modelsService.update(modelsDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteSuccessTest() {

        modelsService.delete("iPhone14");

        verify(modelsRepository).deleteByIdentifier("iPhone14");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Models models = new Models();
        models.setIdentifier("iPhone14");

        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("iPhone14");

        Mockito.when(modelsRepository.findByIdentifier("iPhone14")).thenReturn(models);
        Mockito.when(modelMapper.map(models, ModelsDto.class)).thenReturn(modelsDto);

        ModelsDto response = modelsService.findByIdentifier("iPhone14");

        Assertions.assertEquals("iPhone14", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(modelsRepository.findByIdentifier("iPhone14")).thenReturn(null);

        ModelsDto response = modelsService.findByIdentifier("iPhone14");

        Assertions.assertNull(response);
    }

    @Test
    void findAllSuccessTest() {

        Models m1 = new Models();
        m1.setIdentifier("iPhone14");

        Models m2 = new Models();
        m2.setIdentifier("GalaxyS23");

        List<Models> modelsList = List.of(m1, m2);

        ModelsDto d1 = new ModelsDto();
        d1.setIdentifier("iPhone14");

        ModelsDto d2 = new ModelsDto();
        d2.setIdentifier("GalaxyS23");

        List<ModelsDto> modelsDtos = List.of(d1, d2);

        Page<Models> page = new PageImpl<>(modelsList);
        Pageable pageable = PageRequest.of(0, 20);

        Mockito.when(modelsRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(modelsList), Mockito.any(Type.class))).thenReturn(modelsDtos);

        List<ModelsDto> result = modelsService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void findAllActiveSuccessTest() {

        Models m1 = new Models();
        m1.setIdentifier("iPhone14");
        m1.setStatus(true);

        Models m2 = new Models();
        m2.setIdentifier("GalaxyS23");
        m2.setStatus(true);

        List<Models> activeModels = List.of(m1, m2);

        ModelsDto d1 = new ModelsDto();
        d1.setIdentifier("iPhone14");

        ModelsDto d2 = new ModelsDto();
        d2.setIdentifier("GalaxyS23");

        List<ModelsDto> modelsDtos = List.of(d1, d2);

        Mockito.when(modelsRepository.findByStatus(true)).thenReturn(activeModels);

        Mockito.when(modelMapper.map(Mockito.eq(activeModels), Mockito.any(Type.class))).thenReturn(modelsDtos);

        List<ModelsDto> result = modelsService.findAllActive();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void toggleStatusSuccessTest() {

        Models models = new Models();
        models.setStatus(true);

        Mockito.when(modelsRepository.findByIdentifier("iPhone14"))
                .thenReturn(models);

        modelsService.toggleStatus("iPhone14");

        Assertions.assertFalse(models.isStatus());
        verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatusModelNotFoundTest() {

        Mockito.when(modelsRepository.findByIdentifier("iPhone14"))
                .thenReturn(null);

        modelsService.toggleStatus("iPhone14");

        verify(modelsRepository, Mockito.never()).save(Mockito.any());
    }
}