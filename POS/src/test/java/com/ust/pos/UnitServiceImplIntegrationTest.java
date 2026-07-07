package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.models.Unit;
import com.ust.pos.models.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
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
class UnitServiceImplIntegrationTest {

    @Autowired
    private UnitServiceImpl unitService;

    @Autowired
    private UnitRepository unitRepository;

    @BeforeEach
    void setUp() {
        unitRepository.deleteAll();
    }

    private Unit createUnit(
            String identifier,
            String description,
            Boolean status,
            Boolean deleted) {
        Unit unit = new Unit();
        unit.setIdentifier(identifier);
        unit.setDescription(description);
        unit.setStatus(status);
        unit.setDeleted(deleted);
        return unitRepository.saveAndFlush(unit);
    }

    @Test
    void save_ShouldCreateUnitSuccessfully() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");
        dto.setDescription("Kilogram");
        unitService.save(dto);
        Unit saved = unitRepository.findByIdentifier("UNIT001");
        assertNotNull(saved);
        assertEquals("UNIT001", saved.getIdentifier());
        assertEquals("Kilogram", saved.getDescription());
    }

    @Test
    void save_ShouldFail_WhenUnitAlreadyExists() {
        createUnit("UNIT001", "Kilogram", true, false);
        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");
        UnitDto result = unitService.save(dto);
        assertFalse(result.isSuccess());
        assertEquals(" Unit with identifier - UNIT001 already exists", result.getMessage());
    }

    @Test
    void save_ShouldFail_WhenDeletedUnitExists() {
        createUnit("UNIT001", "Kilogram", true, true);
        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");
        UnitDto result = unitService.save(dto);
        assertFalse(result.isSuccess());
        assertEquals(" Unit with identifier - UNIT001 was deleted and cannot be created again.", result.getMessage());
    }

    @Test
    void update_ShouldUpdateUnitSuccessfully() {
        createUnit("UNIT001", "Kilogram", true, false);
        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");
        dto.setDescription("Gram");
        unitService.update(dto);
        Unit updated = unitRepository.findByIdentifierAndDeletedFalse("UNIT001");
        assertNotNull(updated);
        assertEquals("Gram", updated.getDescription());
    }

    @Test
    void update_ShouldReturnNotFound_WhenUnitDoesNotExist() {
        UnitDto dto = new UnitDto();
        dto.setIdentifier("INVALID");
        UnitDto result = unitService.update(dto);
        assertFalse(result.isSuccess());
        assertEquals("Unit with identifier - INVALID not found", result.getMessage());
    }

    @Test
    void findByIdentifier_ShouldReturnUnit() {
        createUnit("UNIT001", "Kilogram", true, false);
        UnitDto result = unitService.findByIdentifier("UNIT001");
        assertNotNull(result);
        assertEquals("UNIT001", result.getIdentifier());
        assertEquals("Kilogram", result.getDescription());
    }

    @Test
    void findByIdentifier_ShouldThrowException_WhenUnitNotFound() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> unitService.findByIdentifier("INVALID"));
        assertEquals(
                "Unit with identifier 'INVALID' not found",
                exception.getMessage());
    }

    @Test
    void delete_ShouldSoftDeleteUnit() {
        createUnit("UNIT001", "Kilogram", true, false);
        unitService.delete("UNIT001");
        Unit unit = unitRepository.findByIdentifier("UNIT001");
        assertNotNull(unit);
        assertTrue(unit.getDeleted());
    }

    @Test
    void toggleStatus_ShouldDisableUnit() {
        createUnit("UNIT001", "Kilogram", true, false);
        UnitDto result = unitService.toggleStatus("UNIT001");
        assertFalse(result.getStatus());
        Unit updated = unitRepository.findByIdentifier("UNIT001");
        assertFalse(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldEnableUnit() {
        createUnit("UNIT001", "Kilogram", false, false);
        UnitDto result = unitService.toggleStatus("UNIT001");
        assertTrue(result.getStatus());
        Unit updated = unitRepository.findByIdentifier("UNIT001");
        assertTrue(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldThrowException_WhenUnitNotFound() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> unitService.toggleStatus("INVALID"));
        assertEquals("Unit not found with identifier: INVALID", exception.getMessage());
    }

    @Test
    void findAll_ShouldReturnOnlyNonDeletedUnits() {
        createUnit("UNIT001", "Kilogram", true, false);
        createUnit("UNIT002", "Gram", true, false);
        WsDto<UnitDto> result = unitService.findAll(PageRequest.of(0, 10));
        assertNotNull(result);
        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getDtoList().size());
    }

    @Test
    void findAllActive_ShouldReturnOnlyActiveUnits() {
        createUnit("UNIT001", "Kilogram", true, false);
        createUnit("UNIT002", "Gram", false, false);
        List<UnitDto> result = unitService.findAllActive();
        assertEquals(1, result.size());
        assertEquals("UNIT001", result.get(0).getIdentifier());
    }
}