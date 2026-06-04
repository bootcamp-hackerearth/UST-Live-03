package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.model.service.impl.ModelServiceImpl;
import com.ust.pos.modell.Model;
import com.ust.pos.modell.ModelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
    private ModelServiceImpl service;

    @Mock
    private ModelRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifier_shouldHandleBothCases() {
        Model model = new Model();
        model.setIdentifier("M1");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        when(repository.findByIdentifier("M1")).thenReturn(model);
        when(mapper.map(model, ModelDto.class)).thenReturn(dto);
        assertEquals("M1", service.findByIdentifier("M1").getIdentifier());
        when(repository.findByIdentifier("X")).thenReturn(null);
        when(mapper.map(null, ModelDto.class)).thenReturn(null);
        assertNull(service.findByIdentifier("X"));
    }

    @Test
    void save_shouldHandleAllCases() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        Model model = new Model();
        model.setIdentifier("M1");
        when(repository.findByIdentifier("M1")).thenReturn(null);
        when(mapper.map(dto, Model.class)).thenReturn(model);
        ModelDto result = service.save(dto);
        verify(mapper).map(dto, Model.class);
        verify(repository).save(model);
        assertEquals("M1", result.getIdentifier());
        when(repository.findByIdentifier("M1")).thenReturn(model);
        ModelDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void update_shouldHandleBothCases() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        Model model = new Model();
        model.setIdentifier("M1");
        when(repository.findByIdentifier("M1")).thenReturn(model);
        ModelDto success = service.update(dto);
        verify(mapper).map(dto, model);
        verify(repository).save(model);
        assertEquals("M1", success.getIdentifier());
        when(repository.findByIdentifier("X")).thenReturn(null);
        dto.setIdentifier("X");
        ModelDto failure = service.update(dto);
        assertFalse(failure.isSuccess());
        assertTrue(failure.getMessage().contains("not found"));
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete("M1");
        verify(repository).deleteByIdentifier("M1");
    }

    @Test
    void findAll_shouldHandleDataAndEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<ModelDto>>() {}.getType();
        Model model = new Model();
        model.setIdentifier("M1");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        Page<Model> page =
                new PageImpl<>(List.of(model), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));
        assertEquals(1, service.findAll(pageable).size());
        verify(mapper).map(any(), eq(type));
        Page<Model> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(emptyPage);
        when(mapper.map(List.of(), type)).thenReturn(List.of());
        assertTrue(service.findAll(pageable).isEmpty());
    }

    @Test
    void toggleStatus_shouldHandleBothCases() {
        Model model = new Model();
        model.setIdentifier("M1");
        model.setStatus(true);
        when(repository.findByIdentifier("M1")).thenReturn(model);
        service.toggleStatus("M1");
        assertFalse(model.isStatus());
        verify(repository).save(model);
        when(repository.findByIdentifier("X")).thenReturn(null);
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.toggleStatus("X")
        );
        assertEquals("model not found", ex.getMessage());
    }
}