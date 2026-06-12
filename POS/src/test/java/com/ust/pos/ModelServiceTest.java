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
        Page<Model> modelPage = new PageImpl<>(models);

        when(modelRepository.findAll(pageable))
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
    void findAllWithoutPageableTest() {

        Model model = new Model();
        model.setIdentifier("CUST1");

        ModelDto modelDto = new ModelDto();
        modelDto.setIdentifier("CUST1");

        List<Model> models = List.of(model);
        List<ModelDto> modelDtos = List.of(modelDto);

        when(modelRepository.findAll())
                .thenReturn(models);

        when(modelMapper.map(
                eq(models),
                any(Type.class)
        )).thenReturn(modelDtos);

        PaginationResponseDto<ModelDto> result =
                modelService.findAll(null);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(
                "CUST1",
                result.getDtoList().get(0).getIdentifier()
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

        Mockito.when(modelRepository.findByIdentifier("M1"))
                .thenReturn(model);

        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, model);

        Mockito.when(modelRepository.save(model))
                .thenReturn(model);

        ModelDto result = modelService.update(dto);

        Mockito.verify(modelMapper).map(dto, model);
        Mockito.verify(modelRepository).save(model);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Model updated successfully", result.getMessage());
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
    void deleteTest() {
        Mockito.doNothing().when(modelRepository).deleteByIdentifier("M1");

        modelService.delete("M1");

        Mockito.verify(modelRepository).deleteByIdentifier("M1");
    }
}