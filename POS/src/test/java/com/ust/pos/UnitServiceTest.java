package com.ust.pos;

import com.ust.pos.dto.PaginatedResponseDto;
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
    void saveTest() {

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(null);
        Unit unit = new Unit();
        Mockito.when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");
        Unit unit = new Unit();

        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(unit);
        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());

    }

    @Test
    void findByIdentifierTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Admin");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto response = unitService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("Admin");

        Mockito.when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(existingUnit);
        Mockito.when(unitRepository.save(existingUnit))
                .thenReturn(existingUnit);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Mockito.when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(unitRepository)
                .deleteByIdentifier("Admin");

        unitService.delete("Admin");

        Mockito.verify(unitRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Admin");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        List<Unit> units = List.of(unit);
        List<UnitDto> unitDtos = List.of(unitDto);

        Page<Unit> unitPage = new PageImpl<>(units);

        Mockito.when(unitRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(unitPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(units),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(unitDtos);

        PaginatedResponseDto<UnitDto> response = unitService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
    }

    @Test
    void findAllActiveTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Admin");
        unit.setStatus(true);

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        List<Unit> units = List.of(unit);
        List<UnitDto> unitDtos = List.of(unitDto);

        Mockito.when(unitRepository.findByStatus(true)).thenReturn(units);
        Mockito.when(modelMapper.map(
                Mockito.eq(units),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(unitDtos);

        List<UnitDto> response = unitService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Admin");
        unit.setStatus(false);

        Mockito.when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(unit);

        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        unitService.changeStatus("Admin", true);

        Assertions.assertTrue(unit.getStatus());

        Mockito.verify(unitRepository).save(unit);
    }
}