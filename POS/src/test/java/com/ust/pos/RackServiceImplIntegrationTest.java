package com.ust.pos;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.impl.RackServiceImpl;
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
class RackServiceImplIntegrationTest {

    @Autowired
    private RackServiceImpl rackService;

    @Autowired
    private RackRepository rackRepository;

    @BeforeEach
    void setUp() {
        rackRepository.deleteAll();
        Rack rack = new Rack();
        rack.setIdentifier("RACK1");
        rack.setDescription("Test Rack");
        rack.setStatus(true);
        rack.setDeleted(false);
        rackRepository.save(rack);
    }

    @Test
    void testSaveSuccess() {
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK2");
        dto.setDescription("New Rack");
        dto.setStatus(true);
        RackDto response = rackService.save(dto);
        assertTrue(response.isSuccess());
        Rack saved = rackRepository.findByIdentifier("RACK2");
        assertNotNull(saved);
        assertEquals("RACK2", saved.getIdentifier());
        assertEquals("New Rack", saved.getDescription());
        assertTrue(saved.getStatus());
        assertFalse(saved.getDeleted());
    }

    @Test
    void testSaveAlreadyExists() {
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK1");
        RackDto response = rackService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Rack with identifier - RACK1 already exists",
                response.getMessage()
        );
    }

    @Test
    void testSaveDeletedRack() {
        Rack rack = rackRepository.findByIdentifier("RACK1");
        rack.setDeleted(true);
        rackRepository.save(rack);
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK1");
        RackDto response = rackService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Rack - RACK1 was deleted and cannot be recreated",
                response.getMessage()
        );
    }

    @Test
    void testUpdateSuccess() {
        RackDto dto = new RackDto();
        dto.setIdentifier("RACK1");
        dto.setDescription("Updated Rack");
        dto.setStatus(false);
        RackDto response = rackService.update(dto);
        assertTrue(response.isSuccess());
        Rack updated = rackRepository.findByIdentifier("RACK1");
        assertEquals("Updated Rack", updated.getDescription());
        assertFalse(updated.getStatus());
    }

    @Test
    void testUpdateNotFound() {
        RackDto dto = new RackDto();
        dto.setIdentifier("UNKNOWN");
        RackDto response = rackService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Rack with identifier - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void testDelete() {
        rackService.delete("RACK1");
        Rack deleted = rackRepository.findByIdentifier("RACK1");
        assertTrue(deleted.getDeleted());
    }

    @Test
    void testDeleteNotFound() {
        assertDoesNotThrow(() -> rackService.delete("UNKNOWN"));
    }

    @Test
    void testFindByIdentifierSuccess() {
        RackDto dto = rackService.findByIdentifier("RACK1");
        assertNotNull(dto);
        assertEquals("RACK1", dto.getIdentifier());
        assertEquals("Test Rack", dto.getDescription());
        assertTrue(dto.getStatus());
    }

    @Test
    void testFindByIdentifierNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> rackService.findByIdentifier("UNKNOWN")
        );
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        WsDto<RackDto> response = rackService.findAll(pageable);
        assertNotNull(response);
        assertEquals(1, response.getDtoList().size());
        assertEquals(1, response.getTotalRecords());
        assertEquals(1, response.getTotalPages());
        assertEquals(10, response.getSizePerPage());
        assertEquals(0, response.getPage());
    }

    @Test
    void testFindAllActive() {
        List<RackDto> racks = rackService.findAllActive();
        assertEquals(1, racks.size());
        RackDto dto = racks.get(0);
        assertEquals("RACK1", dto.getIdentifier());
        assertEquals("Test Rack", dto.getDescription());
        assertTrue(dto.getStatus());
    }

    @Test
    void testFindAllActiveNoResults() {
        Rack rack = rackRepository.findByIdentifier("RACK1");
        rack.setStatus(false);
        rackRepository.save(rack);
        List<RackDto> racks = rackService.findAllActive();
        assertTrue(racks.isEmpty());
    }

    @Test
    void testUpdateStatusFalse() {
        rackService.updateStatus("RACK1", false);
        Rack rack = rackRepository.findByIdentifier("RACK1");
        assertFalse(rack.getStatus());
    }

    @Test
    void testUpdateStatusTrue() {
        Rack rack = rackRepository.findByIdentifier("RACK1");
        rack.setStatus(false);
        rackRepository.save(rack);
        rackService.updateStatus("RACK1", true);
        Rack updated = rackRepository.findByIdentifier("RACK1");
        assertTrue(updated.getStatus());
    }

    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Rack> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "RACK1");
        WsDto<RackDto> response =
                rackService.findAll(specification, pageable);
        assertEquals(1, response.getDtoList().size());
        assertEquals(
                "RACK1",
                response.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void testFindAllWithSpecificationNoResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Rack> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "XYZ");
        WsDto<RackDto> response =
                rackService.findAll(specification, pageable);
        assertEquals(0, response.getDtoList().size());
        assertEquals(0, response.getTotalRecords());
        assertEquals(0, response.getTotalPages());
    }
}