package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
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
    void findByIdentifierTest() {
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
    void saveTest() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        Model model = new Model();
        model.setIdentifier("M1");

        when(repository.findByIdentifier("M1")).thenReturn(null);
        when(mapper.map(dto, Model.class)).thenReturn(model);

        ModelDto result = service.save(dto);
        verify(repository).save(model);
        assertEquals("M1", result.getIdentifier());

        when(repository.findByIdentifier("M1")).thenReturn(model);
        ModelDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void updateTest() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");
        Model model = new Model();
        model.setIdentifier("M1");

        when(repository.findByIdentifier("M1")).thenReturn(model);

        ModelDto updated = service.update(dto);
        verify(mapper).map(dto, model);
        verify(repository).save(model);
        assertEquals("M1", updated.getIdentifier());

        when(repository.findByIdentifier("X")).thenReturn(null);
        dto.setIdentifier("X");

        ModelDto failure = service.update(dto);
        assertFalse(failure.isSuccess());
        assertTrue(failure.getMessage().contains("not found"));
    }

    @Test
    void deleteTest() {
        service.delete("M1");
        verify(repository).deleteByIdentifier("M1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<ModelDto>>(){}.getType();

        Model model = new Model();
        model.setIdentifier("M1");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        Page<Model> page = new PageImpl<>(List.of(model), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(page.getContent(), type)).thenReturn(List.of(dto));

        WsDto<ModelDto> result = service.findAll(pageable);
        assertEquals(1, result.getDtoList().size());

        Page<Model> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(emptyPage);
        when(mapper.map(emptyPage.getContent(), type)).thenReturn(List.of());

        WsDto<ModelDto> empty = service.findAll(pageable);
        assertTrue(empty.getDtoList().isEmpty());
    }

    @Test
    void toggleStatusTest() {
        Model model = new Model();
        model.setIdentifier("M1");
        model.setStatus(true);

        when(repository.findByIdentifier("M1")).thenReturn(model);

        service.toggleStatus("M1");
        assertFalse(model.getStatus());
        verify(repository).save(model);

        when(repository.findByIdentifier("X")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.toggleStatus("X"));
        assertEquals("model not found", ex.getMessage());
    }

    @Test
    void findAllActiveTest() {
        Model model = new Model();
        model.setIdentifier("M1");
        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        when(repository.findByStatusTrue()).thenReturn(List.of(model));
        when(mapper.map(model, ModelDto.class)).thenReturn(dto);

        List<ModelDto> result = service.findAllActive();
        assertEquals(1, result.size());
    }
}