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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {

    @InjectMocks
    private ModelsServiceImpl modelsService;

    @Mock
    private ModelsRepository modelsRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifier_Found() {
        Models models = new Models();
        models.setIdentifier("M1");

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        when(modelsRepository.findByIdentifier("M1"))
                .thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class))
                .thenReturn(dto);

        ModelsDto result = modelsService.findByIdentifier("M1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("M1", result.getIdentifier());
    }

    @Test
    void save_NewModels() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");
        Models models = new Models();

        when(modelsRepository.findByIdentifier("M1"))
                .thenReturn(null);
        when(modelMapper.map(dto, Models.class))
                .thenReturn(models);
        when(modelsRepository.save(models))
                .thenReturn(models);

        ModelsDto result = modelsService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("M1", result.getIdentifier());
        verify(modelsRepository).save(models);
    }

    @Test
    void save_ModelsExists_NotDeleted() {
        Models existing = new Models();
        existing.setDeleted(false);
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        when(modelsRepository.findByIdentifier("M1"))
                .thenReturn(existing);

        ModelsDto result = modelsService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists."));
        verify(modelsRepository, never()).save(any());
    }

    @Test
    void save_ModelsExists_ButWasDeleted() {
        Models existing = new Models();
        existing.setDeleted(true);
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        when(modelsRepository.findByIdentifier("M1"))
                .thenReturn(existing);

        ModelsDto result = modelsService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was deleted"));
        verify(modelsRepository, never()).save(any());
    }

    @Test
    void update_ModelsExists() {
        Models existing = new Models();
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        when(modelsRepository.findByIdentifier("M1"))
                .thenReturn(existing);
        when(modelsRepository.save(existing))
                .thenReturn(existing);

        ModelsDto result = modelsService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("M1", result.getIdentifier());
        verify(modelMapper).map(dto, existing);
        verify(modelsRepository).save(existing);
    }

    @Test
    void update_ModelsNotFound() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        when(modelsRepository.findByIdentifier("M1"))
                .thenReturn(null);

        ModelsDto result = modelsService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(modelsRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Models models = new Models();

        when(modelsRepository.findByIdentifier("M1"))
                .thenReturn(models);

        modelsService.delete("M1");

        verify(modelsRepository).findByIdentifier("M1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Models model1 = new Models();
        Models model2 = new Models();
        Page<Models> page = new PageImpl<>(List.of(model1, model2), pageable, 2);
        List<ModelsDto> dtoList = List.of(new ModelsDto(), new ModelsDto());

        when(modelsRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<ModelsDto> result = modelsService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(2, result.getTotalRecords());
        verify(modelsRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Models> page = new PageImpl<>(List.of(), pageable, 0);

        when(modelsRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<ModelsDto> result = modelsService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertEquals(0, result.getTotalRecords());
        verify(modelsRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void toggleStatus_Test() {
        Models models = new Models();
        models.setStatus(true);

        when(modelsRepository.findByIdentifier("M1"))
                .thenReturn(models);

        modelsService.toggleStatus("M1");

        Assertions.assertFalse(models.isStatus());
        verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatus_NotFound() {
        when(modelsRepository.findByIdentifier("M1")).thenReturn(null);

        modelsService.toggleStatus("M1");

        verify(modelsRepository).findByIdentifier("M1");
        verify(modelsRepository, never()).save(any(Models.class));
    }
}