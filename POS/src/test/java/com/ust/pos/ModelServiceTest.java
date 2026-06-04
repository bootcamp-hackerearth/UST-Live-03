package com.ust.pos;

import com.ust.pos.dto.ModelDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ModelServiceImpl modelService;

    @Test
    void findAll_success() {

        Model model = new Model();
        ModelDto dto = new ModelDto();

        Page<Model> page = new PageImpl<>(List.of(model));

        Mockito.when(modelRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(model)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<ModelDto> result =
                modelService.findAll(Mockito.mock(Pageable.class));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void save_success() {

        ModelDto input = new ModelDto();
        input.setIdentifier("MOD01");

        Mockito.when(modelRepository.findByIdentifier("MOD01"))
                .thenReturn(null);

        Model entity = new Model();

        Mockito.when(modelMapper.map(input, Model.class))
                .thenReturn(entity);

        Mockito.when(modelRepository.save(entity))
                .thenReturn(entity);

        ModelDto result = modelService.save(input);

        Assertions.assertEquals("MOD01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        ModelDto input = new ModelDto();
        input.setIdentifier("MOD01");

        Mockito.when(modelRepository.findByIdentifier("MOD01"))
                .thenReturn(new Model());

        ModelDto result = modelService.save(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Model model = new Model();
        model.setIdentifier("MOD01");

        ModelDto dto = new ModelDto();
        dto.setIdentifier("MOD01");

        Mockito.when(modelRepository.findByIdentifier("MOD01"))
                .thenReturn(model);

        Mockito.when(modelMapper.map(model, ModelDto.class))
                .thenReturn(dto);

        ModelDto result =
                modelService.findByIdentifier("MOD01");

        Assertions.assertEquals("MOD01", result.getIdentifier());
    }

    @Test
    void update_success() {

        ModelDto input = new ModelDto();
        input.setIdentifier("MOD01");

        Model existing = new Model();

        Mockito.when(modelRepository.findByIdentifier("MOD01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        Mockito.when(modelRepository.save(existing))
                .thenReturn(existing);

        ModelDto result = modelService.update(input);

        Assertions.assertEquals("MOD01", result.getIdentifier());
    }

    @Test
    void update_failure_notFound() {

        ModelDto input = new ModelDto();
        input.setIdentifier("MOD01");

        Mockito.when(modelRepository.findByIdentifier("MOD01"))
                .thenReturn(null);

        ModelDto result = modelService.update(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(modelRepository).deleteByIdentifier("MOD01");

        modelService.delete("MOD01");

        Mockito.verify(modelRepository)
                .deleteByIdentifier("MOD01");
    }

    @Test
    void changeToggleStatus_enable() {

        Model model = new Model();
        model.setStatus(false);

        ModelDto dto = new ModelDto();

        Mockito.when(modelRepository.findByIdentifier("MOD01"))
                .thenReturn(model);

        Mockito.when(modelRepository.save(model))
                .thenReturn(model);

        Mockito.when(modelMapper.map(model, ModelDto.class))
                .thenReturn(dto);

        ModelDto result =
                modelService.changeToggleStatus("MOD01", true);

        Assertions.assertTrue(model.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Model model = new Model();
        model.setStatus(true);

        ModelDto dto = new ModelDto();

        Mockito.when(modelRepository.findByIdentifier("MOD01"))
                .thenReturn(model);

        Mockito.when(modelRepository.save(model))
                .thenReturn(model);

        Mockito.when(modelMapper.map(model, ModelDto.class))
                .thenReturn(dto);

        ModelDto result =
                modelService.changeToggleStatus("MOD01", false);

        Assertions.assertFalse(model.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void findActiveStatus_success() {

        Model active = new Model();
        active.setStatus(true);

        Model inactive = new Model();
        inactive.setStatus(false);

        Mockito.when(modelRepository.findAll())
                .thenReturn(List.of(active, inactive));

        ModelDto dto = new ModelDto();

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(active)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<ModelDto> result = modelService.findActiveStatus();

        Assertions.assertEquals(1, result.size());
    }
}