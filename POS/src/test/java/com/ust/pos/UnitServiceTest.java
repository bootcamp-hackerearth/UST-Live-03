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

    @Test
    void saveTestSuccess() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Unit unit = new Unit();

        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Unit.class))
                .thenReturn(unit);

        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        UnitDto response = unitService.save(dto);

        Assertions.assertEquals("U1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(unitRepository).save(unit);
    }

    @Test
    void saveTestFailureWhenAlreadyExists() {
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

    @Test
    void updateTestSuccess() {
        UnitDto dto = new UnitDto();
        dto.setId(1L);
        dto.setIdentifier("U1");

        Unit existing = new Unit();
        existing.setIdentifier("U1");

        Mockito.when(unitRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        UnitDto response = unitService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper)
                .map(dto, existing);

        Mockito.verify(unitRepository)
                .save(existing);
    }

    @Test
    void updateTestFailureWhenIdNotFound() {
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
    void updateTestFailureWhenIdentifierExists() {
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
        Assertions.assertEquals(
                "Model Already Exists",
                response.getMessage()
        );
    }

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

    @Test
    void findAllTest() {
        List<Unit> units = List.of(new Unit());
        List<UnitDto> dtos = List.of(new UnitDto());

        Type listType = new TypeToken<List<UnitDto>>() {
        }.getType();

        Mockito.when(unitRepository.findByDeletedFalse())
                .thenReturn(units);

        Mockito.when(modelMapper.map(units, listType))
                .thenReturn(dtos);

        List<UnitDto> response = unitService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {
        Unit unit = new Unit();
        unit.setStatus(false);

        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1"))
                .thenReturn(unit);

        unitService.toggleStatus("U1");

        Assertions.assertTrue(unit.isStatus());

        Mockito.verify(unitRepository)
                .save(unit);
    }

    @Test
    void deleteTest() {
        Unit unit = new Unit();

        Mockito.when(unitRepository.findByIdentifierAndDeletedFalse("U1"))
                .thenReturn(unit);

        unitService.delete("U1");

        Assertions.assertTrue(unit.isDeleted());

        Mockito.verify(unitRepository)
                .save(unit);
    }

    @Test
    void findAllWithPaginationTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Unit unit = new Unit();
        unit.setIdentifier("U1");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Page<Unit> page = new PageImpl<>(List.of(unit));

        Mockito.when(unitRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        Page<UnitDto> response = unitService.findAll(pageable, null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals(
                "U1",
                response.getContent().get(0).getIdentifier()
        );

        Mockito.verify(unitRepository)
                .findByDeletedFalse(pageable);
    }

    @Test
    void findAllWithSearchTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Unit unit = new Unit();
        unit.setIdentifier("U1");
        unit.setDeleted(false);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("U1");

        Page<Unit> page = new PageImpl<>(List.of(unit));

        Mockito.when(
                unitRepository.findAll(
                        Mockito.<Specification<Unit>>any(),
                        Mockito.eq(pageable)
                )
        ).thenReturn(page);

        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        Page<UnitDto> response = unitService.findAll(pageable, "U1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals(
                "U1",
                response.getContent().get(0).getIdentifier()
        );

        Mockito.verify(unitRepository)
                .findAll(
                        Mockito.<Specification<Unit>>any(),
                        Mockito.eq(pageable)
                );
    }
}