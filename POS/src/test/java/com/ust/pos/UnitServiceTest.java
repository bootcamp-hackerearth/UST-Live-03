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
import org.springframework.data.jpa.domain.Specification;

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

    // SAVE

    @Test
    void saveTest_Success() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Unit entity = new Unit();

        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1"))
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

        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1"))
                .thenReturn(new Unit());

        UnitDto response = unitService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(unitRepository, Mockito.never())
                .save(Mockito.any());
    }

    // UPDATE

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
        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("NEW"))
                .thenReturn(new Unit());

        UnitDto response = unitService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Model Already Exists", response.getMessage());
    }

    // FIND BY IDENTIFIER

    @Test
    void findByIdentifierTest() {
        Unit unit = new Unit();
        unit.setIdentifier("U1");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1"))
                .thenReturn(unit);
        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        UnitDto response = unitService.findByIdentifier("U1");

        Assertions.assertEquals("U1", response.getIdentifier());
    }

    // FIND ALL

    @Test
    void findAllTest() {
        List<Unit> entities = List.of(new Unit());
        List<UnitDto> dtos = List.of(new UnitDto());

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();

        Mockito.when(unitRepository.findAll())
                .thenReturn(entities);
        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);

        List<UnitDto> response = unitService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    // UPDATE STATUS ONLY

    @Test
    void updateStatusOnlyTest() {
        Unit unit = new Unit();
        unit.setStatus(false);

        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1"))
                .thenReturn(unit);
        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        unitService.updateStatusOnly("U1", true);

        Assertions.assertTrue(unit.getStatus());
        Mockito.verify(unitRepository).save(unit);
    }

    // DELETE

    @Test
    void deleteTest() {

        Unit unit = new Unit();
        unit.setIdentifier("U1");
        unit.setDeleted(false);

        Mockito.when(
                        unitRepository.findByIdentifierAndDeletedFalse("U1"))
                .thenReturn(unit);

        unitService.delete("U1");

        Assertions.assertTrue(unit.isDeleted());

        Mockito.verify(unitRepository)
                .findByIdentifierAndDeletedFalse("U1");

        Mockito.verify(unitRepository)
                .save(unit);
    }

    @Test
    void findAll_WithPagination_ShouldReturnUnitDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        Unit unit = new Unit();
        unit.setIdentifier("U1");

        Page<Unit> unitPage =
                new PageImpl<>(List.of(unit));

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("U1");

        Mockito.when(
                        unitRepository.findByDeletedFalse(pageable))
                .thenReturn(unitPage);

        Mockito.when(
                        modelMapper.map(unit, UnitDto.class))
                .thenReturn(unitDto);

        Page<UnitDto> response =
                unitService.findAll("", pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getContent().size());

        Mockito.verify(unitRepository)
                .findByDeletedFalse(pageable);

        Mockito.verify(modelMapper)
                .map(unit, UnitDto.class);
    }

    @Test
    void findAll_WithSearch_ShouldReturnUnitDtos() {

        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        Unit unit = new Unit();
        unit.setIdentifier("U1");

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("U1");

        Page<Unit> unitPage =
                new PageImpl<>(List.of(unit));

        Mockito.when(
                unitRepository.findAll(
                        Mockito.<Specification<Unit>>any(),
                        Mockito.eq(pageable)
                )
        ).thenReturn(unitPage);

        Mockito.when(
                modelMapper.map(
                        unit,
                        UnitDto.class
                )
        ).thenReturn(unitDto);

        // Act
        Page<UnitDto> response =
                unitService.findAll(
                        "U1",
                        pageable
                );

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getContent().size()
        );

        Mockito.verify(unitRepository)
                .findAll(
                        Mockito.<Specification<Unit>>any(),
                        Mockito.eq(pageable)
                );

        Mockito.verify(modelMapper)
                .map(
                        unit,
                        UnitDto.class
                );

        Mockito.verify(unitRepository, Mockito.never())
                .findByDeletedFalse(Mockito.any(Pageable.class));
    }
}