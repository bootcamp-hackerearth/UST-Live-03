package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UnitServiceImplIT {

    @Autowired
    private UnitService unitService;

    @Autowired
    private UnitRepository unitRepository;

    @BeforeEach
    void cleanUp() {
        unitRepository.deleteAll();
    }

    @Test
    void save_shouldCreateUnit() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");
        dto.setStatus(true);

        unitService.save(dto);

        Unit saved =
                unitRepository.findByIdentifierAndDeletedFalse("UNIT001");

        assertNotNull(saved);
        assertEquals("UNIT001", saved.getIdentifier());
        assertTrue(saved.isStatus());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {

        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        unit.setDeleted(false);

        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT001");

        UnitDto response = unitService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Model - UNIT001 already exists",
                response.getMessage()
        );
    }

    @Test
    void update_shouldModifyUnitStatus() {

        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        unit.setStatus(true);
        unit.setDeleted(false);

        unit = unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setId(unit.getId());
        dto.setIdentifier("UNIT001");
        dto.setStatus(false);

        UnitDto response = unitService.update(dto);

        Unit updated =
                unitRepository.findByIdentifierAndDeletedFalse("UNIT001");

        assertTrue(response.isSuccess());
        assertNotNull(updated);
        assertFalse(updated.isStatus());
    }

    @Test
    void update_shouldFailWhenUnitNotFound() {

        UnitDto dto = new UnitDto();
        dto.setId(999L);
        dto.setIdentifier("UNIT001");

        UnitDto response = unitService.update(dto);

        assertFalse(response.isSuccess());
    }

    @Test
    void update_shouldFailWhenDuplicateIdentifierExists() {

        Unit unit1 = new Unit();
        unit1.setIdentifier("UNIT001");
        unit1.setDeleted(false);
        unit1 = unitRepository.save(unit1);

        Unit unit2 = new Unit();
        unit2.setIdentifier("UNIT002");
        unit2.setDeleted(false);
        unitRepository.save(unit2);

        UnitDto dto = new UnitDto();
        dto.setId(unit1.getId());
        dto.setIdentifier("UNIT002");

        UnitDto response = unitService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals("Model Already Exists", response.getMessage());
    }

    @Test
    void findByIdentifier_shouldReturnUnit() {

        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        unit.setDeleted(false);

        unitRepository.save(unit);

        UnitDto result =
                unitService.findByIdentifier("UNIT001");

        assertNotNull(result);
        assertEquals("UNIT001", result.getIdentifier());
    }

    @Test
    void toggleStatus_shouldToggleValue() {

        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        unit.setStatus(true);
        unit.setDeleted(false);

        unitRepository.save(unit);

        unitService.toggleStatus("UNIT001");

        Unit updated =
                unitRepository.findByIdentifierAndDeletedFalse("UNIT001");

        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {

        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        unit.setDeleted(false);

        unitRepository.save(unit);

        unitService.delete("UNIT001");

        Unit deleted =
                unitRepository.findByIdentifierAndDeletedFalse("UNIT001");

        assertNull(deleted);

        Unit softDeleted =
                unitRepository.findById(unit.getId()).orElse(null);

        assertNotNull(softDeleted);
        assertTrue(softDeleted.isDeleted());
    }

    @Test
    void findAll_shouldReturnOnlyNonDeletedUnits() {

        Unit active = new Unit();
        active.setIdentifier("UNIT001");
        active.setDeleted(false);

        unitRepository.save(active);

        Unit deleted = new Unit();
        deleted.setIdentifier("UNIT002");
        deleted.setDeleted(true);

        unitRepository.save(deleted);

        Page<UnitDto> result =
                unitService.findAll(PageRequest.of(0, 10), "");

        assertEquals(1, result.getTotalElements());
        assertEquals("UNIT001", result.getContent().get(0).getIdentifier());
    }

    @Test
    void findAll_withSearch_shouldReturnMatchingUnits() {

        Unit unit = new Unit();
        unit.setIdentifier("UNIT001");
        unit.setDeleted(false);

        unitRepository.save(unit);

        Page<UnitDto> result =
                unitService.findAll(PageRequest.of(0, 10), "UNIT001");

        assertEquals(1, result.getTotalElements());
        assertEquals("UNIT001", result.getContent().get(0).getIdentifier());
    }

    @Test
    void findAll_shouldReturnAllActiveUnits() {

        Unit unit1 = new Unit();
        unit1.setIdentifier("UNIT001");
        unit1.setDeleted(false);

        unitRepository.save(unit1);

        Unit unit2 = new Unit();
        unit2.setIdentifier("UNIT002");
        unit2.setDeleted(false);

        unitRepository.save(unit2);

        List<UnitDto> result = unitService.findAll();

        assertEquals(2, result.size());
    }
}