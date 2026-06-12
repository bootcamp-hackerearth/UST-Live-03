package com.ust.pos;

import com.ust.pos.dto.PaginationResponseDto;
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

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @InjectMocks
    private UnitServiceImpl unitService;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllWithPageableTest() {

        Unit unit = new Unit();
        unit.setIdentifier("U1");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("U1");

        List<Unit> units = List.of(unit);
        List<UnitDto> unitDtos = List.of(unitDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Unit> unitPage = new PageImpl<>(units);

        Mockito.when(unitRepository.findAll(pageable))
                .thenReturn(unitPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(units),
                Mockito.any(Type.class)
        )).thenReturn(unitDtos);

        PaginationResponseDto<UnitDto> response =
                unitService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(
                "U1",
                response.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void findAllWithoutPageableTest() {

        Unit unit = new Unit();
        unit.setIdentifier("U1");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("U1");

        List<Unit> units = List.of(unit);
        List<UnitDto> unitDtos = List.of(unitDto);

        Mockito.when(unitRepository.findAll())
                .thenReturn(units);

        Mockito.when(modelMapper.map(
                Mockito.eq(units),
                Mockito.any(Type.class)
        )).thenReturn(unitDtos);

        PaginationResponseDto<UnitDto> response =
                unitService.findAll(null);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(
                "U1",
                response.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void findByIdentifierTest() {
        Unit unit = new Unit();
        unit.setIdentifier("U1");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);

        UnitDto response = unitService.findByIdentifier("U1");

        Assertions.assertEquals("U1", response.getIdentifier());
    }

    @Test
    void saveSuccessTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Unit unit = new Unit();

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Unit.class)).thenReturn(unit);

        UnitDto response = unitService.save(dto);

        Mockito.verify(modelMapper).map(dto, Unit.class);
        Mockito.verify(unitRepository).save(unit);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Successfully added the unit", response.getMessage());
    }

    @Test
    void saveFailureTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Mockito.when(unitRepository.findByIdentifier("U1"))
                .thenReturn(new Unit());

        UnitDto response = unitService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit U1 already exists", response.getMessage());
    }

    @Test
    void updateSuccessTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Unit unit = new Unit();

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(unit);

        Mockito.doNothing().when(modelMapper).map(dto, unit);

        UnitDto response = unitService.update(dto);

        Mockito.verify(unitRepository).save(unit);
        Mockito.verify(modelMapper).map(dto, unit);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Unit updated successfully", response.getMessage());
    }

    @Test
    void updateFailureTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(null);

        UnitDto response = unitService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit not found", response.getMessage());
    }

    @Test
    void updateStatusSuccessTest() {
        Unit unit = new Unit();
        unit.setIdentifier("U1");
        unit.setStatus(false);

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(unit);

        UnitDto response = unitService.updateStatus("U1", true);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
        Assertions.assertTrue(unit.isStatus());
    }

    @Test
    void updateStatusFailureTest() {
        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(null);

        UnitDto response = unitService.updateStatus("U1", true);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit not found", response.getMessage());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(unitRepository).deleteByIdentifier("U1");

        unitService.delete("U1");

        Mockito.verify(unitRepository).deleteByIdentifier("U1");
    }
}