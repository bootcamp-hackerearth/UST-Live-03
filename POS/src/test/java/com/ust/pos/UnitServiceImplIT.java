package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.UnitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        dto.setIdentifier("UNT001");
        dto.setStatus(true);

        Unit saved = unitRepository.findByIdentifier("UNT001");

        assertNotNull(saved);
        assertEquals("UNT001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {

        Unit unit = new Unit();
        unit.setIdentifier("UNT001");
        unit.setDeleted(false);

        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNT001");

        UnitDto response = unitService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Unit with identifier - UNT001 already exists",
                response.getMessage()
        );
    }

    @Test
    void save_shouldFailWhenPreviouslyDeleted() {

        Unit unit = new Unit();
        unit.setIdentifier("UNT001");
        unit.setDeleted(true);

        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNT001");

        UnitDto response = unitService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Unit with identifier UNT001 was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateUnitDetails() {

        Unit unit = new Unit();
        unit.setIdentifier("UNT001");
        unit.setStatus(true);
        unit.setDeleted(false);

        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNT001");
        dto.setStatus(false);

        UnitDto response = unitService.update(dto);

        assertTrue(response.isSuccess());

        Unit updated = unitRepository.findByIdentifier("UNT001");

        assertFalse(updated.isStatus());
    }

    @Test
    void findByIdentifier_shouldReturnUnit() {

        Unit unit = new Unit();
        unit.setIdentifier("UNT001");

        unitRepository.save(unit);

        UnitDto result = unitService.findByIdentifier("UNT001");

        assertEquals("UNT001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {

        assertThrows(ResourceNotFoundException.class, () -> {
            unitService.findByIdentifier("NON-EXISTENT");
        });
    }

    @Test
    void toggleStatus_shouldToggleValue() {

        Unit unit = new Unit();
        unit.setIdentifier("UNT001");
        unit.setStatus(true);

        unitRepository.save(unit);

        unitService.toggleStatus("UNT001");

        Unit updated = unitRepository.findByIdentifier("UNT001");

        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {

        Unit unit = new Unit();
        unit.setIdentifier("UNT001");
        unit.setDeleted(false);

        unitRepository.save(unit);

        boolean isDeleted = unitService.delete("UNT001");

        assertTrue(isDeleted);

        Unit deleted = unitRepository.findByIdentifier("UNT001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void findAll_shouldReturnPaginatedData() {

        Unit unit1 = new Unit();
        unit1.setIdentifier("UNT001");
        unit1.setDeleted(false);
        unitRepository.save(unit1);

        Unit unit2 = new Unit();
        unit2.setIdentifier("UNT002");
        unit2.setDeleted(false);
        unitRepository.save(unit2);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<UnitDto> response = unitService.findAll(pageable);

        assertNotNull(response);
        assertEquals(2, response.getTotalRecords());
        assertEquals(2, response.getDtoList().size());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedRecords() {

        Unit activeUnit = new Unit();
        activeUnit.setIdentifier("UNT001");
        activeUnit.setStatus(true);
        activeUnit.setDeleted(false);
        unitRepository.save(activeUnit);

        Unit inactiveUnit = new Unit();
        inactiveUnit.setIdentifier("UNT002");
        inactiveUnit.setStatus(false);
        inactiveUnit.setDeleted(false);
        unitRepository.save(inactiveUnit);

        List<UnitDto> activeList = unitService.findIfTrue();

        assertEquals(1, activeList.size());
        assertEquals("UNT001", activeList.get(0).getIdentifier());
    }
}