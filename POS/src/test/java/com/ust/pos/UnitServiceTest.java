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

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UnitServiceImpl unitService;

    @Test
    void testFindByIdentifier_Success() {
        String identifier = "UNIT-001";

        Unit unit = new Unit();
        unit.setIdentifier(identifier);

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier(identifier);

        when(unitRepository.findByIdentifierAndDeletedFalse(identifier)).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.findByIdentifier(identifier);

        assertEquals(identifier, result.getIdentifier());
    }

    @Test
    void testSave_Success() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT-001");

        Unit unit = new Unit();
        unit.setIdentifier("UNIT-001");

        when(unitRepository.findByIdentifier("UNIT-001")).thenReturn(null);
        when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);
        verify(unitRepository).save(unit);
    }

    @Test
    void testSave_AlreadyExists() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT-001");

        Unit existingUnit = new Unit();
        existingUnit.setDeleted(false);

        when(unitRepository.findByIdentifier("UNIT-001")).thenReturn(existingUnit);

        UnitDto result = unitService.save(unitDto);

        assertFalse(result.isSuccess());
        assertEquals("Unit with identifier - UNIT-001 already exists", result.getMessage());
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void testSave_DeletedUnitExists() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT-001");

        Unit existingUnit = new Unit();
        existingUnit.setDeleted(true);

        when(unitRepository.findByIdentifier("UNIT-001")).thenReturn(existingUnit);

        UnitDto result = unitService.save(unitDto);

        assertFalse(result.isStatus());
        assertEquals(
                "Unit with identifier - UNIT-001 was deleted and cannot be created again.",
                result.getMessage()
        );
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void testUpdate_Success() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT-001");

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("UNIT-001");

        when(unitRepository.findByIdentifier("UNIT-001")).thenReturn(existingUnit);

        UnitDto result = unitService.update(unitDto);

        assertNotNull(result);
        verify(modelMapper).map(unitDto, existingUnit);
        verify(unitRepository).save(existingUnit);
    }

    @Test
    void testUpdate_NotFound() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT-001");

        when(unitRepository.findByIdentifier("UNIT-001")).thenReturn(null);

        UnitDto result = unitService.update(unitDto);

        assertFalse(result.isSuccess());
        assertEquals("Unit with identifier - UNIT-001 not found", result.getMessage());
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void testDelete_Success() {
        String identifier = "UNIT-001";

        Unit unit = new Unit();
        unit.setIdentifier(identifier);
        unit.setDeleted(false);

        when(unitRepository.findByIdentifierAndDeletedFalse(identifier)).thenReturn(unit);

        unitService.delete(identifier);

        assertTrue(unit.getDeleted());
        verify(unitRepository).save(unit);
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Unit unit = new Unit();
        unit.setIdentifier("UNIT-001");

        Page<Unit> unitPage = new PageImpl<>(List.of(unit), pageable, 1);

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT-001");

        when(unitRepository.findAllByDeletedFalse(pageable)).thenReturn(unitPage);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(unitDto));

        WsDto<UnitDto> result = unitService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void testToggleStatus_Success() {
        String identifier = "UNIT-001";

        Unit unit = new Unit();
        unit.setIdentifier(identifier);
        unit.setStatus(true);

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier(identifier);

        when(unitRepository.findByIdentifier(identifier)).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.toggleStatus(identifier);

        assertFalse(unit.isStatus());
        assertNotNull(result);
        verify(unitRepository).save(unit);
    }

    @Test
    void testFindActiveUnits_Success() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT-001");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT-001");

        when(unitRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(unit));
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(unitDto));

        List<UnitDto> result = unitService.findActiveUnits();

        assertEquals(1, result.size());
        assertEquals("UNIT-001", result.get(0).getIdentifier());
    }

}