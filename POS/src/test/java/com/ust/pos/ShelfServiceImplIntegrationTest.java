package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
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
class ShelfServiceImplIntegrationTest {

    @Autowired
    private ShelfServiceImpl shelfService;

    @Autowired
    private ShelfRepository shelfRepository;

    @BeforeEach
    void setUp() {
        shelfRepository.deleteAll();
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SHELF1");
        shelf.setDescription("Test Shelf");
        shelf.setStatus(true);
        shelf.setDeleted(false);
        shelfRepository.save(shelf);
    }

    @Test
    void testSaveSuccess() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SHELF2");
        dto.setDescription("New Shelf");
        dto.setStatus(true);
        ShelfDto response = shelfService.save(dto);
        assertTrue(response.isSuccess());
        Shelf saved = shelfRepository.findByIdentifier("SHELF2");
        assertNotNull(saved);
        assertEquals("SHELF2", saved.getIdentifier());
        assertEquals("New Shelf", saved.getDescription());
        assertTrue(saved.getStatus());
        assertFalse(saved.getDeleted());
    }

    @Test
    void testSaveAlreadyExists() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SHELF1");
        ShelfDto response = shelfService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Shelf with identifier - SHELF1 already exists",
                response.getMessage()
        );
    }

    @Test
    void testSaveDeletedShelf() {
        Shelf shelf = shelfRepository.findByIdentifier("SHELF1");
        shelf.setDeleted(true);
        shelfRepository.save(shelf);
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SHELF1");
        ShelfDto response = shelfService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Shelf - SHELF1 was deleted and cannot be recreated",
                response.getMessage()
        );
    }

    @Test
    void testUpdateSuccess() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SHELF1");
        dto.setDescription("Updated Shelf");
        dto.setStatus(false);
        ShelfDto response = shelfService.update(dto);
        assertTrue(response.isSuccess());
        Shelf updated = shelfRepository.findByIdentifier("SHELF1");
        assertEquals("Updated Shelf", updated.getDescription());
        assertFalse(updated.getStatus());
    }

    @Test
    void testUpdateNotFound() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("UNKNOWN");
        ShelfDto response = shelfService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Shelf with identifier - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void testDelete() {
        shelfService.delete("SHELF1");
        Shelf deleted = shelfRepository.findByIdentifier("SHELF1");
        assertTrue(deleted.getDeleted());
    }

    @Test
    void testDeleteNotFound() {
        assertDoesNotThrow(() -> shelfService.delete("UNKNOWN"));
    }

    @Test
    void testFindByIdentifierSuccess() {
        ShelfDto dto = shelfService.findByIdentifier("SHELF1");
        assertNotNull(dto);
        assertEquals("SHELF1", dto.getIdentifier());
        assertEquals("Test Shelf", dto.getDescription());
        assertTrue(dto.getStatus());
    }

    @Test
    void testFindByIdentifierNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> shelfService.findByIdentifier("UNKNOWN")
        );
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        WsDto<ShelfDto> response = shelfService.findAll(pageable);
        assertNotNull(response);
        assertEquals(1, response.getDtoList().size());
        assertEquals(1, response.getTotalRecords());
        assertEquals(1, response.getTotalPages());
        assertEquals(10, response.getSizePerPage());
        assertEquals(0, response.getPage());
    }

    @Test
    void testFindAllActive() {
        List<ShelfDto> shelves = shelfService.findAllActive();
        assertEquals(1, shelves.size());
        ShelfDto dto = shelves.get(0);
        assertEquals("SHELF1", dto.getIdentifier());
        assertEquals("Test Shelf", dto.getDescription());
        assertTrue(dto.getStatus());
    }

    @Test
    void testFindAllActiveNoResults() {
        Shelf shelf = shelfRepository.findByIdentifier("SHELF1");
        shelf.setStatus(false);
        shelfRepository.save(shelf);
        List<ShelfDto> shelves = shelfService.findAllActive();
        assertTrue(shelves.isEmpty());
    }

    @Test
    void testUpdateStatusFalse() {
        shelfService.updateStatus("SHELF1", false);
        Shelf shelf = shelfRepository.findByIdentifier("SHELF1");
        assertFalse(shelf.getStatus());
    }

    @Test
    void testUpdateStatusTrue() {
        Shelf shelf = shelfRepository.findByIdentifier("SHELF1");
        shelf.setStatus(false);
        shelfRepository.save(shelf);
        shelfService.updateStatus("SHELF1", true);
        Shelf updated = shelfRepository.findByIdentifier("SHELF1");
        assertTrue(updated.getStatus());
    }

    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Shelf> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "SHELF1");
        WsDto<ShelfDto> response =
                shelfService.findAll(specification, pageable);
        assertEquals(1, response.getDtoList().size());
        assertEquals(
                "SHELF1",
                response.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void testFindAllWithSpecificationNoResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Shelf> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "XYZ");
        WsDto<ShelfDto> response =
                shelfService.findAll(specification, pageable);
        assertEquals(0, response.getDtoList().size());
        assertEquals(0, response.getTotalRecords());
        assertEquals(0, response.getTotalPages());
    }
}