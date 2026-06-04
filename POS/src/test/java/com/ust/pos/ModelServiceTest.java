package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.model.service.impl.ModelServiceImpl;
import com.ust.pos.modell.Model;
import com.ust.pos.modell.ModelRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

    @InjectMocks
    private ModelServiceImpl modelService;

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        when(modelRepository.findByIdentifier("M1")).thenReturn(null);
        Model entity = new Model();
        when(modelMapper.map(dto, Model.class)).thenReturn(entity);
        when(modelRepository.save(entity)).thenReturn(entity);
        ModelDto response = modelService.save(dto);
        Assertions.assertEquals("M1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        Model existing = new Model();
        when(modelRepository.findByIdentifier("M1")).thenReturn(existing);
        ModelDto response = modelService.save(dto);
        Assertions.assertEquals("M1", response.getIdentifier());
        assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Model entity = new Model();
        entity.setIdentifier("M1");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        when(modelRepository.findByIdentifier("M1")).thenReturn(entity);
        when(modelMapper.map(entity, ModelDto.class)).thenReturn(dto);
        ModelDto response = modelService.findByIdentifier("M1");
        Assertions.assertEquals("M1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        Model existing = new Model();
        existing.setIdentifier("M1");
        when(modelRepository.findByIdentifier("M1")).thenReturn(existing);
        when(modelRepository.save(existing)).thenReturn(existing);
        ModelDto response = modelService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        when(modelRepository.findByIdentifier("M1")).thenReturn(null);
        ModelDto response = modelService.update(dto);
        assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        modelService.delete("M1");
        verify(modelRepository).deleteByIdentifier("M1");
    }

    @Test
    void findAllTest() {
        Model model = new Model();
        model.setIdentifier("Admin");
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");
        List<Model> models = List.of(model);
        List<ModelDto> modelDtos = List.of(modelDto);
        Page<Model> modelPage = new PageImpl<>(models, PageRequest.of(0, 2), models.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(modelRepository.findAll(pageable)).thenReturn(modelPage);
        Mockito.when(modelMapper.map(Mockito.eq(models), Mockito.any(java.lang.reflect.Type.class))).thenReturn(modelDtos);
        List<ModelDto> response = modelService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllActiveTest() {
        Model entity = new Model();
        entity.setIdentifier("M1");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        List<Model> list = List.of(entity);
        when(modelRepository.findByStatusTrue()).thenReturn(list);
        when(modelMapper.map(entity, ModelDto.class)).thenReturn(dto);
        List<ModelDto> response = modelService.findAllActive();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("M1", response.get(0).getIdentifier());
    }

    @Test
    void toggleStatus_shouldToggleTrueToFalse() {

        String identifier = "123";
        Model model = new Model();
        model.setIdentifier(identifier);
        model.setStatus(true);
        when(modelRepository.findByIdentifier(identifier)).thenReturn(model);
        modelService.toggleStatus(identifier);
        Assertions.assertFalse(model.getStatus());
        verify(modelRepository, times(1)).save(model);
    }

    @Test
    void toggleStatus_shouldToggleFalseToTrue() {

        String identifier = "123";
        Model model = new Model();
        model.setIdentifier(identifier);
        model.setStatus(false);
        when(modelRepository.findByIdentifier(identifier)).thenReturn(model);
        modelService.toggleStatus(identifier);
        Assertions.assertTrue(model.getStatus());
        verify(modelRepository, times(1)).save(model);
    }

    @Test
    void toggleStatus_shouldThrowException_whenModelNotFound() {

        String identifier = "123";
        when(modelRepository.findByIdentifier(identifier)).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> modelService.toggleStatus(identifier));
        Assertions.assertEquals("model not found", ex.getMessage());
        verify(modelRepository, never()).save(any());
    }
}