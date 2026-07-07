package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
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
    void saveTestSuccess() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("Admin");

        Unit unit = new Unit();

        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Unit.class)).thenReturn(unit);

        UnitDto response = unitService.save(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(unitRepository).save(unit);
    }

    @Test
    void saveTestFailure() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("Admin");

        Unit unit = new Unit();
        unit.setIdentifier("Admin");

        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(unit);

        UnitDto response = unitService.save(dto);

        Assertions.assertNotNull(response.getMessage());
        assertFalse(response.isSuccess());

        verify(unitRepository, never()).save(Mockito.any());
    }

    @Test
    void updateTestSuccess() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("Admin");

        Unit unit = new Unit();

        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(unit);

        UnitDto response = unitService.update(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(modelMapper).map(dto, unit);
        verify(unitRepository).save(unit);
    }

    @Test
    void updateTestFailure() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("Admin");

        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(null);

        UnitDto response = unitService.update(dto);

        Assertions.assertNotNull(response.getMessage());
        assertFalse(response.isSuccess());

        verify(unitRepository, never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Admin");

        when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(unit);

        unitService.delete("Admin");

        assertTrue(unit.isDeleted());

        verify(unitRepository)
                .findByIdentifier("Admin");
    }

    @Test
    void findByIdentifierSuccessTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Admin");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("Admin");

        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);

        UnitDto result = unitService.findByIdentifier("Admin");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Admin", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        assertThrows(
                ResourseNotFoundException.class,
                () -> unitService.findByIdentifier("Admin")
        );
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Unit unit = new Unit();
        unit.setIdentifier("Admin");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("Admin");

        Page<Unit> page =
                new PageImpl<>(List.of(unit), pageable, 1);

        when(unitRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        WsDto<UnitDto> result = unitService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Admin",
                result.getContent().getFirst().getIdentifier());

        assertEquals(0, result.getPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalRecords());

        verify(unitRepository)
                .findByIsDeletedFalse(pageable);

        verify(modelMapper)
                .map(unit, UnitDto.class);
    }

    @Test
    void saveTestDeletedUnitExists() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("Admin");

        Unit existing = new Unit();
        existing.setDeleted(true);

        when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(existing);

        UnitDto response = unitService.save(dto);

        assertFalse(response.isSuccess());

        assertTrue(
                response.getMessage()
                        .contains("already exists but was deleted")
        );

        verify(unitRepository, never())
                .save(any());
    }

    @Test
    void toggleStatusTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Admin");
        unit.setStatus(true);

        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(unit);

        unitService.toggleStatus("Admin");

        assertFalse(unit.isStatus());
        verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Unit unit = new Unit();
        unit.setIdentifier("Admin");
        unit.setStatus(false);

        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(unit);

        unitService.toggleStatus("Admin");

        Assertions.assertTrue(unit.isStatus());
        verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusNullTest() {

        Mockito.when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        unitService.toggleStatus("Admin");

        verify(unitRepository, never()).save(Mockito.any());
    }

    @Test
    void findActiveUnitTest() {

        List<Unit> units = List.of(new Unit());

        Mockito.when(unitRepository.findByStatus(true))
                .thenReturn(units);

        List<UnitDto> response = unitService.findActiveUnit();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());

        verify(unitRepository).findByStatus(true);
    }

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Unit> specification = mock(Specification.class);

        Unit unit = new Unit();
        unit.setIdentifier("UNIT01");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT01");

        List<Unit> unitList = List.of(unit);

        Page<Unit> page = new PageImpl<>(unitList, pageable, 1);

        when(unitRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(unitList), any(Type.class)))
                .thenReturn(List.of(unitDto));

        WsDto<UnitDto> result =
                unitService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("UNIT01",
                result.getContent().get(0).getIdentifier());
        assertEquals(1L, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(unitRepository)
                .findAll(specification, pageable);

        verify(modelMapper)
                .map(eq(unitList), any(Type.class));
    }

    @Test
    void findAllWithSpecificationEmptyResultTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Unit> specification = mock(Specification.class);

        Page<Unit> page =
                new PageImpl<>(List.of(), pageable, 0);

        when(unitRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(List.of()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<UnitDto> result =
                unitService.findAll(specification, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalRecords());
        assertEquals(0, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(unitRepository)
                .findAll(specification, pageable);

        verify(modelMapper)
                .map(eq(List.of()), any(Type.class));
    }
}