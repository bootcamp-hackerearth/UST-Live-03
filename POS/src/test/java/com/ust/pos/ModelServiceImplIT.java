package com.ust.pos;

import com.ust.pos.models.service.ModelService;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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

        ModelDto response = modelService.save(dto);

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

        ModelDto response =
                modelService.save(dto);

        assertFalse(response.isSuccess());

        assertEquals(
                "Model MOD001 already exists",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateSuperModel() {

        Model model = new Model();
        model.setIdentifier("MOD001");
        model.setDeleted(false);

        modelRepository.save(model);

        ModelDto dto = new ModelDto();
        dto.setIdentifier("MOD001");

        ModelDto response = modelService.update(dto);

        assertTrue(response.isSuccess());

        Model updated =
                modelRepository.findByIdentifier("MOD001");
    }

    @Test
    void findByIdentifier_shouldReturnModel() {

        Model model = new Model();
        model.setIdentifier("MOD001");

        modelRepository.save(model);

        ModelDto result =
                modelService.findByIdentifier("MOD001");

        assertEquals("MOD001", result.getIdentifier());
    }

    @Test
    void updateStatus_shouldUpdateValue() {

        Model model = new Model();
        model.setIdentifier("MOD001");
        model.setStatus(true);

        modelRepository.save(model);


        ModelDto response =
                modelService.updateStatus("MOD001", false);


        Model updated =
                modelRepository.findByIdentifier("MOD001");


        assertTrue(response.isSuccess());

        assertFalse(updated.isStatus());
    }

    @Test
    void deleteByIdentifier_shouldSoftDelete() {

        Model model = new Model();
        model.setIdentifier("MOD001");
        model.setDeleted(false);

        modelRepository.save(model);

        modelService.delete("MOD001");

        Model deleted =
                modelRepository.findByIdentifier("MOD001");

        assertTrue(deleted.isDeleted());
    }
}