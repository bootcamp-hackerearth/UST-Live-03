package com.ust.pos;

import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.model.ModelProduct;
import com.ust.pos.model.ModelProductRepository;
import com.ust.pos.modelproduct.service.impl.ModelProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ModelProductServiceTest {

    @InjectMocks
    private ModelProductServiceImpl modelProductService;

    @Mock
    private ModelProductRepository modelProductRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest_Success() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("Admin");
        ModelProduct entity = new ModelProduct();
        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, ModelProduct.class))
                .thenReturn(entity);
        Mockito.when(modelProductRepository.save(entity))
                .thenReturn(entity);
        ModelProductDto response = modelProductService.save(dto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(modelProductRepository).save(entity);
    }

    @Test
    void saveTest_Failure_WhenAlreadyExists() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("Admin");
        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(new ModelProduct());
        ModelProductDto response = modelProductService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(modelProductRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest_Success() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("Admin");
        ModelProduct existing = new ModelProduct();
        ModelProduct mapped = new ModelProduct();
        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(existing);
        Mockito.when(modelMapper.map(dto, ModelProduct.class))
                .thenReturn(mapped);
        Mockito.when(modelProductRepository.save(mapped))
                .thenReturn(mapped);
        ModelProductDto response = modelProductService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(modelProductRepository).save(mapped);
    }

    @Test
    void updateTest_Failure_WhenNotFound() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("Admin");
        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(null);
        ModelProductDto response = modelProductService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(modelProductRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        ModelProduct entity = new ModelProduct();
        entity.setIdentifier("Admin");
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("Admin");
        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(entity);
        Mockito.when(modelMapper.map(entity, ModelProductDto.class))
                .thenReturn(dto);
        ModelProductDto response = modelProductService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findAllTest() {
        List<ModelProduct> entities = List.of(new ModelProduct());
        List<ModelProductDto> dtos = List.of(new ModelProductDto());
        Type listType = new TypeToken<List<ModelProductDto>>() {
        }.getType();
        Mockito.when(modelProductRepository.findByDeletedFalse())
                .thenReturn(entities);
        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);
        List<ModelProductDto> response = modelProductService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void togglestatusTest() {
        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setStatus(false);
        Mockito.when(modelProductRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(modelProduct);
        Mockito.when(modelProductRepository.save(modelProduct))
                .thenReturn(modelProduct);
        modelProductService.toggleStatus("Admin");
        Assertions.assertTrue(modelProduct.isStatus());
        Mockito.verify(modelProductRepository).save(modelProduct);
    }

    @Test
    void deleteTest() {

        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setDeleted(false);
        Mockito.when(
                modelProductRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(modelProduct);
        Mockito.when(
                modelProductRepository.save(modelProduct)
        ).thenReturn(modelProduct);
        modelProductService.delete("Admin");
        Assertions.assertTrue(modelProduct.isDeleted());
        Mockito.verify(modelProductRepository)
                .save(modelProduct);
    }

    @Test
    void deleteTest_NotFound() {

        Mockito.when(
                modelProductRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(null);

        modelProductService.delete("Admin");

        Mockito.verify(
                modelProductRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void toggleStatus_NotFound() {

        Mockito.when(
                modelProductRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(null);

        modelProductService.toggleStatus("Admin");

        Mockito.verify(
                modelProductRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void findAllPageableWithoutSearchTest() {

        Pageable pageable = PageRequest.of(0, 10);
        ModelProduct modelProduct = new ModelProduct();
        Page<ModelProduct> page =
                new PageImpl<>(List.of(modelProduct));
        Mockito.when(
                modelProductRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);
        Page<ModelProductDto> result =
                modelProductService.findAll(pageable, null);
        Assertions.assertEquals(1, result.getContent().size());
        Mockito.verify(modelProductRepository)
                .findByDeletedFalse(pageable);
    }

    @Test
    void findAllPageableWithSearchTest() {

        Pageable pageable = PageRequest.of(0, 10);
        ModelProduct modelProduct = new ModelProduct();
        Page<ModelProduct> page =
                new PageImpl<>(List.of(modelProduct));
        Mockito.when(
                modelProductRepository
                        .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                                "Admin",
                                pageable
                        )
        ).thenReturn(page);
        Page<ModelProductDto> result =
                modelProductService.findAll(pageable, "Admin");
        Assertions.assertEquals(1, result.getContent().size());
        Mockito.verify(modelProductRepository)
                .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                        "Admin",
                        pageable
                );
    }
}