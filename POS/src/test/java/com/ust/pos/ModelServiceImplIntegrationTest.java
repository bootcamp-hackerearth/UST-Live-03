package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.service.impl.ModelServiceImpl;
import com.ust.pos.modell.Model;
import com.ust.pos.modell.ModelRepository;
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
class ModelServiceImplIntegrationTest {

    @Autowired
    private ModelServiceImpl modelService;

    @Autowired
    private ModelRepository modelRepository;

    @BeforeEach
    void setUp() {
        modelRepository.deleteAll();
    }

    private Model createModel(String identifier,
                              Boolean status,
                              Boolean deleted) {

        Model model = new Model();
        model.setIdentifier(identifier);
        model.setStatus(status);
        model.setDeleted(deleted);

        return modelRepository.saveAndFlush(model);
    }

    @Test
    void save_ShouldCreateModelSuccessfully() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("MOD001");

        ModelDto result = modelService.save(dto);

        assertNotNull(result);

        Model saved = modelRepository.findByIdentifier("MOD001");

        assertNotNull(saved);
        assertEquals("MOD001", saved.getIdentifier());
        assertTrue(saved.getStatus());
    }

    @Test
    void save_ShouldFail_WhenModelAlreadyExists() {

        createModel(
                "MOD001",
                true,
                false
        );

        ModelDto dto = new ModelDto();
        dto.setIdentifier("MOD001");

        ModelDto result = modelService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Model with identifier - MOD001 already exists",
                result.getMessage()
        );
    }

    @Test
    void save_ShouldFail_WhenSoftDeletedModelExists() {

        createModel(
                "MOD001",
                true,
                true
        );

        ModelDto dto = new ModelDto();
        dto.setIdentifier("MOD001");

        ModelDto result = modelService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Model with Identifier MOD001 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void update_ShouldUpdateExistingModel() {

        createModel(
                "MOD001",
                true,
                false
        );

        ModelDto dto = new ModelDto();
        dto.setIdentifier("MOD001");

        ModelDto result = modelService.update(dto);

        assertNotNull(result);

        Model updated =
                modelRepository.findByIdentifierAndDeletedFalse("MOD001");

        assertNotNull(updated);
        assertEquals("MOD001", updated.getIdentifier());
    }

    @Test
    void update_ShouldReturnError_WhenModelNotFound() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("INVALID");

        ModelDto result = modelService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Model with identifier - INVALID not found",
                result.getMessage()
        );
    }

    @Test
    void findByIdentifier_ShouldReturnModel() {

        createModel(
                "MOD001",
                true,
                false
        );

        ModelDto result =
                modelService.findByIdentifier("MOD001");

        assertNotNull(result);
        assertEquals("MOD001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_ShouldThrowException_WhenModelNotFound() {

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> modelService.findByIdentifier("INVALID")
                );

        assertEquals(
                "Model with identifier 'INVALID' not found",
                exception.getMessage()
        );
    }

    @Test
    void delete_ShouldSoftDeleteModel() {

        createModel(
                "MOD001",
                true,
                false
        );

        modelService.delete("MOD001");

        Model deleted =
                modelRepository.findByIdentifier("MOD001");

        assertNotNull(deleted);
        assertTrue(deleted.getDeleted());
    }

    @Test
    void delete_ShouldNotThrow_WhenModelDoesNotExist() {

        assertDoesNotThrow(
                () -> modelService.delete("INVALID")
        );
    }

    @Test
    void toggleStatus_ShouldDisableModel() {

        createModel(
                "MOD001",
                true,
                false
        );

        modelService.toggleStatus("MOD001");

        Model updated =
                modelRepository.findByIdentifier("MOD001");

        assertNotNull(updated);
        assertFalse(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldEnableModel() {

        createModel(
                "MOD001",
                false,
                false
        );

        modelService.toggleStatus("MOD001");

        Model updated =
                modelRepository.findByIdentifier("MOD001");

        assertNotNull(updated);
        assertTrue(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldThrowException_WhenModelNotFound() {

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> modelService.toggleStatus("INVALID")
                );

        assertEquals(
                "model not found",
                exception.getMessage()
        );
    }

    @Test
    void findAll_ShouldReturnAllNonDeletedModels() {

        createModel(
                "MOD001",
                true,
                false
        );

        createModel(
                "MOD002",
                true,
                false
        );

        WsDto<ModelDto> result =
                modelService.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getDtoList().size());
    }

    @Test
    void findAllActive_ShouldReturnOnlyActiveModels() {

        createModel(
                "MOD001",
                true,
                false
        );

        createModel(
                "MOD002",
                false,
                false
        );

        List<ModelDto> result =
                modelService.findAllActive();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(
                "MOD001",
                result.get(0).getIdentifier()
        );
    }
}