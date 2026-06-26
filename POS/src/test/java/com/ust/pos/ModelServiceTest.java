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
        model.setIdentifier("M1");
        model.setStatus(true);
        model.setDeleted(false);

        modelDto = new ModelDto();
        modelDto.setIdentifier("M1");
    }

    // ✅ FIND BY IDENTIFIER
    @Test
    void testFindByIdentifier() {
        when(modelRepository.findByIdentifier("M1")).thenReturn(model);
        when(modelMapper.map(model, ModelDto.class)).thenReturn(modelDto);

        ModelDto result = modelService.findByIdentifier("M1");

        assertNotNull(result);
    }

    // ✅ SAVE - NEW MODEL
    @Test
    void testSave_NewModel() {
        when(modelRepository.findByIdentifier("M1")).thenReturn(null);
        when(modelMapper.map(modelDto, Model.class)).thenReturn(model);

        doNothing().when(modelService).setAuditFields(model, true);

        ModelDto result = modelService.save(modelDto);

        assertNotNull(result);
        verify(modelRepository).save(model);
    }

    // ✅ SAVE - ALREADY EXISTS
    @Test
    void testSave_AlreadyExists() {
        when(modelRepository.findByIdentifier("M1")).thenReturn(model);

        ModelDto result = modelService.save(modelDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    // ✅ SAVE - SOFT DELETED
    @Test
    void testSave_SoftDeleted() {
        model.setDeleted(true);

        when(modelRepository.findByIdentifier("M1")).thenReturn(model);

        ModelDto result = modelService.save(modelDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    // ✅ UPDATE - SUCCESS
    @Test
    void testUpdate_Success() {
        when(modelRepository.findByIdentifier("M1")).thenReturn(model);

        doNothing().when(modelMapper).map(modelDto, model);
        doNothing().when(modelService).setAuditFields(model, false);

        ModelDto result = modelService.update(modelDto);

        assertNotNull(result);
        verify(modelRepository).save(model);
    }

    // ✅ UPDATE - NOT FOUND
    @Test
    void testUpdate_NotFound() {
        when(modelRepository.findByIdentifier("M1")).thenReturn(null);

        ModelDto result = modelService.update(modelDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    // ✅ DELETE
    @Test
    void testDelete() {
        when(modelRepository.findByIdentifier("M1")).thenReturn(model);

        doNothing().when(modelService).softDelete(model);
        doNothing().when(modelService).setAuditFields(model, false);

        modelService.delete("M1");

        verify(modelRepository).save(model);
    }

    // ✅ FIND ALL (Pagination)
    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Model> page = new PageImpl<>(Collections.singletonList(model));

        when(modelRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(modelDto));

        WsDto<ModelDto> result = modelService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    // ✅ CHANGE TOGGLE STATUS
    @Test
    void testChangeToggleStatus() {
        when(modelRepository.findByIdentifier("M1")).thenReturn(model);
        when(modelMapper.map(model, ModelDto.class)).thenReturn(modelDto);

        ModelDto result = modelService.changeToggleStatus("M1", false);

        assertNotNull(result);
        assertFalse(model.isStatus());
        verify(modelRepository).save(model);
    }

    // ✅ FIND ACTIVE STATUS
    @Test
    void testFindActiveStatus() {
        model.setStatus(true);

        Model inactive = new Model();
        inactive.setStatus(false);

        List<Model> models = List.of(model, inactive);

        when(modelRepository.findAll()).thenReturn(models);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(modelDto));

        List<ModelDto> result = modelService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}