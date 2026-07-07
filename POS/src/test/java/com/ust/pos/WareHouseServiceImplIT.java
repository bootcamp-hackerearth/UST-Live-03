package com.ust.pos;

import com.ust.pos.dto.WareHouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.WareHouse;
import com.ust.pos.model.WareHouseRepository;
import com.ust.pos.warehouse.service.WareHouseService;
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
class WareHouseServiceImplIT {

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private WareHouseRepository wareHouseRepository;

    @BeforeEach
    void cleanUp() {
        wareHouseRepository.deleteAll();
    }

    @Test
    void save_shouldCreateWareHouse() {
        WareHouseDto dto = new WareHouseDto();
        dto.setIdentifier("WH001");
        dto.setStatus(true);

        WareHouse saved = wareHouseRepository.findByIdentifier("WH001");

        assertNotNull(saved);
        assertEquals("WH001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setIdentifier("WH001");
        wareHouse.setDeleted(false);
        wareHouseRepository.save(wareHouse);

        WareHouseDto dto = new WareHouseDto();
        dto.setIdentifier("WH001");

        WareHouseDto response = wareHouseService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "WareHouse with identifier - WH001 already exists",
                response.getMessage()
        );
    }

    @Test
    void save_shouldFailWhenPreviouslyDeleted() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setIdentifier("WH001");
        wareHouse.setDeleted(true);
        wareHouseRepository.save(wareHouse);

        WareHouseDto dto = new WareHouseDto();
        dto.setIdentifier("WH001");

        WareHouseDto response = wareHouseService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "WareHouse with identifier WH001 was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateWareHouseDetails() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setIdentifier("WH001");
        wareHouse.setStatus(true);
        wareHouse.setDeleted(false);
        wareHouseRepository.save(wareHouse);

        WareHouseDto dto = new WareHouseDto();
        dto.setIdentifier("WH001");
        dto.setStatus(false);

        WareHouseDto response = wareHouseService.update(dto);

        assertTrue(response.isSuccess());

        WareHouse updated = wareHouseRepository.findByIdentifier("WH001");

        assertFalse(updated.isStatus());
    }

    @Test
    void findByIdentifier_shouldReturnWareHouse() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setIdentifier("WH001");
        wareHouseRepository.save(wareHouse);

        WareHouseDto result = wareHouseService.findByIdentifier("WH001");

        assertEquals("WH001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            wareHouseService.findByIdentifier("NON-EXISTENT");
        });
    }

    @Test
    void toggleStatus_shouldToggleValue() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setIdentifier("WH001");
        wareHouse.setStatus(true);
        wareHouseRepository.save(wareHouse);

        wareHouseService.toggleStatus("WH001");

        WareHouse updated = wareHouseRepository.findByIdentifier("WH001");

        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {
        WareHouse wareHouse = new WareHouse();
        wareHouse.setIdentifier("WH001");
        wareHouse.setDeleted(false);
        wareHouseRepository.save(wareHouse);

        boolean isDeleted = wareHouseService.delete("WH001");

        assertTrue(isDeleted);

        WareHouse deleted = wareHouseRepository.findByIdentifier("WH001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void findAll_shouldReturnPaginatedData() {
        WareHouse warehouse1 = new WareHouse();
        warehouse1.setIdentifier("WH001");
        warehouse1.setDeleted(false);
        wareHouseRepository.save(warehouse1);

        WareHouse warehouse2 = new WareHouse();
        warehouse2.setIdentifier("WH002");
        warehouse2.setDeleted(false);
        wareHouseRepository.save(warehouse2);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<WareHouseDto> response = wareHouseService.findAll(pageable);

        assertNotNull(response);
        assertEquals(2, response.getTotalRecords());
        assertEquals(2, response.getDtoList().size());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedRecords() {
        WareHouse activeWareHouse = new WareHouse();
        activeWareHouse.setIdentifier("WH001");
        activeWareHouse.setStatus(true);
        activeWareHouse.setDeleted(false);
        wareHouseRepository.save(activeWareHouse);

        WareHouse inactiveWareHouse = new WareHouse();
        inactiveWareHouse.setIdentifier("WH002");
        inactiveWareHouse.setStatus(false);
        inactiveWareHouse.setDeleted(false);
        wareHouseRepository.save(inactiveWareHouse);

        List<WareHouseDto> activeList = wareHouseService.findIfTrue();

        assertEquals(1, activeList.size());
        assertEquals("WH001", activeList.get(0).getIdentifier());
    }
}