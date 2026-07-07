package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
        modelDto.setIdentifier("Admin");

        Mockito.when(modelRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        Model model = new Model();

        Mockito.when(modelMapper.map(modelDto, Model.class))
                .thenReturn(model);

        Mockito.when(modelRepository.save(model))
                .thenReturn(model);

        ModelDto response = modelService.save(modelDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelRepository).save(model);
        Assertions.assertFalse(model.getIsDeleted());
    }

    @Test
    void saveTestFailure() {

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");

        Model model = new Model();
        model.setIsDeleted(false);

        Mockito.when(modelRepository.findByIdentifier("Admin"))
                .thenReturn(model);

        ModelDto response = modelService.save(modelDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveDeletedModelFailureTest() {

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");

        Model model = new Model();
        model.setIsDeleted(true);

        Mockito.when(modelRepository.findByIdentifier("Admin"))
                .thenReturn(model);

        ModelDto response = modelService.save(modelDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(
                response.getMessage().contains("was deleted")
        );
    }

    @Test
    void findByIdentifierTest() {

        Model model = new Model();
        model.setIdentifier("Admin");

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");

        Mockito.when(modelRepository.findByIdentifier("Admin"))
                .thenReturn(model);

        Mockito.when(modelMapper.map(model, ModelDto.class))
                .thenReturn(modelDto);

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

        Mockito.verify(modelMapper)
                .map(modelDto, existingModel);

        Mockito.verify(modelRepository)
                .save(existingModel);
    }

    @Test
    void updateTestFailure() {

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");

        Mockito.when(modelRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        ModelDto response = modelService.update(modelDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {

        Model model = new Model();
        model.setIdentifier("Admin");
        model.setStatus(true);
        model.setIsDeleted(false);

        Mockito.when(modelRepository.findByIdentifier("Admin"))
                .thenReturn(model);

        Mockito.when(modelRepository.save(model))
                .thenReturn(model);

        ModelDto response = modelService.delete("Admin");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Model deleted successfully",
                response.getMessage()
        );

        Assertions.assertTrue(model.getIsDeleted());
        Assertions.assertFalse(model.getStatus());

        Mockito.verify(modelRepository)
                .save(model);
    }

    @Test
    void deleteNotFoundTest() {

        Mockito.when(modelRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        ModelDto response = modelService.delete("Admin");

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Model with identifier - Admin not found",
                response.getMessage()
        );
    }

    @Test
    void findAllTest() {

        Model model = new Model();
        model.setIdentifier("Admin");

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");

        List<Model> models = List.of(model);
        List<ModelDto> modelDtos = List.of(modelDto);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Model> modelPage =
                new PageImpl<>(models, pageable, models.size());

        Mockito.when(
                modelRepository.findByIsDeleted(
                        false,
                        pageable
                )
        ).thenReturn(modelPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(models),
                        Mockito.any(Type.class)
                )
        ).thenReturn(modelDtos);

        PaginatedResponseDto<ModelDto> response =
                modelService.findAll(pageable);

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals(
                "Admin",
                response.getItems().get(0).getIdentifier()
        );

        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
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

        Mockito.when(
                modelRepository.findByStatusAndIsDeleted(
                        true,
                        false
                )
        ).thenReturn(models);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(models),
                        Mockito.any(Type.class)
                )
        ).thenReturn(modelDtos);

        List<ModelDto> response =
                modelService.findAllActive();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "Admin",
                response.get(0).getIdentifier()
        );
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

        Mockito.verify(modelRepository)
                .save(model);
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(modelRepository.findByIdentifier("INVALID"))
                .thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> modelService.findByIdentifier("INVALID")
        );

        Assertions.assertEquals(
                "Model with identifier INVALID not found",
                exception.getMessage()
        );
    }

    @Test
    void findAllSpecificationTest() {

        Model model = new Model();
        model.setIdentifier("Admin");

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("Admin");

        List<Model> models = List.of(model);
        List<ModelDto> modelDtos = List.of(modelDto);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Model> page = new PageImpl<>(models, pageable, models.size());

        Mockito.when(
                modelRepository.findAll(
                        Mockito.<org.springframework.data.jpa.domain.Specification<Model>>any(),
                        Mockito.eq(pageable)
                )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(models),
                        Mockito.any(Type.class)
                )
        ).thenReturn(modelDtos);

        PaginatedResponseDto<ModelDto> response =
                modelService.findAll(
                        Mockito.mock(org.springframework.data.jpa.domain.Specification.class),
                        pageable
                );

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}