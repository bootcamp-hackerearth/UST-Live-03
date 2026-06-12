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
        unitDto.setIdentifier("U101");
        Mockito.when(unitRepository.findByIdentifier("U101")).thenReturn(null);
        Unit unit = new Unit();

        Mockito.when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        UnitDto response = unitService.save(unitDto);
        Assertions.assertEquals("U101", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("U101");
        Unit unit = new Unit();
        
        Mockito.when(unitRepository.findByIdentifier("U101")).thenReturn(unit);
        UnitDto response = unitService.save(unitDto);
        Assertions.assertEquals("U101", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Unit unit = new Unit();
        unit.setIdentifier("U101");
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("U101");
        
        Mockito.when(unitRepository.findByIdentifier("U101")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);
        UnitDto response = unitService.findByIdentifier("U101");
        Assertions.assertEquals("U101", response.getIdentifier());
    }

    @Test
    void updateTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("U101");
        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("U101");
        
        Mockito.when(unitRepository.findByIdentifier("U101")).thenReturn(existingUnit);
        Mockito.when(unitRepository.save(existingUnit)).thenReturn(existingUnit);
        UnitDto response = unitService.update(unitDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("U101");
        
        Mockito.when(unitRepository.findByIdentifier("U101")).thenReturn(null);
        UnitDto response = unitService.update(unitDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(unitRepository).deleteByIdentifier("U101");
        unitService.delete("U101");
        Mockito.verify(unitRepository).deleteByIdentifier("U101");
    }

    @Test
    void findAllTest() {
        Unit unit = new Unit();
        unit.setIdentifier("U101");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("U101");

        List<Unit> units = List.of(unit);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Unit> unitPage = new PageImpl<>(units, pageable, units.size());

        Mockito.when(unitRepository.findAll(pageable)).thenReturn(unitPage);
        Mockito.when(modelMapper.map(Mockito.eq(units),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(unitDto));
        WsDto<UnitDto> response = unitService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getDtoList());
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("U101", response.getDtoList().get(0).getIdentifier());

        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void toggleStatusSuccessTest() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT_001");
        unit.setStatus(false); 
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT_001");
        unitDto.setStatus(true); 
        Mockito.when(unitRepository.findByIdentifier("UNIT_001")).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);
        UnitDto response = unitService.toggleStatus("UNIT_001", true);
        Assertions.assertEquals("UNIT_001", response.getIdentifier());
        Assertions.assertTrue(response.isStatus()); 
    }

    @Test
    void changeUnitStatusFailureTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT_001");
        
        Mockito.when(unitRepository.findByIdentifier("UNIT_001")).thenReturn(null);
        UnitDto response = unitService.toggleStatus("UNIT_001", true);
        Assertions.assertNull(response);
        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveUnitsTest() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT_001");
        unit.setStatus(true);
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT_001");
        unitDto.setStatus(true);

        List<Unit> activeUnits = List.of(unit);
        List<UnitDto> activeUnitDtos = List.of(unitDto);
        Mockito.when(unitRepository.findByStatusTrue()).thenReturn(activeUnits);
        Mockito.when(modelMapper.map(
                Mockito.eq(activeUnits),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(activeUnitDtos);
        List<UnitDto> response = unitService.findActiveUnit();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("UNIT_001", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
    }
}