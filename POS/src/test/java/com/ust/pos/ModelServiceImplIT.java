package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.models.service.ModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ModelServiceImplIT {

    @Autowired
    private ModelService modelService;

    @Autowired
    private ModelRepository modelRepository;

    @BeforeEach
    void cleanUp() {
        modelRepository.deleteAll();
    }

    @Test
    void save_shouldCreateModel() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("MOD001");
        dto.setStatus(true);

        modelService.save(dto);

        Model saved =
                modelRepository.findByIdentifier("MOD001");

        assertNotNull(saved);
        assertEquals("MOD001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {

        Model model = new Model();
        model.setIdentifier("MOD001");
        model.setDeleted(false);

        modelRepository.save(model);

        ModelDto dto = new ModelDto();
        dto.setIdentifier("MOD001");

        ModelDto response = modelService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Model already exists : MOD001",
                response.getMessage()
        );
    }

    @Test
    void save_shouldFailWhenSoftDeletedRecordExists() {

        Model model = new Model();
        model.setIdentifier("MOD001");
        model.setDeleted(true);

        modelRepository.save(model);

        ModelDto dto = new ModelDto();
        dto.setIdentifier("MOD001");

        ModelDto response = modelService.save(dto);

        assertFalse(response.isSuccess());
        assertTrue(
                response.getMessage().contains("soft deleted")
        );
    }

    @Test
    void findByIdentifier_shouldReturnModel() {

        Model model = new Model();
        model.setIdentifier("MOD001");
        model.setDeleted(false);

        modelRepository.save(model);

        ModelDto result =
                modelService.findByIdentifier("MOD001");

        assertNotNull(result);
        assertEquals("MOD001", result.getIdentifier());
    }

    @Test
    void update_shouldUpdateModel() {

        Model model = new Model();
        model.setIdentifier("MOD001");
        model.setStatus(true);
        model.setDeleted(false);

        modelRepository.save(model);

        ModelDto dto = new ModelDto();
        dto.setIdentifier("MOD001");
        dto.setStatus(false);

        modelService.update(dto);

        Model updated =
                modelRepository.findByIdentifier("MOD001");

        assertFalse(updated.isStatus());
    }

    @Test
    void update_shouldFailWhenModelNotFound() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("MOD001");

        ModelDto response = modelService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Model not found : MOD001",
                response.getMessage()
        );
    }

    @Test
    void delete_shouldSoftDeleteModel() {

        Model model = new Model();
        model.setIdentifier("MOD001");
        model.setDeleted(false);

        modelRepository.save(model);

        modelService.delete("MOD001");

        Model deleted =
                modelRepository.findByIdentifier("MOD001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void changeToggleStatus_shouldUpdateStatus() {

        Model model = new Model();
        model.setIdentifier("MOD001");
        model.setStatus(true);

        modelRepository.save(model);

        modelService.changeToggleStatus(
                "MOD001",
                false
        );

        Model updated =
                modelRepository.findByIdentifier("MOD001");

        assertFalse(updated.isStatus());
    }

    @Test
    void findActiveStatus_shouldReturnOnlyActiveModels() {

        Model active = new Model();
        active.setIdentifier("MOD001");
        active.setStatus(true);

        Model inactive = new Model();
        inactive.setIdentifier("MOD002");
        inactive.setStatus(false);

        modelRepository.save(active);
        modelRepository.save(inactive);

        List<ModelDto> result =
                modelService.findActiveStatus();

        assertEquals(1, result.size());
        assertEquals(
                "MOD001",
                result.getFirst().getIdentifier()
        );
    }
}