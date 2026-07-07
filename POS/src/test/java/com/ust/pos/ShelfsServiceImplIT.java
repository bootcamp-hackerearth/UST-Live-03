package com.ust.pos;

import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Shelfs;
import com.ust.pos.model.ShelfsRepository;
import com.ust.pos.shelfs.service.ShelfsService;
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
class ShelfsServiceImplIT {

    @Autowired
    private ShelfsService shelfsService;

    @Autowired
    private ShelfsRepository shelfsRepository;

    @BeforeEach
    void cleanUp() {
        shelfsRepository.deleteAll();
    }

    @Test
    void save_shouldCreateShelfs() {
        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("SHF001");
        dto.setStatus(true);

        Shelfs saved = shelfsRepository.findByIdentifier("SHF001");

        assertNotNull(saved);
        assertEquals("SHF001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Shelfs shelfs = new Shelfs();
        shelfs.setIdentifier("SHF001");
        shelfs.setDeleted(false);
        shelfsRepository.save(shelfs);

        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("SHF001");

        ShelfsDto response = shelfsService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Shelfs with identifier - SHF001 already exists",
                response.getMessage()
        );
    }

    @Test
    void save_shouldFailWhenPreviouslyDeleted() {
        Shelfs shelfs = new Shelfs();
        shelfs.setIdentifier("SHF001");
        shelfs.setDeleted(true);
        shelfsRepository.save(shelfs);

        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("SHF001");

        ShelfsDto response = shelfsService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Shelfs with identifier SHF001 was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateShelfsDetails() {
        Shelfs shelfs = new Shelfs();
        shelfs.setIdentifier("SHF001");
        shelfs.setStatus(true);
        shelfs.setDeleted(false);
        shelfsRepository.save(shelfs);

        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("SHF001");
        dto.setStatus(false);

        ShelfsDto response = shelfsService.update(dto);

        assertTrue(response.isSuccess());

        Shelfs updated = shelfsRepository.findByIdentifier("SHF001");

        assertFalse(updated.isStatus());
    }

    @Test
    void findByIdentifier_shouldReturnShelfs() {
        Shelfs shelfs = new Shelfs();
        shelfs.setIdentifier("SHF001");
        shelfsRepository.save(shelfs);

        ShelfsDto result = shelfsService.findByIdentifier("SHF001");

        assertEquals("SHF001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            shelfsService.findByIdentifier("NON-EXISTENT");
        });
    }

    @Test
    void toggleStatus_shouldToggleValue() {
        Shelfs shelfs = new Shelfs();
        shelfs.setIdentifier("SHF001");
        shelfs.setStatus(true);
        shelfsRepository.save(shelfs);

        shelfsService.toggleStatus("SHF001");

        Shelfs updated = shelfsRepository.findByIdentifier("SHF001");

        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {
        Shelfs shelfs = new Shelfs();
        shelfs.setIdentifier("SHF001");
        shelfs.setDeleted(false);
        shelfsRepository.save(shelfs);

        boolean isDeleted = shelfsService.delete("SHF001");

        assertTrue(isDeleted);

        Shelfs deleted = shelfsRepository.findByIdentifier("SHF001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void findAll_shouldReturnPaginatedData() {
        Shelfs shelfs1 = new Shelfs();
        shelfs1.setIdentifier("SHF001");
        shelfs1.setDeleted(false);
        shelfsRepository.save(shelfs1);

        Shelfs shelfs2 = new Shelfs();
        shelfs2.setIdentifier("SHF002");
        shelfs2.setDeleted(false);
        shelfsRepository.save(shelfs2);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<ShelfsDto> response = shelfsService.findAll(pageable);

        assertNotNull(response);
        assertEquals(2, response.getTotalRecords());
        assertEquals(2, response.getDtoList().size());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedRecords() {
        Shelfs activeShelfs = new Shelfs();
        activeShelfs.setIdentifier("SHF001");
        activeShelfs.setStatus(true);
        activeShelfs.setDeleted(false);
        shelfsRepository.save(activeShelfs);

        Shelfs inactiveShelfs = new Shelfs();
        inactiveShelfs.setIdentifier("SHF002");
        inactiveShelfs.setStatus(false);
        inactiveShelfs.setDeleted(false);
        shelfsRepository.save(inactiveShelfs);

        List<ShelfsDto> activeList = shelfsService.findIfTrue();

        assertEquals(1, activeList.size());
        assertEquals("SHF001", activeList.get(0).getIdentifier());
    }
}