package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginationResponseDto;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

    @InjectMocks
    private ModelServiceImpl modelService;

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllWithPageableTest() {

        Model model = new Model();
        model.setIdentifier("MODEL1");

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("MODEL1");

        List<Model> models = List.of(model);
        List<ModelDto> modelDtos = List.of(modelDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Model> modelPage =
                new PageImpl<>(models, pageable, models.size());

        when(modelRepository.findByIsDeletedFalse(pageable))
                .thenReturn(modelPage);

        when(modelMapper.map(
                eq(models),
                any(Type.class)
        )).thenReturn(modelDtos);

        PaginationResponseDto<ModelDto> result =
                modelService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(
                "MODEL1",
                result.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 5);

        Model model = new Model();
        model.setIdentifier("M1");

        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        Page<Model> page =
                new PageImpl<>(
                        List.of(model),
                        pageable,
                        1
                );

        Specification<Model> specification =
                Mockito.mock(Specification.class);

        Mockito.when(
                modelRepository.findAll(
                        Mockito.eq(specification),
                        Mockito.eq(pageable)
                )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(List.of(model)),
                        Mockito.any(Type.class)
                )
        ).thenReturn(List.of(dto));

        PaginationResponseDto<ModelDto> response =
                modelService.findAll(
                        specification,
                        pageable
                );

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );

        Assertions.assertEquals(
                1,
                response.getTotalPages()
        );

        Assertions.assertEquals(
                5,
                response.getSizePerPage()
        );
    }

    @Test
    void findByStatusTrueTest() {
        Model model = new Model();
        ModelDto dto = new ModelDto();

        Mockito.when(modelRepository.findByStatusTrue()).thenReturn(List.of(model));
        Mockito.when(
                modelMapper.map(
                        Mockito.anyList(),
                        Mockito.any(java.lang.reflect.Type.class)
                )
        ).thenReturn(List.of(dto));
        List<ModelDto> result = modelService.findByStatusTrue();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void findByIdentifierTest() {
        Model model = new Model();
        ModelDto dto = new ModelDto();

        Mockito.when(modelRepository.findByIdentifier("M1")).thenReturn(model);
        Mockito.when(modelMapper.map(model, ModelDto.class)).thenReturn(dto);

        ModelDto result = modelService.findByIdentifier("M1");

        Assertions.assertNotNull(result);
    }

    @Test
    void save_success() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        Model model = new Model();

        Mockito.when(modelRepository.findByIdentifier("M1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Model.class)).thenReturn(model);

        ModelDto result = modelService.save(dto);

        Mockito.verify(modelRepository).save(model);
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Successfully added the model", result.getMessage());
    }

    @Test
    void save_softDeletedModel() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        Model model = new Model();
        model.setIdentifier("M1");
        model.setDeleted(true);

        Mockito.when(
                modelRepository.findByIdentifier("M1")
        ).thenReturn(model);

        ModelDto response =
                modelService.save(dto);

        Assertions.assertFalse(
                response.isSuccess()
        );

        Assertions.assertTrue(
                response.getMessage()
                        .contains("deleted")
        );

        Mockito.verify(
                modelRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void save_failure_existing() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        Mockito.when(modelRepository.findByIdentifier("M1")).thenReturn(new Model());

        ModelDto result = modelService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Model M1 already exists", result.getMessage());
    }

    @Test
    void update_success() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        Model model = new Model();
        model.setIdentifier("M1");

        Mockito.when(
                modelRepository.findByIdentifier("M1")
        ).thenReturn(model);

        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, model);

        Mockito.when(
                modelRepository.save(model)
        ).thenReturn(model);

        ModelDto result = modelService.update(dto);

        Mockito.verify(modelMapper).map(dto, model);
        Mockito.verify(modelRepository).save(model);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals(
                "Model updated successfully",
                result.getMessage()
        );
    }

    @Test
    void update_failure() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        Mockito.when(modelRepository.findByIdentifier("M1")).thenReturn(null);

        ModelDto result = modelService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Model does not exist", result.getMessage());
    }

    @Test
    void updateStatus_success() {
        Model model = new Model();

        Mockito.when(modelRepository.findByIdentifier("M1")).thenReturn(model);

        ModelDto result = modelService.updateStatus("M1", true);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Status updated successfully", result.getMessage());
    }

    @Test
    void updateStatus_failure() {
        Mockito.when(modelRepository.findByIdentifier("M1")).thenReturn(null);

        ModelDto result = modelService.updateStatus("M1", true);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Model not found", result.getMessage());
    }

    @Test
    void updateStatus_verifyStatusChanged() {

        Model model = new Model();
        model.setStatus(false);

        Mockito.when(
                modelRepository.findByIdentifier("M1")
        ).thenReturn(model);

        ModelDto response =
                modelService.updateStatus(
                        "M1",
                        true
                );

        Assertions.assertTrue(
                response.isSuccess()
        );

        Assertions.assertTrue(
                model.isStatus()
        );
    }

    @Test
    void update_softDeletedModel() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        Model model = new Model();
        model.setIdentifier("M1");
        model.setDeleted(true);

        Mockito.when(
                modelRepository.findByIdentifier("M1")
        ).thenReturn(model);

        ModelDto response =
                modelService.update(dto);

        Assertions.assertFalse(
                response.isSuccess()
        );

        Assertions.assertTrue(
                response.getMessage()
                        .contains("deleted")
        );

        Mockito.verify(
                modelRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void deleteTest() {

        Model model = new Model();
        model.setIdentifier("M1");
        model.setDeleted(false);

        Mockito.when(
                modelRepository.findByIdentifier("M1")
        ).thenReturn(model);

        Mockito.when(
                modelRepository.save(model)
        ).thenReturn(model);

        modelService.delete("M1");

        Assertions.assertTrue(model.isDeleted());

        Mockito.verify(modelRepository)
                .findByIdentifier("M1");

        Mockito.verify(modelRepository)
                .save(model);
    }
}