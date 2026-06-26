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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;

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
    void saveTestSuccess() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Unit unit = new Unit();

        Mockito.when(
                unitRepository.findByIdentifierAndDeletedFalse("KG")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(dto, Unit.class)
        ).thenReturn(unit);

        UnitDto response = unitService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void saveTestFail() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        Mockito.when(
                unitRepository.findByIdentifierAndDeletedFalse("KG")
        ).thenReturn(new Unit());

        UnitDto response = unitService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Model - KG already exists",
                response.getMessage()
        );
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

        Mockito.verify(unitRepository,
                Mockito.never()).save(Mockito.any());
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

        Mockito.when(
                unitRepository.findByIdentifierAndDeletedFalse("LITRE")
        ).thenReturn(new Unit());

        UnitDto response = unitService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Model Already Exists",
                response.getMessage()
        );

        Mockito.verify(unitRepository,
                Mockito.never()).save(Mockito.any());
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

        Mockito.when(
                unitRepository.findByIdentifierAndDeletedFalse("KG")
        ).thenReturn(unit);

        Mockito.when(
                modelMapper.map(unit, UnitDto.class)
        ).thenReturn(dto);

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

        Mockito.when(
                unitRepository.findByDeletedFalse()
        ).thenReturn(units);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(units),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<UnitDto> response = unitService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Unit> units = List.of(new Unit());

        Page<Unit> page =
                new PageImpl<>(units, pageable, 1);

        List<UnitDto> dtoList =
                List.of(new UnitDto());

        Type listType =
                new TypeToken<List<UnitDto>>() {}.getType();

        Mockito.when(
                unitRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(units, listType)
        ).thenReturn(dtoList);

        WsDto<UnitDto> response =
                unitService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );
        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void findAllSearchTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Unit unit = new Unit();

        Page<Unit> page =
                new PageImpl<>(List.of(unit));

        Mockito.when(
                unitRepository
                        .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                                "KG",
                                pageable
                        )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(unit, UnitDto.class)
        ).thenReturn(new UnitDto());

        Page<UnitDto> response =
                unitService.findAll("KG", pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void findAllWithoutSearchTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Unit unit = new Unit();

        Page<Unit> page =
                new PageImpl<>(List.of(unit));

        Mockito.when(
                unitRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(unit, UnitDto.class)
        ).thenReturn(new UnitDto());

        Page<UnitDto> response =
                unitService.findAll("", pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void deleteUnitTest() {

        Unit unit = new Unit();
        unit.setDeleted(false);

        Mockito.when(
                unitRepository.findByIdentifierAndDeletedFalse("KG")
        ).thenReturn(unit);

        unitService.delete("KG");

        Assertions.assertTrue(unit.getDeleted());

        Mockito.verify(unitRepository)
                .save(unit);
    }

    @Test
    void deleteUnitNotFoundTest() {

        Mockito.when(
                unitRepository.findByIdentifierAndDeletedFalse("KG")
        ).thenReturn(null);

        unitService.delete("KG");

        Mockito.verify(unitRepository,
                Mockito.never()).save(Mockito.any());
    }

    @Test
    void toggleUnitStatusSuccess() {

        Unit unit = new Unit();
        unit.setStatus(true);

        Mockito.when(
                unitRepository.findByIdentifierAndDeletedFalse("KG")
        ).thenReturn(unit);

        unitService.toggleStatus("KG");

        Assertions.assertFalse(unit.getStatus());

        Mockito.verify(unitRepository)
                .save(unit);
    }

    @Test
    void toggleUnitStatusNotFound() {

        Mockito.when(
                unitRepository.findByIdentifierAndDeletedFalse("KG")
        ).thenReturn(null);

        unitService.toggleStatus("KG");

        Mockito.verify(unitRepository,
                Mockito.never()).save(Mockito.any());
    }
}