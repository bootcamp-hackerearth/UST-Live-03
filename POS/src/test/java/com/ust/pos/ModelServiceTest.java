package com.ust.pos;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.models.service.impl.ModelServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

    @InjectMocks
    private ModelServiceImpl modelService;

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private ModelMapper modelMapper;


    @Test
    void saveTest_success() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MODEL001");

        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(null);

        Model model = new Model();
        Mockito.when(modelMapper.map(dto, Model.class))
                .thenReturn(model);

        Mockito.when(modelRepository.save(model))
                .thenReturn(model);

        ModelDto response = modelService.save(dto);

        Assertions.assertEquals("MODEL001", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTest_duplicate() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MODEL001");

        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(new Model());

        ModelDto response = modelService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTest_softDeleted() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MODEL001");

        Model existing = new Model();
        existing.setDeleted(true);

        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(existing);

        ModelDto response = modelService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }


    @Test
    void findByIdentifierTest() {
        Model model = new Model();
        model.setIdentifier("MODEL001");

        ModelDto dto = new ModelDto();
        dto.setIdentifier("MODEL001");

        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(model);

        Mockito.when(modelMapper.map(model, ModelDto.class))
                .thenReturn(dto);

        ModelDto response = modelService.findByIdentifier("MODEL001");

        Assertions.assertEquals("MODEL001", response.getIdentifier());
    }


    @Test
    void updateTest_success() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MODEL001");

        Model existing = new Model();
        existing.setIdentifier("MODEL001");

        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(existing);

        Mockito.when(modelRepository.save(existing))
                .thenReturn(existing);

        ModelDto response = modelService.update(dto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTest_notFound() {
        ModelDto dto = new ModelDto();
        dto.setIdentifier("MODEL001");

        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(null);

        ModelDto response = modelService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }


    @Test
    void deleteTest_success() {
        Model model = new Model();
        model.setIdentifier("MODEL001");

        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(model);

        Mockito.when(modelRepository.save(model))
                .thenReturn(model);

        boolean result = modelService.delete("MODEL001");

        Assertions.assertTrue(result);
        Mockito.verify(modelRepository).save(model);
    }

    @Test
    void deleteTest_notFound() {
        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(null);

        boolean result = modelService.delete("MODEL001");

        Assertions.assertFalse(result);
    }


    @Test
    void toggleStatusTest() {
        Model model = new Model();
        model.setIdentifier("MODEL001");
        model.setStatus(true);

        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(model);

        Mockito.when(modelRepository.save(model))
                .thenReturn(model);

        modelService.toggleStatus("MODEL001");

        Assertions.assertFalse(model.getStatus());
    }

    @Test
    void toggleStatusTest_notFound() {
        Mockito.when(modelRepository.findByIdentifier("MODEL001"))
                .thenReturn(null);

        modelService.toggleStatus("MODEL001");

        Mockito.verify(modelRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findActiveModelsTest() {
        Model model = new Model();
        model.setIdentifier("MODEL001");
        model.setStatus(true);

        ModelDto dto = new ModelDto();
        dto.setIdentifier("MODEL001");

        List<Model> list = List.of(model);

        Type listType = new TypeToken<List<ModelDto>>() {}.getType();

        Mockito.when(modelRepository.findByStatusTrue())
                .thenReturn(list);

        Mockito.when(modelMapper.map(list, listType))
                .thenReturn(List.of(dto));

        List<ModelDto> response = modelService.findActiveModels();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("MODEL001", response.get(0).getIdentifier());
    }

    @Test
    void findAllPaginationTest() {

        Model model = new Model();
        model.setIdentifier("MODEL001");

        ModelDto dto = new ModelDto();
        dto.setIdentifier("MODEL001");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Model> page =
                new PageImpl<>(List.of(model), pageable, 1);

        Mockito.when(modelRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Type listType = new TypeToken<List<ModelDto>>() {}.getType();

        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(dto));

        PageDto<ModelDto> response = modelService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("MODEL001", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }
}