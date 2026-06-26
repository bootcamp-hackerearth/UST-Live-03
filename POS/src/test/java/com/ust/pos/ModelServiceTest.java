package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.modelmodule.service.impl.ModelServiceImpl;
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
        modelDto.setIdentifier("Jordan");

        Mockito.when(
                modelRepository.findByIdentifier("Jordan")
        ).thenReturn(null);

        Model model = new Model();

        Mockito.when(
                modelMapper.map(modelDto, Model.class)
        ).thenReturn(model);

        Mockito.when(
                modelRepository.save(model)
        ).thenReturn(model);

        ModelDto response =
                modelService.save(modelDto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Model created successfully",
                response.getMessage()
        );

        Mockito.verify(modelRepository)
                .save(model);
    }

    @Test
    void saveTestFailureExistingModel() {

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Jordan");

        Model existingModel = new Model();
        existingModel.setDeleted(false);

        Mockito.when(
                modelRepository.findByIdentifier("Jordan")
        ).thenReturn(existingModel);

        ModelDto response =
                modelService.save(modelDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage()
                        .contains("already exists")
        );
    }

    @Test
    void saveTestFailureSoftDeletedModel() {

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Jordan");

        Model deletedModel = new Model();
        deletedModel.setDeleted(true);

        Mockito.when(
                modelRepository.findByIdentifier("Jordan")
        ).thenReturn(deletedModel);

        ModelDto response =
                modelService.save(modelDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage()
                        .contains("soft deleted")
        );
    }

    @Test
    void findAllWithPageableTest() {

        Model model = new Model();
        model.setIdentifier("Jordan");

        ModelDto dto = new ModelDto();
        dto.setIdentifier("Jordan");

        List<Model> models =
                List.of(model);

        List<ModelDto> dtos =
                List.of(dto);

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<Model> modelPage =
                new PageImpl<>(
                        models,
                        pageable,
                        1
                );

        Mockito.when(
                modelRepository.findByDeletedFalse(pageable)
        ).thenReturn(modelPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(models),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<ModelDto> response =
                modelService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Jordan",
                response.getDtoList()
                        .get(0)
                        .getIdentifier()
        );
    }

    @Test
    void findAllWithoutPageableTest() {

        Model model = new Model();
        model.setIdentifier("Jordan");

        ModelDto dto = new ModelDto();
        dto.setIdentifier("Jordan");

        List<ModelDto> dtos =
                List.of(dto);

        Page<Model> modelPage =
                new PageImpl<>(
                        List.of(model)
                );

        Mockito.when(
                modelRepository.findByDeletedFalse(null)
        ).thenReturn(modelPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(modelPage),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<ModelDto> response =
                modelService.findAll(null);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Jordan",
                response.getDtoList()
                        .get(0)
                        .getIdentifier()
        );
    }

    @Test
    void updateTest() {

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Jordan");

        Model existingModel = new Model();
        existingModel.setIdentifier("Jordan");

        Mockito.when(
                modelRepository.findByIdentifier("Jordan")
        ).thenReturn(existingModel);

        Mockito.when(
                modelRepository.save(existingModel)
        ).thenReturn(existingModel);

        ModelDto response =
                modelService.update(modelDto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Model updated successfully",
                response.getMessage()
        );

        Mockito.verify(modelRepository)
                .save(existingModel);
    }

    @Test
    void updateTestFailure() {

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Jordan");

        Mockito.when(
                modelRepository.findByIdentifier("Jordan")
        ).thenReturn(null);

        ModelDto response =
                modelService.update(modelDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage()
                        .contains("not found")
        );
    }

    @Test
    void findByIdentifierTest() {

        Model model = new Model();
        model.setIdentifier("Jordan");

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Jordan");

        Mockito.when(
                modelRepository.findByIdentifier("Jordan")
        ).thenReturn(model);

        Mockito.when(
                modelMapper.map(
                        model,
                        ModelDto.class
                )
        ).thenReturn(modelDto);

        ModelDto response =
                modelService.findByIdentifier("Jordan");

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                "Jordan",
                response.getIdentifier()
        );
    }

    @Test
    void deleteByIdentifierTest() {

        Model model = new Model();
        model.setIdentifier("Jordan");

        Mockito.when(
                modelRepository.findByIdentifier("Jordan")
        ).thenReturn(model);

        modelService.deleteByIdentifier("Jordan");

        Mockito.verify(modelRepository)
                .findByIdentifier("Jordan");

        Mockito.verify(modelRepository)
                .save(model);
    }

    @Test
    void toggleStatusSuccessTest() {

        Model model = new Model();
        model.setIdentifier("Jordan");
        model.setStatus(false);

        ModelDto mappedDto =
                new ModelDto();

        mappedDto.setIdentifier("Jordan");
        mappedDto.setStatus(true);

        Mockito.when(
                modelRepository.findByIdentifier("Jordan")
        ).thenReturn(model);

        Mockito.when(
                modelMapper.map(
                        model,
                        ModelDto.class
                )
        ).thenReturn(mappedDto);

        ModelDto response =
                modelService.toggleStatus(
                        "Jordan",
                        true
                );

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Status updated successfully",
                response.getMessage()
        );

        Mockito.verify(modelRepository)
                .save(model);
    }

    @Test
    void toggleStatusFailureTest() {

        Mockito.when(
                modelRepository.findByIdentifier("Jordan")
        ).thenReturn(null);

        ModelDto response =
                modelService.toggleStatus(
                        "Jordan",
                        true
                );

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Model not found",
                response.getMessage()
        );
    }
}