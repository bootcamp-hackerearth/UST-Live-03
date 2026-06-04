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
    private ModelServiceImpl modelService;

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Model1");

        Mockito.when(modelRepository.findByIdentifier("Model1"))
                .thenReturn(null);

        Model model = new Model();
        Mockito.when(modelMapper.map(modelDto, Model.class))
                .thenReturn(model);
        Mockito.when(modelRepository.save(model))
                .thenReturn(model);

        ModelDto response = modelService.save(modelDto);

        Assertions.assertEquals("Model1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Model1");

        Mockito.when(modelRepository.findByIdentifier("Model1"))
                .thenReturn(new Model());

        ModelDto response = modelService.save(modelDto);

        Assertions.assertEquals("Model1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Model model = new Model();
        model.setIdentifier("Model1");

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Model1");

        Mockito.when(modelRepository.findByIdentifier("Model1"))
                .thenReturn(model);
        Mockito.when(modelMapper.map(model, ModelDto.class))
                .thenReturn(modelDto);

        ModelDto response = modelService.findByIdentifier("Model1");

        Assertions.assertEquals("Model1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Model1");

        Model existingModel = new Model();
        existingModel.setIdentifier("Model1");

        Mockito.when(modelRepository.findByIdentifier("Model1"))
                .thenReturn(existingModel);
        Mockito.when(modelRepository.save(existingModel))
                .thenReturn(existingModel);

        ModelDto response = modelService.update(modelDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Model1");

        Mockito.when(modelRepository.findByIdentifier("Model1"))
                .thenReturn(null);

        ModelDto response = modelService.update(modelDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(modelRepository)
                .deleteByIdentifier("Model1");

        modelService.delete("Model1");

        Mockito.verify(modelRepository)
                .deleteByIdentifier("Model1");
    }

    @Test
    void toggleStatusTest() {
        Model model = new Model();
        model.setIdentifier("Model1");
        model.setStatus(true);

        Mockito.when(modelRepository.findByIdentifier("Model1"))
                .thenReturn(model);
        Mockito.when(modelRepository.save(model))
                .thenReturn(model);

        modelService.toggleStatus("Model1");

        Assertions.assertFalse(model.getStatus());
        Mockito.verify(modelRepository).save(model);
    }

    @Test
    void toggleStatusTestFailure() {
        Mockito.when(modelRepository.findByIdentifier("Model1"))
                .thenReturn(null);

        modelService.toggleStatus("Model1");

        Mockito.verify(modelRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllPaginationTest() {

        Model model = new Model();
        model.setIdentifier("Model1");

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Model1");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Model> modelPage =
                new PageImpl<>(List.of(model), pageable, 1);

        Mockito.when(modelRepository.findAll(pageable))
                .thenReturn(modelPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(modelPage.getContent()),
                Mockito.any(Type.class)
        )).thenReturn(List.of(modelDto));

        List<ModelDto> response = modelService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }
}