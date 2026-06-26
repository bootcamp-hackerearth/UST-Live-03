package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
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
class WarehouseServiceTest {

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    // FIND ALL

    @Test
    void findAll_Success() {

        List<Warehouse> entities = List.of(new Warehouse());
        List<WarehouseDto> dtos = List.of(new WarehouseDto());

        Type type = new TypeToken<List<WarehouseDto>>() {
        }.getType();

        Mockito.when(warehouseRepository.findByIsDeleteFalse())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, type))
                .thenReturn(dtos);

        List<WarehouseDto> result = warehouseService.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // SAVE

    @Test
    void save_Success() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse entity = new Warehouse();

        Mockito.when(warehouseRepository.findByIdentifierAndIsDeleteFalse("W1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Warehouse.class))
                .thenReturn(entity);

        Mockito.when(warehouseRepository.save(entity))
                .thenReturn(entity);

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(warehouseRepository).save(entity);
    }

    @Test
    void save_WhenExists_ShouldFail() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Mockito.when(warehouseRepository.findByIdentifierAndIsDeleteFalse("W1"))
                .thenReturn(new Warehouse());

        WarehouseDto result = warehouseService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Warehouse already exists", result.getMessage());

        Mockito.verify(warehouseRepository, Mockito.never())
                .save(Mockito.any());
    }

    // UPDATE

    @Test
    void update_Success() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse existing = new Warehouse();
        existing.setIdentifier("W1");

        Mockito.when(warehouseRepository.findByIdentifierAndIsDeleteFalse("W1"))
                .thenReturn(existing);

        Mockito.doNothing().when(modelMapper).map(dto, existing);

        Mockito.when(warehouseRepository.save(existing))
                .thenReturn(existing);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(warehouseRepository).save(existing);
    }

    @Test
    void update_WhenNotFound_ShouldFail() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Mockito.when(warehouseRepository.findByIdentifierAndIsDeleteFalse("W1"))
                .thenReturn(null);

        WarehouseDto result = warehouseService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));

        Mockito.verify(warehouseRepository, Mockito.never())
                .save(Mockito.any());
    }

    // DELETE

    @Test
    void delete_Success() {

        Warehouse entity = new Warehouse();
        entity.setIdentifier("W1");

        Mockito.when(warehouseRepository.findByIdentifierAndIsDeleteFalse("W1"))
                .thenReturn(entity);

        Mockito.when(warehouseRepository.save(entity)).thenReturn(entity);

        warehouseService.delete("W1");

        Assertions.assertTrue(entity.isDelete());

        Mockito.verify(warehouseRepository).save(entity);
    }

    @Test
    void delete_WhenNotFound_ShouldDoNothing() {

        Mockito.when(warehouseRepository.findByIdentifierAndIsDeleteFalse("W1"))
                .thenReturn(null);

        warehouseService.delete("W1");

        Mockito.verify(warehouseRepository, Mockito.never())
                .save(Mockito.any());
    }

    // FIND BY IDENTIFIER

    @Test
    void findByIdentifier_Success() {

        Warehouse entity = new Warehouse();
        entity.setIdentifier("W1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Mockito.when(warehouseRepository.findByIdentifierAndIsDeleteFalse("W1"))
                .thenReturn(entity);

        Mockito.when(modelMapper.map(entity, WarehouseDto.class))
                .thenReturn(dto);

        WarehouseDto result = warehouseService.findByIdentifier("W1");

        Assertions.assertEquals("W1", result.getIdentifier());
    }

    // PAGINATION

    @Test
    void findAll_Pageable_NoSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Warehouse entity = new Warehouse();
        entity.setIdentifier("W1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Page<Warehouse> page = new PageImpl<>(List.of(entity));

        Mockito.when(warehouseRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(entity, WarehouseDto.class))
                .thenReturn(dto);

        Page<WarehouseDto> result = warehouseService.findAll(pageable, null);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void findAll_WithSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Warehouse entity = new Warehouse();
        entity.setIdentifier("W1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Page<Warehouse> page = new PageImpl<>(List.of(entity));

        Mockito.when(warehouseRepository
                        .findByIdentifierContainingIgnoreCaseAndIsDeleteFalse("W", pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(entity, WarehouseDto.class))
                .thenReturn(dto);

        Page<WarehouseDto> result = warehouseService.findAll(pageable, "W");

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void findAll_WithBlankSearch_ShouldFallback() {

        Pageable pageable = PageRequest.of(0, 10);

        Warehouse entity = new Warehouse();
        entity.setIdentifier("W1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Page<Warehouse> page = new PageImpl<>(List.of(entity));

        Mockito.when(warehouseRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(entity, WarehouseDto.class))
                .thenReturn(dto);

        Page<WarehouseDto> result = warehouseService.findAll(pageable, " ");

        Assertions.assertEquals(1, result.getContent().size());
    }
}