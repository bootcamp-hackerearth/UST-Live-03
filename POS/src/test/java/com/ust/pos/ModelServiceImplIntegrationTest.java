package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.models.service.impl.ModelServiceImpl;
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
class ModelServiceImplIntegrationTest {

    @Autowired
    private ModelServiceImpl modelService;

    @Autowired
    private ModelRepository modelRepository;

    @BeforeEach
    void setUp() {
        modelRepository.deleteAll();

        Model model = new Model();
        model.setIdentifier("IPHONE16");
        model.setDescription("iPhone 16");
        model.setStatus(true);
        model.setIsDeleted(false);

        modelRepository.save(model);
    }

    @Test
    void testSaveSuccess() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("GALAXYS25");
        dto.setDescription("Galaxy S25");
        dto.setStatus(true);

        ModelDto response = modelService.save(dto);

        assertTrue(response.isSuccess());

        Model saved = modelRepository.findByIdentifier("GALAXYS25");

        assertNotNull(saved);
        assertEquals("GALAXYS25", saved.getIdentifier());
        assertEquals("Galaxy S25", saved.getDescription());
        assertTrue(saved.getStatus());
        assertFalse(saved.getIsDeleted());
    }

    @Test
    void testSaveAlreadyExists() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("IPHONE16");

        ModelDto response = modelService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Model with identifier - IPHONE16 already exists",
                response.getMessage()
        );
    }

    @Test
    void testSaveDeletedModel() {

        Model model = modelRepository.findByIdentifier("IPHONE16");
        model.setIsDeleted(true);
        modelRepository.save(model);

        ModelDto dto = new ModelDto();
        dto.setIdentifier("IPHONE16");

        ModelDto response = modelService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Model with identifier - IPHONE16 was deleted. Contact admin for further support or try with a different identifier.",
                response.getMessage()
        );
    }

    @Test
    void testUpdateSuccess() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("IPHONE16");
        dto.setDescription("Updated iPhone 16");
        dto.setStatus(false);

        ModelDto response = modelService.update(dto);

        assertTrue(response.isSuccess());

        Model updated = modelRepository.findByIdentifier("IPHONE16");

        assertEquals("Updated iPhone 16", updated.getDescription());
        assertFalse(updated.getStatus());
    }

    @Test
    void testUpdateNotFound() {

        ModelDto dto = new ModelDto();
        dto.setIdentifier("UNKNOWN");

        ModelDto response = modelService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Model with identifier - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void testDeleteSuccess() {

        ModelDto response = modelService.delete("IPHONE16");

        assertTrue(response.isSuccess());
        assertEquals("Model deleted successfully", response.getMessage());

        Model deleted = modelRepository.findByIdentifier("IPHONE16");

        assertTrue(deleted.getIsDeleted());
        assertFalse(deleted.getStatus());
    }

    @Test
    void testDeleteNotFound() {

        ModelDto response = modelService.delete("UNKNOWN");

        assertFalse(response.isSuccess());
        assertEquals(
                "Model with identifier - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void testFindByIdentifierSuccess() {

        ModelDto dto = modelService.findByIdentifier("IPHONE16");

        assertNotNull(dto);
        assertEquals("IPHONE16", dto.getIdentifier());
        assertEquals("iPhone 16", dto.getDescription());
        assertTrue(dto.getStatus());
    }

    @Test
    void testFindByIdentifierNotFound() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> modelService.findByIdentifier("UNKNOWN")
        );
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        PaginatedResponseDto<ModelDto> response =
                modelService.findAll(pageable);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(1, response.getTotalRecords());
        assertEquals(1, response.getTotalPages());
        assertEquals(10, response.getSizePerPage());
        assertEquals(0, response.getPage());
    }

    @Test
    void testFindAllActive() {

        List<ModelDto> models = modelService.findAllActive();

        assertEquals(1, models.size());

        ModelDto dto = models.get(0);

        assertEquals("IPHONE16", dto.getIdentifier());
        assertTrue(dto.getStatus());
        assertFalse(dto.getIsDeleted());
    }

    @Test
    void testFindAllActiveNoResults() {

        Model model = modelRepository.findByIdentifier("IPHONE16");
        model.setStatus(false);
        modelRepository.save(model);

        List<ModelDto> models = modelService.findAllActive();

        assertTrue(models.isEmpty());
    }

    @Test
    void testChangeStatusToFalse() {

        modelService.changeStatus("IPHONE16", false);

        Model model = modelRepository.findByIdentifier("IPHONE16");

        assertFalse(model.getStatus());
    }

    @Test
    void testChangeStatusToTrue() {

        Model model = modelRepository.findByIdentifier("IPHONE16");
        model.setStatus(false);
        modelRepository.save(model);

        modelService.changeStatus("IPHONE16", true);

        Model updated = modelRepository.findByIdentifier("IPHONE16");

        assertTrue(updated.getStatus());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Model> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "IPHONE16");

        PaginatedResponseDto<ModelDto> response =
                modelService.findAll(specification, pageable);

        assertEquals(1, response.getItems().size());
        assertEquals(
                "IPHONE16",
                response.getItems().get(0).getIdentifier()
        );
    }

    @Test
    void testFindAllWithSpecificationNoResults() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Model> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "XYZ");

        PaginatedResponseDto<ModelDto> response =
                modelService.findAll(specification, pageable);

        assertEquals(0, response.getItems().size());
        assertEquals(0, response.getTotalRecords());
        assertEquals(0, response.getTotalPages());
    }
}