package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.impl.ModelsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {

    @Mock
    private ModelsRepository modelsRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ModelsServiceImpl modelsService;

    @Test
    @DisplayName("Save - Success Case")
    void save_Success() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        Mockito.when(modelsRepository.findByIdentifier("M1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Models.class)).thenReturn(new Models());

        ModelsDto result = modelsService.save(dto);

        Assertions.assertNotNull(result);
        Mockito.verify(modelsRepository).save(any(Models.class));
    }

    @Test
    @DisplayName("Save - Already Exists")
    void save_AlreadyExists() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");
        Mockito.when(modelsRepository.findByIdentifier("M1")).thenReturn(new Models());

        ModelsDto result = modelsService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Models with identifier - M1 already exists", result.getMessage());
        Mockito.verify(modelsRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Find All - Paginated")
    void findAll_Test() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Models> list = List.of(new Models());
        Page<Models> page = new PageImpl<>(list);

        Mockito.when(modelsRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(eq(list), any(Type.class))).thenReturn(List.of(new ModelsDto()));

        List<ModelsDto> result = modelsService.findAll(pageable);

        Assertions.assertFalse(result.isEmpty());
        Mockito.verify(modelsRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Find All Active")
    void findAllActive_Test() {
        List<Models> list = List.of(new Models());
        Mockito.when(modelsRepository.findAllByStatus(true)).thenReturn(list);
        Mockito.when(modelMapper.map(eq(list), any(Type.class))).thenReturn(List.of(new ModelsDto()));

        List<ModelsDto> result = modelsService.findAllActive();

        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        Models entity = new Models();
        ModelsDto mappedDto = new ModelsDto();

        Mockito.when(modelsRepository.findByIdentifier("M1")).thenReturn(entity);
        Mockito.when(modelMapper.map(entity, ModelsDto.class)).thenReturn(mappedDto);

        ModelsDto result = modelsService.findByIdentifier("M1");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("Find By Identifier - Not Found")
    void findByIdentifier_NotFound() {
        Mockito.when(modelsRepository.findByIdentifier("M1")).thenReturn(null);

        ModelsDto result = modelsService.findByIdentifier("M1");

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Models with identifier - M1 not found", result.getMessage());
    }

    @Test
    @DisplayName("Update - Success")
    void update_Success() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");
        Models entity = new Models();

        Mockito.when(modelsRepository.findByIdentifier("M1")).thenReturn(entity);

        ModelsDto result = modelsService.update(dto);

        Mockito.verify(modelMapper).map(dto, entity);
        Mockito.verify(modelsRepository).save(entity);
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Update - Not Found")
    void update_NotFound() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        Mockito.when(modelsRepository.findByIdentifier("M1")).thenReturn(null);

        ModelsDto result = modelsService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Models with identifier - M1 not found", result.getMessage());
        Mockito.verify(modelsRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        Models entity = new Models();
        entity.setStatus(true);

        Mockito.when(modelsRepository.findByIdentifier("M1")).thenReturn(entity);
        Mockito.when(modelMapper.map(any(), eq(ModelsDto.class))).thenReturn(new ModelsDto());

        modelsService.toggleStatus("M1");

        Assertions.assertFalse(entity.isStatus());
        Mockito.verify(modelsRepository).save(entity);
    }

    @Test
    @DisplayName("Toggle Status - Not Found")
    void toggleStatus_NotFound() {
        Mockito.when(modelsRepository.findByIdentifier("M1")).thenReturn(null);

        ModelsDto result = modelsService.toggleStatus("M1");

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Models with identifier - M1 not found", result.getMessage());
        Mockito.verify(modelsRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Delete - Success")
    void delete_Test() {
        boolean result = modelsService.delete("M1");

        Assertions.assertTrue(result);
        Mockito.verify(modelsRepository).deleteByIdentifier("M1");
    }
}