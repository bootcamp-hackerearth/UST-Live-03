package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.models.service.impl.ModelServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private ModelServiceImpl modelService;

    private Model model;
    private ModelDto modelDto;

    @BeforeEach
    void setUp() {

        model = new Model();
        model.setIdentifier("MOD1");
        model.setStatus(true);
        model.setDeleted(false);

        modelDto = new ModelDto();
        modelDto.setIdentifier("MOD1");
    }

    @Test
    void testFindByIdentifier() {

        when(modelRepository.findByIdentifier("MOD1"))
                .thenReturn(model);

        when(modelMapper.map(model, ModelDto.class))
                .thenReturn(modelDto);

        ModelDto result = modelService.findByIdentifier("MOD1");

        assertNotNull(result);
        assertEquals("MOD1", result.getIdentifier());
    }

    @Test
    void testSave_NewModel() {

        when(modelRepository.findByIdentifier("MOD1"))
                .thenReturn(null);

        when(modelMapper.map(modelDto, Model.class))
                .thenReturn(model);

        ModelDto result = modelService.save(modelDto);

        assertNotNull(result);

        verify(modelRepository).save(model);
    }

    @Test
    void testSave_AlreadyExists() {

        when(modelRepository.findByIdentifier("MOD1"))
                .thenReturn(model);

        ModelDto result = modelService.save(modelDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_SoftDeleted() {

        model.setDeleted(true);

        when(modelRepository.findByIdentifier("MOD1"))
                .thenReturn(model);

        ModelDto result = modelService.save(modelDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    @Test
    void testUpdate_Success() {

        when(modelRepository.findByIdentifier("MOD1"))
                .thenReturn(model);

        doNothing().when(modelMapper)
                .map(modelDto, model);

        ModelDto result = modelService.update(modelDto);

        assertNotNull(result);

        verify(modelRepository).save(model);
    }

    @Test
    void testUpdate_NotFound() {

        when(modelRepository.findByIdentifier("MOD1"))
                .thenReturn(null);

        ModelDto result = modelService.update(modelDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testDelete() {

        when(modelRepository.findByIdentifier("MOD1"))
                .thenReturn(model);

        modelService.delete("MOD1");

        assertTrue(model.isDeleted());
        assertFalse(model.isStatus());

        verify(modelRepository).save(model);
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Model> page =
                new PageImpl<>(Collections.singletonList(model));

        when(modelRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(modelDto));

        WsDto<ModelDto> result =
                modelService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Model> specification =
                mock(Specification.class);

        Page<Model> page =
                new PageImpl<>(Collections.singletonList(model));

        when(modelRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(modelDto));

        WsDto<ModelDto> result =
                modelService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());

        verify(modelRepository)
                .findAll(specification, pageable);
    }

    @Test
    void testChangeToggleStatus() {

        when(modelRepository.findByIdentifier("MOD1"))
                .thenReturn(model);

        when(modelMapper.map(model, ModelDto.class))
                .thenReturn(modelDto);

        ModelDto result =
                modelService.changeToggleStatus("MOD1", false);

        assertNotNull(result);
        assertFalse(model.isStatus());

        verify(modelRepository).save(model);
    }

    @Test
    void testFindActiveStatus() {

        Model inactive = new Model();
        inactive.setStatus(false);

        when(modelRepository.findAll())
                .thenReturn(List.of(model, inactive));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(modelDto));

        List<ModelDto> result =
                modelService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}