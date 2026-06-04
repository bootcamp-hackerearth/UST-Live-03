package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UnitServiceImpl unitService;

    private Unit unit;

    private UnitDto unitDto;

    @BeforeEach
    void setUp() {

        unit = new Unit();
        unit.setIdentifier("UNIT1");
        unit.setStatus(true);

        unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT1");
        unitDto.setStatus(true);
    }

    @Test
    void findByIdentifierTest() {

        when(unitRepository.findByIdentifier("UNIT1")).thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.findByIdentifier("UNIT1");

        assertNotNull(result);

        assertEquals("UNIT1", result.getIdentifier());

        verify(unitRepository).findByIdentifier("UNIT1");

        verify(modelMapper).map(unit, UnitDto.class);
    }

    @Test
    void findByIdentifierNullTest() {

        when(unitRepository.findByIdentifier("UNIT1")).thenReturn(null);

        when(modelMapper.map(null, UnitDto.class)).thenReturn(null);

        UnitDto result = unitService.findByIdentifier("UNIT1");

        assertNull(result);
    }

    @Test
    void toggleStatusTrueToFalseTest() {

        unit.setStatus(true);

        when(unitRepository.findByIdentifier("UNIT1")).thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.toggleStatus("UNIT1");

        assertNotNull(result);

        assertFalse(unit.isStatus());

        verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        unit.setStatus(false);

        when(unitRepository.findByIdentifier("UNIT1")).thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.toggleStatus("UNIT1");

        assertNotNull(result);

        assertTrue(unit.isStatus());

        verify(unitRepository).save(unit);
    }

    @Test
    void saveSuccessTest() {

        when(unitRepository.findByIdentifier("UNIT1")).thenReturn(null);

        when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);

        assertEquals("UNIT1", result.getIdentifier());

        verify(unitRepository).save(unit);
    }

    @Test
    void saveAlreadyExistsTest() {

        when(unitRepository.findByIdentifier("UNIT1")).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertFalse(result.isSuccess());

        assertEquals("Unit with identifier - UNIT1 already exists", result.getMessage());

        verify(unitRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {

        when(unitRepository.findByIdentifier("UNIT1")).thenReturn(unit);

        doNothing().when(modelMapper).map(unitDto, unit);

        UnitDto result = unitService.update(unitDto);

        assertNotNull(result);

        verify(modelMapper).map(unitDto, unit);

        verify(unitRepository).save(unit);
    }

    @Test
    void updateNotFoundTest() {

        when(unitRepository.findByIdentifier("UNIT1")).thenReturn(null);

        UnitDto result = unitService.update(unitDto);

        assertFalse(result.isSuccess());

        assertEquals("Unit with identifier - UNIT1 not found", result.getMessage());

        verify(unitRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        doNothing().when(unitRepository).deleteByIdentifier("UNIT1");

        boolean result = unitService.delete("UNIT1");

        assertTrue(result);

        verify(unitRepository).deleteByIdentifier("UNIT1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Unit> unitList = List.of(unit);

        Page<Unit> unitPage = new PageImpl<>(unitList);

        List<UnitDto> dtoList = List.of(unitDto);

        when(unitRepository.findAll(pageable)).thenReturn(unitPage);

        when(modelMapper.map(eq(unitPage.getContent()), any(Type.class))).thenReturn(dtoList);

        List<UnitDto> result = unitService.findAll(pageable);

        assertNotNull(result);

        assertEquals(1, result.size());

        verify(unitRepository).findAll(pageable);
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Unit> unitPage = new PageImpl<>(List.of());

        when(unitRepository.findAll(pageable)).thenReturn(unitPage);

        when(modelMapper.map(eq(List.of()), any(Type.class))).thenReturn(List.of());

        List<UnitDto> result = unitService.findAll(pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void findIfTrueTest() {

        List<Unit> unitList = List.of(unit);

        List<UnitDto> dtoList = List.of(unitDto);

        when(unitRepository.findByStatusIsTrue()).thenReturn(unitList);

        when(modelMapper.map(eq(unitList), any(Type.class))).thenReturn(dtoList);

        List<UnitDto> result = unitService.findIfTrue();

        assertNotNull(result);

        assertEquals(1, result.size());

        verify(unitRepository).findByStatusIsTrue();
    }

    @Test
    void findIfTrueEmptyTest() {

        when(unitRepository.findByStatusIsTrue()).thenReturn(List.of());

        when(modelMapper.map(eq(List.of()), any(Type.class))).thenReturn(List.of());

        List<UnitDto> result = unitService.findIfTrue();

        assertTrue(result.isEmpty());
    }
}