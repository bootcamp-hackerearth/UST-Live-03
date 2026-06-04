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
        unitDto.setIdentifier("Unit1");

        Unit unit = new Unit();

        Mockito.when(unitRepository.findByIdentifier("Unit1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(unitDto, Unit.class))
                .thenReturn(unit);
        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("Unit1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Unit1");

        Mockito.when(unitRepository.findByIdentifier("Unit1"))
                .thenReturn(new Unit());

        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("Unit1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Unit unit = new Unit();
        unit.setIdentifier("Unit1");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Unit1");

        Mockito.when(unitRepository.findByIdentifier("Unit1"))
                .thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(unitDto);

        UnitDto response = unitService.findByIdentifier("Unit1");

        Assertions.assertEquals("Unit1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Unit1");

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("Unit1");

        Mockito.when(unitRepository.findByIdentifier("Unit1"))
                .thenReturn(existingUnit);
        Mockito.when(unitRepository.save(existingUnit))
                .thenReturn(existingUnit);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Unit1");

        Mockito.when(unitRepository.findByIdentifier("Unit1"))
                .thenReturn(null);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(unitRepository)
                .deleteByIdentifier("Unit1");

        unitService.delete("Unit1");

        Mockito.verify(unitRepository)
                .deleteByIdentifier("Unit1");
    }

    @Test
    void toggleStatusTest() {
        Unit unit = new Unit();
        unit.setIdentifier("Unit1");
        unit.setStatus(true);

        Mockito.when(unitRepository.findByIdentifier("Unit1"))
                .thenReturn(unit);
        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        unitService.toggleStatus("Unit1");

        Assertions.assertFalse(unit.getStatus());
        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void toggleStatusTestFailure() {
        Mockito.when(unitRepository.findByIdentifier("Unit1"))
                .thenReturn(null);

        unitService.toggleStatus("Unit1");

        Mockito.verify(unitRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllPaginationTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Unit1");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Unit1");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Unit> unitPage =
                new PageImpl<>(List.of(unit), pageable, 1);

        Mockito.when(unitRepository.findAll(pageable))
                .thenReturn(unitPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(unitPage.getContent()),
                Mockito.any(Type.class)
        )).thenReturn(List.of(unitDto));

        List<UnitDto> response = unitService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }
}