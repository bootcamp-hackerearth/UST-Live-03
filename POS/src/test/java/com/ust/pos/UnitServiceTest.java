package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import com.ust.pos.exception.ResourceNotFoundException;
import org.springframework.data.jpa.domain.Specification;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void save_success() {

        UnitDto dto = new UnitDto();
        dto.setUnitName("KG");
        dto.setStatus(true);

        when(unitRepository.findByIdentifier("KG")).thenReturn(null);
        UnitDto response = unitService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Unit added successfully", response.getMessage());
        Assertions.assertEquals("KG", response.getIdentifier());

        verify(unitRepository).save(any(Unit.class));
    }

    @Test
    void save_failure_emptyName() {

        UnitDto dto = new UnitDto();

        UnitDto response = unitService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit name is required", response.getMessage());

        verify(unitRepository, never()).save(any());
    }

    @Test
    void save_failure_exists() {

        UnitDto dto = new UnitDto();
        dto.setUnitName("KG");

        when(unitRepository.findByIdentifier("KG")).thenReturn(new Unit());
        UnitDto response = unitService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit already exists", response.getMessage());

        verify(unitRepository, never()).save(any());
    }

    @Test
    void save_failure_softDeleted() {

        UnitDto dto = new UnitDto();
        dto.setUnitName("KG");

        Unit deletedUnit = new Unit();
        deletedUnit.setDeleted(true);

        when(unitRepository.findByIdentifier("KG")).thenReturn(deletedUnit);
        UnitDto response = unitService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("has been soft deleted"));

        verify(unitRepository, never()).save(any());
    }


    @Test
    void find_success() {

        Unit unit = new Unit();

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);

        UnitDto response = unitService.findByIdentifier("KG");

        Assertions.assertEquals("KG", response.getIdentifier());
    }

    @Test
    void find_failure_notFound() {

        when(unitRepository.findByIdentifier("KG")).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> unitService.findByIdentifier("KG"));

        Assertions.assertEquals("Unit with identifier 'KG' not found", ex.getMessage());
    }

    @Test
    void find_failure_deleted() {

        Unit unit = new Unit();
        unit.setDeleted(true);

        when(unitRepository.findByIdentifier("KG")).thenReturn(unit);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> unitService.findByIdentifier("KG"));

        Assertions.assertEquals("Unit with identifier 'KG' not found", ex.getMessage());
    }

    @Test
    void update_success() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        dto.setStatus(true);

        Unit unit = new Unit();

        UnitDto mapped = new UnitDto();

        when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        when(unitRepository.save(unit)).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(mapped);

        UnitDto response = unitService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Unit updated successfully", response.getMessage());

        verify(unitRepository).save(unit);
    }

    @Test
    void update_failure_invalidIdentifier() {

        UnitDto dto = new UnitDto();

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> unitService.update(dto));

        Assertions.assertEquals("Invalid identifier", ex.getMessage());
    }

    @Test
    void update_failure_notFound() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        when(unitRepository.findByIdentifier("KG")).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> unitService.update(dto));

        Assertions.assertEquals("Unit with identifier KG' not found", ex.getMessage());
    }

    @Test
    void update_failure_deleted() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Unit unit = new Unit();
        unit.setDeleted(true);

        when(unitRepository.findByIdentifier("KG")).thenReturn(unit);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> unitService.update(dto));

        Assertions.assertEquals("Unit with identifier KG' not found", ex.getMessage());
    }

    @Test
    void delete_success() {

        Unit unit = new Unit();

        when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        unitService.delete("KG");
        verify(unitRepository).save(unit);

        Assertions.assertTrue(unit.getDeleted());
    }

    @Test
    void delete_notFound() {

        when(unitRepository.findByIdentifier("KG")).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> unitService.delete("KG"));

        Assertions.assertEquals("Unit with identifier 'KG' not found", ex.getMessage());

        verify(unitRepository, never()).save(any());
    }

    @Test
    void findAll_success() {

        Unit unit = new Unit();
        List<Unit> units = List.of(unit);
        List<UnitDto> dtoList = List.of(new UnitDto());

        Pageable pageable = PageRequest.of(0, 5);

        Page<Unit> page = new PageImpl<>(units);
        when(unitRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(units), ArgumentMatchers.<Type>any())).thenReturn(dtoList);
        WsDto<UnitDto> result = unitService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
    }

    @Test
    void findAll_withSpecification() {

        Specification<Unit> specification = (root, query, cb) -> cb.conjunction();

        Page<Unit> page = new PageImpl<>(List.of(new Unit()));

        when(unitRepository.findAll(eq(specification), any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any()))
                .thenReturn(List.of(new UnitDto()));

        WsDto<UnitDto> result =
                unitService.findAll(specification, PageRequest.of(0, 5));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
    }

    @Test
    void toggle_success() {

        Unit unit = new Unit();
        unit.setStatus(true);

        UnitDto dto = new UnitDto();

        when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        when(unitRepository.save(unit)).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);
        UnitDto response = unitService.toggleStatus("KG");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());

        verify(unitRepository).save(unit);
    }

    @Test
    void toggle_failure_notFound() {

        when(unitRepository.findByIdentifier("KG")).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> unitService.toggleStatus("KG"));

        Assertions.assertEquals("Unit with identifier 'KG' not found", ex.getMessage());

        verify(unitRepository, never()).save(any());
    }

    @Test
    void toggle_failure_deleted() {

        Unit unit = new Unit();
        unit.setDeleted(true);

        when(unitRepository.findByIdentifier("KG")).thenReturn(unit);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> unitService.toggleStatus("KG"));

        Assertions.assertEquals("Unit with identifier 'KG' not found", ex.getMessage());

        verify(unitRepository, never()).save(any());
    }

    @Test
    void active_units_test() {

        Unit active = new Unit();
        active.setStatus(true);
        active.setDeleted(false);

        Unit inactive = new Unit();
        inactive.setStatus(false);
        inactive.setDeleted(false);

        Unit deleted = new Unit();
        deleted.setStatus(true);
        deleted.setDeleted(true);

        when(unitRepository.findAll()).thenReturn(List.of(active, inactive, deleted));
        when(modelMapper.map(active, UnitDto.class)).thenReturn(new UnitDto());
        List<UnitDto> result = unitService.findActiveUnits();

        Assertions.assertEquals(1, result.size());
    }
}