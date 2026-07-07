package com.ust.pos;

import com.ust.pos.racks.service.RacksService;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Racks;
import com.ust.pos.model.RacksRepository;
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

@SpringBootTest
@ActiveProfiles("test")
class RacksServiceImplIT {

    @Autowired
    private RacksService racksService;

    @Autowired
    private RacksRepository racksRepository;

    @BeforeEach
    void cleanUp() {
        racksRepository.deleteAll();
    }

    @Test
    void save_shouldCreateRacks() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK001");
        dto.setStatus(true);
        RacksDto response = racksService.save(dto);
        Racks saved = racksRepository.findByIdentifier("RACK001");
        assertTrue(response.isSuccess());
        assertEquals("Racks created successfully", response.getMessage());
        assertNotNull(saved);
        assertEquals("RACK001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Racks racks = new Racks();
        racks.setIdentifier("RACK001");
        racks.setDeleted(false);
        racksRepository.save(racks);
        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK001");
        RacksDto response = racksService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Racks with identifier - RACK001 already exists",
                response.getMessage()
        );
    }

    @Test
    void save_shouldFailWhenPreviouslyDeleted() {
        Racks racks = new Racks();
        racks.setIdentifier("RACK001");
        racks.setDeleted(true);
        racksRepository.save(racks);
        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK001");
        RacksDto response = racksService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Racks with identifier - RACK001 was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void update_shouldModifyRacksDetails() {
        Racks racks = new Racks();
        racks.setIdentifier("RACK001");
        racks.setDeleted(false);
        racksRepository.save(racks);
        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK001");
        racksService.update(dto);
        Racks updated = racksRepository.findByIdentifier("RACK001");
        assertNotNull(updated);
        assertEquals("RACK001", updated.getIdentifier());
    }

    @Test
    void update_shouldFailWhenNotFound() {
        RacksDto dto = new RacksDto();
        dto.setIdentifier("RACK_MISSING");
        RacksDto response = racksService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Racks with identifier - RACK_MISSING not found",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifier_shouldReturnRacks() {
        Racks racks = new Racks();
        racks.setIdentifier("RACK001");
        racksRepository.save(racks);
        RacksDto result = racksService.findByIdentifier("RACK001");
        assertNotNull(result);
        assertEquals("RACK001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowWhenNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> racksService.findByIdentifier("RACK_MISSING"));
    }

    @Test
    void toggleStatus_shouldToggleValue() {
        Racks racks = new Racks();
        racks.setIdentifier("RACK001");
        racks.setStatus(true);
        racksRepository.save(racks);
        racksService.toggleStatus("RACK001");
        Racks updated = racksRepository.findByIdentifier("RACK001");
        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {
        Racks racks = new Racks();
        racks.setIdentifier("RACK001");
        racks.setDeleted(false);
        racksRepository.save(racks);
        boolean result = racksService.delete("RACK001");
        Racks deleted = racksRepository.findByIdentifier("RACK001");
        assertTrue(result);
        assertTrue(deleted.isDeleted());
    }

    @Test
    void delete_shouldReturnFalseWhenNotFound() {
        boolean result = racksService.delete("RACK_MISSING");
        assertFalse(result);
    }

    @Test
    void findAll_pageable_shouldReturnOnlyNonDeletedRacks() {
        Racks active = new Racks();
        active.setIdentifier("RACK001");
        active.setDeleted(false);
        racksRepository.save(active);
        Racks deleted = new Racks();
        deleted.setIdentifier("RACK002");
        deleted.setDeleted(true);
        racksRepository.save(deleted);
        Pageable pageable = PageRequest.of(0, 10);
        WsDto<RacksDto> result = racksService.findAll(pageable);
        assertEquals(1, result.getTotalRecords());
        assertEquals("RACK001", result.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAll_specification_shouldReturnFilteredResults() {
        Racks racks = new Racks();
        racks.setIdentifier("RACK001");
        racks.setDeleted(false);
        racksRepository.save(racks);
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Racks> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        WsDto<RacksDto> result = racksService.findAll(spec, pageable, "RACK001");
        assertEquals(1, result.getTotalRecords());
        assertEquals("RACK001", result.getKeyword());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedRacks() {
        Racks validRack = new Racks();
        validRack.setIdentifier("RACK001");
        validRack.setStatus(true);
        validRack.setDeleted(false);
        racksRepository.save(validRack);
        Racks inactiveRack = new Racks();
        inactiveRack.setIdentifier("RACK002");
        inactiveRack.setStatus(false);
        inactiveRack.setDeleted(false);
        racksRepository.save(inactiveRack);
        List<RacksDto> result = racksService.findIfTrue();
        assertEquals(1, result.size());
        assertEquals("RACK001", result.get(0).getIdentifier());
    }
}