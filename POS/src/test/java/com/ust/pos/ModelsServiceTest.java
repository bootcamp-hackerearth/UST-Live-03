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
    void saveTestSuccess() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        Models models = new Models();

        when(modelsRepository.findByIdentifier("M1")).thenReturn(null);
        when(modelMapper.map(dto, Models.class)).thenReturn(models);

        ModelsDto result = modelsService.save(dto);

        Assertions.assertEquals("M1", result.getIdentifier());
        verify(modelsRepository).save(models);
    }

    @Test
    void saveTestFailure() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        Models models = new Models();
        models.setIdentifier("M1");

        when(modelsRepository.findByIdentifier("M1")).thenReturn(models);

        ModelsDto result = modelsService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(modelsRepository, never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        Models models = new Models();

        when(modelsRepository.findByIdentifier("M1")).thenReturn(models);

        ModelsDto result = modelsService.update(dto);

        Assertions.assertEquals("M1", result.getIdentifier());
        verify(modelMapper).map(dto, models);
        verify(modelsRepository).save(models);
    }

    @Test
    void updateTestFailure() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        when(modelsRepository.findByIdentifier("M1")).thenReturn(null);

        ModelsDto result = modelsService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(modelsRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        modelsService.delete("M1");
        verify(modelsRepository).deleteByIdentifier("M1");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Models models = new Models();
        models.setIdentifier("M1");

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("M1");

        when(modelsRepository.findByIdentifier("M1")).thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class)).thenReturn(dto);

        ModelsDto result = modelsService.findByIdentifier("M1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("M1", result.getIdentifier());
    }

    @Test
    void findByIdentifierTest() {
        when(modelsRepository.findByIdentifier("M1")).thenReturn(null);

        ModelsDto result = modelsService.findByIdentifier("M1");

        Assertions.assertNull(result);
    }

    @Test
    void findAllTest() {

        Pageable pageable = mock(Pageable.class);
        Page<Models> page = mock(Page.class);

        List<Models> list = List.of(
                new Models(),
                new Models()
        );

        List<ModelsDto> dtoList = List.of(
                new ModelsDto(),
                new ModelsDto()
        );

        when(modelsRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(list);
        when(page.getTotalElements()).thenReturn(2L);
        when(page.getTotalPages()).thenReturn(1);
        when(pageable.getPageSize()).thenReturn(10);
        when(pageable.getPageNumber()).thenReturn(0);
        when(modelMapper.map(eq(list), any(Type.class))).thenReturn(dtoList);

        WsDto<ModelsDto> result = modelsService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(dtoList, result.getDtoList());
        Assertions.assertEquals(2L, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());

        verify(modelsRepository).findAll(pageable);
        verify(page).getContent();
        verify(page).getTotalElements();
        verify(page).getTotalPages();
        verify(pageable).getPageSize();
        verify(pageable).getPageNumber();
        verify(modelMapper).map(eq(list), any(Type.class));
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Models models = new Models();
        models.setStatus(true);

        when(modelsRepository.findByIdentifier("M1")).thenReturn(models);

        modelsService.toggleStatus("M1");

        Assertions.assertFalse(models.isStatus());
        verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Models models = new Models();
        models.setStatus(false);

        when(modelsRepository.findByIdentifier("M1")).thenReturn(models);

        modelsService.toggleStatus("M1");

        Assertions.assertTrue(models.isStatus());
        verify(modelsRepository).save(models);
    }

    @Test
    void toggleStatusNullTest() {
        when(modelsRepository.findByIdentifier("M1")).thenReturn(null);

        modelsService.toggleStatus("M1");

        verify(modelsRepository, never()).save(any());
    }
}