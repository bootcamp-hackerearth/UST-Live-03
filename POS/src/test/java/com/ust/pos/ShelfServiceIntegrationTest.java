package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.ShelfService;
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
class ShelfServiceIntegrationTest {

    @Autowired
    private ShelfService shelfService;

    @Autowired
    private ShelfRepository shelfRepository;

    @BeforeEach
    void setUp() {
        shelfRepository.deleteAll();
    }

    @Test
    void save_ShouldCreateShelf() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SH001");
        dto.setStatus(true);

        shelfService.save(dto);

        Shelf savedShelf = shelfRepository.findByIdentifier("SH001");

        assertNotNull(savedShelf);
        assertEquals("SH001", savedShelf.getIdentifier());
        assertFalse(savedShelf.getDeleted());
    }

    @Test
    void save_ShouldNotCreateDuplicateShelf() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SH001");
        shelf.setStatus(true);
        shelf.setDeleted(false);
        shelfRepository.save(shelf);

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SH001");

        ShelfDto result = shelfService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Shelf with identifier - SH001 already exists",
                result.getMessage()
        );
    }

    @Test
    void save_ShouldNotCreateDeletedShelfAgain() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SH001");
        shelf.setStatus(true);
        shelf.setDeleted(true);
        shelfRepository.save(shelf);

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SH001");

        ShelfDto result = shelfService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Shelf with identifier - SH001 was deleted and cannot be created again.",
                result.getMessage()
        );
    }

    @Test
    void update_ShouldUpdateExistingShelf() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SH001");
        shelf.setStatus(true);
        shelf.setDeleted(false);
        shelfRepository.save(shelf);

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SH001");
        dto.setStatus(true);

        shelfService.update(dto);
        Shelf updatedShelf = shelfRepository.findByIdentifier("SH001");
        assertNotNull(updatedShelf);
    }

    @Test
    void update_ShouldReturnFailure_WhenShelfNotFound() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("SH999");

        ShelfDto result = shelfService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Shelf with identifier - SH999 not found",
                result.getMessage()
        );
    }

    @Test
    void delete_ShouldSoftDeleteShelf() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SH001");
        shelf.setStatus(true);
        shelf.setDeleted(false);
        shelfRepository.save(shelf);

        shelfService.delete("SH001");

        Shelf deletedShelf = shelfRepository.findByIdentifier("SH001");

        assertNotNull(deletedShelf);
        assertTrue(deletedShelf.getDeleted());
    }

    @Test
    void findByIdentifier_ShouldReturnShelfDto() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SH001");
        shelf.setStatus(true);
        shelf.setDeleted(false);
        shelfRepository.save(shelf);

        ShelfDto result = shelfService.findByIdentifier("SH001");

        assertNotNull(result);
        assertEquals("SH001", result.getIdentifier());
    }

    @Test
    void findAll_ShouldReturnOnlyNonDeletedShelves() {
        Shelf shelf1 = new Shelf();
        shelf1.setIdentifier("SH001");
        shelf1.setStatus(true);
        shelf1.setDeleted(false);
        shelfRepository.save(shelf1);

        Shelf shelf2 = new Shelf();
        shelf2.setIdentifier("SH002");
        shelf2.setStatus(true);
        shelf2.setDeleted(true);
        shelfRepository.save(shelf2);

        WsDto<ShelfDto> result =
                shelfService.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getDtoList().size());
        assertEquals("SH001", result.getDtoList().get(0).getIdentifier());
    }

    @Test
    void toggleStatus_ShouldChangeShelfStatus() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("SH001");
        shelf.setStatus(true);
        shelf.setDeleted(false);
        shelfRepository.save(shelf);

        ShelfDto result = shelfService.toggleStatus("SH001");

        Shelf updatedShelf = shelfRepository.findByIdentifier("SH001");

        assertNotNull(result);
        assertFalse(updatedShelf.isStatus());
    }

    @Test
    void findActiveShelves_ShouldReturnOnlyActiveShelves() {
        Shelf activeShelf = new Shelf();
        activeShelf.setIdentifier("SH001");
        activeShelf.setStatus(true);
        activeShelf.setDeleted(false);
        shelfRepository.save(activeShelf);

        Shelf inactiveShelf = new Shelf();
        inactiveShelf.setIdentifier("SH002");
        inactiveShelf.setStatus(false);
        inactiveShelf.setDeleted(false);
        shelfRepository.save(inactiveShelf);

        List<ShelfDto> result = shelfService.findActiveShelves();

        assertEquals(1, result.size());
        assertEquals("SH001", result.get(0).getIdentifier());
    }
}