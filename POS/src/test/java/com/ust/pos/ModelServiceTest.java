package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.service.impl.ModelServiceImpl;
import com.ust.pos.modell.Model;
import com.ust.pos.modell.ModelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        Model entity = new Model();
        when(modelRepository.findByIdentifier("M1")).thenReturn(null);
        when(modelMapper.map(dto, Model.class)).thenReturn(entity);
        ModelDto response = modelService.save(dto);
        assertEquals("M1", response.getIdentifier());
        verify(modelRepository).save(entity);
    }

    @Test
    void saveTestFailure() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        when(modelRepository.findByIdentifier("M1")).thenReturn(new Model());
        ModelDto response = modelService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals("Model with identifier - M1 already exists", response.getMessage());
        verify(modelRepository, never()).save(any());
    }

    @Test
    void updateTest() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        Model existing = new Model();
        existing.setIdentifier("M1");
        when(modelRepository.findByIdentifierAndDeletedFalse("M1")).thenReturn(existing);
        ModelDto response = modelService.update(dto);
        assertTrue(response.isSuccess());
        verify(modelMapper).map(dto, existing);
        verify(modelRepository).save(existing);
    }

    @Test
    void updateTestFailure() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        when(modelRepository.findByIdentifierAndDeletedFalse("M1")).thenReturn(null);
        ModelDto response = modelService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals("Model with identifier - M1 not found", response.getMessage());
        verify(modelRepository, never()).save(any());
    }

    @Test
    void findByIdentifierTest() {
        Model entity = new Model();
        entity.setIdentifier("M1");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        when(modelRepository.findByIdentifierAndDeletedFalse("M1")).thenReturn(entity);
        when(modelMapper.map(entity, ModelDto.class)).thenReturn(dto);
        ModelDto response = modelService.findByIdentifier("M1");
        assertEquals("M1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        when(modelRepository.findByIdentifierAndDeletedFalse("M1")).thenReturn(null);
        when(modelMapper.map(null, ModelDto.class)).thenReturn(null);
        ModelDto response = modelService.findByIdentifier("M1");
        assertNull(response);
    }

    @Test
    void deleteTest() {
        Model model = new Model();
        model.setIdentifier("M1");
        model.setDeleted(false);
        when(modelRepository.findByIdentifierAndDeletedFalse("M1")).thenReturn(model);
        modelService.delete("M1");
        verify(modelRepository).save(model);
        assertTrue(model.getDeleted());
    }

    @Test
    void findAllTest() {
        Model model = new Model();
        model.setIdentifier("Admin");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("Admin");
        List<Model> models = List.of(model);
        List<ModelDto> modelDtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 50);
        Page<Model> page = new PageImpl<>(models, pageable, 1);
        when(modelRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(models), any(Type.class))).thenReturn(modelDtos);
        WsDto<ModelDto> response = modelService.findAll(pageable);
        assertNotNull(response);
        assertEquals(1, response.getDtoList().size());
        assertEquals(1, response.getTotalRecords());
        assertEquals(1, response.getTotalPage());
        assertEquals(50, response.getSizePerPage());
        assertEquals(0, response.getPage());
        verify(modelRepository).findAllByDeletedFalse(pageable);
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Model> page = new PageImpl<>(List.of(), pageable, 0);
        when(modelRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(List.class), any(Type.class))).thenReturn(List.of());
        WsDto<ModelDto> response = modelService.findAll(pageable);
        assertEquals(0, response.getTotalRecords());
        assertEquals(0, response.getDtoList().size());
    }

    @Test
    void findAllActiveTest() {
        Model entity = new Model();
        entity.setIdentifier("M1");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        when(modelRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(entity));
        when(modelMapper.map(entity, ModelDto.class)).thenReturn(dto);
        List<ModelDto> response = modelService.findAllActive();
        assertEquals(1, response.size());
        assertEquals("M1", response.get(0).getIdentifier());
    }

    @Test
    void findAllActiveEmptyTest() {
        when(modelRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of());
        List<ModelDto> response = modelService.findAllActive();
        assertTrue(response.isEmpty());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Model model = new Model();
        model.setIdentifier("M1");
        model.setStatus(true);
        when(modelRepository.findByIdentifierAndDeletedFalse("M1")).thenReturn(model);
        when(modelRepository.save(model)).thenReturn(model);
        when(modelMapper.map(model, ModelDto.class)).thenReturn(new ModelDto());
        modelService.toggleStatus("M1");
        assertFalse(model.getStatus());
        verify(modelRepository).save(model);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Model model = new Model();
        model.setIdentifier("M1");
        model.setStatus(false);
        when(modelRepository.findByIdentifierAndDeletedFalse("M1")).thenReturn(model);
        when(modelRepository.save(model)).thenReturn(model);
        when(modelMapper.map(model, ModelDto.class)).thenReturn(new ModelDto());
        modelService.toggleStatus("M1");
        assertTrue(model.getStatus());
    }

    @Test
    void toggleStatusNullToTrueTest() {
        Model model = new Model();
        model.setIdentifier("M1");
        model.setStatus(null);
        when(modelRepository.findByIdentifierAndDeletedFalse("M1")).thenReturn(model);
        when(modelRepository.save(model)).thenReturn(model);
        when(modelMapper.map(model, ModelDto.class)).thenReturn(new ModelDto());
        modelService.toggleStatus("M1");
        assertTrue(model.getStatus());
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(modelRepository.findByIdentifierAndDeletedFalse("M1")).thenReturn(null);
        IllegalArgumentException ex =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> modelService.toggleStatus("M1")
                );
        assertEquals(
                "model not found with identifier: M1",
                ex.getMessage()
        );
        verify(modelRepository, never()).save(any());
    }
}