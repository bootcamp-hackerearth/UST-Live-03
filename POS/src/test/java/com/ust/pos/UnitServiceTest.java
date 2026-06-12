package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @InjectMocks
    private UnitServiceImpl unitService;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);

        Unit unit = new Unit();
        unit.setIdentifier("U1");

        List<Unit> units = List.of(unit);
        Page<Unit> page = new PageImpl<>(units);

        when(unitRepository.findAll(pageable)).thenReturn(page);

        List<UnitDto> dtoList = List.of(new UnitDto());

        when(modelMapper.map(any(), any(java.lang.reflect.Type.class)))
                .thenReturn(dtoList);

        WsDto<UnitDto> result = unitService.findAll(pageable);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());

        verify(unitRepository).findAll(pageable);
    }

    @Test
    void testFindByIdentifier() {
        String id = "U1";

        Unit unit = new Unit();
        UnitDto dto = new UnitDto();

        when(unitRepository.findByIdentifier(id)).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);

        UnitDto result = unitService.findByIdentifier(id);

        assertNotNull(result);
        verify(unitRepository).findByIdentifier(id);
    }

    @Test
    void testSave_WhenUnitExists() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        when(unitRepository.findByIdentifier("U1")).thenReturn(new Unit());

        UnitDto result = unitService.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));

        verify(unitRepository, never()).save(any());
    }

    @Test
    void testSave_NewUnit() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U2");

        Unit unit = new Unit();

        when(unitRepository.findByIdentifier("U2")).thenReturn(null);
        when(modelMapper.map(dto, Unit.class)).thenReturn(unit);

        UnitDto result = unitService.save(dto);

        assertEquals("U2", result.getIdentifier());
        verify(unitRepository).save(unit);
    }

    @Test
    void testDelete() {
        unitService.delete("U1");

        verify(unitRepository).deleteByIdentifier("U1");
    }

    @Test
    void testUpdate_WhenUnitNotFound() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        when(unitRepository.findByIdentifier("U1")).thenReturn(null);

        UnitDto result = unitService.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_Success() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Unit existing = new Unit();

        when(unitRepository.findByIdentifier("U1")).thenReturn(existing);

        UnitDto result = unitService.update(dto);

        verify(modelMapper).map(dto, existing);
        verify(unitRepository).save(existing);
        assertEquals("U1", result.getIdentifier());
    }

    @Test
    void testToggleStatus_WhenUnitExists() {
        Unit unit = new Unit();
        unit.setStatus(true);

        when(unitRepository.findByIdentifier("U1")).thenReturn(unit);

        unitService.toggleStatus("U1");

        assertFalse(unit.isStatus());
        verify(unitRepository).save(unit);
    }

    @Test
    void testToggleStatus_WhenUnitNotFound() {
        when(unitRepository.findByIdentifier("U1")).thenReturn(null);

        unitService.toggleStatus("U1");

        verify(unitRepository, never()).save(any());
    }

    @Test
    void testFindActiveUnit() {
        List<Unit> activeUnits = List.of(new Unit(), new Unit());

        when(unitRepository.findByStatus(true)).thenReturn(activeUnits);

        List<Unit> result = unitService.findActiveUnit();

        assertEquals(2, result.size());
        verify(unitRepository).findByStatus(true);
    }
}