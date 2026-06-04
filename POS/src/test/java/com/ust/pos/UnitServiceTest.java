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
import java.util.ArrayList;
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
    void saveTestSuccess()
    {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Unit unit = new Unit();

        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Unit.class)).thenReturn(unit);

        UnitDto response = unitService.save(dto);
        Assertions.assertNull(response.getMessage());
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

    @Test
    void saveTestFail()
    {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Mockito.when(unitRepository.findByIdentifier("KG")).thenReturn(new Unit());

        UnitDto response = unitService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Model - KG already exists", response.getMessage());
    }

    @Test
    void updateUnitNotFound() {
        UnitDto dto = new UnitDto();
        dto.setId(1L);
        dto.setIdentifier("KG");

        Mockito.when(unitRepository.findById(1L))
                .thenReturn(Optional.empty());

        UnitDto response = unitService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(unitRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateUnitIdentifierConflict() {
        UnitDto dto = new UnitDto();
        dto.setId(1L);
        dto.setIdentifier("LITRE");

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("KG");

        Mockito.when(unitRepository.findById(1L))
                .thenReturn(Optional.of(existingUnit));
        Mockito.when(unitRepository.findByIdentifier("LITRE"))
                .thenReturn(new Unit());

        UnitDto response = unitService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Model Already Exists", response.getMessage());

        Mockito.verify(unitRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateUnitSuccess() {
        UnitDto dto = new UnitDto();
        dto.setId(1L);
        dto.setIdentifier("KG");

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("KG");

        Mockito.when(unitRepository.findById(1L))
                .thenReturn(Optional.of(existingUnit));

        UnitDto response = unitService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(modelMapper)
                .map(dto, existingUnit);
        Mockito.verify(unitRepository)
                .save(existingUnit);
    }

    @Test
    void findUnitByIdentifierTest() {
        Unit unit = new Unit();
        unit.setIdentifier("KG");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Mockito.when(unitRepository.findByIdentifier("KG"))
                .thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        UnitDto response = unitService.findByIdentifier("KG");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("KG", response.getIdentifier());
    }

    @Test
    void findAllUnitsTest() {
        List<Unit> units = new ArrayList<>();
        units.add(new Unit());
        units.add(new Unit());

        List<UnitDto> dtoList = new ArrayList<>();
        dtoList.add(new UnitDto());
        dtoList.add(new UnitDto());

        Mockito.when(unitRepository.findAll())
                .thenReturn(units);
        Mockito.when(modelMapper.map(
                Mockito.eq(units),
                Mockito.any(Type.class))
        ).thenReturn(dtoList);

        List<UnitDto> response = unitService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void deleteUnitTest() {
        String identifier = "KG";

        Mockito.doNothing()
                .when(unitRepository)
                .deleteByIdentifier(identifier);

        unitService.delete(identifier);

        Mockito.verify(unitRepository)
                .deleteByIdentifier(identifier);
    }

    @Test
    void toggleUnitStatusSuccess() {
        Unit unit = new Unit();
        unit.setStatus(Boolean.TRUE);
        Mockito.when(unitRepository.findByIdentifier("KG"))
                .thenReturn(unit);

        unitService.toggleStatus("KG");

        Assertions.assertEquals(Boolean.FALSE, unit.getStatus());
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void toggleUnitStatusNotFound() {
        Mockito.when(unitRepository.findByIdentifier("KG"))
                .thenReturn(null);

        unitService.toggleStatus("KG");

        Mockito.verify(unitRepository, Mockito.never())
                .save(Mockito.any());
    }
}
