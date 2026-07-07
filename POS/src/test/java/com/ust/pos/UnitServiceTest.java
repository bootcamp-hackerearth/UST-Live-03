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
import org.springframework.data.jpa.domain.Specification;

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

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U101");
        Unit unit = new Unit();
        Mockito.when(unitRepository.findByIdentifier("U101")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Unit.class)).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        UnitDto response = unitService.save(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("U101", response.getIdentifier());
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void saveFailure_existingActive() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U101");
        Unit existing = new Unit();
        existing.setDeleted(false);
        Mockito.when(unitRepository.findByIdentifier("U101")).thenReturn(existing);

        UnitDto response = unitService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveFailure_softDeleted() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U101");
        Unit existing = new Unit();
        existing.setDeleted(true);
        Mockito.when(unitRepository.findByIdentifier("U101")).thenReturn(existing);
        UnitDto response = unitService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Unit unit = new Unit();
        unit.setIdentifier("U101");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U101");
        Mockito.when(unitRepository.findByIdentifier("U101")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);
        UnitDto response = unitService.findByIdentifier("U101");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("U101", response.getIdentifier());
    }

    @Test
    void updateTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U101");
        Unit existing = new Unit();
        Mockito.when(unitRepository.findByIdentifier("U101")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(unitRepository.save(existing)).thenReturn(existing);
        UnitDto response = unitService.update(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("U101", response.getIdentifier());
        Mockito.verify(unitRepository).save(existing);
    }

    @Test
    void updateFailure() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U101");
        Mockito.when(unitRepository.findByIdentifier("U101")).thenReturn(null);
        UnitDto response = unitService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Unit unit = new Unit();
        unit.setDeleted(false);
        Mockito.when(unitRepository.findByIdentifier("U101")).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        unitService.delete("U101");
        Mockito.verify(unitRepository).findByIdentifier("U101");
        Mockito.verify(unitRepository).save(unit);
        Assertions.assertTrue(unit.isDeleted());
    }

    @Test
    void findAllTest() {
        Unit unit = new Unit();
        unit.setIdentifier("U101");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U101");
        List<Unit> units = List.of(unit);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Unit> page = new PageImpl<>(units, pageable, 1);
        Mockito.when(unitRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(units), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of(dto));

        WsDto<UnitDto> response = unitService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("U101", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findAllWithSpecificationTest() {
        Unit unit = new Unit();
        unit.setIdentifier("U101");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U101");
        List<Unit> units = List.of(unit);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Unit> page = new PageImpl<>(units, pageable, 1);
        @SuppressWarnings("unchecked")
        Specification<Unit> specification = Mockito.mock(Specification.class);
        Mockito.when(unitRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(units), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of(dto));

        WsDto<UnitDto> response = unitService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
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

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT_001");
        dto.setStatus(true);
        Mockito.when(unitRepository.findByIdentifier("UNIT_001")).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);
        UnitDto response = unitService.toggleStatus("UNIT_001", true);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("UNIT_001", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(unitRepository.findByIdentifier("UNIT_001")).thenReturn(null);
        Mockito.when(modelMapper.map(Mockito.isNull(), Mockito.eq(UnitDto.class))).thenReturn(null);
        UnitDto response = unitService.toggleStatus("UNIT_001", true);
        Assertions.assertNull(response);
        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveUnitsTest() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT_001");
        unit.setStatus(true);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT_001");
        dto.setStatus(true);

        List<Unit> units = List.of(unit);
        Mockito.when(unitRepository.findByStatusTrue()).thenReturn(units);
        Mockito.when(modelMapper.map(Mockito.eq(units), Mockito.any(java.lang.reflect.Type.class))).thenReturn(List.of(dto));

        List<UnitDto> response = unitService.findActiveUnit();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("UNIT_001", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
        Mockito.verify(unitRepository).findByStatusTrue();
    }
}