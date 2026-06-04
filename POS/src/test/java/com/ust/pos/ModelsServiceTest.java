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

@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {

    @InjectMocks
    private ModelsServiceImpl modelsService;

    @Mock
    private ModelsRepository modelsRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierTest() {

        Models models = new Models();
        models.setIdentifier("IPHONE");

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("IPHONE");

        Mockito.when(modelsRepository.findByIdentifier("IPHONE")).thenReturn(models);

        Mockito.when(modelMapper.map(models, ModelsDto.class)).thenReturn(dto);

        ModelsDto response = modelsService.findByIdentifier("IPHONE");

        Assertions.assertEquals("IPHONE", response.getIdentifier());
    }

    @Test
    void toggleStatusTrueToFalseTest() {

        Models models = new Models();
        models.setIdentifier("IPHONE");
        models.setStatus(true);

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("IPHONE");

        Mockito.when(modelsRepository.findByIdentifier("IPHONE")).thenReturn(models);

        Mockito.when(modelMapper.map(models, ModelsDto.class)).thenReturn(dto);

        ModelsDto response = modelsService.toggleStatus("IPHONE");

        Assertions.assertEquals("IPHONE", response.getIdentifier());

        Assertions.assertFalse(models.isStatus());

        Mockito.verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Models models = new Models();
        models.setIdentifier("IPHONE");
        models.setStatus(false);

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("IPHONE");

        Mockito.when(modelsRepository.findByIdentifier("IPHONE")).thenReturn(models);

        Mockito.when(modelMapper.map(models, ModelsDto.class)).thenReturn(dto);

        ModelsDto response = modelsService.toggleStatus("IPHONE");

        Assertions.assertEquals("IPHONE", response.getIdentifier());

        Assertions.assertTrue(models.isStatus());

        Mockito.verify(modelsRepository).save(models);
    }

    @Test
    void saveTest() {

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier(" IPHONE ");

        Models models = new Models();

        Mockito.when(modelsRepository.findByIdentifier("IPHONE")).thenReturn(null);

        Mockito.when(modelMapper.map(dto, Models.class)).thenReturn(models);

        ModelsDto response = modelsService.save(dto);

        Assertions.assertEquals("IPHONE", response.getIdentifier());

        Mockito.verify(modelsRepository).save(models);
    }

    @Test
    void saveDuplicateTest() {

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("IPHONE");

        Models existing = new Models();

        Mockito.when(modelsRepository.findByIdentifier("IPHONE")).thenReturn(existing);

        ModelsDto response = modelsService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Models with identifier - IPHONE already exists", response.getMessage());
    }

    @Test
    void updateTest() {

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("IPHONE");

        Models existing = new Models();
        existing.setIdentifier("IPHONE");

        Mockito.when(modelsRepository.findByIdentifier("IPHONE")).thenReturn(existing);

        Mockito.doAnswer(invocation -> {

            ModelsDto source = invocation.getArgument(0);
            Models target = invocation.getArgument(1);

            target.setIdentifier(source.getIdentifier());

            return null;

        }).when(modelMapper).map(Mockito.any(ModelsDto.class), Mockito.any(Models.class));

        ModelsDto response = modelsService.update(dto);

        Assertions.assertEquals("IPHONE", response.getIdentifier());

        Mockito.verify(modelsRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("IPHONE");

        Mockito.when(modelsRepository.findByIdentifier("IPHONE")).thenReturn(null);

        ModelsDto response = modelsService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Models with identifier - IPHONE not found", response.getMessage());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(modelsRepository).deleteByIdentifier("IPHONE");

        boolean result = modelsService.delete("IPHONE");

        Assertions.assertTrue(result);

        Mockito.verify(modelsRepository).deleteByIdentifier("IPHONE");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Models models = new Models();
        models.setIdentifier("IPHONE");

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("IPHONE");

        List<Models> modelsList = List.of(models);
        List<ModelsDto> dtos = List.of(dto);

        Page<Models> modelsPage = new PageImpl<>(modelsList);

        Mockito.when(modelsRepository.findAll(pageable)).thenReturn(modelsPage);

        Mockito.when(modelMapper.map(Mockito.eq(modelsList), Mockito.any(Type.class))).thenReturn(dtos);

        List<ModelsDto> response = modelsService.findAll(pageable);

        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("IPHONE", response.get(0).getIdentifier());
    }

    @Test
    void findIfTrueTest() {

        Models models = new Models();
        models.setIdentifier("IPHONE");
        models.setStatus(true);

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("IPHONE");

        List<Models> modelsList = List.of(models);
        List<ModelsDto> dtos = List.of(dto);

        Mockito.when(modelsRepository.findByStatusIsTrue()).thenReturn(modelsList);

        Mockito.when(modelMapper.map(Mockito.eq(modelsList), Mockito.any(Type.class))).thenReturn(dtos);

        List<ModelsDto> response = modelsService.findIfTrue();

        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("IPHONE", response.get(0).getIdentifier());
    }
}