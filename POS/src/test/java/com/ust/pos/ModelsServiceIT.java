package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.ModelsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ModelsServiceIT {

    @Autowired
    private ModelsService modelsService;

    @Autowired
    private ModelsRepository modelsRepository;

    @BeforeEach
    void cleanUp() {
        modelsRepository.deleteAll();
    }

    @Test
    void save_shouldCreateModels() {

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MOD001");
        dto.setStatus(true);

        ModelsDto response = modelsService.save(dto);

        Models saved =
                modelsRepository.findByIdentifier("MOD001");

        assertNotNull(saved);
        assertEquals("MOD001", saved.getIdentifier());
        assertNotNull(response);
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {

        Models models = new Models();
        models.setIdentifier("MOD001");
        models.setDeleted(false);

        modelsRepository.save(models);

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MOD001");

        ModelsDto response = modelsService.save(dto);

        assertFalse(response.isSuccess());

        assertEquals(
                " models with identifier - MOD001 already exists.",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateModels() {

        Models models = new Models();
        models.setIdentifier("MOD001");
        models.setDeleted(false);

        modelsRepository.save(models);

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MOD001");

        ModelsDto response = modelsService.update(dto);

        Models updated =
                modelsRepository.findByIdentifier("MOD001");

        assertNotNull(response);
        assertNotNull(updated);
        assertEquals("MOD001", updated.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldReturnModels() {

        Models models = new Models();
        models.setIdentifier("MOD001");

        modelsRepository.save(models);

        ModelsDto result =
                modelsService.findByIdentifier("MOD001");

        assertEquals("MOD001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> modelsService.findByIdentifier("MOD001")
                );

        assertEquals(
                "Models with identifier 'MOD001' not found",
                exception.getMessage()
        );
    }

    @Test
    void toggleStatus_shouldToggleValue() {

        Models models = new Models();
        models.setIdentifier("MOD001");
        models.setStatus(true);

        modelsRepository.save(models);

        modelsService.toggleStatus("MOD001");

        Models updated =
                modelsRepository.findByIdentifier("MOD001");

        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {

        Models models = new Models();
        models.setIdentifier("MOD001");
        models.setDeleted(false);

        modelsRepository.save(models);

        modelsService.delete("MOD001");

        Models deleted =
                modelsRepository.findByIdentifier("MOD001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void findAll_shouldReturnPagedModels() {

        Models models1 = new Models();
        models1.setIdentifier("MOD001");
        models1.setDeleted(false);

        Models models2 = new Models();
        models2.setIdentifier("MOD002");
        models2.setDeleted(false);

        modelsRepository.save(models1);
        modelsRepository.save(models2);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<ModelsDto> result =
                modelsService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getContent().size());
    }

    @Test
    void findAllWithSpecification_shouldReturnFilteredModels() {

        Models models = new Models();
        models.setIdentifier("MOD001");

        modelsRepository.save(models);

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Models> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "MOD001");

        WsDto<ModelsDto> result =
                modelsService.findAll(
                        specification,
                        pageable,
                        "MOD001"
                );

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getContent().size());
        assertEquals(
                "MOD001",
                result.getContent().get(0).getIdentifier()
        );
        assertEquals("MOD001", result.getKeyword());
    }
}