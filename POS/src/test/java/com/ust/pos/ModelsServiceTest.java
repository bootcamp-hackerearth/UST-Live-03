package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.impl.ModelsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

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
        dto.setIdentifier("MDL_107");

        Models model = new Models();

        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Models.class)).thenReturn(model);
        Mockito.when(modelsRepository.save(model)).thenReturn(model);
        ModelsDto response = modelsService.save(dto);
        Assertions.assertEquals("MDL_107", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveFailure_existingActive() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL_107");

        Models existing = new Models();
        existing.setDeleted(false);

        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(existing);
        ModelsDto response = modelsService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveFailure_softDeleted() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL_107");

        Models existing = new Models();
        existing.setDeleted(true);

        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(existing);
        ModelsDto response = modelsService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Models model = new Models();
        model.setIdentifier("MDL_107");

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL_107");

        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(model);
        Mockito.when(modelMapper.map(model, ModelsDto.class)).thenReturn(dto);
        ModelsDto response = modelsService.findByIdentifier("MDL_107");
        Assertions.assertEquals("MDL_107", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL_107");

        Models existing = new Models();

        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(modelsRepository.save(existing)).thenReturn(existing);

        ModelsDto response = modelsService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(modelsRepository).save(existing);
    }

    @Test
    void updateFailure() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL_107");

        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(null);
        ModelsDto response = modelsService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Models model = new Models();
        model.setDeleted(false);

        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(model);
        Mockito.when(modelsRepository.save(model)).thenReturn(model);

        modelsService.delete("MDL_107");
        Mockito.verify(modelsRepository).findByIdentifier("MDL_107");
        Mockito.verify(modelsRepository).save(model);
        Assertions.assertTrue(model.isDeleted());
    }

    @Test
    void findAllTest() {
        Models model = new Models();
        model.setIdentifier("MDL_107");

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL_107");

        List<Models> list = List.of(model);
        Page<Models> page = new PageImpl<>(list, PageRequest.of(0, 10), 1);

        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(modelsRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));

        WsDto<ModelsDto> response = modelsService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("MDL_107", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void toggleStatusSuccessTest() {
        Models model = new Models();
        model.setIdentifier("MDL_107");
        model.setStatus(false);

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL_107");
        dto.setStatus(true);

        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(model);
        Mockito.when(modelsRepository.save(model)).thenReturn(model);
        Mockito.when(modelMapper.map(model, ModelsDto.class)).thenReturn(dto);
        ModelsDto response = modelsService.toggleStatus("MDL_107", true);
        Assertions.assertEquals("MDL_107", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(modelsRepository.findByIdentifier("MDL_107")).thenReturn(null);
        ModelsDto response = modelsService.toggleStatus("MDL_107", true);
        Assertions.assertNull(response);
        Mockito.verify(modelsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveModelsTest() {
        Models model = new Models();
        model.setIdentifier("MDL_107");
        model.setStatus(true);

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL_107");
        dto.setStatus(true);

        List<Models> list = List.of(model);
        Mockito.when(modelsRepository.findByStatusTrue()).thenReturn(list);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));

        List<ModelsDto> response = modelsService.findActiveModel();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("MDL_107", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
    }
}