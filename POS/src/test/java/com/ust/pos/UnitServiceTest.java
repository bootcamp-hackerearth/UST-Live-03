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
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UnitServiceImpl unitService;

    @Test
    void saveTestSuccess() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Unit");

        Unit unit = new Unit();
        Unit savedUnit = new Unit();
        savedUnit.setIdentifier("Unit");

        UnitDto mappedDto = new UnitDto();
        mappedDto.setIdentifier("Unit");
        mappedDto.setSuccess(true);

        Mockito.when(unitRepository.findByIdentifier("Unit")).thenReturn(null);
        Mockito.when(modelMapper.map(unitDto, Unit.class)).thenReturn(unit);
        Mockito.when(unitRepository.save(unit)).thenReturn(savedUnit);
        Mockito.when(modelMapper.map(savedUnit, UnitDto.class)).thenReturn(mappedDto);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Unit", response.getIdentifier());
    }

    @Test
    void saveTestFailure_DuplicateIdentifier() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Unit");

        Unit existing = new Unit();
        existing.setIdentifier("Unit");

        Mockito.when(unitRepository.findByIdentifier("Unit")).thenReturn(existing);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdTestSuccess() {
        Unit unit = new Unit();
        unit.setIdentifier("Unit");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Unit");

        Mockito.when(unitRepository.findById(1L))
                .thenReturn(Optional.of(unit));
        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(unitDto);

        UnitDto response = unitService.findById(1L);

        Assertions.assertEquals("Unit", response.getIdentifier());
    }

    @Test
    void findByIdTestFailure() {
        Mockito.when(unitRepository.findById(99L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class,
                () -> unitService.findById(99L));
    }

    @Test
    void findAllTest() {
        Unit unit = new Unit();
        unit.setIdentifier("Unit");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Unit");

        Page<Unit> page = new PageImpl<>(List.of(unit));

        Mockito.when(unitRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(unitDto));

        Pageable pageable = PageRequest.of(0, 50, Sort.unsorted());
        List<UnitDto> response = unitService.findAll(pageable).getDtoList();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Unit", response.get(0).getIdentifier());
    }

    @Test
    void updateTestSuccess() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Unit");

        Unit unit = new Unit();
        unit.setIdentifier("Unit");

        Mockito.when(unitRepository.findByIdentifier("Unit"))
                .thenReturn(unit);
        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure_NotFound() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Unit");

        Mockito.when(unitRepository.findByIdentifier("Unit"))
                .thenReturn(null);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTestFailure_DuplicateIdentifier() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("NewUnit");

        Unit existing = new Unit();
        existing.setIdentifier("OldUnit");

        Mockito.when(unitRepository.findByIdentifier("NewUnit"))
                .thenReturn(existing);
        Mockito.when(unitRepository.existsByIdentifier("NewUnit"))
                .thenReturn(true);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("This unit already exist!", response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Unit unit = new Unit();
        unit.setIdentifier("Unit");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Unit");

        Mockito.when(unitRepository.findByIdentifier("Unit"))
                .thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(unitDto);

        UnitDto response = unitService.findByIdentifier("Unit");

        Assertions.assertEquals("Unit", response.getIdentifier());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(unitRepository).deleteById(1L);

        unitService.delete(1L);

        Mockito.verify(unitRepository, Mockito.times(1)).deleteById(1L);
    }
}