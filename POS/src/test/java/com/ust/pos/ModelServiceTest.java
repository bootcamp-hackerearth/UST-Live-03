package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PageDto;
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
import org.modelmapper.TypeToken;
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
        modelDto.setIdentifier("MODEL001");
        modelDto.setSuccess(true);

        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(null);

        Model model = new Model();

        Mockito.when(modelMapper.map(modelDto, Model.class))
                .thenReturn(model);

        Mockito.when(modelRepository.save(model))
                .thenReturn(model);

        ModelDto response = modelService.save(modelDto);

        Assertions.assertEquals("MODEL001", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("MODEL001");

        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(new Model());

        ModelDto response = modelService.save(modelDto);

        Assertions.assertEquals("MODEL001", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Model model = new Model();
        model.setIdentifier("MODEL001");

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("MODEL001");

        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(model);

        Mockito.when(modelMapper.map(model, ModelDto.class))
                .thenReturn(modelDto);

        ModelDto response = modelService.findByIdentifier("MODEL001");

        Assertions.assertEquals("MODEL001", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("MODEL001");
        modelDto.setSuccess(true);

        Model existingModel = new Model();
        existingModel.setIdentifier("MODEL001");

        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(existingModel);

        Mockito.when(modelRepository.save(existingModel))
                .thenReturn(existingModel);

        ModelDto response = modelService.update(modelDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("MODEL001");

        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(null);

        ModelDto response = modelService.update(modelDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(modelRepository)
                .deleteByIdentifier("MODEL001");

        modelService.delete("MODEL001");

        Mockito.verify(modelRepository)
                .deleteByIdentifier("MODEL001");
    }

    @Test
    void toggleStatusTest() {
        Model model = new Model();
        model.setIdentifier("MODEL001");
        model.setStatus(true);

        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(model);

        Mockito.when(modelRepository.save(model))
                .thenReturn(model);

        modelService.toggleStatus("MODEL001");

        Assertions.assertFalse(model.getStatus());
        Mockito.verify(modelRepository).save(model);
    }

    @Test
    void toggleStatusTestFailure() {
        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(null);

        modelService.toggleStatus("MODEL001");

        Mockito.verify(modelRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findActiveModelsTest() {
        Model model = new Model();
        model.setIdentifier("MODEL001");
        model.setStatus(true);

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("MODEL001");

        List<Model> modelList = List.of(model);

        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();

        Mockito.when(modelRepository.findByStatusTrue())
                .thenReturn(modelList);

        Mockito.when(modelMapper.map(Mockito.eq(modelList), Mockito.eq(listType)))
                .thenReturn(List.of(modelDto));

        List<ModelDto> response = modelService.findActiveModels();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("MODEL001", response.get(0).getIdentifier());
    }

    @Test
    void findAllPaginationTest() {

        Model model = new Model();
        model.setIdentifier("MODEL001");

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("MODEL001");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Model> modelPage = new PageImpl<>(List.of(model), pageable, 1);

        Mockito.when(modelRepository.findAll(pageable))
                .thenReturn(modelPage);

        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();

        Mockito.when(modelMapper.map(
                        Mockito.eq(modelPage.getContent()),
                        Mockito.eq(listType)))
                .thenReturn(List.of(modelDto));

        PageDto<ModelDto> response = modelService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("MODEL001",
                response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}