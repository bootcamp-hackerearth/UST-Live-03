package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.impl.ModelsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {

    @InjectMocks
    private ModelsServiceImpl modelsService;
    @Mock
    private ModelsRepository modelsRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void testFindByIdentifier_Success() {
        String identifier = "MODEL-001";

        Models models = new Models();
        models.setIdentifier(identifier);

        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier(identifier);

        when(modelsRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class))
                .thenReturn(modelsDto);

        ModelsDto result = modelsService.findByIdentifier(identifier);

        assertEquals(identifier, result.getIdentifier());
    }

    @Test
    void testSave_Success() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MODEL-001");

        Models models = new Models();
        models.setIdentifier("MODEL-001");

        when(modelsRepository.findByIdentifier("MODEL-001"))
                .thenReturn(null);
        when(modelMapper.map(modelsDto, Models.class))
                .thenReturn(models);

        ModelsDto result = modelsService.save(modelsDto);

        assertTrue(result.isSuccess());
        assertEquals("Model Saved Successfully", result.getMessage());

        verify(modelsRepository).save(models);
    }

    @Test
    void testSave_AlreadyExists() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MODEL-001");

        Models existingModels = new Models();
        existingModels.setIdentifier("MODEL-001");
        existingModels.setDeleted(false);

        when(modelsRepository.findByIdentifier("MODEL-001"))
                .thenReturn(existingModels);

        ModelsDto result = modelsService.save(modelsDto);

        assertFalse(result.isSuccess());
        assertEquals("Model with identifier - MODEL-001 already exists", result.getMessage());

        verify(modelsRepository, never()).save(any(Models.class));
    }

    @Test
    void testSave_DeletedModelExists() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MODEL-001");

        Models existingModels = new Models();
        existingModels.setIdentifier("MODEL-001");
        existingModels.setDeleted(true);

        when(modelsRepository.findByIdentifier("MODEL-001"))
                .thenReturn(existingModels);

        ModelsDto result = modelsService.save(modelsDto);

        assertFalse(result.isSuccess());
        assertEquals("Model with identifier - MODEL-001 already exists", result.getMessage());

        verify(modelsRepository, never()).save(any(Models.class));
    }

    @Test
    void testUpdate_Success() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MODEL-001");

        Models existingModels = new Models();
        existingModels.setIdentifier("MODEL-001");

        when(modelsRepository.findByIdentifier("MODEL-001"))
                .thenReturn(existingModels);

        ModelsDto result = modelsService.update(modelsDto);

        assertTrue(result.isSuccess());
        assertEquals("Model Updated Successfully", result.getMessage());

        verify(modelMapper).map(modelsDto, existingModels);
        verify(modelsRepository).save(existingModels);
    }

    @Test
    void testUpdate_NotFound() {
        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MODEL-001");

        when(modelsRepository.findByIdentifier("MODEL-001"))
                .thenReturn(null);

        ModelsDto result = modelsService.update(modelsDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Models with identifier - MODEL-001 was deleted and cannot be created again.",
                result.getMessage()
        );

        verify(modelsRepository, never()).save(any(Models.class));
    }

    @Test
    void testDelete_Success() {
        String identifier = "MODEL-001";

        Models models = new Models();
        models.setIdentifier(identifier);
        models.setDeleted(false);

        when(modelsRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(models);

        modelsService.delete(identifier);

        assertTrue(models.getDeleted());

        verify(modelsRepository).save(models);
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Models models = new Models();
        models.setIdentifier("MODEL-001");

        Page<Models> modelPage =
                new PageImpl<>(List.of(models), pageable, 1);

        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MODEL-001");

        when(modelsRepository.findAllByDeletedFalse(pageable))
                .thenReturn(modelPage);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(modelsDto));

        WsDto<ModelsDto> result = modelsService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void testToggleStatus_Success() {
        String identifier = "MODEL-001";

        Models models = new Models();
        models.setIdentifier(identifier);
        models.setStatus(true);

        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier(identifier);

        when(modelsRepository.findByIdentifier(identifier))
                .thenReturn(models);
        when(modelMapper.map(models, ModelsDto.class))
                .thenReturn(modelsDto);

        ModelsDto result = modelsService.toggleStatus(identifier);

        assertFalse(models.isStatus());
        assertNotNull(result);

        verify(modelsRepository).save(models);
    }

    @Test
    void testFindActiveModels_Success() {
        Models models = new Models();
        models.setIdentifier("MODEL-001");

        ModelsDto modelsDto = new ModelsDto();
        modelsDto.setIdentifier("MODEL-001");

        when(modelsRepository.findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(models));

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(modelsDto));

        List<ModelsDto> result = modelsService.findActiveModels();

        assertEquals(1, result.size());
        assertEquals("MODEL-001", result.get(0).getIdentifier());
    }

}