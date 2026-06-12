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
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ModelServiceImpl modelService;

    @Test
    void save_success() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("MODEL1");

        Model model = new Model();

        Mockito.when(modelRepository.findByIdentifier("MODEL1")).thenReturn(null);
        Mockito.when(modelMapper.map(modelDto, Model.class)).thenReturn(model);

        ModelDto response = modelService.save(modelDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void save_failure_alreadyExists() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("MODEL1");

        Mockito.when(modelRepository.findByIdentifier("MODEL1")).thenReturn(new Model());

        ModelDto response = modelService.save(modelDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifier() {
        Model model = new Model();
        model.setIdentifier("MODEL1");

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("MODEL1");

        Mockito.when(modelRepository.findByIdentifier("MODEL1")).thenReturn(model);
        Mockito.when(modelMapper.map(model, ModelDto.class)).thenReturn(modelDto);

        ModelDto response = modelService.findByIdentifier("MODEL1");

        Assertions.assertEquals("MODEL1", response.getIdentifier());
    }

    @Test
    void update_success() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("MODEL1");

        Mockito.when(modelRepository.findByIdentifier("MODEL1")).thenReturn(new Model());

        ModelDto response = modelService.update(modelDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void update_failure() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("MODEL1");

        Mockito.when(modelRepository.findByIdentifier("MODEL1")).thenReturn(null);

        ModelDto response = modelService.update(modelDto);

        Assertions.assertFalse(response.isSuccess());
    }


    @Test
    void delete_success() {
        Assertions.assertDoesNotThrow(() -> modelService.delete("MODEL1"));
        Mockito.verify(modelRepository).deleteByIdentifier("MODEL1");
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

        List<ModelDto> response = modelService.findAll(pageable).getDtoList();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByStatusTrue() {
        Model model = new Model();
        model.setIdentifier("Admin");

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");

        List<Model> models = List.of(model);
        List<ModelDto> modelDtos = List.of(modelDto);

        Mockito.when(modelRepository.findByStatusIsTrue()).thenReturn(models);
        Mockito.when(modelMapper.map(Mockito.eq(models), Mockito.any(java.lang.reflect.Type.class))).thenReturn(modelDtos);

        List<ModelDto> response = modelService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggle_activeToInactive() {
        Model model = new Model();
        model.setStatus(true);

        Mockito.when(modelRepository.findByIdentifier("MODEL1")).thenReturn(model);

        modelService.toggleStatus("MODEL1");

        Assertions.assertFalse(model.isStatus());
    }

    @Test
    void toggle_inactiveToActive() {
        Model model = new Model();
        model.setStatus(false);

        Mockito.when(modelRepository.findByIdentifier("MODEL1")).thenReturn(model);

        modelService.toggleStatus("MODEL1");

        Assertions.assertTrue(model.isStatus());
    }
}