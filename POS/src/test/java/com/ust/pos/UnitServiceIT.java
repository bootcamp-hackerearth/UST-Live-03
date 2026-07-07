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
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UnitServiceIT {

    @Autowired
    private UnitServiceImpl unitService;

    @Autowired
    private UnitRepository unitRepository;

    @BeforeEach
    void setUp() {
        unitRepository.deleteAll();
    }

    @Test
    void saveTest() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        dto.setDescription("Kilogram");

        UnitDto result = unitService.save(dto);

        assertEquals("KG", result.getIdentifier());

        Unit saved = unitRepository.findByIdentifier("KG");

        assertNotNull(saved);
        assertEquals("Kilogram", saved.getDescription());
    }

    @Test
    void findByIdentifierTest() {

        Unit unit = new Unit();
        unit.setIdentifier("LTR");
        unitRepository.save(unit);

        UnitDto result = unitService.findByIdentifier("LTR");

        assertNotNull(result);
        assertEquals("LTR", result.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> unitService.findByIdentifier("INVALID")
        );
    }

    @Test
    void updateTest() {

        Unit unit = new Unit();
        unit.setIdentifier("PCS");
        unit.setDescription("Old");
        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("PCS");
        dto.setDescription("Updated");

        unitService.update(dto);

        Unit updated = unitRepository.findByIdentifier("PCS");

        assertEquals("Updated", updated.getDescription());
    }

    @Test
    void deleteTest() {

        Unit unit = new Unit();
        unit.setIdentifier("BOX");
        unitRepository.save(unit);

        unitService.delete("BOX");

        Unit deleted = unitRepository.findByIdentifier("BOX");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void toggleStatusTest() {

        Unit unit = new Unit();
        unit.setIdentifier("TEST");
        unit.setStatus(false);

        unitRepository.save(unit);

        unitService.toggleStatus("TEST");

        Unit updated = unitRepository.findByIdentifier("TEST");

        assertTrue(updated.isStatus());
    }

    @Test
    void findActiveUnitTest() {

        Unit active = new Unit();
        active.setIdentifier("ACTIVE");
        active.setStatus(true);

        Unit inactive = new Unit();
        inactive.setIdentifier("INACTIVE");
        inactive.setStatus(false);

        unitRepository.save(active);
        unitRepository.save(inactive);

        List<Unit> result = unitService.findActiveUnit();

        assertEquals(1, result.size());
        assertEquals("ACTIVE", result.get(0).getIdentifier());
    }

    @Test
    void findAllTest() {

        Unit unit = new Unit();
        unit.setIdentifier("KG");
        unitRepository.save(unit);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<UnitDto> result = unitService.findAll(pageable);

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
    }
}