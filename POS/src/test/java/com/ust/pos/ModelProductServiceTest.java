package com.ust.pos;

import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ModelProductServiceTest {

    @InjectMocks
    private ModelProductServiceImpl modelProductService;

    @Mock
    private ModelProductRepository modelProductRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTestSuccess() {

        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("MODEL1");

        ModelProduct modelProduct = new ModelProduct();

        Mockito.when(
                modelProductRepository.findByIdentifierAndDeletedFalse("MODEL1")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(dto, ModelProduct.class)
        ).thenReturn(modelProduct);

        ModelProductDto response = modelProductService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(modelProductRepository)
                .save(modelProduct);

        Assertions.assertFalse(modelProduct.getDeleted());
    }

    @Test
    void saveTestFailure() {

        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("MODEL1");

        Mockito.when(
                modelProductRepository.findByIdentifierAndDeletedFalse("MODEL1")
        ).thenReturn(new ModelProduct());

        ModelProductDto response = modelProductService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Model - MODEL1 already exists",
                response.getMessage()
        );

        Mockito.verify(modelProductRepository,
                Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateTestIdNotFound() {

        ModelProductDto dto = new ModelProductDto();
        dto.setId(1L);
        dto.setIdentifier("MODEL1");

        Mockito.when(modelProductRepository.findById(1L))
                .thenReturn(Optional.empty());

        ModelProductDto response =
                modelProductService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Mockito.verify(modelProductRepository,
                Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateTestDuplicateIdentifier() {

        ModelProductDto dto = new ModelProductDto();
        dto.setId(1L);
        dto.setIdentifier("NEW");

        ModelProduct existing = new ModelProduct();
        existing.setIdentifier("OLD");

        Mockito.when(modelProductRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(
                modelProductRepository.findByIdentifierAndDeletedFalse("NEW")
        ).thenReturn(new ModelProduct());

        ModelProductDto response =
                modelProductService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Model Already Exists",
                response.getMessage()
        );
    }

    @Test
    void updateTestSuccess() {

        ModelProductDto dto = new ModelProductDto();
        dto.setId(1L);
        dto.setIdentifier("MODEL1");

        ModelProduct existing = new ModelProduct();
        existing.setId(1L);
        existing.setIdentifier("MODEL1");

        Mockito.when(modelProductRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        ModelProductDto response =
                modelProductService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper)
                .map(dto, existing);

        Mockito.verify(modelProductRepository)
                .save(existing);
    }

    @Test
    void findByIdentifierTest() {

        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setIdentifier("MODEL1");

        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("MODEL1");

        Mockito.when(
                modelProductRepository.findByIdentifierAndDeletedFalse("MODEL1")
        ).thenReturn(modelProduct);

        Mockito.when(
                modelMapper.map(modelProduct, ModelProductDto.class)
        ).thenReturn(dto);

        ModelProductDto response =
                modelProductService.findByIdentifier("MODEL1");

        Assertions.assertEquals(
                "MODEL1",
                response.getIdentifier()
        );
    }

    @Test
    void findAllTest() {

        List<ModelProduct> entities =
                List.of(new ModelProduct(), new ModelProduct());

        List<ModelProductDto> dtos =
                List.of(new ModelProductDto(), new ModelProductDto());

        Mockito.when(
                modelProductRepository.findByDeletedFalse()
        ).thenReturn(entities);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(entities),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        List<ModelProductDto> response =
                modelProductService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<ModelProduct> entities =
                List.of(new ModelProduct());

        Page<ModelProduct> page =
                new PageImpl<>(entities, pageable, 1);

        List<ModelProductDto> dtoList =
                List.of(new ModelProductDto());

        Type listType =
                new TypeToken<List<ModelProductDto>>() {}.getType();

        Mockito.when(
                modelProductRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(entities, listType)
        ).thenReturn(dtoList);

        WsDto<ModelProductDto> response =
                modelProductService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void findAllSearchTest() {

        Pageable pageable = PageRequest.of(0, 10);

        ModelProduct modelProduct = new ModelProduct();

        Page<ModelProduct> page =
                new PageImpl<>(List.of(modelProduct));

        Mockito.when(
                modelProductRepository
                        .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                                "MODEL",
                                pageable
                        )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(modelProduct, ModelProductDto.class)
        ).thenReturn(new ModelProductDto());

        Page<ModelProductDto> response =
                modelProductService.findAll("MODEL", pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void deleteTest() {

        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setDeleted(false);

        Mockito.when(
                modelProductRepository.findByIdentifierAndDeletedFalse("MODEL1")
        ).thenReturn(modelProduct);

        modelProductService.delete("MODEL1");

        Assertions.assertTrue(modelProduct.getDeleted());

        Mockito.verify(modelProductRepository)
                .save(modelProduct);
    }

    @Test
    void deleteNotFoundTest() {

        Mockito.when(
                modelProductRepository.findByIdentifierAndDeletedFalse("MODEL1")
        ).thenReturn(null);

        modelProductService.delete("MODEL1");

        Mockito.verify(modelProductRepository,
                Mockito.never()).save(Mockito.any());
    }

    @Test
    void toggleStatusSuccess() {

        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setStatus(true);

        Mockito.when(
                modelProductRepository.findByIdentifierAndDeletedFalse("MODEL1")
        ).thenReturn(modelProduct);

        modelProductService.toggleStatus("MODEL1");

        Assertions.assertFalse(modelProduct.getStatus());

        Mockito.verify(modelProductRepository)
                .save(modelProduct);
    }

    @Test
    void toggleStatusNotFound() {

        Mockito.when(
                modelProductRepository.findByIdentifierAndDeletedFalse("MODEL1")
        ).thenReturn(null);

        modelProductService.toggleStatus("MODEL1");

        Mockito.verify(modelProductRepository,
                Mockito.never()).save(Mockito.any());
    }
}