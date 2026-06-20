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
    void findByIdentifierNullTest() {
        Mockito.when(modelsRepository.findByIdentifier("INVALID")).thenReturn(null);

        ModelsDto response = modelsService.findByIdentifier("INVALID");

        Assertions.assertNull(response);
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
    void toggleStatusNullTest() {
        Mockito.when(modelsRepository.findByIdentifier("INVALID")).thenReturn(null);

        Assertions.assertThrows(NullPointerException.class, () -> modelsService.toggleStatus("INVALID"));
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
    void saveSoftDeletedTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("IPHONE");

        Models existing = new Models();
        existing.setDeleted(true);

        Mockito.when(modelsRepository.findByIdentifier("IPHONE")).thenReturn(existing);

        ModelsDto response = modelsService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Models with identifier - IPHONE has been soft deleted.(Rollback by changing status", response.getMessage());
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
        Models models = new Models();
        models.setIdentifier("IPHONE");

        Mockito.when(modelsRepository.findByIdentifier("IPHONE")).thenReturn(models);

        boolean result = modelsService.delete("IPHONE");

        Assertions.assertTrue(result);

        Mockito.verify(modelsRepository).save(models);
    }

    @Test
    void deleteNotFoundTest() {
        Mockito.when(modelsRepository.findByIdentifier("INVALID")).thenReturn(null);

        boolean result = modelsService.delete("INVALID");

        Assertions.assertFalse(result);

        Mockito.verify(modelsRepository, Mockito.never()).save(Mockito.any());
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

        Page<Models> modelsPage = new PageImpl<>(modelsList, pageable, 1);

        Mockito.when(modelsRepository.findByDeletedFalse(pageable)).thenReturn(modelsPage);
        Mockito.when(modelMapper.map(Mockito.eq(modelsList), Mockito.any(Type.class))).thenReturn(dtos);

        WsDto<ModelsDto> response = modelsService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("IPHONE", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1L, response.getTotalRecords());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Models> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        Mockito.when(modelsRepository.findByDeletedFalse(pageable)).thenReturn(emptyPage);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());

        WsDto<ModelsDto> response = modelsService.findAll(pageable);

        Assertions.assertEquals(0, response.getDtoList().size());
        Assertions.assertEquals(0L, response.getTotalRecords());
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

    @Test
    void findIfTrueEmptyTest() {
        Mockito.when(modelsRepository.findByStatusIsTrue()).thenReturn(List.of());
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());

        List<ModelsDto> response = modelsService.findIfTrue();

        Assertions.assertEquals(0, response.size());
    }

}
