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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class UnitServiceTest {
    @InjectMocks
    private UnitServiceImpl unitService;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Kg");

        Unit unit = new Unit();
        Mockito.when(unitRepository.findByIdentifier("Kg")).thenReturn(null);
        Mockito.when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("Kg", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Kg");

        Unit unit = new Unit();
        unit.setIdentifier("Kg");

        Mockito.when(unitRepository.findByIdentifier("Kg")).thenReturn(unit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("Kg", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());

    }

    @Test
    void updateTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Kg");

        Unit unit = new Unit();
        unit.setIdentifier("Kg");

        Mockito.when(unitRepository.findByIdentifier("Kg")).thenReturn(unit);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertEquals("Kg", response.getIdentifier());
    }

    @Test
    void updateTestFailure() {

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Kg");

        Mockito.when(unitRepository.findByIdentifier("Kg")).thenReturn(null);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertEquals("Kg", response.getIdentifier());
        Assertions.assertNotNull((response.getMessage()));
        Assertions.assertFalse(response.isSuccess());

    }

    @Test
    void deleteTestSuccess() {

        unitService.delete("Kg");

        verify(unitRepository).deleteByIdentifier("Kg");
    }

    @Test
    void findByIdentifierSuccessTest() {

        String identifier = "Kg";

        Unit unit = new Unit();
        unit.setIdentifier("Kg");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Kg");

        Mockito.when(unitRepository.findByIdentifier(identifier)).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto existingUnitDto = unitService.findByIdentifier(identifier);

        Assertions.assertEquals(identifier, existingUnitDto.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        String identifier = "Kg";

        Mockito.when(unitRepository.findByIdentifier(identifier)).thenReturn(null);
        Mockito.when(modelMapper.map(null, UnitDto.class)).thenReturn(null);

        UnitDto existingUnitDto = unitService.findByIdentifier(identifier);

        Assertions.assertNull(existingUnitDto);
    }

    @Test
    void findAllSuccessTest() {

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

        Page<Unit> page = new PageImpl<>(units);
        Pageable pageable = PageRequest.of(0, 20);

        Mockito.when(unitRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(units), Mockito.any(Type.class))).thenReturn(unitDtos);

        List<UnitDto> result = unitService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Kg", result.get(0).getIdentifier());
        Assertions.assertEquals("L", result.get(1).getIdentifier());
    }

    @Test
    void findAllActiveSuccessTest() {

        Unit unit1 = new Unit();
        unit1.setIdentifier("Kg");
        unit1.setStatus(true);

        Unit unit2 = new Unit();
        unit2.setIdentifier("L");
        unit2.setStatus(true);

        List<Unit> activeUnits = List.of(unit1, unit2);

        UnitDto dto1 = new UnitDto();
        dto1.setIdentifier("Kg");

        UnitDto dto2 = new UnitDto();
        dto2.setIdentifier("L");

        List<UnitDto> activeUnitDtos = List.of(dto1, dto2);

        Mockito.when(unitRepository.findByStatus(true)).thenReturn(activeUnits);

        Mockito.when(modelMapper.map(Mockito.eq(activeUnits), Mockito.any(Type.class))).thenReturn(activeUnitDtos);

        List<UnitDto> result = unitService.findAllActive();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Kg", result.get(0).getIdentifier());
        Assertions.assertEquals("L", result.get(1).getIdentifier());
    }

    @Test
    void toggleStatusSuccessTest() {

        Unit unit = new Unit();
        unit.setStatus(true);

        Mockito.when(unitRepository.findByIdentifier("Kg")).thenReturn(unit);

        unitService.toggleStatus("Kg");

        Assertions.assertFalse(unit.isStatus());
    }

    @Test
    void toggleStatusUnitNotFoundTest() {

        Mockito.when(unitRepository.findByIdentifier("Kg")).thenReturn(null);

        unitService.toggleStatus("Kg");

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }
}