package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
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
class UnitServiceImplIT {

    @Autowired
    private UnitServiceImpl unitService;

    @Autowired
    private UnitRepository unitRepository;

    @BeforeEach
    void setup() {
        unitRepository.deleteAll();
    }

    @Test
    void save_ShouldCreateUnit() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        UnitDto response = unitService.save(dto);

        assertTrue(response.isSuccess());
        assertEquals("Unit created successfully", response.getMessage());

        Unit saved = unitRepository.findByIdentifier("KG");

        assertNotNull(saved);
        assertEquals("KG", saved.getIdentifier());
    }

    @Test
    void save_ShouldThrowException_WhenIdentifierIsNull() {

        UnitDto dto = new UnitDto();

        assertThrows(
                IllegalArgumentException.class,
                () -> unitService.save(dto)
        );
    }

    @Test
    void save_ShouldFail_WhenUnitAlreadyExists() {

        Unit unit = new Unit();
        unit.setIdentifier("KG");
        unit.setDeleted(false);

        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        UnitDto response = unitService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Unit with identifier - KG already exists",
                response.getMessage()
        );
    }

    @Test
    void save_ShouldFail_WhenDeletedUnitExists() {

        Unit unit = new Unit();
        unit.setIdentifier("KG");
        unit.setDeleted(true);

        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        UnitDto response = unitService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Unit with identifier - KG was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifier_ShouldReturnUnit() {

        Unit unit = new Unit();
        unit.setIdentifier("KG");

        unitRepository.save(unit);

        UnitDto response = unitService.findByIdentifier("KG");

        assertEquals("KG", response.getIdentifier());
    }

    @Test
    void findByIdentifier_ShouldThrowException_WhenNotFound() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> unitService.findByIdentifier("UNKNOWN")
        );
    }

    @Test
    void update_ShouldUpdateUnit() {

        Unit unit = new Unit();
        unit.setIdentifier("KG");
        unit.setStatus(true);
        unit.setDeleted(false);

        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        dto.setStatus(false);

        UnitDto response = unitService.update(dto);

        assertTrue(response.isSuccess());
        assertEquals("Unit updated successfully", response.getMessage());

        Unit updated = unitRepository.findByIdentifier("KG");

        assertFalse(updated.getStatus());
    }

    @Test
    void update_ShouldFail_WhenUnitNotFound() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNKNOWN");

        UnitDto response = unitService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Unit with identifier - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void update_ShouldFail_WhenUnitDeleted() {

        Unit unit = new Unit();
        unit.setIdentifier("KG");
        unit.setDeleted(true);

        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        UnitDto response = unitService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Unit with identifier - KG was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void delete_ShouldSoftDeleteUnit() {

        Unit unit = new Unit();
        unit.setIdentifier("KG");
        unit.setDeleted(false);

        unitRepository.save(unit);

        unitService.delete("KG");

        Unit deleted = unitRepository.findByIdentifier("KG");

        assertTrue(deleted.getDeleted());
    }

    @Test
    void toggleStatus_ShouldToggleStatus() {

        Unit unit = new Unit();
        unit.setIdentifier("KG");
        unit.setStatus(true);

        unitRepository.save(unit);

        UnitDto response = unitService.toggleStatus("KG");

        assertFalse(response.isStatus());

        Unit updated = unitRepository.findByIdentifier("KG");

        assertFalse(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldFail_WhenUnitNotFound() {

        UnitDto response = unitService.toggleStatus("UNKNOWN");

        assertFalse(response.isSuccess());
        assertEquals(
                "Unit with identifier - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void findIfTrue_ShouldReturnOnlyActiveUnits() {

        Unit active = new Unit();
        active.setIdentifier("KG");
        active.setStatus(true);
        active.setDeleted(false);

        Unit inactive = new Unit();
        inactive.setIdentifier("LTR");
        inactive.setStatus(false);
        inactive.setDeleted(false);

        unitRepository.save(active);
        unitRepository.save(inactive);

        List<UnitDto> result = unitService.findIfTrue();

        assertEquals(1, result.size());
        assertEquals("KG", result.get(0).getIdentifier());
    }

    @Test
    void findAll_ShouldReturnPagedUnits() {

        Unit unit1 = new Unit();
        unit1.setIdentifier("KG");
        unit1.setDeleted(false);

        Unit unit2 = new Unit();
        unit2.setIdentifier("LTR");
        unit2.setDeleted(false);

        unitRepository.save(unit1);
        unitRepository.save(unit2);

        WsDto<UnitDto> result =
                unitService.findAll(PageRequest.of(0, 10));

        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getDtoList().size());
    }
}
