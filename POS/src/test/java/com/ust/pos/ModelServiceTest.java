package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.service.impl.ModelServiceImpl;
import com.ust.pos.models.Model;
import com.ust.pos.models.ModelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

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
        Model existing = new Model();
        existing.setDeleted(false);
        when(modelRepository.findByIdentifier("M2")).thenReturn(existing);
        ModelDto result = modelService.save(new ModelDto() {{setIdentifier("M2");}});
        assertFalse(result.isSuccess());
        Model deleted = new Model();
        deleted.setDeleted(true);
        when(modelRepository.findByIdentifier("M3")).thenReturn(deleted);
        result = modelService.save(new ModelDto() {{setIdentifier("M3");}});
        assertFalse(result.isSuccess());
    }

    @Test
    void updateTest() {
        Model existing = new Model();
        existing.setIdentifier("M1");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        when(modelRepository.findByIdentifierAndDeletedFalse("M1")).thenReturn(existing);
        ModelDto response = modelService.update(dto);
        assertTrue(response.isSuccess());
        verify(modelMapper).map(dto, existing);
        verify(modelRepository).save(existing);
        when(modelRepository.findByIdentifierAndDeletedFalse("M2")).thenReturn(null);
        ModelDto result = modelService.update(new ModelDto() {{setIdentifier("M2");}});
        assertFalse(result.isSuccess());
    }

    @Test
    void findByIdentifierAndDeleteTest() {
        Model model = new Model();
        model.setIdentifier("M1");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        when(modelRepository.findByIdentifierAndDeletedFalse("M1")).thenReturn(model);
        when(modelMapper.map(model, ModelDto.class)).thenReturn(dto);
        ModelDto result = modelService.findByIdentifier("M1");
        assertEquals("M1", result.getIdentifier());
        modelService.delete("M1");
        assertTrue(model.getDeleted());
        verify(modelRepository).save(model);
        when(modelRepository.findByIdentifierAndDeletedFalse("M2")).thenReturn(null);
        assertNull(modelService.findByIdentifier("M2"));
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Model> page = new PageImpl<>(List.of(new Model()), pageable, 1);
        when(modelRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(new ModelDto()));
        WsDto<ModelDto> result = modelService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        Page<Model> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(modelRepository.findAllByDeletedFalse(pageable)).thenReturn(emptyPage);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of());
        result = modelService.findAll(pageable);
        assertEquals(0, result.getTotalRecords());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<Model> specification = mock(Specification.class);
        Page<Model> page = new PageImpl<>(List.of(new Model()), pageable, 1);
        when(modelRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(new ModelDto()));
        WsDto<ModelDto> result = modelService.findAll(specification, pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        verify(modelRepository).findAll(specification, pageable);
    }

    @Test
    void findAllActiveTest() {
        Model model = new Model();
        model.setIdentifier("M1");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        when(modelRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(model));
        when(modelMapper.map(model, ModelDto.class)).thenReturn(dto);
        List<ModelDto> result = modelService.findAllActive();
        assertEquals(1, result.size());
        when(modelRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of());
        assertTrue(modelService.findAllActive().isEmpty()
        );
    }

    @Test
    void toggleStatusTest() {
        when(modelRepository.save(any(Model.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Model.class), eq(ModelDto.class))).thenReturn(new ModelDto());
        Model model = new Model();
        model.setIdentifier("M1");
        model.setStatus(true);
        when(modelRepository.findByIdentifierAndDeletedFalse("M1")).thenReturn(model);
        modelService.toggleStatus("M1");
        assertFalse(model.getStatus());
        model.setStatus(false);
        when(modelRepository.findByIdentifierAndDeletedFalse("M2")).thenReturn(model);
        modelService.toggleStatus("M2");
        assertTrue(model.getStatus());
        model.setStatus(null);
        when(modelRepository.findByIdentifierAndDeletedFalse("M3")).thenReturn(model);
        modelService.toggleStatus("M3");
        assertTrue(model.getStatus());
        when(modelRepository.findByIdentifierAndDeletedFalse("M4")).thenReturn(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> modelService.toggleStatus("M4"));
        assertEquals("model not found with identifier: M4", ex.getMessage());
    }
}