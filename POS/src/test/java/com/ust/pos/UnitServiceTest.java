package com.ust.pos;

import com.ust.pos.dto.PaginatedResponseDto;
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

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Mockito.when(unitRepository.findByIdentifier("Admin")).thenReturn(null);

        Unit unit = new Unit();

        Mockito.when(modelMapper.map(unitDto, Unit.class))
                .thenReturn(unit);

        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());

        Assertions.assertFalse(unit.getIsDeleted());
    }

    @Test
    void saveTestFailureAlreadyExists() {

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Unit unit = new Unit();
        unit.setIsDeleted(false);

        Mockito.when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(unit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveTestFailureDeletedRecordExists() {

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Unit unit = new Unit();
        unit.setIsDeleted(true);

        Mockito.when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(unit);

        UnitDto response = unitService.save(unitDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage().contains("was deleted")
        );
    }

    @Test
    void findByIdentifierTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Admin");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Mockito.when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(unit);

        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(unitDto);

        UnitDto response = unitService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Unit existingUnit = new Unit();
        existingUnit.setIdentifier("Admin");

        Mockito.when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(existingUnit);

        Mockito.when(unitRepository.save(existingUnit))
                .thenReturn(existingUnit);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        Mockito.when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage().contains("not found")
        );
    }

    @Test
    void deleteTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Admin");
        unit.setStatus(true);
        unit.setIsDeleted(false);

        Mockito.when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(unit);

        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        UnitDto response = unitService.delete("Admin");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Unit deleted successfully",
                response.getMessage()
        );

        Assertions.assertTrue(unit.getIsDeleted());
        Assertions.assertFalse(unit.getStatus());

        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void deleteTestFailure() {

        Mockito.when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        UnitDto response = unitService.delete("Admin");

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage().contains("not found")
        );
    }

    @Test
    void findAllTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Admin");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        List<Unit> units = List.of(unit);
        List<UnitDto> unitDtos = List.of(unitDto);

        Page<Unit> unitPage = new PageImpl<>(units);

        Mockito.when(
                unitRepository.findByIsDeleted(
                        Mockito.eq(false),
                        Mockito.any(Pageable.class)
                )
        ).thenReturn(unitPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(units),
                        Mockito.any(java.lang.reflect.Type.class)
                )
        ).thenReturn(unitDtos);

        PaginatedResponseDto<UnitDto> response =
                unitService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
    }

    @Test
    void findAllActiveTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Admin");
        unit.setStatus(true);

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        List<Unit> units = List.of(unit);
        List<UnitDto> unitDtos = List.of(unitDto);

        Mockito.when(
                unitRepository.findByStatusAndIsDeleted(true, false)
        ).thenReturn(units);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(units),
                        Mockito.any(java.lang.reflect.Type.class)
                )
        ).thenReturn(unitDtos);

        List<UnitDto> response = unitService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Admin");
        unit.setStatus(false);

        Mockito.when(unitRepository.findByIdentifier("Admin"))
                .thenReturn(unit);

        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        unitService.changeStatus("Admin", true);

        Assertions.assertTrue(unit.getStatus());

        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void findAllWithSpecificationTest() {

        Unit unit = new Unit();
        unit.setIdentifier("Admin");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("Admin");

        List<Unit> units = List.of(unit);
        List<UnitDto> unitDtos = List.of(unitDto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Unit> page = new PageImpl<>(units, pageable, units.size());

        @SuppressWarnings("unchecked")
        Specification<Unit> specification = Mockito.mock(Specification.class);

        Mockito.when(unitRepository.findAll(
                Mockito.eq(specification),
                Mockito.any(Pageable.class)
        )).thenReturn(page);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(unitDtos);

        PaginatedResponseDto<UnitDto> response =
                unitService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals("Admin", response.getItems().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());

        Mockito.verify(unitRepository).findAll(specification, pageable);
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(unitRepository.findByIdentifier("INVALID"))
                .thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> unitService.findByIdentifier("INVALID")
        );

        Assertions.assertEquals(
                "Unit with identifier - INVALID not found",
                exception.getMessage()
        );
    }
}