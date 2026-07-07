package com.ust.pos;

import com.ust.pos.unit.service.UnitService;
import com.ust.pos.dto.UnitDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
        dto.setIdentifier("UN001");
        dto.setStatus(true);

        UnitDto response = unitService.save(dto);

        Unit saved =
                unitRepository.findByIdentifier("UN001");

        assertNotNull(saved);
        assertEquals("UN001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {

        Unit unit = new Unit();
        unit.setIdentifier("UN001");
        unit.setDeleted(false);

        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UN001");

        UnitDto response =
                unitService.save(dto);

        assertFalse(response.isSuccess());

        assertEquals(
                "Unit UN001 already exists",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateSuperUnit() {

        Unit unit = new Unit();
        unit.setIdentifier("UN001");
        unit.setDeleted(false);

        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UN001");

        UnitDto response = unitService.update(dto);

        assertTrue(response.isSuccess());

        Unit updated =
                unitRepository.findByIdentifier("UN001");
    }

    @Test
    void findByIdentifier_shouldReturnUnit() {

        Unit unit = new Unit();
        unit.setIdentifier("UN001");

        unitRepository.save(unit);

        UnitDto result =
                unitService.findByIdentifier("UN001");

        assertEquals("UN001", result.getIdentifier());
    }

    @Test
    void updateStatus_shouldUpdateValue() {

        Unit unit = new Unit();
        unit.setIdentifier("UN001");
        unit.setStatus(true);

        unitRepository.save(unit);


        UnitDto response =
                unitService.updateStatus("UN001", false);


        Unit updated =
                unitRepository.findByIdentifier("UN001");


        assertTrue(response.isSuccess());

        assertFalse(updated.isStatus());
    }

    @Test
    void deleteByIdentifier_shouldSoftDelete() {

        Unit unit = new Unit();
        unit.setIdentifier("UN001");
        unit.setDeleted(false);

        unitRepository.save(unit);

        unitService.delete("UN001");

        Unit deleted =
                unitRepository.findByIdentifier("UN001");

        assertTrue(deleted.isDeleted());
    }
}