package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.models.Warehouse;
import com.ust.pos.models.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
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
class WarehouseServiceImplIntegrationTest {

    @Autowired
    private WarehouseServiceImpl warehouseService;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @BeforeEach
    void setUp() {
        warehouseRepository.deleteAll();
    }

    private void createWarehouse(
            String identifier,
            String description,
            Boolean status,
            Boolean deleted) {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier(identifier);
        warehouse.setDescription(description);
        warehouse.setStatus(status);
        warehouse.setDeleted(deleted);
        warehouseRepository.saveAndFlush(warehouse);
    }

    @Test
    void save_ShouldCreateWarehouseSuccessfully() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH001");
        dto.setDescription("Main Warehouse");
        warehouseService.save(dto);
        Warehouse saved = warehouseRepository.findByIdentifier("WH001");
        assertNotNull(saved);
        assertEquals("WH001", saved.getIdentifier());
        assertEquals("Main Warehouse", saved.getDescription());
    }

    @Test
    void save_ShouldFail_WhenWarehouseAlreadyExists() {
        createWarehouse("WH001", "Main Warehouse", true, false);
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH001");
        WarehouseDto result = warehouseService.save(dto);
        assertFalse(result.isSuccess());
        assertEquals("Warehouse with identifier - WH001 already exists", result.getMessage());
    }

    @Test
    void save_ShouldFail_WhenDeletedWarehouseExists() {
        createWarehouse("WH001", "Main Warehouse", true, true);
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH001");
        WarehouseDto result = warehouseService.save(dto);
        assertFalse(result.isSuccess());
        assertEquals("Warehouse with identifier - WH001 was deleted and cannot be created again.", result.getMessage());
    }

    @Test
    void update_ShouldUpdateWarehouseSuccessfully() {
        createWarehouse("WH001", "Old Warehouse", true, false);
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH001");
        dto.setDescription("Updated Warehouse");
        warehouseService.update(dto);
        Warehouse updated = warehouseRepository.findByIdentifier("WH001");
        assertNotNull(updated);
        assertEquals("Updated Warehouse", updated.getDescription());
    }

    @Test
    void update_ShouldReturnNotFound_WhenWarehouseDoesNotExist() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("INVALID");
        dto.setDescription("Test");
        WarehouseDto result = warehouseService.update(dto);
        assertFalse(result.isSuccess());
        assertEquals("Warehouse with identifier - INVALID not found", result.getMessage());
    }

    @Test
    void findByIdentifier_ShouldReturnWarehouse() {
        createWarehouse("WH001", "Main Warehouse", true, false);
        WarehouseDto result = warehouseService.findByIdentifier("WH001");
        assertNotNull(result);
        assertEquals("WH001", result.getIdentifier());
        assertEquals("Main Warehouse", result.getDescription());
    }

    @Test
    void findByIdentifier_ShouldThrowException_WhenWarehouseNotFound() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> warehouseService.findByIdentifier("INVALID"));
        assertEquals("Warehouse with identifier 'INVALID' not found", exception.getMessage());
    }

    @Test
    void delete_ShouldSoftDeleteWarehouse() {
        createWarehouse("WH001", "Main Warehouse", true, false);
        warehouseService.delete("WH001");
        Warehouse warehouse = warehouseRepository.findByIdentifier("WH001");
        assertNotNull(warehouse);
        assertTrue(warehouse.getDeleted());
    }

    @Test
    void toggleStatus_ShouldDisableWarehouse() {
        createWarehouse("WH001", "Main Warehouse", true, false);
        WarehouseDto result = warehouseService.toggleStatus("WH001");
        assertFalse(result.getStatus());
        Warehouse updated = warehouseRepository.findByIdentifier("WH001");
        assertFalse(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldEnableWarehouse() {
        createWarehouse("WH001", "Main Warehouse", false, false);
        WarehouseDto result = warehouseService.toggleStatus("WH001");
        assertTrue(result.getStatus());
        Warehouse updated = warehouseRepository.findByIdentifier("WH001");
        assertTrue(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldThrowException_WhenWarehouseNotFound() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> warehouseService.toggleStatus("INVALID"));
        assertEquals("Warehouse not found with identifier: INVALID", exception.getMessage());
    }

    @Test
    void findAll_ShouldReturnOnlyNonDeletedWarehouses() {
        createWarehouse("WH001", "Warehouse One", true, false);
        createWarehouse("WH002", "Warehouse Two", true, false);
        WsDto<WarehouseDto> result = warehouseService.findAll(PageRequest.of(0, 10));
        assertNotNull(result);
        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getDtoList().size());
    }

    @Test
    void findAllActive_ShouldReturnOnlyActiveWarehouses() {
        createWarehouse("WH001", "Warehouse One", true, false);
        createWarehouse("WH002", "Warehouse Two", false, false);
        List<WarehouseDto> result = warehouseService.findAllActive();
        assertEquals(1, result.size());
        assertEquals("WH001", result.get(0).getIdentifier());
    }
}