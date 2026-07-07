package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UnitServiceIT {

    @Autowired
    private UnitService unitService;

    @Autowired
    private UnitRepository unitRepository;

    private Unit testUnit;

    @BeforeEach
    void setUp() {
        testUnit = new Unit();
        testUnit.setIdentifier("KG");
        testUnit.setStatus(true);
        testUnit.setDeleted(false);
    }

    @Test
    void saveSuccessTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("LITER");

        UnitDto response = unitService.save(unitDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("LITER", response.getIdentifier());

        Unit savedUnit = unitRepository.findByIdentifier("LITER");
        Assertions.assertNotNull(savedUnit);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        unitRepository.save(testUnit);

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("KG");

        UnitDto response = unitService.save(unitDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit with identifier - KG already exists", response.getMessage());
    }

    @Test
    void saveFailureAlreadyDeletedTest() {
        testUnit.setDeleted(true);
        unitRepository.save(testUnit);

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("KG");

        UnitDto response = unitService.save(unitDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit with identifier - KG was deleted , Please Contact the Administrator to add.", response.getMessage());
    }

    @Test
    void updateSuccessTest() {
        unitRepository.save(testUnit);

        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("KG");
        unitDto.setStatus(false);

        UnitDto response = unitService.update(unitDto);

        Assertions.assertNotNull(response);

        Unit updatedUnit = unitRepository.findByIdentifier("KG");
        Assertions.assertFalse(updatedUnit.isStatus());
    }

    @Test
    void updateFailureTest() {
        UnitDto unitDto = new UnitDto();
        unitDto.setIdentifier("NON_EXISTENT");

        UnitDto response = unitService.update(unitDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Unit with identifier - NON_EXISTENT not found", response.getMessage());
    }

    @Test
    void deleteSuccessTest() {
        unitRepository.save(testUnit);

        unitService.delete("KG");

        Unit deletedUnit = unitRepository.findByIdentifier("KG");
        Assertions.assertNotNull(deletedUnit);
        Assertions.assertTrue(deletedUnit.isDeleted());
    }

    @Test
    void findAllSuccessTest() {
        unitRepository.save(testUnit);
        Pageable pageable = PageRequest.of(0, 10);

        WsDto<UnitDto> result = unitService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getDtoList().isEmpty());
    }

    @Test
    void findByIdentifierSuccessTest() {
        unitRepository.save(testUnit);

        UnitDto response = unitService.findByIdentifier("KG");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("KG", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            unitService.findByIdentifier("ABSENT");
        });
    }

    @Test
    void findAllActiveSuccessTest() {
        unitRepository.save(testUnit);

        List<UnitDto> result = unitService.findAllActive();

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void toggleStatusSuccessTest() {
        testUnit.setStatus(false);
        unitRepository.save(testUnit);

        unitService.toggleStatus("KG");

        Unit toggledUnit = unitRepository.findByIdentifier("KG");
        Assertions.assertTrue(toggledUnit.isStatus());
    }

    @Test
    void toggleStatusUnitNotFoundTest() {
        Assertions.assertDoesNotThrow(() -> unitService.toggleStatus("NON_EXISTENT"));
    }

    @Test
    void findAllSpecificationSuccessTest() {
        unitRepository.save(testUnit);
        Pageable pageable = PageRequest.of(0, 10);

        Specification<Unit> specification = (root, query, cb) -> cb.conjunction();

        WsDto<UnitDto> result = unitService.findAll(specification, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.getDtoList().isEmpty());
    }
}