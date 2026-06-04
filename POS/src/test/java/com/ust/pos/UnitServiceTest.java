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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @InjectMocks
    private UnitServiceImpl unitService;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    private Unit unit;
    private UnitDto unitDto;

    @BeforeEach
    void setUp() {

        unitDto = new UnitDto();
        unitDto.setIdentifier("Kg");
        unitDto.setSuccess(true);

        unit = new Unit();
        unit.setIdentifier("Kg");
        unit.setStatus(true);
    }

    @Test
    void testSave_NewUnit() {

        when(unitRepository.findByIdentifier("Kg"))
                .thenReturn(null);

        when(modelMapper.map(unitDto, Unit.class))
                .thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);
        assertEquals("Kg", result.getIdentifier());
        assertTrue(result.isSuccess());

        verify(unitRepository, times(1))
                .findByIdentifier("Kg");

        verify(modelMapper, times(1))
                .map(unitDto, Unit.class);

        verify(unitRepository, times(1))
                .save(unit);
    }

    @Test
    void testSave_UnitAlreadyExists() {

        when(unitRepository.findByIdentifier("Kg"))
                .thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);
        assertEquals("Kg", result.getIdentifier());
        assertFalse(result.isSuccess());

        assertEquals(
                "Unit with identifier - Kg already exists",
                result.getMessage()
        );

        verify(unitRepository, times(1))
                .findByIdentifier("Kg");

        verify(unitRepository, never())
                .save(any());

        verify(modelMapper, never())
                .map(any(), any());
    }

    @Test
    void testUpdate_UnitFound() {

        when(unitRepository.findByIdentifier("Kg"))
                .thenReturn(unit);

        UnitDto result = unitService.update(unitDto);

        assertNotNull(result);
        assertEquals("Kg", result.getIdentifier());

        verify(unitRepository, times(1))
                .findByIdentifier("Kg");

        verify(modelMapper, times(1))
                .map(unitDto, unit);

        verify(unitRepository, times(1))
                .save(unit);
    }

    @Test
    void testUpdate_UnitNotFound() {

        when(unitRepository.findByIdentifier("Kg"))
                .thenReturn(null);

        UnitDto result = unitService.update(unitDto);

        assertNotNull(result);
        assertEquals("Kg", result.getIdentifier());
        assertFalse(result.isSuccess());

        assertEquals(
                "unit with identifier - Kg not found",
                result.getMessage()
        );

        verify(unitRepository, times(1))
                .findByIdentifier("Kg");

        verify(unitRepository, never())
                .save(any());

        verify(modelMapper, never())
                .map(any(), any());
    }

    @Test
    void testDelete() {

        doNothing().when(unitRepository)
                .deleteByIdentifier("Kg");

        unitService.delete("Kg");

        verify(unitRepository, times(1))
                .deleteByIdentifier("Kg");
    }

    @Test
    void testFindByIdentifier_Found() {

        when(unitRepository.findByIdentifier("Kg"))
                .thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(unitDto);

        UnitDto result =
                unitService.findByIdentifier("Kg");

        assertNotNull(result);
        assertEquals("Kg", result.getIdentifier());

        verify(unitRepository, times(1))
                .findByIdentifier("Kg");

        verify(modelMapper, times(1))
                .map(unit, UnitDto.class);
    }

    @Test
    void testFindByIdentifier_NotFound() {

        when(unitRepository.findByIdentifier("Kg"))
                .thenReturn(null);

        when(modelMapper.map(null, UnitDto.class))
                .thenReturn(null);

        UnitDto result =
                unitService.findByIdentifier("Kg");

        assertNull(result);

        verify(unitRepository, times(1))
                .findByIdentifier("Kg");

        verify(modelMapper, times(1))
                .map(null, UnitDto.class);
    }

    @Test
    void testFindAll_WithData() {

        Unit unit1 = new Unit();
        unit1.setIdentifier("Kg");

        Unit unit2 = new Unit();
        unit2.setIdentifier("L");

        List<Unit> units = List.of(unit1, unit2);

        UnitDto dto1 = new UnitDto();
        dto1.setIdentifier("Kg");

        UnitDto dto2 = new UnitDto();
        dto2.setIdentifier("L");

        List<UnitDto> unitDtos = List.of(dto1, dto2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Unit> unitPage =
                new PageImpl<>(units, pageable, units.size());

        Type listType =
                new TypeToken<List<UnitDto>>() {
                }.getType();

        when(unitRepository.findAll(pageable))
                .thenReturn(unitPage);

        when(modelMapper.map(units, listType))
                .thenReturn(unitDtos);

        List<UnitDto> result =
                unitService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Kg", result.get(0).getIdentifier());
        assertEquals("L", result.get(1).getIdentifier());

        verify(unitRepository, times(1))
                .findAll(pageable);

        verify(modelMapper, times(1))
                .map(units, listType);
    }

    @Test
    void testFindAll_EmptyList() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Unit> emptyPage =
                new PageImpl<>(Collections.emptyList());

        Type listType =
                new TypeToken<List<UnitDto>>() {
                }.getType();

        when(unitRepository.findAll(pageable))
                .thenReturn(emptyPage);

        when(modelMapper.map(emptyPage.getContent(), listType))
                .thenReturn(Collections.emptyList());

        List<UnitDto> result =
                unitService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(unitRepository, times(1))
                .findAll(pageable);

        verify(modelMapper, times(1))
                .map(emptyPage.getContent(), listType);
    }

    @Test
    void testFindActiveUnits() {

        Unit unit1 = new Unit();
        unit1.setIdentifier("Kg");
        unit1.setStatus(true);

        Unit unit2 = new Unit();
        unit2.setIdentifier("L");
        unit2.setStatus(true);

        List<Unit> activeUnits =
                List.of(unit1, unit2);

        when(unitRepository.findByStatus(true))
                .thenReturn(activeUnits);

        List<Unit> result =
                unitService.findActiveUnits();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(unitRepository, times(1))
                .findByStatus(true);
    }

    @Test
    void testToggleStatus_TrueToFalse() {

        unit.setStatus(true);

        when(unitRepository.findByIdentifier("Kg"))
                .thenReturn(unit);

        unitService.toggleStatus("Kg");

        assertFalse(unit.isStatus());

        verify(unitRepository, times(1))
                .save(unit);
    }

    @Test
    void testToggleStatus_FalseToTrue() {

        unit.setStatus(false);

        when(unitRepository.findByIdentifier("Kg"))
                .thenReturn(unit);

        unitService.toggleStatus("Kg");

        assertTrue(unit.isStatus());

        verify(unitRepository, times(1))
                .save(unit);
    }

    @Test
    void testToggleStatus_UnitNotFound() {

        when(unitRepository.findByIdentifier("Kg"))
                .thenReturn(null);

        unitService.toggleStatus("Kg");

        verify(unitRepository, times(1))
                .findByIdentifier("Kg");

        verify(unitRepository, never())
                .save(any());
    }
}