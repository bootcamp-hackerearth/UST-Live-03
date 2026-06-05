package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.models.service.impl.ModelServiceImpl;
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
class ModelServiceTest {

    @InjectMocks
    ModelServiceImpl modelService;

    @Mock
    ModelRepository modelRepository;

    @Mock
    ModelMapper modelMapper;

    @Test
    void saveTest() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Jordan");
        Model model = new Model();
        Mockito.when(modelMapper.map(modelDto, Model.class)).thenReturn(model);
        Mockito.when(modelRepository.save(model)).thenReturn(model);
        ModelDto response = modelService.save(modelDto);
        Assertions.assertEquals("Jordan", response.getIdentifier());
    }

    @Test
    void findAllWithPageableTest() {
        Model model = new Model();
        model.setIdentifier("Jordan");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("Jordan");
        List<Model> models = List.of(model);
        List<ModelDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Model> modelPage = new PageImpl<>(models);
        Mockito.when(modelRepository.findAll(pageable)).thenReturn(modelPage);
        Mockito.when(modelMapper.map(Mockito.eq(models), Mockito.any(Type.class)
        )).thenReturn(dtos);
        List<ModelDto> response = modelService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Jordan", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Model model = new Model();
        model.setIdentifier("Jordan");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("Jordan");
        List<Model> models = List.of(model);
        List<ModelDto> dtos = List.of(dto);
        Mockito.when(modelRepository.findAll()).thenReturn(models);
        Mockito.when(modelMapper.map(Mockito.eq(models), Mockito.any(Type.class)
        )).thenReturn(dtos);
        List<ModelDto> response = modelService.findAll(null);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void updateTest() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Jordan");
        Model model = new Model();
        model.setIdentifier("Jordan");
        Mockito.when(modelRepository.findByIdentifier(modelDto.getIdentifier())).thenReturn(model);
        Mockito.when(modelRepository.save(model)).thenReturn(model);
        ModelDto response = modelService.update(modelDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Jordan");
        Model model = new Model();
        model.setIdentifier("Jordan");
        Mockito.when(modelRepository.findByIdentifier(modelDto.getIdentifier())).thenReturn(null);
        ModelDto response = modelService.update(modelDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Jordan");
        Model model = new Model();
        model.setIdentifier("Jordan");
        Mockito.when(modelRepository.findByIdentifier("Jordan")).thenReturn(model);
        Mockito.when(modelMapper.map(model, ModelDto.class)).thenReturn(modelDto);
        ModelDto response = modelService.findByIdentifier("Jordan");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Jordan", response.getIdentifier());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(modelRepository).deleteByIdentifier("Jordan");
        modelService.deleteByIdentifier("Jordan");
        Mockito.verify(modelRepository).deleteByIdentifier("Jordan");
    }

    @Test
    void toggleStatusSuccessTest() {
        Model model = new Model();
        model.setIdentifier("MDL001");
        model.setStatus(false);
        Mockito.when(modelRepository.findByIdentifier("MDL001")).thenReturn(model);
        ModelDto response = modelService.toggleStatus("MDL001", true);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
        Assertions.assertTrue(model.isStatus());
        Mockito.verify(modelRepository).findByIdentifier("MDL001");
        Mockito.verify(modelRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void toggleStatusModelNotFoundTest() {
        Mockito.when(modelRepository.findByIdentifier("MDL001")).thenReturn(null);
        ModelDto response = modelService.toggleStatus("MDL001", true);
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Model not found", response.getMessage());
        Mockito.verify(modelRepository).findByIdentifier("MDL001");
        Mockito.verifyNoMoreInteractions(modelRepository);
    }
}