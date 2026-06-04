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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @InjectMocks
    private UnitServiceImpl unitService;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest_Success() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Unit entity = new Unit();
        Mockito.when(unitRepository.findByIdentifier("U1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Unit.class))
                .thenReturn(entity);
        Mockito.when(unitRepository.save(entity))
                .thenReturn(entity);
        UnitDto response = unitService.save(dto);
        Assertions.assertEquals("U1", response.getIdentifier());
        Mockito.verify(unitRepository).save(entity);
    }

    @Test
    void saveTest_Failure_WhenAlreadyExists() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Mockito.when(unitRepository.findByIdentifier("U1"))
                .thenReturn(new Unit());
        UnitDto response = unitService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(unitRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest_Success() {
        UnitDto dto = new UnitDto();
        dto.setId(1L);
        dto.setIdentifier("U1");
        Unit existing = new Unit();
        existing.setIdentifier("U1");
        Mockito.when(unitRepository.findById(1L))
                .thenReturn(Optional.of(existing));
        Mockito.doNothing()
                .when(modelMapper).map(dto, existing);
        Mockito.when(unitRepository.save(existing))
                .thenReturn(existing);
        UnitDto response = unitService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(unitRepository).save(existing);
    }

    @Test
    void updateTest_Failure_WhenIdNotFound() {
        UnitDto dto = new UnitDto();
        dto.setId(1L);
        dto.setIdentifier("U1");
        Mockito.when(unitRepository.findById(1L))
                .thenReturn(Optional.empty());
        UnitDto response = unitService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Mockito.verify(unitRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest_Failure_WhenIdentifierExists() {
        UnitDto dto = new UnitDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");
        Unit existing = new Unit();
        existing.setIdentifier("OLD");
        Mockito.when(unitRepository.findById(1L))
                .thenReturn(Optional.of(existing));
        Mockito.when(unitRepository.findByIdentifier("NEW"))
                .thenReturn(new Unit());
        UnitDto response = unitService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Model Already Exists", response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Unit unit = new Unit();
        unit.setIdentifier("U1");
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");
        Mockito.when(unitRepository.findByIdentifier("U1"))
                .thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);
        UnitDto response = unitService.findByIdentifier("U1");
        Assertions.assertEquals("U1", response.getIdentifier());
    }

    @Test
    void findAllTest() {
        List<Unit> entities = List.of(new Unit());
        List<UnitDto> dtos = List.of(new UnitDto());
        Type listType = new TypeToken<List<UnitDto>>() {}.getType();
        Mockito.when(unitRepository.findAll())
                .thenReturn(entities);
        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);
        List<UnitDto> response = unitService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {
        Unit unit = new Unit();
        unit.setStatus(false);
        Mockito.when(unitRepository.findByIdentifier("U1"))
                .thenReturn(unit);
        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);
        unitService.toggleStatus("U1");
        Assertions.assertTrue(unit.isStatus());
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(unitRepository)
                .deleteByIdentifier("U1");
        unitService.delete("U1");
        Mockito.verify(unitRepository)
                .deleteByIdentifier("U1");
    }

    @Test
    void findAll_WithPagination_ShouldReturnUnitDtos() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Unit> units = List.of(new Unit());
        Page<Unit> page = new PageImpl<>(units);
        List<UnitDto> unitDtos = List.of(new UnitDto());
        Type listType = new TypeToken<List<UnitDto>>() {}.getType();
        Mockito.when(unitRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(units, listType))
                .thenReturn(unitDtos);
        List<UnitDto> response = unitService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(unitRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(units, listType);
    }
}