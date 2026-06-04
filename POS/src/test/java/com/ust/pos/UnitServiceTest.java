package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
import org.junit.jupiter.api.Assertions;
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
        when(unitRepository.save(any(Unit.class))).thenAnswer(i -> i.getArgument(0));
        UnitDto response = unitService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("KG", response.getIdentifier());
        Assertions.assertEquals("Unit added successfully", response.getMessage());
    }

    @Test
    void save_failure_emptyName() {
        UnitDto dto = new UnitDto();
        UnitDto response = unitService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit name is required", response.getMessage());
    }

    @Test
    void save_failure_exists() {
        UnitDto dto = new UnitDto();
        dto.setUnitName("KG");
        when(unitRepository.findByIdentifier("KG")).thenReturn(new Unit());
        UnitDto response = unitService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit already exists", response.getMessage());
    }

    @Test
    void find_success() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        dto.setSuccess(true);
        when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);
        UnitDto response = unitService.findByIdentifier("KG");
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("KG", response.getIdentifier());
    }

    @Test
    void find_notFound() {
        when(unitRepository.findByIdentifier("KG")).thenReturn(null);
        UnitDto response = unitService.findByIdentifier("KG");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit not found", response.getMessage());
    }

    @Test
    void update_success() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        dto.setStatus(true);
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        when(unitRepository.save(unit)).thenReturn(unit);
        UnitDto response = unitService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Unit updated successfully", response.getMessage());
    }

    @Test
    void update_failure_notFound() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        when(unitRepository.findByIdentifier("KG")).thenReturn(null);
        UnitDto response = unitService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit not found", response.getMessage());
    }

    @Test
    void update_failure_invalidIdentifier() {
        UnitDto dto = new UnitDto();
        UnitDto response = unitService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Invalid identifier", response.getMessage());
    }

    @Test
    void delete_test() {
        doNothing().when(unitRepository).deleteByIdentifier("KG");
        unitService.delete("KG");
        verify(unitRepository).deleteByIdentifier("KG");
    }

    @Test
    void findAll_test() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        List<Unit> units = List.of(unit);
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        List<UnitDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Unit> page = new PageImpl<>(units);
        when(unitRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(units), any(Type.class))).thenReturn(dtoList);
        List<UnitDto> result = unitService.findAll(pageable);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("KG", result.get(0).getIdentifier());
    }

    @Test
    void toggle_success() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        unit.setStatus(true);
        UnitDto dto = new UnitDto();
        dto.setSuccess(true);
        when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        when(unitRepository.save(unit)).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);
        UnitDto response = unitService.toggleStatus("KG");
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
    }

    @Test
    void toggle_failure() {
        when(unitRepository.findByIdentifier("KG")).thenReturn(null);
        UnitDto response = unitService.toggleStatus("KG");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit not found", response.getMessage());
    }

    @Test
    void active_units_test() {
        Unit active = new Unit();
        active.setStatus(true);
        Unit inactive = new Unit();
        inactive.setStatus(false);
        when(unitRepository.findAll()).thenReturn(List.of(active, inactive));
        when(modelMapper.map(eq(active), eq(UnitDto.class))).thenReturn(new UnitDto());
        List<UnitDto> result = unitService.findActiveUnits();
        Assertions.assertEquals(1, result.size());
    }
}