package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
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
    void saveTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(null);
        Unit unit = new Unit();
        Mockito.when(modelMapper.map(dto, Unit.class)).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        UnitDto response = unitService.save(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestAlreadyExists() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Unit existing = new Unit();
        existing.setDeleted(false);
        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(existing);
        UnitDto response = unitService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestDeletedExists() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Unit existing = new Unit();
        existing.setDeleted(true);
        Mockito.when(unitRepository.findByIdentifier("U1")).thenReturn(existing);
        UnitDto response = unitService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTest() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Unit existing = new Unit();
        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(unitRepository.save(existing)).thenReturn(existing);
        UnitDto response = unitService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(null);
        UnitDto response = unitService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Unit unit = new Unit();
        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        unitService.delete("U1");
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void deleteNullTest() {
        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(null);
        unitService.delete("U1");
        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllTest() {
        Unit unit = new Unit();
        UnitDto dto = new UnitDto();
        List<Unit> list = List.of(unit);
        List<UnitDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Unit> page = new PageImpl<>(list);
        Mockito.when(unitRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        WsDto<UnitDto> response = unitService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findByIdentifierTest() {
        Unit unit = new Unit();
        UnitDto dto = new UnitDto();
        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);
        UnitDto response = unitService.findByIdentifier("U1");
        Assertions.assertNotNull(response);
    }

    @Test
    void updateStatusTest() {
        Unit unit = new Unit();
        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(unit);
        unitService.updateStatus("U1", true);
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void updateStatusNullTest() {
        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1")).thenReturn(null);
        unitService.updateStatus("U1", true);
        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllActiveTest() {
        Unit unit = new Unit();
        UnitDto dto = new UnitDto();
        List<Unit> list = List.of(unit);
        List<UnitDto> dtoList = List.of(dto);
        Mockito.when(unitRepository.findByStatusAndDeletedFalse(true)).thenReturn(list);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        List<UnitDto> response = unitService.findAllActive();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllSpecificationTest() {
        Unit unit = new Unit();
        UnitDto dto = new UnitDto();
        List<Unit> list = List.of(unit);
        List<UnitDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Unit> page = new PageImpl<>(list);
        Specification<Unit> specification = Mockito.mock(Specification.class);
        Mockito.when(unitRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        WsDto<UnitDto> response = unitService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(0, response.getPage());
        Assertions.assertEquals(1, response.getSizePerPage());
        Mockito.verify(unitRepository).findAll(specification, pageable);
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1"))
                .thenReturn(null);
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> unitService.findByIdentifier("U1")
        );
    }
}