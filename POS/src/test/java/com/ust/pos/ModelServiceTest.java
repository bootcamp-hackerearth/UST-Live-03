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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

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
    void saveSuccessTest() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MDL001");
        Model model = new Model();
        Mockito.when(modelRepository.findByIdentifier("MDL001")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Model.class)).thenReturn(model);
        Mockito.when(modelRepository.save(model)).thenReturn(model);
        ModelDto response = modelService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("MDL001", response.getIdentifier());
        Mockito.verify(modelRepository).save(model);
    }

    @Test
    void saveDuplicateModelTest() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MDL001");
        Model existing = new Model();
        existing.setDeleted(false);
        Mockito.when(modelRepository.findByIdentifier("MDL001")).thenReturn(existing);
        ModelDto response = modelService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));
        Mockito.verify(modelRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveSoftDeletedModelTest() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MDL001");
        Model existing = new Model();
        existing.setDeleted(true);
        Mockito.when(modelRepository.findByIdentifier("MDL001")).thenReturn(existing);
        ModelDto response = modelService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
        Mockito.verify(modelRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Model model = new Model();
        model.setIdentifier("MDL001");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MDL001");
        Mockito.when(modelRepository.findByIdentifier("MDL001")).thenReturn(model);
        Mockito.when(modelMapper.map(model, ModelDto.class)).thenReturn(dto);
        ModelDto response = modelService.findByIdentifier("MDL001");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("MDL001", response.getIdentifier());
    }

    @Test
    void findAllWithPageableTest() {
        Model model = new Model();
        model.setIdentifier("MDL001");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MDL001");
        List<Model> models = List.of(model);
        List<ModelDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Model> page = new PageImpl<>(models);
        Mockito.when(modelRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(models), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<ModelDto> response = modelService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("MDL001", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Model model = new Model();
        model.setIdentifier("MDL001");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MDL001");
        List<Model> models = List.of(model);
        List<ModelDto> dtos = List.of(dto);
        Mockito.when(modelRepository.findAll()).thenReturn(models);
        Mockito.when(modelMapper.map(Mockito.eq(models), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<ModelDto> response = modelService.findAll(null);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("MDL001", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void updateSuccessTest() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MDL001");
        Model existing = new Model();
        existing.setIdentifier("MDL001");
        Mockito.when(modelRepository.findByIdentifier("MDL001")).thenReturn(existing);
        Mockito.when(modelRepository.save(existing)).thenReturn(existing);
        ModelDto response = modelService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(modelRepository).save(existing);
    }

    @Test
    void updateFailureTest() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MDL001");
        Mockito.when(modelRepository.findByIdentifier("MDL001")).thenReturn(null);
        ModelDto response = modelService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void deleteSuccessTest() {
        Model model = new Model();
        model.setIdentifier("MDL001");
        Mockito.when(modelRepository.findByIdentifier("MDL001")).thenReturn(model);
        modelService.deleteByIdentifier("MDL001");
        Assertions.assertTrue(model.isDeleted());
        Mockito.verify(modelRepository).save(model);
    }

    @Test
    void deleteModelNotFoundTest() {
        Mockito.when(modelRepository.findByIdentifier("MDL001")).thenReturn(null);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                        () -> modelService.deleteByIdentifier("MDL001"));
        Assertions.assertEquals("Model not found", exception.getMessage());
    }

    @Test
    void toggleStatusSuccessTest() {
        Model model = new Model();
        model.setIdentifier("MDL001");
        model.setStatus(false);
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MDL001");
        dto.setStatus(true);
        Mockito.when(modelRepository.findByIdentifier("MDL001")).thenReturn(model);
        Mockito.when(modelRepository.save(model)).thenReturn(model);
        Mockito.when(modelMapper.map(model, ModelDto.class)).thenReturn(dto);
        ModelDto response = modelService.toggleStatus("MDL001", true);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
        Assertions.assertTrue(model.isStatus());
        Mockito.verify(modelRepository).save(model);
    }

    @Test
    void toggleStatusModelNotFoundTest() {
        Mockito.when(modelRepository.findByIdentifier("MDL001")).thenReturn(null);
        ModelDto response = modelService.toggleStatus("MDL001", true);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Model not found", response.getMessage());
        Mockito.verify(modelRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllWithSpecificationTest() {
        Model model = new Model();
        model.setIdentifier("MDL001");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MDL001");
        List<Model> models = List.of(model);
        List<ModelDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Model> page = new PageImpl<>(models, pageable, 1);
        Specification<Model> specification = Mockito.mock(Specification.class);
        Mockito.when(modelRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(models), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<ModelDto> response = modelService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("MDL001", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(5, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(modelRepository).findAll(specification, pageable);
    }

    @Test
    void findAllWithSpecificationNoDataTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Specification<Model> specification = Mockito.mock(Specification.class);
        Page<Model> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        Mockito.when(modelRepository.findAll(specification, pageable)).thenReturn(emptyPage);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());
        PaginationResponseDto<ModelDto> response = modelService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getDtoList().isEmpty());
        Assertions.assertEquals(0, response.getTotalRecords());
        Assertions.assertEquals(0, response.getTotalPages());
        Assertions.assertEquals(5, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(modelRepository).findAll(specification, pageable);
    }
}