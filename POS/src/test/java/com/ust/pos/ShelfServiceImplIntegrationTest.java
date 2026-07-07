package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.models.Shelf;
import com.ust.pos.models.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
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
class ShelfServiceImplIntegrationTest {

    @Autowired
    private ShelfServiceImpl shelfService;

    @Autowired
    private ShelfRepository shelfRepository;

    @BeforeEach
    void setUp() {
        shelfRepository.deleteAll();
    }

    private Shelf createShelf(
            String identifier,
            String description,
            Boolean status,
            Boolean deleted) {
        Shelf shelf = new Shelf();
        shelf.setIdentifier(identifier);
        shelf.setDescription(description);
        shelf.setStatus(status);
        shelf.setDeleted(deleted);
        return shelfRepository.saveAndFlush(shelf);
    }

    @Test
    void save_ShouldCreateShelfSuccessfully() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SH001");
        dto.setDescription("Shelf One");
        shelfService.save(dto);
        Shelf saved = shelfRepository.findByIdentifier("SH001");
        assertNotNull(saved);
        assertEquals("SH001", saved.getIdentifier());
        assertEquals("Shelf One", saved.getDescription());
    }

    @Test
    void save_ShouldFail_WhenShelfAlreadyExists() {
        createShelf("SH001", "Shelf One", true, false);
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SH001");
        ShelfDto result = shelfService.save(dto);
        assertFalse(result.isSuccess());
        assertEquals("Shelf with identifier - SH001 already exists", result.getMessage());
    }

    @Test
    void save_ShouldFail_WhenDeletedShelfExists() {
        createShelf("SH001", "Shelf One", true, true);
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SH001");
        ShelfDto result = shelfService.save(dto);
        assertFalse(result.isSuccess());
        assertEquals("Shelf with identifier - SH001 was deleted and cannot be created again.",result.getMessage());
    }

    @Test
    void update_ShouldUpdateShelfSuccessfully() {
        createShelf("SH001", "Old Shelf", true, false);
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SH001");
        dto.setDescription("Updated Shelf");
        shelfService.update(dto);
        Shelf updated = shelfRepository.findByIdentifierAndDeletedFalse("SH001");
        assertNotNull(updated);
        assertEquals("Updated Shelf", updated.getDescription());
    }

    @Test
    void update_ShouldReturnNotFound_WhenShelfDoesNotExist() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("INVALID");
        dto.setDescription("Test Shelf");
        ShelfDto result = shelfService.update(dto);
        assertFalse(result.isSuccess());
        assertEquals("Shelf with identifier - INVALID not found", result.getMessage());
    }

    @Test
    void findByIdentifier_ShouldReturnShelf() {
        createShelf("SH001", "Shelf One", true, false);
        ShelfDto result = shelfService.findByIdentifier("SH001");
        assertNotNull(result);
        assertEquals("SH001", result.getIdentifier());
        assertEquals("Shelf One", result.getDescription());
    }

    @Test
    void findByIdentifier_ShouldThrowException_WhenShelfNotFound() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> shelfService.findByIdentifier("INVALID"));
        assertEquals("Shelf with identifier 'INVALID' not found", exception.getMessage());
    }

    @Test
    void delete_ShouldSoftDeleteShelf() {
        createShelf("SH001", "Shelf One", true, false);
        shelfService.delete("SH001");
        Shelf shelf = shelfRepository.findByIdentifier("SH001");
        assertNotNull(shelf);
        assertTrue(shelf.getDeleted());
    }

    @Test
    void toggleStatus_ShouldDisableShelf() {
        createShelf("SH001", "Shelf One", true, false);
        ShelfDto result = shelfService.toggleStatus("SH001");
        assertFalse(result.getStatus());
        Shelf updated = shelfRepository.findByIdentifier("SH001");
        assertFalse(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldEnableShelf() {
        createShelf("SH001", "Shelf One", false, false);
        ShelfDto result = shelfService.toggleStatus("SH001");
        assertTrue(result.getStatus());
        Shelf updated = shelfRepository.findByIdentifier("SH001");
        assertTrue(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldThrowException_WhenShelfNotFound() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> shelfService.toggleStatus("INVALID"));
        assertEquals("Shelf not found with identifier: INVALID", exception.getMessage());
    }

    @Test
    void findAll_ShouldReturnNonDeletedShelves() {
        createShelf("SH001", "Shelf One", true, false);
        createShelf("SH002", "Shelf Two", true, false);
        WsDto<ShelfDto> result = shelfService.findAll(PageRequest.of(0, 10));
        assertNotNull(result);
        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getDtoList().size());
    }

    @Test
    void findAllActive_ShouldReturnOnlyActiveShelves() {
        createShelf("SH001", "Shelf One", true, false);
        createShelf("SH002", "Shelf Two", false, false);
        List<ShelfDto> result = shelfService.findAllActive();
        assertEquals(1, result.size());
        assertEquals("SH001", result.get(0).getIdentifier()
        );
    }
}