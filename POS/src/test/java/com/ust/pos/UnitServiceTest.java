package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.jpa.domain.Specification;

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
        unit.setDeleted(false);
    }

    @Test
    void testSave_NewUnit() {
        when(unitRepository.findByIdentifier("Kg")).thenReturn(null);
        when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);
        assertEquals("Kg", result.getIdentifier());
        assertTrue(result.isSuccess());

        verify(unitRepository, times(1)).findByIdentifier("Kg");
        verify(modelMapper, times(1)).map(unitDto, Unit.class);
        verify(unitRepository, times(1)).save(unit);
    }

    @Test
    void testSave_UnitAlreadyExists_Active() {
        unit.setDeleted(false);
        when(unitRepository.findByIdentifier("Kg")).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Unit with identifier - Kg already exists", result.getMessage());

        verify(unitRepository, times(1)).findByIdentifier("Kg");
        verify(unitRepository, never()).save(any());
    }

    @Test
    void testSave_UnitAlreadyExists_SoftDeleted() {
        unit.setDeleted(true);
        when(unitRepository.findByIdentifier("Kg")).thenReturn(unit);

        UnitDto result = unitService.save(unitDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Unit identifier - Kg not available", result.getMessage());

        verify(unitRepository, times(1)).findByIdentifier("Kg");
        verify(unitRepository, never()).save(any());
    }

    @Test
    void testUpdate_UnitFound() {
        when(unitRepository.findByIdentifier("Kg")).thenReturn(unit);

        UnitDto result = unitService.update(unitDto);

        assertNotNull(result);
        assertEquals("Kg", result.getIdentifier());

        verify(unitRepository, times(1)).findByIdentifier("Kg");
        verify(modelMapper, times(1)).map(unitDto, unit);
        verify(unitRepository, times(1)).save(unit);
    }

    @Test
    void testUpdate_UnitNotFound() {
        when(unitRepository.findByIdentifier("Kg")).thenReturn(null);

        UnitDto result = unitService.update(unitDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("unit with identifier - Kg not found", result.getMessage());

        verify(unitRepository, times(1)).findByIdentifier("Kg");
        verify(unitRepository, never()).save(any());
    }

    @Test
    void testDelete_Success() {
        String identifier = "Kg";
        when(unitRepository.findByIdentifierAndDeletedFalse(identifier)).thenReturn(unit);

        assertDoesNotThrow(() -> unitService.delete(identifier));

        verify(unitRepository, times(1)).findByIdentifierAndDeletedFalse(identifier);
        assertTrue(unit.getDeleted());
    }

    @Test
    void testFindByIdentifier_Found() {
        when(unitRepository.findByIdentifierAndDeletedFalse("Kg")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.findByIdentifier("Kg");

        assertNotNull(result);
        assertEquals("Kg", result.getIdentifier());
        verify(unitRepository, times(1)).findByIdentifierAndDeletedFalse("Kg");
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(unitRepository.findByIdentifierAndDeletedFalse("Kg")).thenReturn(null);
        when(modelMapper.map(null, UnitDto.class)).thenReturn(null);

        UnitDto result = unitService.findByIdentifier("Kg");

        assertNull(result);
        verify(unitRepository, times(1)).findByIdentifierAndDeletedFalse("Kg");
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
        Page<Unit> unitPage = new PageImpl<>(units, pageable, units.size());
        Type listType = new TypeToken<List<UnitDto>>() {}.getType();

        when(unitRepository.findAllByDeletedFalse(pageable)).thenReturn(unitPage);
        when(modelMapper.map(units, listType)).thenReturn(unitDtos);

        WsDto<UnitDto> result = unitService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getDtoList().size());
        assertEquals("Kg", result.getDtoList().get(0).getIdentifier());
        assertEquals("L", result.getDtoList().get(1).getIdentifier());
        assertEquals(2L, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(unitRepository, times(1)).findAllByDeletedFalse(pageable);
    }

    @Test
    void testFindAll_EmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Unit> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Type listType = new TypeToken<List<UnitDto>>() {}.getType();

        when(unitRepository.findAllByDeletedFalse(pageable)).thenReturn(emptyPage);
        when(modelMapper.map(emptyPage.getContent(), listType)).thenReturn(Collections.emptyList());

        WsDto<UnitDto> result = unitService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getDtoList().isEmpty());
        assertEquals(0L, result.getTotalRecords());
        assertEquals(0, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(unitRepository, times(1)).findAllByDeletedFalse(pageable);
    }

    @Test
    void testFindActiveUnits() {
        Unit unit1 = new Unit();
        unit1.setIdentifier("Kg");
        unit1.setStatus(true);
        Unit unit2 = new Unit();
        unit2.setIdentifier("L");
        unit2.setStatus(true);
        List<Unit> activeUnits = List.of(unit1, unit2);

        List<UnitDto> dtoList = List.of(unitDto, new UnitDto());
        Type listType = new TypeToken<List<UnitDto>>() {}.getType();

        when(unitRepository.findByStatusTrueAndDeletedFalse()).thenReturn(activeUnits);
        when(modelMapper.map(activeUnits, listType)).thenReturn(dtoList);

        List<UnitDto> result = unitService.findActiveUnits();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(unitRepository, times(1)).findByStatusTrueAndDeletedFalse();
        verify(modelMapper, times(1)).map(activeUnits, listType);
    }

    @Test
    void testToggleStatus_TrueToFalse() {
        unit.setStatus(true);
        when(unitRepository.findByIdentifier("Kg")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.toggleStatus("Kg");

        assertNotNull(result);
        assertFalse(unit.isStatus());
        verify(unitRepository, times(1)).save(unit);
        verify(modelMapper, times(1)).map(unit, UnitDto.class);
    }

    @Test
    void testToggleStatus_FalseToTrue() {
        unit.setStatus(false);
        when(unitRepository.findByIdentifier("Kg")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.toggleStatus("Kg");

        assertNotNull(result);
        assertTrue(unit.isStatus());
        verify(unitRepository, times(1)).save(unit);
        verify(modelMapper, times(1)).map(unit, UnitDto.class);
    }

    @Test
    void testToggleStatus_UnitNotFound() {
        when(unitRepository.findByIdentifier("Kg")).thenReturn(null);

        UnitDto result = unitService.toggleStatus("Kg");

        assertNull(result);
        verify(unitRepository, times(1)).findByIdentifier("Kg");
        verify(unitRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindAll_WithSpecification_Success() {
        Specification<Unit> mockSpec = mock(Specification.class);
        Pageable pageable = PageRequest.of(1, 15);
        List<Unit> unitsList = Collections.singletonList(unit);
        List<UnitDto> unitDtoList = Collections.singletonList(unitDto);
        Page<Unit> unitPage = new PageImpl<>(unitsList, pageable, 45);
        Type listType = new TypeToken<List<UnitDto>>() {}.getType();

        when(unitRepository.findAll(mockSpec, pageable)).thenReturn(unitPage);
        when(modelMapper.map(unitPage.getContent(), listType)).thenReturn(unitDtoList);

        WsDto<UnitDto> result = unitService.findAll(mockSpec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(45L, result.getTotalRecords());
        assertEquals(3, result.getTotalPages());
        assertEquals(15, result.getSizePerPage());
        assertEquals(1, result.getPage());

        verify(unitRepository, times(1)).findAll(mockSpec, pageable);
        verify(modelMapper, times(1)).map(unitPage.getContent(), listType);
    }
}