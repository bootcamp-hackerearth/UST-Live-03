package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Unit;
import com.ust.pos.modell.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.List;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        dto.setIdentifier("U1");
        when(unitRepository.findByIdentifier("U1")).thenReturn(null);
        Unit entity = new Unit();
        when(modelMapper.map(dto, Unit.class)).thenReturn(entity);
        when(unitRepository.save(entity)).thenReturn(entity);
        UnitDto result = unitService.save(dto);
        assertEquals("U1", result.getIdentifier());
        Assertions.assertNull(result.getMessage());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        when(unitRepository.findByIdentifier("U1")).thenReturn(new Unit());
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
        when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(unit);
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);
        UnitDto result = unitService.findByIdentifier("U1");
        assertEquals("U1", result.getIdentifier());
    }

    @Test
    void update_success() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Unit existing = new Unit();
        existing.setIdentifier("U1");
        when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(existing);
        when(unitRepository.save(existing)).thenReturn(existing);
        UnitDto result = unitService.update(dto);
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void update_failure() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(null);
        UnitDto result = unitService.update(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void delete_test() {
        Unit unit = new Unit();
        unit.setIdentifier("U1");
        when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(unit);
        unitService.delete("U1");
        verify(unitRepository, times(1)).save(unit);
    }

    @Test
    void findAllTest() {
        Unit unit = new Unit();
        unit.setIdentifier("Admin");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("Admin");
        List<Unit> units = List.of(unit);
        List<UnitDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 50);
        Page<Unit> page = new PageImpl<>(units, pageable, 1);
        when(unitRepository.findALlByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(units), any(java.lang.reflect.Type.class))).thenReturn(dtos);
        WsDto<UnitDto> result = unitService.findAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals("Admin", result.getDtoList().get(0).getIdentifier());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(50, result.getSizePerPage());
        assertEquals(0, result.getPage());
        verify(unitRepository).findALlByDeletedFalse(pageable);
    }

    @Test
    void toggleStatus_trueToFalse() {
        Unit unit = new Unit();
        unit.setIdentifier("U1");
        unit.setStatus(true);
        when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(unit);
        when(unitRepository.save(unit)).thenReturn(unit);
        unitService.toggleStatus("U1");
        Assertions.assertFalse(unit.getStatus());
    }

    @Test
    void toggleStatus_falseToTrue() {
        Unit unit = new Unit();
        unit.setIdentifier("U1");
        unit.setStatus(false);
        when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(unit);
        when(unitRepository.save(unit)).thenReturn(unit);
        unitService.toggleStatus("U1");
        Assertions.assertTrue(unit.getStatus());
    }

    @Test
    void toggleStatus_nullToTrue() {
        Unit unit = new Unit();
        unit.setIdentifier("U1");
        unit.setStatus(null);
        when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(unit);
        when(unitRepository.save(unit)).thenReturn(unit);
        unitService.toggleStatus("U1");
        Assertions.assertTrue(unit.getStatus());
    }

    @Test
    void toggleStatus_failure() {
        when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(null);
        NullPointerException ex = Assertions.assertThrows(NullPointerException.class, () -> unitService.toggleStatus("U1"));
        Assertions.assertTrue(ex.getMessage().contains("Unit not found with identifier: U1"));
        verify(unitRepository, never()).save(any());
    }

    @Test
    void findAllActive_test() {
        Unit unit = new Unit();
        unit.setIdentifier("U1");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        when(unitRepository.findByStatusTrueAndDeletedFalse()).thenReturn(List.of(unit));
        when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);
        List<UnitDto> result = unitService.findAllActive();
        assertEquals(1, result.size());
        assertEquals("U1", result.get(0).getIdentifier());
    }
}