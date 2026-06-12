package com.ust.pos;

import com.ust.pos.dto.PageDto;
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
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT001");
        unitDto.setSuccess(true);

        Mockito.when(unitRepository.findByIdentifier("UNIT001"))
                .thenReturn(null);

        Unit unit = new Unit();

        Mockito.when(modelMapper.map(unitDto, Unit.class))
                .thenReturn(unit);

        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("UNIT001", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT001");

        Mockito.when(unitRepository.findByIdentifier("UNIT001"))
                .thenReturn(new Unit());

        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("UNIT001", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT001");

        Mockito.when(unitRepository.findByIdentifier("UNIT001"))
                .thenReturn(unit);

        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(unitDto);

        UnitDto response = unitService.findByIdentifier("UNIT001");

        Assertions.assertEquals("UNIT001", response.getIdentifier());
    }

    @Test
    void updateTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT001");
        unitDto.setSuccess(true);

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("UNIT001");

        Mockito.when(unitRepository.findByIdentifier("UNIT001"))
                .thenReturn(existingUnit);

        Mockito.when(unitRepository.save(existingUnit))
                .thenReturn(existingUnit);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT001");

        Mockito.when(unitRepository.findByIdentifier("UNIT001"))
                .thenReturn(null);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(unitRepository)
                .deleteByIdentifier("UNIT001");

        unitService.delete("UNIT001");

        Mockito.verify(unitRepository)
                .deleteByIdentifier("UNIT001");
    }

    @Test
    void toggleStatusTest() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        unit.setStatus(true);

        Mockito.when(unitRepository.findByIdentifier("UNIT001"))
                .thenReturn(unit);

        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        unitService.toggleStatus("UNIT001");

        Assertions.assertFalse(unit.getStatus());

        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusTestFailure() {
        Mockito.when(unitRepository.findByIdentifier("UNIT001"))
                .thenReturn(null);

        unitService.toggleStatus("UNIT001");

        Mockito.verify(unitRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findActiveUnitsTest() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        unit.setStatus(true);

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT001");

        List<Unit> unitList = List.of(unit);

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();

        Mockito.when(unitRepository.findByStatusTrue())
                .thenReturn(unitList);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(unitList),
                        Mockito.eq(listType)
                )
        ).thenReturn(List.of(unitDto));

        List<UnitDto> response = unitService.findActiveUnits();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "UNIT001",
                response.get(0).getIdentifier()
        );
    }

    @Test
    void findAllPaginationTest() {

        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("UNIT001");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Unit> unitPage =
                new PageImpl<>(List.of(unit), pageable, 1);

        Mockito.when(unitRepository.findAll(pageable))
                .thenReturn(unitPage);

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(unitPage.getContent()),
                        Mockito.eq(listType)
                )
        ).thenReturn(List.of(unitDto));

        PageDto<UnitDto> response =
                unitService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(
                "UNIT001",
                response.getDtoList().get(0).getIdentifier()
        );
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}