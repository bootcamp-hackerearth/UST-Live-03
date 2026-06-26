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

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.verify;

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

        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("KG", response.getIdentifier());
        verify(unitRepository).save(unit);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("KG");

        Unit existingUnit = new Unit();
        existingUnit.setDeleted(false);

        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(existingUnit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("KG", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit with identifier - KG already exists", response.getMessage());
        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureAlreadyDeletedTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("KG");

        Unit existingUnit = new Unit();
        existingUnit.setDeleted(true);

        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(existingUnit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("KG", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Models with identifier - KG was deleted , Please Contact the Administrator to add.", response.getMessage());
        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("KG");

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("KG");

        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(existingUnit);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertEquals("KG", response.getIdentifier());
        verify(modelMapper).map(unitDto, existingUnit);
        verify(unitRepository).save(existingUnit);
    }

    @Test
    void updateFailureTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("KG");

        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(null);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertEquals("KG", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit with identifier - KG not found", response.getMessage());
        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Unit unit = new Unit();
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(unit);

        unitService.delete("KG");

        verify(unitRepository).findByIdentifier("KG");
    }

    @Test
    void findAllSuccessTest() {
        Unit unit = new Unit();
        List<Unit> unitList = List.of(unit);

        UnitDto dto = new UnitDto();
        List<UnitDto> unitDtos = List.of(dto);

        Page<Unit> page = new PageImpl<>(unitList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(unitRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(unitList), Mockito.any(Type.class))).thenReturn(unitDtos);

        WsDto<UnitDto> result = unitService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Unit unit = new Unit();
        UnitDto unitDto = new UnitDto();

        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class)).thenReturn(unitDto);

        UnitDto response = unitService.findByIdentifier("KG");

        Assertions.assertNotNull(response);
    }

    @Test
    void findAllActiveSuccessTest() {
        Unit unit = new Unit();
        List<Unit> activeUnits = List.of(unit);

        UnitDto dto = new UnitDto();
        List<UnitDto> dtos = List.of(dto);

        Mockito.when(unitRepository.findByStatusTrueAndIsDeletedFalse()).thenReturn(activeUnits);
        Mockito.when(modelMapper.map(Mockito.eq(activeUnits), Mockito.any(Type.class))).thenReturn(dtos);

        List<UnitDto> result = unitService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void toggleStatusSuccessTest() {
        Unit unit = new Unit();
        unit.setStatus(false);

        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(unit);

        unitService.toggleStatus("KG");

        Assertions.assertTrue(unit.isStatus());
        verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusUnitNotFoundTest() {
        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(null);

        unitService.toggleStatus("KG");

        Mockito.verify(unitRepository, Mockito.never()).save(Mockito.any());
    }
}