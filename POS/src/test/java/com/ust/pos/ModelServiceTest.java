package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

    public static final String INVALID = "INVALID";
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

        assertNotNull(service.findByIdentifier("M1"));

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findByIdentifier(INVALID)
                );

        assertEquals(
                "Model with identifier 'INVALID' not found",
                exception.getMessage()
        );
    }

    @Test
    void saveTest() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("M1");

        Model model = new Model();
        model.setStatus(null);

        when(repository.findByIdentifier("M1"))
                .thenReturn(null);

        when(mapper.map(dto, Model.class))
                .thenReturn(model);

        ModelDto result = service.save(dto);

        verify(repository).save(model);

        assertNotNull(result);
        assertTrue(model.getStatus());

        Model existing = new Model();
        existing.setDeleted(false);

        when(repository.findByIdentifier("M1"))
                .thenReturn(existing);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Model with identifier - M1 already exists",
                result.getMessage()
        );

        existing.setDeleted(true);

        when(repository.findByIdentifier("M1"))
                .thenReturn(existing);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Model with Identifier M1 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateAndDeleteTest() {

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

        ModelDto invalidDto = new ModelDto();
        invalidDto.setIdentifier(INVALID);

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        result = service.update(invalidDto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Model with identifier - INVALID not found",
                result.getMessage()
        );

        service.delete("M1");

        verify(repository, atLeast(2)).save(any(Model.class));

        when(repository.findByIdentifierAndDeletedFalse("NOTFOUND"))
                .thenReturn(null);

        service.delete("NOTFOUND");
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Model model = new Model();
        model.setStatus(false);

        when(repository.findByIdentifierAndDeletedFalse("M2"))
                .thenReturn(model);

        service.toggleStatus("M2");

        assertTrue(model.getStatus());

        verify(repository).save(model);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Model> page =
                new PageImpl<>(
                        List.of(new Model()),
                        pageable,
                        1
                );

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new ModelDto()));

        WsDto<ModelDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());

        Specification<Model> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<ModelDto> specResult =
                service.findAll(specification, pageable);

        assertEquals(1, specResult.getDtoList().size());
        assertEquals(1, specResult.getTotalRecords());

        verify(repository).findAll(any(Specification.class), eq(pageable));
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

        when(repository.findByStatusTrueAndDeletedFalse())
                .thenReturn(Collections.emptyList());

        result = service.findAllActive();

        assertTrue(result.isEmpty());
    }

    @Test
    void toggleStatusTest() {

        Model model = new Model();
        model.setStatus(true);

        when(repository.findByIdentifierAndDeletedFalse("M1"))
                .thenReturn(model);

        service.toggleStatus("M1");

        assertFalse(model.getStatus());

        verify(repository).save(model);

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.toggleStatus(INVALID)
                );

        assertEquals(
                "model not found",
                exception.getMessage()
        );

        assertEquals(
                "model not found",
                ModelServiceImpl.MODEL_NOT_FOUND.getMessage()
        );
    }
}