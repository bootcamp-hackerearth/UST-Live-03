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
import org.springframework.data.jpa.domain.Specification;

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
    void saveSuccessTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("KG");
        Unit unit = new Unit();
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(null);
        Mockito.when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        UnitDto response = unitService.save(unitDto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("KG", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("KG");
        Unit existingUnit = new Unit();
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(existingUnit);
        UnitDto response = unitService.save(unitDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));
        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureSoftDeletedTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("KG");
        Unit existingUnit = new Unit();
        existingUnit.setDeleted(true);
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(existingUnit);
        UnitDto response = unitService.save(unitDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);
        UnitDto response = unitService.findByIdentifier("KG");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("KG", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(null);
        UnitDto response = unitService.findByIdentifier("KG");
        Assertions.assertNull(response);
    }

    @Test
    void updateSuccessTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("KG");
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        UnitDto response = unitService.update(unitDto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(modelMapper).map(unitDto, unit);
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void updateFailureTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("KG");
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(null);
        UnitDto response = unitService.update(unitDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void deleteSuccessTest() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        unitService.delete("KG");
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void deleteFailureTest() {
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(null);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> unitService.delete("KG"));
        Assertions.assertEquals("Unit not found", exception.getMessage());
    }

    @Test
    void findAllWithPageableTest() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        List<Unit> units = List.of(unit);
        List<UnitDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Unit> page = new PageImpl<>(units);
        Mockito.when(unitRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(units), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<UnitDto> response = unitService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("KG", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        List<Unit> units = List.of(unit);
        List<UnitDto> dtos = List.of(dto);
        Mockito.when(unitRepository.findAll()).thenReturn(units);
        Mockito.when(modelMapper.map(Mockito.eq(units), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<UnitDto> response = unitService.findAll(null);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("KG", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void toggleStatusSuccessTest() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");
        unit.setStatus(false);
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        dto.setStatus(true);
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);
        UnitDto response = unitService.toggleStatus("KG", true);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("KG", response.getIdentifier());
        Assertions.assertTrue(unit.isStatus());
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusNotFoundTest() {
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(null);
        Mockito.when(modelMapper.map(null, UnitDto.class)).thenReturn(null);
        UnitDto response = unitService.toggleStatus("KG", true);
        Assertions.assertNull(response);
        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllWithSpecificationTest() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");
        List<Unit> units = List.of(unit);
        List<UnitDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Specification<Unit> specification = Mockito.mock(Specification.class);
        Page<Unit> page = new PageImpl<>(units);
        Mockito.when(unitRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(units), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<UnitDto> response = unitService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("UNIT001", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Mockito.verify(unitRepository).findAll(specification, pageable);
    }

    @Test
    void findAllWithSpecificationNoDataTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Specification<Unit> specification = Mockito.mock(Specification.class);
        Page<Unit> page = new PageImpl<>(List.of());
        Mockito.when(unitRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());
        PaginationResponseDto<UnitDto> response = unitService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getDtoList().isEmpty());
        Assertions.assertEquals(0, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Mockito.verify(unitRepository).findAll(specification, pageable);
    }
}