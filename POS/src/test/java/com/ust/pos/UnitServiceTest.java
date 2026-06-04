package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.modell.Unit;
import com.ust.pos.modell.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import java.util.ArrayList;
import java.util.List;

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
        dto.setIdentifier("U1");
        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(null);
        Unit unit = new Unit();
        Mockito.when(modelMapper.map(dto, Unit.class)).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        UnitDto result = unitService.save(dto);
        Assertions.assertEquals("U1", result.getIdentifier());
        Assertions.assertNull(result.getMessage());
    }

    @Test
    void save_failure_duplicate() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(new Unit());
        UnitDto result = unitService.save(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void findByIdentifier_test() {
        Unit unit = new Unit();
        unit.setIdentifier("U1");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);
        UnitDto result = unitService.findByIdentifier("U1");
        Assertions.assertEquals("U1", result.getIdentifier());
    }

    @Test
    void update_success() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Unit existing = new Unit();
        existing.setIdentifier("U1");
        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(existing);
        Mockito.when(unitRepository.save(existing)).thenReturn(existing);
        UnitDto result = unitService.update(dto);
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void update_failure() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(null);
        UnitDto result = unitService.update(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void delete_test() {
        unitService.delete("U1");
        Mockito.verify(unitRepository).deleteByIdentifier("U1");
    }

    @Test
    void findAllTest() {
        Unit unit = new Unit();
        unit.setIdentifier("Admin");
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");
        List<Unit> units = List.of(unit);
        List<UnitDto> unitDtos = List.of(unitDto);
        Page<Unit> unitPage = new PageImpl<>(units, PageRequest.of(0, 2), units.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(unitRepository.findAll(pageable)).thenReturn(unitPage);
        Mockito.when(modelMapper.map(Mockito.eq(units), Mockito.any(java.lang.reflect.Type.class))).thenReturn(unitDtos);
        List<UnitDto> response = unitService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatus_trueToFalse() {
        Unit unit = new Unit();
        unit.setIdentifier("U1");
        unit.setStatus(true);
        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(unit);
        unitService.toggleStatus("U1");
        Assertions.assertFalse(unit.getStatus());
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatus_falseToTrue() {
        Unit unit = new Unit();
        unit.setIdentifier("U1");
        unit.setStatus(false);
        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(unit);
        unitService.toggleStatus("U1");
        Assertions.assertTrue(unit.getStatus());
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatus_failure() {
        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(null);
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> unitService.toggleStatus("U1")
        );
        Assertions.assertTrue(ex.getMessage().contains("Shelf not found"));
        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

}
