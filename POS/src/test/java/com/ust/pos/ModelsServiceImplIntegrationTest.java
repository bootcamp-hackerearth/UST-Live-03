package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.impl.ModelsServiceImpl;
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
class ModelsServiceImplIntegrationTest {

    @Autowired
    private ModelsServiceImpl modelsService;

    @Autowired
    private ModelsRepository modelsRepository;

    @BeforeEach
    void setUp() {
        modelsRepository.deleteAll();
        Models model = new Models();
        model.setIdentifier("MODEL1");
        model.setDescription("Test Model");
        model.setStatus(true);
        model.setDeleted(false);
        modelsRepository.save(model);
    }

    @Test
    void testSaveSuccess() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MODEL2");
        dto.setDescription("New Model");
        dto.setStatus(true);
        ModelsDto response = modelsService.save(dto);
        assertTrue(response.isSuccess());
        Models saved = modelsRepository.findByIdentifier("MODEL2");
        assertNotNull(saved);
        assertEquals("MODEL2", saved.getIdentifier());
        assertEquals("New Model", saved.getDescription());
        assertTrue(saved.getStatus());
        assertFalse(saved.getDeleted());
    }

    @Test
    void testSaveAlreadyExists() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MODEL1");
        ModelsDto response = modelsService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Model - MODEL1 already exists",
                response.getMessage()
        );
    }

    @Test
    void testSaveDeletedModel() {
        Models model = modelsRepository.findByIdentifier("MODEL1");
        model.setDeleted(true);
        modelsRepository.save(model);
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MODEL1");
        ModelsDto response = modelsService.save(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Model - MODEL1 was deleted and cannot be recreated",
                response.getMessage()
        );
    }

    @Test
    void testUpdateSuccess() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MODEL1");
        dto.setDescription("Updated Model");
        dto.setStatus(false);
        ModelsDto response = modelsService.update(dto);
        assertTrue(response.isSuccess());
        Models updated = modelsRepository.findByIdentifier("MODEL1");
        assertEquals("Updated Model", updated.getDescription());
        assertFalse(updated.getStatus());
    }

    @Test
    void testUpdateNotFound() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("UNKNOWN");
        ModelsDto response = modelsService.update(dto);
        assertFalse(response.isSuccess());
        assertEquals(
                "Model - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void testDelete() {
        modelsService.delete("MODEL1");
        Models deleted = modelsRepository.findByIdentifier("MODEL1");
        assertTrue(deleted.getDeleted());
    }

    @Test
    void testDeleteNotFound() {
        assertDoesNotThrow(() -> modelsService.delete("UNKNOWN"));
    }

    @Test
    void testFindByIdentifierSuccess() {
        ModelsDto dto = modelsService.findByIdentifier("MODEL1");
        assertNotNull(dto);
        assertEquals("MODEL1", dto.getIdentifier());
        assertEquals("Test Model", dto.getDescription());
        assertTrue(dto.getStatus());
    }

    @Test
    void testFindByIdentifierNotFound() {
        assertThrows(
                ResourceNotFoundException.class,
                () -> modelsService.findByIdentifier("UNKNOWN")
        );
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        WsDto<ModelsDto> response =
                modelsService.findAll(pageable);
        assertNotNull(response);
        assertEquals(1, response.getDtoList().size());
        assertEquals(1, response.getTotalRecords());
        assertEquals(1, response.getTotalPages());
        assertEquals(10, response.getSizePerPage());
        assertEquals(0, response.getPage());
    }

    @Test
    void testFindAllActive() {
        List<ModelsDto> models = modelsService.findAllActive();
        assertEquals(1, models.size());
        ModelsDto dto = models.get(0);
        assertEquals("MODEL1", dto.getIdentifier());
        assertTrue(dto.getStatus());
    }

    @Test
    void testFindAllActiveNoResults() {
        Models model = modelsRepository.findByIdentifier("MODEL1");
        model.setStatus(false);
        modelsRepository.save(model);
        List<ModelsDto> models = modelsService.findAllActive();
        assertTrue(models.isEmpty());
    }

    @Test
    void testUpdateStatusFalse() {
        modelsService.updateStatus("MODEL1", false);
        Models model = modelsRepository.findByIdentifier("MODEL1");
        assertFalse(model.getStatus());
    }

    @Test
    void testUpdateStatusTrue() {
        Models model = modelsRepository.findByIdentifier("MODEL1");
        model.setStatus(false);
        modelsRepository.save(model);
        modelsService.updateStatus("MODEL1", true);
        Models updated = modelsRepository.findByIdentifier("MODEL1");
        assertTrue(updated.getStatus());
    }

    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Models> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "MODEL1");
        WsDto<ModelsDto> response =
                modelsService.findAll(specification, pageable);
        assertEquals(1, response.getDtoList().size());
        assertEquals(
                "MODEL1",
                response.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void testFindAllWithSpecificationNoResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Models> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "XYZ");
        WsDto<ModelsDto> response =
                modelsService.findAll(specification, pageable);
        assertEquals(0, response.getDtoList().size());
        assertEquals(0, response.getTotalRecords());
        assertEquals(0, response.getTotalPages());
    }
}