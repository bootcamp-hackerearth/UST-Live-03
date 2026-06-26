package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.service.impl.ModelServiceImpl;
import com.ust.pos.modell.Model;
import com.ust.pos.modell.ModelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        ModelDto dto = new ModelDto();

        when(repository.findByIdentifierAndDeletedFalse("M1"))
                .thenReturn(model);

        when(mapper.map(model, ModelDto.class))
                .thenReturn(dto);

        ModelDto result = service.findByIdentifier("M1");

        assertNotNull(result);
    }

    @Test
    void saveSuccessTest() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        Model model = new Model();

        when(repository.findByIdentifier("M1"))
                .thenReturn(null);

        when(mapper.map(dto, Model.class))
                .thenReturn(model);

        ModelDto result = service.save(dto);

        assertNotNull(result);

        verify(repository).save(model);
    }

    @Test
    void saveDuplicateTest() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        Model model = new Model();
        model.setDeleted(false);

        when(repository.findByIdentifier("M1"))
                .thenReturn(model);

        ModelDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Model with identifier - M1 already exists",
                result.getMessage()
        );
    }

    @Test
    void saveSoftDeletedTest() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        Model model = new Model();
        model.setDeleted(true);

        when(repository.findByIdentifier("M1"))
                .thenReturn(model);

        ModelDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Model with Identifier M1 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateSuccessTest() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        Model model = new Model();
        model.setIdentifier("M1");
        model.setCreatedBy("admin");
        model.setCreatedOn(LocalDateTime.now());

        when(repository.findByIdentifierAndDeletedFalse("M1"))
                .thenReturn(model);

        ModelDto result = service.update(dto);

        assertNotNull(result);

        verify(mapper).map(dto, model);
        verify(repository).save(model);
    }

    @Test
    void updateNotFoundTest() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        when(repository.findByIdentifierAndDeletedFalse("M1"))
                .thenReturn(null);

        ModelDto result = service.update(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Model with identifier - M1 not found",
                result.getMessage()
        );
    }

    @Test
    void deleteExistingTest() {

        Model model = new Model();

        when(repository.findByIdentifierAndDeletedFalse("M1"))
                .thenReturn(model);

        service.delete("M1");

        verify(repository).save(model);
    }

    @Test
    void deleteNotFoundTest() {

        when(repository.findByIdentifierAndDeletedFalse("M1"))
                .thenReturn(null);

        service.delete("M1");

        verify(repository, never()).save(any());
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Model model = new Model();
        ModelDto dto = new ModelDto();

        Page<Model> page =
                new PageImpl<>(List.of(model), pageable, 1);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<ModelDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Model> page =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        WsDto<ModelDto> result =
                service.findAll(pageable);

        assertTrue(result.getDtoList().isEmpty());
    }

    @Test
    void findAllActiveTest() {

        Model model = new Model();
        ModelDto dto = new ModelDto();

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(model));

        when(mapper.map(model, ModelDto.class))
                .thenReturn(dto);

        List<ModelDto> result =
                service.findAllActive();

        assertEquals(1, result.size());
    }

    @Test
    void findAllActiveEmptyTest() {

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(Collections.emptyList());

        List<ModelDto> result =
                service.findAllActive();

        assertTrue(result.isEmpty());
    }

    @Test
    void toggleStatusSuccessTest() {

        Model model = new Model();
        model.setStatus(true);

        when(repository.findByIdentifierAndDeletedFalse("M1"))
                .thenReturn(model);

        service.toggleStatus("M1");

        assertFalse(model.getStatus());

        verify(repository).save(model);
    }

    @Test
    void toggleStatusNotFoundTest() {

        when(repository.findByIdentifierAndDeletedFalse("M1"))
                .thenReturn(null);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.toggleStatus("M1")
                );

        assertEquals(
                "model not found",
                exception.getMessage()
        );
    }
}