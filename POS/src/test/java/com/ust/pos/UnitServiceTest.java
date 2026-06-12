package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UnitServiceImpl unitService;

    @Test
    @DisplayName("Save Unit - Success Case")
    void saveTest_Success() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        Unit unitEntity = new Unit();

        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Unit.class)).thenReturn(unitEntity);

        UnitDto result = unitService.save(dto);

        Assertions.assertNotNull(result);
        Mockito.verify(unitRepository).save(unitEntity);
    }

    @Test
    @DisplayName("Save Unit - Failure: Already Exists")
    void saveTest_AlreadyExists() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(new Unit());

        UnitDto result = unitService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Unit with identifier - KG already exists", result.getMessage());
        Mockito.verify(unitRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Update Unit - Success Case")
    void updateTest_Success() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        Unit existingUnit = new Unit();

        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(existingUnit);

        UnitDto result = unitService.update(dto);

        Assertions.assertNotNull(result);
        Mockito.verify(modelMapper).map(dto, existingUnit);
        Mockito.verify(unitRepository).save(existingUnit);
    }

    @Test
    @DisplayName("Update Unit - Failure: Not Found")
    void updateTest_NotFound() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(null);

        UnitDto result = unitService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Unit with identifier - KG not found", result.getMessage());
        Mockito.verify(unitRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Find All - Paginated Success")
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Unit> units = List.of(new Unit());
        Page<Unit> unitPage = new PageImpl<>(units);
        List<UnitDto> dtos = List.of(new UnitDto());

        Mockito.when(unitRepository.findAll(pageable)).thenReturn(unitPage);
        Mockito.when(modelMapper.map(eq(units), any(Type.class))).thenReturn(dtos);

        List<UnitDto> result = unitService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
        Mockito.verify(unitRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Find All Active - Success")
    void findAllActiveTest() {
        List<Unit> units = List.of(new Unit());
        List<UnitDto> dtos = List.of(new UnitDto());

        Mockito.when(unitRepository.findAllByStatus(true)).thenReturn(units);
        Mockito.when(modelMapper.map(eq(units), any(Type.class))).thenReturn(dtos);

        List<UnitDto> result = unitService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifierTest() {
        Unit unitEntity = new Unit();
        UnitDto unitDto = new UnitDto();
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(unitEntity);
        Mockito.when(modelMapper.map(unitEntity, UnitDto.class)).thenReturn(unitDto);

        UnitDto result = unitService.findByIdentifier("KG");
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Toggle Status - Logic Flip")
    void toggleStatusTest() {
        Unit unit = new Unit();
        unit.setStatus(false);
        UnitDto dto = new UnitDto();

        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(dto);

        unitService.toggleStatus("KG");

        Assertions.assertTrue(unit.isStatus());
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    @DisplayName("Delete Unit - Success")
    void deleteTest() {
        boolean result = unitService.delete("KG");
        Assertions.assertTrue(result);
        Mockito.verify(unitRepository).deleteByIdentifier("KG");
    }
}