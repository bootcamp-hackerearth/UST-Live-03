package com.ust.pos;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Racks;
import com.ust.pos.model.RacksRepository;
import com.ust.pos.racks.service.RacksService;
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
        dto.setIdentifier("RCK001");
        dto.setStatus(true);

        Racks saved = racksRepository.findByIdentifier("RCK001");

        assertNotNull(saved);
        assertEquals("RCK001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Racks racks = new Racks();
        racks.setIdentifier("RCK001");
        racks.setDeleted(false);
        racksRepository.save(racks);

        RacksDto dto = new RacksDto();
        dto.setIdentifier("RCK001");

        RacksDto response = racksService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Racks with identifier - RCK001 already exists",
                response.getMessage()
        );
    }

    @Test
    void save_shouldFailWhenPreviouslyDeleted() {
        Racks racks = new Racks();
        racks.setIdentifier("RCK001");
        racks.setDeleted(true);
        racksRepository.save(racks);

        RacksDto dto = new RacksDto();
        dto.setIdentifier("RCK001");

        RacksDto response = racksService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Racks with identifier RCK001 was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateRacksDetails() {
        Racks racks = new Racks();
        racks.setIdentifier("RCK001");
        racks.setStatus(true);
        racks.setDeleted(false);
        racksRepository.save(racks);

        RacksDto dto = new RacksDto();
        dto.setIdentifier("RCK001");
        dto.setStatus(false);

        RacksDto response = racksService.update(dto);

        assertTrue(response.isSuccess());

        Racks updated = racksRepository.findByIdentifier("RCK001");

        assertFalse(updated.isStatus());
    }

    @Test
    void findByIdentifier_shouldReturnRacks() {
        Racks racks = new Racks();
        racks.setIdentifier("RCK001");
        racksRepository.save(racks);

        RacksDto result = racksService.findByIdentifier("RCK001");

        assertEquals("RCK001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            racksService.findByIdentifier("NON-EXISTENT");
        });
    }

    @Test
    void toggleStatus_shouldToggleValue() {
        Racks racks = new Racks();
        racks.setIdentifier("RCK001");
        racks.setStatus(true);
        racksRepository.save(racks);

        racksService.toggleStatus("RCK001");

        Racks updated = racksRepository.findByIdentifier("RCK001");

        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {
        Racks racks = new Racks();
        racks.setIdentifier("RCK001");
        racks.setDeleted(false);
        racksRepository.save(racks);

        boolean isDeleted = racksService.delete("RCK001");

        assertTrue(isDeleted);

        Racks deleted = racksRepository.findByIdentifier("RCK001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void findAll_shouldReturnPaginatedData() {
        Racks racks1 = new Racks();
        racks1.setIdentifier("RCK001");
        racks1.setDeleted(false);
        racksRepository.save(racks1);

        Racks racks2 = new Racks();
        racks2.setIdentifier("RCK002");
        racks2.setDeleted(false);
        racksRepository.save(racks2);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<RacksDto> response = racksService.findAll(pageable);

        assertNotNull(response);
        assertEquals(2, response.getTotalRecords());
        assertEquals(2, response.getDtoList().size());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedRecords() {
        Racks activeRacks = new Racks();
        activeRacks.setIdentifier("RCK001");
        activeRacks.setStatus(true);
        activeRacks.setDeleted(false);
        racksRepository.save(activeRacks);

        Racks inactiveRacks = new Racks();
        inactiveRacks.setIdentifier("RCK002");
        inactiveRacks.setStatus(false);
        inactiveRacks.setDeleted(false);
        racksRepository.save(inactiveRacks);

        List<RacksDto> activeList = racksService.findIfTrue();

        assertEquals(1, activeList.size());
        assertEquals("RCK001", activeList.get(0).getIdentifier());
    }
}