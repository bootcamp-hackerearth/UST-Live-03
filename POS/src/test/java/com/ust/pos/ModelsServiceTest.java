package com.ust.pos;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.impl.ModelsServiceImpl;
import org.junit.jupiter.api.Assertions;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void findByIdentifierSuccessTest() {

        Models models = new Models();
        models.setIdentifier("MDL01");

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL01");

        when(modelsRepository.findByIdentifier("MDL01"))
                .thenReturn(models);

        when(modelMapper.map(models, ModelsDto.class))
                .thenReturn(dto);

        ModelsDto response =
                modelsService.findByIdentifier("MDL01");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "MDL01",
                response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        when(modelsRepository.findByIdentifier("MDL01"))
                .thenReturn(null);

        when(modelMapper.map(null, ModelsDto.class))
                .thenReturn(null);

        ModelsDto response =
                modelsService.findByIdentifier("MDL01");

        Assertions.assertNull(response);
    }

    @Test
    void saveSuccessTest() {

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL01");

        Models models = new Models();

        when(modelsRepository.findByIdentifier("MDL01"))
                .thenReturn(null);

        when(modelMapper.map(dto, Models.class))
                .thenReturn(models);

        ModelsDto response =
                modelsService.save(dto);

        Assertions.assertEquals(
                "MDL01",
                response.getIdentifier());

        Assertions.assertNull(response.getMessage());

        verify(modelsRepository)
                .save(models);
    }

    @Test
    void saveFailureTest() {

        Models existing = new Models();

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL01");

        when(modelsRepository.findByIdentifier("MDL01"))
                .thenReturn(existing);

        ModelsDto response =
                modelsService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Models with identifier - MDL01 already exists",
                response.getMessage());

        verify(modelsRepository, never())
                .save(any());
    }

    @Test
    void updateSuccessTest() {

        Models existing = new Models();
        existing.setIdentifier("MDL01");

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL01");

        when(modelsRepository.findByIdentifier("MDL01"))
                .thenReturn(existing);

        ModelsDto response =
                modelsService.update(dto);

        Assertions.assertEquals(
                "MDL01",
                response.getIdentifier());

        verify(modelMapper)
                .map(dto, existing);

        verify(modelsRepository)
                .save(existing);
    }

    @Test
    void updateFailureTest() {

        ModelsDto dto = new ModelsDto();
        dto.setIdentifier("MDL01");

        when(modelsRepository.findByIdentifier("MDL01"))
                .thenReturn(null);

        ModelsDto response =
                modelsService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Models with identifier - MDL01 not found",
                response.getMessage());

        verify(modelsRepository, never())
                .save(any());
    }

    @Test
    void deleteSuccessTest() {

        doNothing().when(modelsRepository)
                .deleteByIdentifier("MDL01");

        modelsService.delete("MDL01");

        verify(modelsRepository)
                .deleteByIdentifier("MDL01");
    }

    @Test
    void findAllTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Models> modelsList =
                List.of(new Models(), new Models());

        Page<Models> page =
                new PageImpl<>(
                        modelsList,
                        pageable,
                        2
                );

        List<ModelsDto> dtoList =
                List.of(new ModelsDto(), new ModelsDto());

        when(modelsRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(modelsList), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<ModelsDto> result =
                modelsService.findAll(pageable);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                2,
                result.getContent().size());

        Assertions.assertEquals(
                0,
                result.getPage());

        Assertions.assertEquals(
                10,
                result.getSizePerPage());

        Assertions.assertEquals(
                1,
                result.getTotalPages());

        Assertions.assertEquals(
                2,
                result.getTotalRecords());
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Models> emptyList =
                List.of();

        Page<Models> page =
                new PageImpl<>(
                        emptyList,
                        pageable,
                        0
                );

        when(modelsRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(emptyList), any(Type.class)))
                .thenReturn(List.of());

        WsDto<ModelsDto> result =
                modelsService.findAll(pageable);

        Assertions.assertNotNull(result);

        Assertions.assertTrue(
                result.getContent().isEmpty());

        Assertions.assertEquals(
                0,
                result.getTotalRecords());
    }

    @Test
    void toggleStatusSuccessTest() {

        Models models = new Models();
        models.setStatus(true);

        when(modelsRepository.findByIdentifier("MDL01"))
                .thenReturn(models);

        modelsService.toggleStatus("MDL01");

        Assertions.assertFalse(models.isStatus());

        verify(modelsRepository)
                .save(models);
    }

    @Test
    void toggleStatusFailureTest() {

        when(modelsRepository.findByIdentifier("MDL01"))
                .thenReturn(null);

        modelsService.toggleStatus("MDL01");

        verify(modelsRepository, never())
                .save(any());
    }
}