package com.ust.pos;

import com.ust.pos.brand.service.BrandService;
import com.ust.pos.dto.BrandDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Brand;
import com.ust.pos.model.BrandRepository;
import com.ust.pos.util.FileStorageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@ActiveProfiles("test")
public class BrandServiceImplIT {

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandRepository brandRepository;

    @MockitoBean
    private FileStorageUtil fileStorageUtil;

    private BrandDto sampleBrandDto;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        brandRepository.deleteAll();

        mockFile = new MockMultipartFile(
                "icon",
                "test-image.png",
                "image/png",
                "mockImageIconBytes".getBytes()
        );

        Mockito.when(fileStorageUtil.saveBrandIcon(any(MultipartFile.class), anyString()))
                .thenReturn("/mock/storage/path/brand-icon.png");

        sampleBrandDto = new BrandDto();
        sampleBrandDto.setIdentifier("BRD-001");
        sampleBrandDto.setDescription("Test Brand Description");
        sampleBrandDto.setIcon(mockFile);
        sampleBrandDto.setStatus(true);
    }

    @Test
    void testSave_Success() {
        BrandDto result = brandService.save(sampleBrandDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Added Successfully"));

        Brand savedBrand = brandRepository.findByIdentifier("BRD-001");
        assertNotNull(savedBrand);
        assertEquals("/mock/storage/path/brand-icon.png", savedBrand.getIconPath());
        assertFalse(savedBrand.isDeleted());
    }

    @Test
    void testSave_AlreadyExists_Active() {
        brandService.save(sampleBrandDto);

        BrandDto duplicateDto = new BrandDto();
        duplicateDto.setIdentifier("BRD-001");

        BrandDto result = brandService.save(duplicateDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_AlreadyExists_PreviouslyDeleted() {
        Brand activeDeletedBrand = new Brand();
        activeDeletedBrand.setIdentifier("BRD-DEL");
        activeDeletedBrand.setDeleted(true);
        brandRepository.save(activeDeletedBrand);

        BrandDto dto = new BrandDto();
        dto.setIdentifier("BRD-DEL");

        BrandDto result = brandService.save(dto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testFindByIdentifier_Success() {
        brandService.save(sampleBrandDto);

        BrandDto foundDto = brandService.findByIdentifier("BRD-001");

        assertNotNull(foundDto);
        assertEquals("BRD-001", foundDto.getIdentifier());
        assertEquals("Test Brand Description", foundDto.getDescription());
    }

    @Test
    void testFindByIdentifier_ThrowsResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> brandService.findByIdentifier("NON-EXISTENT-ID"));
    }

    @Test
    void testFindAll_Paged_Success() {
        brandService.save(sampleBrandDto);

        Pageable pageable = PageRequest.of(0, 5);
        WsDto<BrandDto> wsResult = brandService.findAll(pageable);

        assertNotNull(wsResult);
        assertEquals(1, wsResult.getTotalRecords());
        assertEquals(1, wsResult.getDtoList().size());
        assertEquals(0, wsResult.getPage());
    }

    @Test
    void testFindAll_WithSpecification_Success() {
        brandService.save(sampleBrandDto);

        Specification<Brand> searchSpec = (root, query, cb) -> cb.equal(root.get("identifier"), "BRD-001");
        Pageable pageable = PageRequest.of(0, 5);

        WsDto<BrandDto> wsResult = brandService.findAll(searchSpec, pageable);

        assertNotNull(wsResult);
        assertEquals(1, wsResult.getTotalRecords());
        assertEquals("BRD-001", wsResult.getDtoList().getFirst().getIdentifier());
    }

    @Test
    void testFindAllActive() {
        brandService.save(sampleBrandDto);

        List<BrandDto> activeList = brandService.findAllActive();

        assertNotNull(activeList);
        assertEquals(1, activeList.size());
        assertTrue(activeList.getFirst().isStatus());
    }

    @Test
    void testUpdate_Success() {
        brandService.save(sampleBrandDto);

        BrandDto updateDto = new BrandDto();
        updateDto.setIdentifier("BRD-001");
        updateDto.setDescription("Updated Brand Description Metadata");
        updateDto.setIcon(mockFile);

        BrandDto result = brandService.update(updateDto);

        assertNotNull(result);
        assertTrue(result.getMessage().contains("Updated"));

        Brand updatedEntity = brandRepository.findByIdentifier("BRD-001");
        assertEquals("Updated Brand Description Metadata", updatedEntity.getDescription());
    }

    @Test
    void testUpdate_BrandNotFound() {
        BrandDto nonExistentDto = new BrandDto();
        nonExistentDto.setIdentifier("MISSING-ID");

        BrandDto result = brandService.update(nonExistentDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Brand not found", result.getMessage());
    }

    @Test
    void testToggleStatus() {
        brandService.save(sampleBrandDto);

        BrandDto toggledOff = brandService.toggleStatus("BRD-001");
        assertFalse(toggledOff.isStatus());

        BrandDto toggledOn = brandService.toggleStatus("BRD-001");
        assertTrue(toggledOn.isStatus());
    }

    @Test
    void testDelete_Success() {
        brandService.save(sampleBrandDto);

        boolean deletionFlag = brandService.delete("BRD-001");
        assertTrue(deletionFlag);

        Brand deletedEntity = brandRepository.findByIdentifier("BRD-001");
        assertNotNull(deletedEntity);
        assertTrue(deletedEntity.isDeleted());
    }

    @Test
    void testDelete_BrandNotFound() {
        boolean deletionFlag = brandService.delete("UNKNOWN-ID");
        assertFalse(deletionFlag);
    }
}