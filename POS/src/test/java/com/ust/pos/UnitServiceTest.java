package com.ust.pos;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;
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
    void saveTest() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        dto.setSuccess(true);

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(null);

        Unit unit = new Unit();

        Mockito.when(modelMapper.map(dto, Unit.class)).thenReturn(unit);

        Mockito.when(unitRepository.save(unit)).thenReturn(unit);

        UnitDto response = unitService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("U1", response.getIdentifier());

        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void saveDuplicateTest() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Unit existing = new Unit();
        existing.setDeleted(false);

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(existing);

        UnitDto response = unitService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit with identifier - U1 already exists", response.getMessage());

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveSoftDeletedRecordTest() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Unit deletedUnit = new Unit();
        deletedUnit.setDeleted(true);

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(deletedUnit);

        UnitDto response = unitService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit with identifier - U1 has been soft deleted. Restore it by changing status.", response.getMessage());

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {

        Unit entity = new Unit();
        entity.setIdentifier("U1");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(entity);

        Mockito.when(modelMapper.map(entity, UnitDto.class)).thenReturn(dto);

        UnitDto response = unitService.findByIdentifier("U1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("U1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> unitService.findByIdentifier("U1"));

        Assertions.assertEquals("Unit with identifier 'U1' not found", exception.getMessage());

        Mockito.verify(modelMapper, Mockito.never()).map(Mockito.any(), Mockito.eq(UnitDto.class));
    }

    @Test
    void updateSuccessTest() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        dto.setSuccess(true);

        Unit entity = new Unit();
        entity.setIdentifier("U1");

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(entity);

        Mockito.doNothing().when(modelMapper).map(dto, entity);

        Mockito.when(unitRepository.save(entity)).thenReturn(entity);

        UnitDto response = unitService.update(dto);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(unitRepository).save(entity);
    }

    @Test
    void updateFailureTest() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(null);

        UnitDto response = unitService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit with identifier - U1 not found", response.getMessage());

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSoftDeleteSuccessTest() {

        Unit unit = new Unit();
        unit.setIdentifier("U1");
        unit.setDeleted(false);
        unit.setStatus(true);

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(unit);

        Mockito.when(unitRepository.save(unit)).thenReturn(unit);

        boolean result = unitService.delete("U1");

        Assertions.assertTrue(result);
        Assertions.assertTrue(unit.getDeleted());
        Assertions.assertFalse(unit.getStatus());

        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void deleteSoftDeleteFailureTest() {

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(null);

        boolean result = unitService.delete("U1");

        Assertions.assertFalse(result);

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void toggleStatusTest() {

        Unit entity = new Unit();
        entity.setIdentifier("U1");
        entity.setStatus(true);

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(entity);

        Mockito.when(unitRepository.save(entity)).thenReturn(entity);

        unitService.toggleStatus("U1");

        Assertions.assertFalse(entity.getStatus());

        Mockito.verify(unitRepository).save(entity);
    }

    @Test
    void toggleStatusFailureTest() {

        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(null);

        unitService.toggleStatus("U1");

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllPaginationTest() {

        Unit entity = new Unit();
        entity.setIdentifier("U1");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Unit> page = new PageImpl<>(List.of(entity), pageable, 1);

        Mockito.when(unitRepository.findByDeletedFalse(pageable)).thenReturn(page);

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();

        Mockito.when(modelMapper.map(page.getContent(), listType)).thenReturn(List.of(dto));

        PageDto<UnitDto> response = unitService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("U1", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAllWithSpecificationTest() {

        Unit entity = new Unit();
        entity.setIdentifier("U1");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Pageable pageable = PageRequest.of(0, 10);
        Specification<Unit> spec = Mockito.mock(Specification.class);

        Page<Unit> page = new PageImpl<>(List.of(entity), pageable, 1);

        Mockito.when(unitRepository.findAll(spec, pageable)).thenReturn(page);
        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();

        Mockito.when(modelMapper.map(page.getContent(), listType)).thenReturn(List.of(dto));

        PageDto<UnitDto> response = unitService.findAll(spec, pageable, "U1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("U1", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Assertions.assertEquals("U1", response.getKeyword());
    }

    @Test
    void findActiveUnitsTest() {

        Unit entity = new Unit();
        entity.setIdentifier("U1");
        entity.setStatus(true);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        List<Unit> units = List.of(entity);

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();

        Mockito.when(unitRepository.findByStatusTrue()).thenReturn(units);

        Mockito.when(modelMapper.map(units, listType)).thenReturn(List.of(dto));

        List<UnitDto> response = unitService.findActiveUnits();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("U1", response.get(0).getIdentifier());
    }
}