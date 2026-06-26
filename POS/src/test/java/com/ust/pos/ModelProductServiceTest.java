package com.ust.pos;

import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.model.ModelProduct;
import com.ust.pos.model.ModelProductRepository;
import com.ust.pos.modelproduct.service.impl.ModelProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelProductServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ModelProductRepository modelProductRepository;

    @InjectMocks
    private ModelProductServiceImpl modelProductService;

    private ModelProduct modelProduct;
    private ModelProductDto modelProductDto;

    @BeforeEach
    void setUp() {

        modelProduct = new ModelProduct();
        modelProduct.setId(1L);
        modelProduct.setIdentifier("MODEL001");
        modelProduct.setStatus(true);

        modelProductDto = new ModelProductDto();
        modelProductDto.setIdentifier("MODEL001");
        modelProductDto.setStatus(true);
    }

    @Test
    void save_ShouldReturnFailure_WhenModelAlreadyExists() {

        when(modelProductRepository
                .findByIdentifierAndIsDeleteFalse("MODEL001"))
                .thenReturn(modelProduct);

        ModelProductDto result =
                modelProductService.save(modelProductDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Model - MODEL001 already exists",
                result.getMessage());

        verify(modelProductRepository, never()).save(any());
    }

    @Test
    void save_ShouldSaveModel_WhenNotExists() {

        when(modelProductRepository
                .findByIdentifierAndIsDeleteFalse("MODEL001"))
                .thenReturn(null);

        when(modelMapper.map(
                modelProductDto,
                ModelProduct.class))
                .thenReturn(modelProduct);

        ModelProductDto result =
                modelProductService.save(modelProductDto);

        assertNotNull(result);

        verify(modelMapper)
                .map(modelProductDto, ModelProduct.class);

        verify(modelProductRepository)
                .save(modelProduct);
    }

    @Test
    void update_ShouldReturnFailure_WhenModelNotFound() {

        when(modelProductRepository
                .findByIdentifierAndIsDeleteFalse("MODEL001"))
                .thenReturn(null);

        ModelProductDto result =
                modelProductService.update(modelProductDto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Model - MODEL001 not found",
                result.getMessage());

        verify(modelProductRepository, never()).save(any());
    }

    @Test
    void update_ShouldUpdateModel_WhenExists() {

        when(modelProductRepository
                .findByIdentifierAndIsDeleteFalse("MODEL001"))
                .thenReturn(modelProduct);

        ModelProductDto result =
                modelProductService.update(modelProductDto);

        assertNotNull(result);

        verify(modelMapper)
                .map(modelProductDto, modelProduct);

        verify(modelProductRepository)
                .save(modelProduct);
    }

    @Test
    void findByIdentifier_ShouldReturnMappedDto() {

        when(modelProductRepository
                .findByIdentifierAndIsDeleteFalse("MODEL001"))
                .thenReturn(modelProduct);

        when(modelMapper.map(
                modelProduct,
                ModelProductDto.class))
                .thenReturn(modelProductDto);

        ModelProductDto result =
                modelProductService.findByIdentifier("MODEL001");

        assertNotNull(result);
        assertEquals(
                "MODEL001",
                result.getIdentifier());
    }

    @Test
    void findAll_ShouldReturnMappedList() {

        List<ModelProduct> modelProducts =
                List.of(modelProduct);

        List<ModelProductDto> expected =
                List.of(modelProductDto);

        when(modelProductRepository.findByIsDeleteFalse())
                .thenReturn(modelProducts);

        when(modelMapper.map(
                eq(modelProducts),
                any(Type.class)))
                .thenReturn(expected);

        List<ModelProductDto> result =
                modelProductService.findAll();

        assertEquals(1, result.size());

        verify(modelProductRepository)
                .findByIsDeleteFalse();
    }

    @Test
    void delete_ShouldSoftDelete_WhenModelExists() {

        when(modelProductRepository
                .findByIdentifierAndIsDeleteFalse("MODEL001"))
                .thenReturn(modelProduct);

        modelProductService.delete("MODEL001");

        assertTrue(modelProduct.isDelete());

        verify(modelProductRepository)
                .save(modelProduct);
    }

    @Test
    void delete_ShouldDoNothing_WhenModelNotFound() {

        when(modelProductRepository
                .findByIdentifierAndIsDeleteFalse("MODEL001"))
                .thenReturn(null);

        modelProductService.delete("MODEL001");

        verify(modelProductRepository, never()).save(any());
    }

    @Test
    void updateStatusOnly_ShouldUpdateStatus() {

        when(modelProductRepository
                .findByIdentifierAndIsDeleteFalse("MODEL001"))
                .thenReturn(modelProduct);

        modelProductService.updateStatusOnly("MODEL001", false);

        assertFalse(modelProduct.getStatus());

        verify(modelProductRepository)
                .save(modelProduct);
    }

    @Test
    void findAll_WithPageable_ShouldReturnMappedList() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<ModelProduct> page =
                new PageImpl<>(List.of(modelProduct));

        List<ModelProductDto> expected =
                List.of(modelProductDto);

        when(modelProductRepository
                .findByIsDeleteFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()),
                any(Type.class)))
                .thenReturn(expected);

        List<ModelProductDto> result =
                modelProductService.findAll(pageable);

        assertEquals(1, result.size());

        verify(modelProductRepository)
                .findByIsDeleteFalse(pageable);
    }
}