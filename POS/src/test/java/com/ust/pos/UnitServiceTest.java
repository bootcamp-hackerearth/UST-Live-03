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
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UnitServiceImpl unitService;

    @Test
    void saveTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("S1");

        Mockito.when(unitRepository.findByIdentifier("S1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Unit.class))
                .thenReturn(new Unit());

        UnitDto response = unitService.save(dto);

        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("S1");

        Mockito.when(unitRepository.findByIdentifier("S1"))
                .thenReturn(new Unit());

        UnitDto response = unitService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Unit unit = new Unit();
        unit.setIdentifier("S1");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("S1");

        Mockito.when(unitRepository.findByIdentifier("S1"))
                .thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        UnitDto response = unitService.findByIdentifier("S1");

        Assertions.assertEquals("S1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("S1");

        Unit unit = new Unit();
        unit.setIdentifier("S1");

        Mockito.when(unitRepository.findByIdentifier("S1"))
                .thenReturn(unit);

        UnitDto response = unitService.update(dto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("S1");

        Mockito.when(unitRepository.findByIdentifier("S1"))
                .thenReturn(null);

        UnitDto response = unitService.update(dto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(unitRepository)
                .deleteByIdentifier("S1");

        unitService.delete("S1");

        Mockito.verify(unitRepository)
                .deleteByIdentifier("S1");
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
    void toggleActive() {
        Unit unit = new Unit();
        unit.setStatus(true);

        Mockito.when(unitRepository.findByIdentifier("S1"))
                .thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(new UnitDto());

        unitService.toggleStatus("S1");

        Assertions.assertFalse(unit.isStatus());
    }

    @Test
    void toggleInactive() {
        Unit unit = new Unit();
        unit.setStatus(false);

        Mockito.when(unitRepository.findByIdentifier("S1"))
                .thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(new UnitDto());

        unitService.toggleStatus("S1");

        Assertions.assertTrue(unit.isStatus());
    }

    @Test
    void findByStatusTest() {
        List<Unit> shelves = List.of(new Unit());
        List<UnitDto> unitDtos = List.of(new UnitDto());

        Mockito.when(unitRepository.findByStatusIsTrue())
                .thenReturn(shelves);
        Mockito.when(modelMapper.map(
                Mockito.eq(shelves),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(unitDtos);

        List<UnitDto> response = unitService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }
}
