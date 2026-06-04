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

import java.util.List;

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

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");

        Mockito.when(modelRepository.findByIdentifier("Admin")).thenReturn(null);
        Model model = new Model();
        Mockito.when(modelMapper.map(modelDto, Model.class)).thenReturn(model);
        Mockito.when(modelRepository.save(model)).thenReturn(model);
        ModelDto response = modelService.save(modelDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");
        Model model = new Model();

        Mockito.when(modelRepository.findByIdentifier("Admin")).thenReturn(model);
        ModelDto response = modelService.save(modelDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());

    }

    @Test
    void findByIdentifierTest() {

        Model model = new Model();
        model.setIdentifier("Admin");

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");

        Mockito.when(modelRepository.findByIdentifier("Admin")).thenReturn(model);
        Mockito.when(modelMapper.map(model, ModelDto.class)).thenReturn(modelDto);

        ModelDto response = modelService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");

        Model existingModel = new Model();
        existingModel.setIdentifier("Admin");

        Mockito.when(modelRepository.findByIdentifier("Admin"))
                .thenReturn(existingModel);
        Mockito.when(modelRepository.save(existingModel))
                .thenReturn(existingModel);

        ModelDto response = modelService.update(modelDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");

        Mockito.when(modelRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        ModelDto response = modelService.update(modelDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(modelRepository)
                .deleteByIdentifier("Admin");

        modelService.delete("Admin");

        Mockito.verify(modelRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {

        Model model = new Model();
        model.setIdentifier("Admin");

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");

        List<Model> models = List.of(model);
        List<ModelDto> modelDtos = List.of(modelDto);

        Page<Model> modelPage = new PageImpl<>(models);

        Mockito.when(modelRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(modelPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(models),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(modelDtos);

        List<ModelDto> response = modelService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllActiveTest() {

        Model model = new Model();
        model.setIdentifier("Admin");
        model.setStatus(true);

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");

        List<Model> models = List.of(model);
        List<ModelDto> modelDtos = List.of(modelDto);

        Mockito.when(modelRepository.findByStatus(true)).thenReturn(models);
        Mockito.when(modelMapper.map(
                Mockito.eq(models),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(modelDtos);

        List<ModelDto> response = modelService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTest() {

        Model model = new Model();
        model.setIdentifier("Admin");
        model.setStatus(false);

        Mockito.when(modelRepository.findByIdentifier("Admin"))
                .thenReturn(model);

        Mockito.when(modelRepository.save(model))
                .thenReturn(model);

        modelService.changeStatus("Admin", true);

        Assertions.assertTrue(model.getStatus());

        Mockito.verify(modelRepository).save(model);
    }
}