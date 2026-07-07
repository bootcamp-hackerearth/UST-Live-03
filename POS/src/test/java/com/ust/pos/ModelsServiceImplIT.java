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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ModelsServiceImplIT {

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

        Models saved = modelsRepository.findByIdentifier("MOD001");

        assertNotNull(saved);
        assertEquals("MOD001", saved.getIdentifier());
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
                "Models with identifier - MOD001 already exists",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateModelsDetails() {

        Models models = new Models();
        models.setIdentifier("MOD001");
        models.setStatus(true);
        models.setDeleted(false);

        modelsRepository.save(models);

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MOD001");
        dto.setStatus(false);

        ModelsDto response = modelsService.update(dto);

        assertTrue(response.isSuccess());

        Models updated = modelsRepository.findByIdentifier("MOD001");

        assertFalse(updated.isStatus());
    }

    @Test
    void findByIdentifier_shouldReturnModels() {

        Models models = new Models();
        models.setIdentifier("MOD001");

        modelsRepository.save(models);

        ModelsDto result = modelsService.findByIdentifier("MOD001");

        assertEquals("MOD001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {

        assertThrows(ResourceNotFoundException.class, () -> {
            modelsService.findByIdentifier("NON-EXISTENT");
        });
    }

    @Test
    void toggleStatus_shouldToggleValue() {

        Models models = new Models();
        models.setIdentifier("MOD001");
        models.setStatus(true);

        modelsRepository.save(models);

        modelsService.toggleStatus("MOD001");

        Models updated = modelsRepository.findByIdentifier("MOD001");

        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {

        Models models = new Models();
        models.setIdentifier("MOD001");
        models.setDeleted(false);

        modelsRepository.save(models);

        boolean isDeleted = modelsService.delete("MOD001");

        assertTrue(isDeleted);

        Models deleted = modelsRepository.findByIdentifier("MOD001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void findAll_shouldReturnPaginatedData() {

        Models models1 = new Models();
        models1.setIdentifier("MOD001");
        models1.setDeleted(false);
        modelsRepository.save(models1);

        Models models2 = new Models();
        models2.setIdentifier("MOD002");
        models2.setDeleted(false);
        modelsRepository.save(models2);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<ModelsDto> response = modelsService.findAll(pageable);

        assertNotNull(response);
        assertEquals(2, response.getTotalRecords());
        assertEquals(2, response.getDtoList().size());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedRecords() {

        Models activeModel = new Models();
        activeModel.setIdentifier("MOD001");
        activeModel.setStatus(true);
        activeModel.setDeleted(false);
        modelsRepository.save(activeModel);

        Models inactiveModel = new Models();
        inactiveModel.setIdentifier("MOD002");
        inactiveModel.setStatus(false);
        inactiveModel.setDeleted(false);
        modelsRepository.save(inactiveModel);

        List<ModelsDto> activeList = modelsService.findIfTrue();

        assertEquals(1, activeList.size());
        assertEquals("MOD001", activeList.get(0).getIdentifier());
    }
}