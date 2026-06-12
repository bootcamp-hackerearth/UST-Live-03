package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private org.modelmapper.ModelMapper modelMapper;

    @InjectMocks
    private UnitServiceImpl unitService;

    @Test
    void saveSuccessTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT1");

        Unit unit = new Unit();
        unit.setIdentifier("UNIT1");

        Mockito.when(unitRepository.findByIdentifier("UNIT1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Unit.class)).thenReturn(unit);

        UnitDto result = unitService.save(dto);

        Assertions.assertEquals("UNIT1", result.getIdentifier());

        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        Unit existing = new Unit();
        existing.setIdentifier("UNIT1");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT1");

        Mockito.when(unitRepository.findByIdentifier("UNIT1")).thenReturn(existing);

        UnitDto result = unitService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Model - UNIT1 already exists", result.getMessage());

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateFailureUnitNotFoundTest() {
        UnitDto dto = new UnitDto();
        dto.setId(1L);
        dto.setIdentifier("UNIT2");

        Mockito.when(unitRepository.findById(1L)).thenReturn(Optional.empty());

        UnitDto result = unitService.update(dto);

        Assertions.assertFalse(result.isSuccess());
    }

    @Test
    void updateFailureIdentifierAlreadyExistsTest() {
        Unit existing = new Unit();
        existing.setId(1L);
        existing.setIdentifier("OLD_UNIT");

        Unit duplicate = new Unit();
        duplicate.setIdentifier("NEW_UNIT");

        UnitDto dto = new UnitDto();
        dto.setId(1L);
        dto.setIdentifier("NEW_UNIT");

        Mockito.when(unitRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(unitRepository.findByIdentifier("NEW_UNIT")).thenReturn(duplicate);

        UnitDto result = unitService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Model Already Exists", result.getMessage());
    }

    @Test
    void updateSuccessTest() {
        Unit existing = new Unit();
        existing.setId(1L);
        existing.setIdentifier("UNIT3");

        UnitDto dto = new UnitDto();
        dto.setId(1L);
        dto.setIdentifier("UNIT3");

        Mockito.when(unitRepository.findById(1L)).thenReturn(Optional.of(existing));

        UnitDto result = unitService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(modelMapper).map(dto, existing);
        Mockito.verify(unitRepository).save(existing);
    }

    @Test
    void findByIdentifierSuccessTest() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT4");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT4");

        Mockito.when(unitRepository.findByIdentifier("UNIT4")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);

        UnitDto result = unitService.findByIdentifier("UNIT4");

        Assertions.assertEquals("UNIT4", result.getIdentifier());
    }

    @Test
    void findAllSuccessTest() {
        Unit u1 = new Unit();
        u1.setIdentifier("U1");

        Unit u2 = new Unit();
        u2.setIdentifier("U2");

        List<Unit> units = Arrays.asList(u1, u2);

        UnitDto d1 = new UnitDto();
        d1.setIdentifier("U1");

        UnitDto d2 = new UnitDto();
        d2.setIdentifier("U2");

        List<UnitDto> dtos = Arrays.asList(d1, d2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Unit> unitPage = new PageImpl<>(units, pageable, units.size());

        Mockito.when(unitRepository.findAll(pageable)).thenReturn(unitPage);
        Mockito.when(modelMapper.map(Mockito.eq(units), Mockito.any(Type.class))).thenReturn(dtos);

        WsDto<UnitDto> result = unitService.findAll(pageable);

        Assertions.assertEquals(2, result.getDtoList().size());
    }

    @Test
    void deleteTest() {
        unitService.delete("UNIT5");
        Mockito.verify(unitRepository).deleteByIdentifier("UNIT5");
    }

    @Test
    void toggleStatusTest() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT6");
        unit.setStatus(true);

        Mockito.when(unitRepository.findByIdentifier("UNIT6")).thenReturn(unit);

        unitService.toggleStatus("UNIT6");

        Assertions.assertFalse(unit.getStatus());

        Mockito.verify(unitRepository).save(unit);
    }
}