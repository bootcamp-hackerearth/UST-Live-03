package com.ust.pos;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.UnitDto;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class UnitServiceImplIntegrationTest {

    @Autowired
    private UnitServiceImpl unitService;

    @Autowired
    private UnitRepository unitRepository;

    @BeforeEach
    void setUp() {
        unitRepository.deleteAll();

        Unit unit = new Unit();
        unit.setIdentifier("KG");
        unit.setDescription("Kilogram");
        unit.setStatus(true);
        unit.setIsDeleted(false);

        unitRepository.save(unit);
    }

    @Test
    void testSaveSuccess() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("LTR");
        dto.setDescription("Litre");
        dto.setStatus(true);

        UnitDto response = unitService.save(dto);

        assertTrue(response.isSuccess());

        Unit saved = unitRepository.findByIdentifier("LTR");

        assertNotNull(saved);
        assertEquals("LTR", saved.getIdentifier());
        assertEquals("Litre", saved.getDescription());
        assertTrue(saved.getStatus());
        assertFalse(saved.getIsDeleted());
    }

    @Test
    void testSaveAlreadyExists() {

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
    void testSaveDeletedUnit() {

        Unit unit = unitRepository.findByIdentifier("KG");
        unit.setIsDeleted(true);
        unitRepository.save(unit);

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");

        UnitDto response = unitService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Unit with identifier - KG was deleted. Contact admin for further support or try with a different identifier.",
                response.getMessage()
        );
    }

    @Test
    void testUpdateSuccess() {

        UnitDto dto = new UnitDto();
        dto.setIdentifier("KG");
        dto.setDescription("Updated Kilogram");
        dto.setStatus(false);

        UnitDto response = unitService.update(dto);

        assertTrue(response.isSuccess());

        Unit updated = unitRepository.findByIdentifier("KG");

        assertEquals("Updated Kilogram", updated.getDescription());
        assertFalse(updated.getStatus());
    }

    @Test
    void testUpdateNotFound() {

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
    void testDeleteSuccess() {

        UnitDto response = unitService.delete("KG");

        assertTrue(response.isSuccess());
        assertEquals("Unit deleted successfully", response.getMessage());

        Unit deleted = unitRepository.findByIdentifier("KG");

        assertTrue(deleted.getIsDeleted());
        assertFalse(deleted.getStatus());
    }

    @Test
    void testDeleteNotFound() {

        UnitDto response = unitService.delete("UNKNOWN");

        assertFalse(response.isSuccess());
        assertEquals(
                "Unit with identifier - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void testFindByIdentifierSuccess() {

        UnitDto dto = unitService.findByIdentifier("KG");

        assertNotNull(dto);
        assertEquals("KG", dto.getIdentifier());
        assertEquals("Kilogram", dto.getDescription());
        assertTrue(dto.getStatus());
    }

    @Test
    void testFindByIdentifierNotFound() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> unitService.findByIdentifier("UNKNOWN")
        );
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        PaginatedResponseDto<UnitDto> response =
                unitService.findAll(pageable);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(1, response.getTotalRecords());
        assertEquals(1, response.getTotalPages());
        assertEquals(10, response.getSizePerPage());
        assertEquals(0, response.getPage());
    }

    @Test
    void testFindAllActive() {

        List<UnitDto> units = unitService.findAllActive();

        assertEquals(1, units.size());

        UnitDto dto = units.get(0);

        assertEquals("KG", dto.getIdentifier());
        assertTrue(dto.getStatus());
        assertFalse(dto.getIsDeleted());
    }

    @Test
    void testFindAllActiveNoResults() {

        Unit unit = unitRepository.findByIdentifier("KG");
        unit.setStatus(false);
        unitRepository.save(unit);

        List<UnitDto> units = unitService.findAllActive();

        assertTrue(units.isEmpty());
    }

    @Test
    void testChangeStatusToFalse() {

        unitService.changeStatus("KG", false);

        Unit unit = unitRepository.findByIdentifier("KG");

        assertFalse(unit.getStatus());
    }

    @Test
    void testChangeStatusToTrue() {

        Unit unit = unitRepository.findByIdentifier("KG");
        unit.setStatus(false);
        unitRepository.save(unit);

        unitService.changeStatus("KG", true);

        Unit updated = unitRepository.findByIdentifier("KG");

        assertTrue(updated.getStatus());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Unit> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "KG");

        PaginatedResponseDto<UnitDto> response =
                unitService.findAll(specification, pageable);

        assertEquals(1, response.getItems().size());
        assertEquals(
                "KG",
                response.getItems().get(0).getIdentifier()
        );
    }

    @Test
    void testFindAllWithSpecificationNoResults() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Unit> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "XYZ");

        PaginatedResponseDto<UnitDto> response =
                unitService.findAll(specification, pageable);

        assertEquals(0, response.getItems().size());
        assertEquals(0, response.getTotalRecords());
        assertEquals(0, response.getTotalPages());
    }
}