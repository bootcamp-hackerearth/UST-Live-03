package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
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
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private UnitServiceImpl unitService;

    private Unit unit;
    private UnitDto unitDto;

    @BeforeEach
    void setUp() {
        unit = new Unit();
        unit.setIdentifier("U1");
        unit.setStatus(true);
        unit.setDeleted(false);

        unitDto = new UnitDto();
        unitDto.setIdentifier("U1");
    }

    // ✅ FIND BY IDENTIFIER
    @Test
    void testFindByIdentifier() {
        when(unitRepository.findByIdentifier("U1")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.findByIdentifier("U1");

        assertNotNull(result);
    }

    // ✅ SAVE - NEW UNIT
    @Test
    void testSave_NewUnit() {
        when(unitRepository.findByIdentifier("U1")).thenReturn(null);
        when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);

        doNothing().when(unitService).setAuditFields(unit, true);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);
        verify(unitRepository).save(unit);
    }

    // ✅ SAVE - ALREADY EXISTS
    @Test
    void testSave_AlreadyExists() {
        when(unitRepository.findByIdentifier("U1")).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    // ✅ SAVE - SOFT DELETED
    @Test
    void testSave_SoftDeleted() {
        unit.setDeleted(true);

        when(unitRepository.findByIdentifier("U1")).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    // ✅ UPDATE - SUCCESS
    @Test
    void testUpdate_Success() {
        when(unitRepository.findByIdentifier("U1")).thenReturn(unit);

        doNothing().when(modelMapper).map(unitDto, unit);
        doNothing().when(unitService).setAuditFields(unit, false);

        UnitDto result = unitService.update(unitDto);

        assertNotNull(result);
        verify(unitRepository).save(unit);
    }

    // ✅ UPDATE - NOT FOUND
    @Test
    void testUpdate_NotFound() {
        when(unitRepository.findByIdentifier("U1")).thenReturn(null);

        UnitDto result = unitService.update(unitDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    // ✅ DELETE (Soft Delete)
    @Test
    void testDelete() {
        when(unitRepository.findByIdentifier("U1")).thenReturn(unit);

        doNothing().when(unitService).softDelete(unit);
        doNothing().when(unitService).setAuditFields(unit, false);

        unitService.delete("U1");

        verify(unitRepository).save(unit);
    }

    // ✅ FIND ALL (Pagination)
    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Unit> page = new PageImpl<>(Collections.singletonList(unit));

        when(unitRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(unitDto));

        WsDto<UnitDto> result = unitService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    // ✅ CHANGE TOGGLE STATUS
    @Test
    void testChangeToggleStatus() {
        when(unitRepository.findByIdentifier("U1")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.changeToggleStatus("U1", false);

        assertNotNull(result);
        assertFalse(unit.isStatus());
        verify(unitRepository).save(unit);
    }

    // ✅ FIND ACTIVE STATUS
    @Test
    void testFindActiveStatus() {
        unit.setStatus(true);

        Unit inactive = new Unit();
        inactive.setStatus(false);

        List<Unit> units = List.of(unit, inactive);

        when(unitRepository.findAll()).thenReturn(units);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(unitDto));

        List<UnitDto> result = unitService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}