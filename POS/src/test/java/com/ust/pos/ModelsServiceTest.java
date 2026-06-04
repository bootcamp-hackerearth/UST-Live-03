package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.impl.ModelsServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ModelsServiceTest {

    @Mock
    private ModelsRepository modelsRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ModelsServiceImpl modelsService;

    @Test
    void saveSuccess() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MODEL-1");

        Models models = new Models();

        Mockito.when(modelsRepository.findByIdentifier("MODEL-1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Models.class))
                .thenReturn(models);

        ModelsDto response = modelsService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("MODEL-1", response.getIdentifier());
    }

    @Test
    void saveFailure_duplicateIdentifier() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MODEL-1");

        Models existing = new Models();

        Mockito.when(modelsRepository.findByIdentifier("MODEL-1"))
                .thenReturn(existing);

        ModelsDto response = modelsService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exist"));
        Mockito.verify(modelsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdSuccess() {
        Models models = new Models();
        ModelsDto dto = new ModelsDto();

        Mockito.when(modelsRepository.findById(1L))
                .thenReturn(Optional.of(models));
        Mockito.when(modelMapper.map(models, ModelsDto.class))
                .thenReturn(dto);

        ModelsDto response = modelsService.findById(1L);

        Assertions.assertNotNull(response);
    }

    @Test
    void findByIdFailure() {
        Mockito.when(modelsRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> modelsService.findById(99L)
        );

        Assertions.assertTrue(exception.getMessage().contains("Models not found"));
    }

    @Test
    void findAllSuccess() {
        Models models = new Models();
        ModelsDto dto = new ModelsDto();

        Page<Models> page = new PageImpl<>(List.of(models));

        Mockito.when(modelsRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(dto));

        Pageable pageable = PageRequest.of(0, 10);
        List<ModelsDto> response = modelsService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void updateSuccess() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MODEL-1");

        Models models = new Models();

        Mockito.when(modelsRepository.findByIdentifier("MODEL-1"))
                .thenReturn(models);
        Mockito.when(modelsRepository.save(models))
                .thenReturn(models);

        ModelsDto response = modelsService.update(dto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateFailure_ModelNotFound() {
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MODEL-404");

        Mockito.when(modelsRepository.findByIdentifier("MODEL-404"))
                .thenReturn(null);

        ModelsDto response = modelsService.update(dto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void changeModelsStatusSuccess() {
        Models models = new Models();
        ModelsDto dto = new ModelsDto();

        Mockito.when(modelsRepository.findByIdentifier("MODEL-1"))
                .thenReturn(models);
        Mockito.when(modelsRepository.save(models))
                .thenReturn(models);
        Mockito.when(modelMapper.map(models, ModelsDto.class))
                .thenReturn(dto);

        ModelsDto response = modelsService.changeModelsStatus("MODEL-1", true);

        Assertions.assertNotNull(response);
    }

    @Test
    void deleteByIdSuccess() {
        Mockito.doNothing()
                .when(modelsRepository)
                .deleteById(1L);

        modelsService.deleteById(1L);

        Mockito.verify(modelsRepository, Mockito.times(1))
                .deleteById(1L);
    }
}