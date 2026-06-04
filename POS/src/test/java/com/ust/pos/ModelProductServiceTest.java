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
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ModelProductServiceTest {

    @InjectMocks
    private ModelProductServiceImpl modelProductService;

    @Mock
    private ModelProductRepository modelProductRepository;

    @Mock
    private ModelMapper modelMapper;

    // ---------------- SAVE ----------------

    @Test
    void saveTest_Success() {
        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("Admin");

        ModelProduct entity = new ModelProduct();

        Mockito.when(modelProductRepository.findByIdentifier("Admin"))
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

        Mockito.when(modelProductRepository.findByIdentifier("Admin"))
                .thenReturn(new ModelProduct());

        ModelProductDto response = modelProductService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(modelProductRepository, Mockito.never())
                .save(Mockito.any());
    }

    // ---------------- UPDATE ----------------


    @Test
    void updateTest_Failure_IdNotFound() {

        ModelProductDto dto = new ModelProductDto();
        dto.setId(1L);
        dto.setIdentifier("M1");

        Mockito.when(modelProductRepository.findById(1L))
                .thenReturn(Optional.empty());

        ModelProductDto response = modelProductService.update(dto);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isSuccess());

        Mockito.verify(modelProductRepository, Mockito.never())
                .save(Mockito.any());
    }

    // ✅ 2. FAILURE: Identifier already exists
    @Test
    void updateTest_Failure_DuplicateIdentifier() {

        ModelProductDto dto = new ModelProductDto();
        dto.setId(1L);
        dto.setIdentifier("NEW_ID");

        ModelProduct existing = new ModelProduct();
        existing.setId(1L);
        existing.setIdentifier("OLD_ID");

        ModelProduct duplicate = new ModelProduct();
        duplicate.setIdentifier("NEW_ID");

        Mockito.when(modelProductRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(modelProductRepository.findByIdentifier("NEW_ID"))
                .thenReturn(duplicate);

        ModelProductDto response = modelProductService.update(dto);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Model Already Exists", response.getMessage());

        Mockito.verify(modelProductRepository, Mockito.never())
                .save(Mockito.any());
    }

    // ✅ 3. SUCCESS CASE
    @Test
    void updateTest_Success() {

        // ✅ Arrange
        ModelProductDto dto = new ModelProductDto();
        dto.setId(1L);                      // ✅ REQUIRED
        dto.setIdentifier("Admin");

        ModelProduct existing = new ModelProduct();
        existing.setId(1L);
        existing.setIdentifier("Admin");

        // ✅ Mock findById (correct method)
        Mockito.when(modelProductRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        // ✅ Correct mapper mocking (void method)
        Mockito.doNothing()
                .when(modelMapper)
                .map(Mockito.any(ModelProductDto.class), Mockito.any(ModelProduct.class));

        // ✅ Mock save
        Mockito.when(modelProductRepository.save(Mockito.any(ModelProduct.class)))
                .thenReturn(existing);

        // ✅ Act
        ModelProductDto response = modelProductService.update(dto);

        // ✅ Assert
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());

        // ✅ Verify correct object is saved
        Mockito.verify(modelProductRepository).save(existing);
    }

// ---------------- FIND BY IDENTIFIER ----------------

    @Test
    void findByIdentifierTest() {
        ModelProduct entity = new ModelProduct();
        entity.setIdentifier("Admin");

        ModelProductDto dto = new ModelProductDto();
        dto.setIdentifier("Admin");

        Mockito.when(modelProductRepository.findByIdentifier("Admin"))
                .thenReturn(entity);
        Mockito.when(modelMapper.map(entity, ModelProductDto.class))
                .thenReturn(dto);

        ModelProductDto response = modelProductService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    // ---------------- FIND ALL ----------------

    @Test
    void findAllTest() {
        List<ModelProduct> entities = List.of(new ModelProduct());
        List<ModelProductDto> dtos = List.of(new ModelProductDto());

        Type listType = new TypeToken<List<ModelProductDto>>() {
        }.getType();

        Mockito.when(modelProductRepository.findAll())
                .thenReturn(entities);
        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);

        List<ModelProductDto> response = modelProductService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    // ---------------- UPDATE STATUS ONLY ----------------

    @Test
    void updateStatusOnlyTest() {
        ModelProduct modelProduct = new ModelProduct();
        modelProduct.setStatus(false);

        Mockito.when(modelProductRepository.findByIdentifier("Admin"))
                .thenReturn(modelProduct);
        Mockito.when(modelProductRepository.save(modelProduct))
                .thenReturn(modelProduct);

        modelProductService.toggleStatus("Admin");

        Assertions.assertTrue(modelProduct.isStatus());
        Mockito.verify(modelProductRepository).save(modelProduct);
    }

    // ---------------- DELETE ----------------

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(modelProductRepository)
                .deleteByIdentifier("Admin");

        modelProductService.delete("Admin");

        Mockito.verify(modelProductRepository)
                .deleteByIdentifier("Admin");
    }

    @Test
    void findAll_WithPagination_ShouldReturnModelProductDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<ModelProduct> products = List.of(new ModelProduct());
        Page<ModelProduct> page = new PageImpl<>(products);

        List<ModelProductDto> productDtos = List.of(new ModelProductDto());

        Type listType = new TypeToken<List<ModelProductDto>>() {
        }.getType();

        Mockito.when(modelProductRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(products, listType))
                .thenReturn(productDtos);

        List<ModelProductDto> response = modelProductService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(modelProductRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(products, listType);
    }

}