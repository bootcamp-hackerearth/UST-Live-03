package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UnitServiceIntegrationTest {

    @Autowired
    private UnitService unitService;

    @Autowired
    private UnitRepository unitRepository;

    @BeforeEach
    void setUp() {
        unitRepository.deleteAll();
    }

    @Test
    void save_ShouldCreateUnit() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");
        dto.setStatus(true);

        UnitDto result = unitService.save(dto);

        Unit savedUnit = unitRepository.findByIdentifier("UNIT001");

        assertNotNull(savedUnit);
        assertEquals("UNIT001", savedUnit.getIdentifier());
        assertFalse(savedUnit.getDeleted());
    }

    @Test
    void save_ShouldNotCreateDuplicateUnit() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        unit.setStatus(true);
        unit.setDeleted(false);
        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");

        UnitDto result = unitService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Unit with identifier - UNIT001 already exists",
                result.getMessage()
        );
    }

    @Test
    void save_ShouldNotCreateDeletedUnitAgain() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        unit.setStatus(true);
        unit.setDeleted(true);
        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");

        UnitDto result = unitService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Unit with identifier - UNIT001 was deleted and cannot be created again.",
                result.getMessage()
        );
    }

    @Test
    void update_ShouldUpdateExistingUnit() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        unit.setStatus(true);
        unit.setDeleted(false);
        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");
        dto.setStatus(true);

        UnitDto result = unitService.update(dto);

        Unit updatedUnit = unitRepository.findByIdentifier("UNIT001");

        assertNotNull(updatedUnit);
    }

    @Test
    void update_ShouldReturnFailure_WhenUnitNotFound() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT999");

        UnitDto result = unitService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Unit with identifier - UNIT999 not found",
                result.getMessage()
        );
    }

    @Test
    void delete_ShouldSoftDeleteUnit() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        unit.setStatus(true);
        unit.setDeleted(false);
        unitRepository.save(unit);

        unitService.delete("UNIT001");

        Unit deletedUnit = unitRepository.findByIdentifier("UNIT001");

        assertNotNull(deletedUnit);
        assertTrue(deletedUnit.getDeleted());
    }

    @Test
    void findByIdentifier_ShouldReturnUnitDto() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        unit.setStatus(true);
        unit.setDeleted(false);
        unitRepository.save(unit);

        UnitDto result = unitService.findByIdentifier("UNIT001");

        assertNotNull(result);
        assertEquals("UNIT001", result.getIdentifier());
    }

    @Test
    void findAll_ShouldReturnOnlyNonDeletedUnits() {
        Unit unit1 = new Unit();
        unit1.setIdentifier("UNIT001");
        unit1.setStatus(true);
        unit1.setDeleted(false);
        unitRepository.save(unit1);

        Unit unit2 = new Unit();
        unit2.setIdentifier("UNIT002");
        unit2.setStatus(true);
        unit2.setDeleted(true);
        unitRepository.save(unit2);

        WsDto<UnitDto> result =
                unitService.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getDtoList().size());
        assertEquals("UNIT001", result.getDtoList().get(0).getIdentifier());
    }

    @Test
    void toggleStatus_ShouldChangeUnitStatus() {
        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        unit.setStatus(true);
        unit.setDeleted(false);
        unitRepository.save(unit);

        UnitDto result = unitService.toggleStatus("UNIT001");

        Unit updatedUnit = unitRepository.findByIdentifier("UNIT001");

        assertNotNull(result);
        assertFalse(updatedUnit.isStatus());
    }

    @Test
    void findActiveUnits_ShouldReturnOnlyActiveUnits() {
        Unit activeUnit = new Unit();
        activeUnit.setIdentifier("UNIT001");
        activeUnit.setStatus(true);
        activeUnit.setDeleted(false);
        unitRepository.save(activeUnit);

        Unit inactiveUnit = new Unit();
        inactiveUnit.setIdentifier("UNIT002");
        inactiveUnit.setStatus(false);
        inactiveUnit.setDeleted(false);
        unitRepository.save(inactiveUnit);

        List<UnitDto> result = unitService.findActiveUnits();

        assertEquals(1, result.size());
        assertEquals("UNIT001", result.get(0).getIdentifier());
    }
}