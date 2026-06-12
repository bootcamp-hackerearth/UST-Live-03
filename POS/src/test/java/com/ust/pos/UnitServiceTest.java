package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
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
public class UnitServiceTest {

    @InjectMocks
    UnitServiceImpl unitService;

    @Mock
    UnitRepository unitRepository;
    @Mock
    ModelMapper modelMapper;

    @Test
    void saveTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("kg");

        Mockito.when(unitRepository.findByIdentifier("kg")).thenReturn(null);

        Unit unit = new Unit();

        Mockito.when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("kg", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("kg");

        Unit unit = new Unit();

        Mockito.when(unitRepository.findByIdentifier("kg")).thenReturn(unit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("kg", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findAllWithPageableTest() {
        Unit unit = new Unit();
        unit.setIdentifier("kg");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("kg");

        List<Unit> units = List.of(unit);
        List<UnitDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Unit> unitPage = new PageImpl<>(units);

        Mockito.when(unitRepository.findAll(pageable)).thenReturn(unitPage);
        Mockito.when(modelMapper.map(Mockito.eq(units), Mockito.any(Type.class))).thenReturn(dtos);

        WsDto<UnitDto> response = unitService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("kg", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Unit unit = new Unit();
        unit.setIdentifier("kg");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("kg");

        List<Unit> units = List.of(unit);
        List<UnitDto> dtos = List.of(dto);

        Mockito.when(unitRepository.findAll()).thenReturn(units);
        Mockito.when(modelMapper.map(Mockito.eq(units), Mockito.any(Type.class))).thenReturn(dtos);

        WsDto<UnitDto> response = unitService.findAll(null);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("kg", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void updateTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("kg");

        Unit unit = new Unit();
        unit.setIdentifier("kg");

        Mockito.when(unitRepository.findByIdentifier(unitDto.getIdentifier())).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("kg");

        Unit unit = new Unit();
        unit.setIdentifier("kg");

        Mockito.when(unitRepository.findByIdentifier(unitDto.getIdentifier())).thenReturn(null);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("kg");

        Unit unit = new Unit();
        unit.setIdentifier("kg");

        Mockito.when(unitRepository.findByIdentifier("kg")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto response = unitService.findByIdentifier("kg");

        Assertions.assertEquals("kg", response.getIdentifier());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(unitRepository).deleteByIdentifier("kg");
        unitService.delete("kg");
        Mockito.verify(unitRepository).deleteByIdentifier("kg");
    }

    @Test
    void toggleStatusSuccessTest() {
        Unit unit = new Unit();
        unit.setIdentifier("kg");
        unit.setStatus(false);

        Mockito.when(unitRepository.findByIdentifier("kg")).thenReturn(unit);

        UnitDto response = unitService.toggleStatus("kg", true);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(unitRepository.findByIdentifier("kg")).thenReturn(null);

        UnitDto response = unitService.toggleStatus("kg", true);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit not found", response.getMessage());
    }

}