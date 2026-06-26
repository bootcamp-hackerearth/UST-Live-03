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
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("S1");
        Mockito.when(modelsRepository.findByIdentifier("S1")).thenReturn(null);
        Models model = new Models();
        Mockito.when(modelMapper.map(dto, Models.class)).thenReturn(model);
        Mockito.when(modelsRepository.save(model)).thenReturn(model);
        ModelsDto response = modelsService.save(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestAlreadyExists() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("S1");
        Models existing = new Models();
        existing.setDeleted(false);
        Mockito.when(modelsRepository.findByIdentifier("S1")).thenReturn(existing);
        ModelsDto response = modelsService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestDeletedExists() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("S1");
        Models existing = new Models();
        existing.setDeleted(true);
        Mockito.when(modelsRepository.findByIdentifier("S1")).thenReturn(existing);
        ModelsDto response = modelsService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("S1");
        Models existing = new Models();
        Mockito.when(modelsRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(modelsRepository.save(existing)).thenReturn(existing);
        ModelsDto response = modelsService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("S1");
        Mockito.when(modelsRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        ModelsDto response = modelsService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Models model = new Models();
        ModelsDto dto = new ModelsDto();
        Mockito.when(modelsRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(model);
        Mockito.when(modelMapper.map(model, ModelsDto.class)).thenReturn(dto);
        ModelsDto response = modelsService.findByIdentifier("S1");
        Assertions.assertNotNull(response);
    }

    @Test
    void findAllTest() {
        Models model = new Models();
        ModelsDto dto = new ModelsDto();
        List<Models> list = List.of(model);
        List<ModelsDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Models> page = new PageImpl<>(list);
        Mockito.when(modelsRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        WsDto<ModelsDto> response = modelsService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void deleteTest() {
        Models model = new Models();
        Mockito.when(modelsRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(model);
        Mockito.when(modelsRepository.save(model)).thenReturn(model);
        modelsService.delete("S1");
        Mockito.verify(modelsRepository).save(model);
    }

    @Test
    void deleteNullTest() {
        Mockito.when(modelsRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        modelsService.delete("S1");
        Mockito.verify(modelsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateStatusTest() {
        Models model = new Models();
        Mockito.when(modelsRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(model);
        Mockito.when(modelsRepository.save(model)).thenReturn(model);
        modelsService.updateStatus("S1", true);
        Mockito.verify(modelsRepository).save(model);
    }

    @Test
    void updateStatusNullTest() {
        Mockito.when(modelsRepository.findByIdentifierAndDeletedFalse("S1")).thenReturn(null);
        modelsService.updateStatus("S1", true);
        Mockito.verify(modelsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllActiveTest() {
        Models model = new Models();
        ModelsDto dto = new ModelsDto();
        List<Models> list = List.of(model);
        List<ModelsDto> dtoList = List.of(dto);
        Mockito.when(modelsRepository.findByStatusAndDeletedFalse(true)).thenReturn(list);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        List<ModelsDto> response = modelsService.findAllActive();
        Assertions.assertEquals(1, response.size());
    }
}