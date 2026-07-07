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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

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


    @Test
    void save_Success() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse entity = new Warehouse();

        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Warehouse.class))
                .thenReturn(entity);

        Mockito.when(warehouseRepository.save(entity))
                .thenReturn(entity);

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertEquals("WH1", response.getIdentifier());
        Mockito.verify(warehouseRepository).save(entity);
    }

    @Test
    void save_Failure_WhenExists() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(new Warehouse());

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Warehouse already exists", response.getMessage());

        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void update_Success() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse existing = new Warehouse();
        existing.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(existing);

        Mockito.doNothing().when(modelMapper).map(dto, existing);

        Mockito.when(warehouseRepository.save(existing))
                .thenReturn(existing);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertEquals("WH1", response.getIdentifier());
        Mockito.verify(warehouseRepository).save(existing);
    }

    @Test
    void update_Failure_WhenNotFound() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(null);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));

        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }


    @Test
    void findByIdentifier_Success() {
        Warehouse entity = new Warehouse();
        WarehouseDto dto = new WarehouseDto();

        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(entity);

        Mockito.when(modelMapper.map(entity, WarehouseDto.class))
                .thenReturn(dto);

        WarehouseDto response = warehouseService.findByIdentifier("WH1");

        Assertions.assertNotNull(response);
    }


    @Test
    void findAll_List() {
        List<Warehouse> entities = List.of(new Warehouse());
        List<WarehouseDto> dtos = List.of(new WarehouseDto());

        Type listType = new TypeToken<List<WarehouseDto>>() {}.getType();

        Mockito.when(warehouseRepository.findByDeletedFalse())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);

        List<WarehouseDto> response = warehouseService.findAll();

        Assertions.assertEquals(1, response.size());
    }


    @Test
    void findAll_WithSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Warehouse warehouse = new Warehouse();
        WarehouseDto dto = new WarehouseDto();
        Page<Warehouse> page = new PageImpl<>(List.of(warehouse));
        Mockito.when(warehouseRepository.findAll(
                        Mockito.<Specification<Warehouse>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(page);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);
        Page<WarehouseDto> response =
                warehouseService.findAll(pageable, "WH");
        Assertions.assertEquals(1, response.getContent().size());
        Mockito.verify(warehouseRepository)
                .findAll(
                        Mockito.<Specification<Warehouse>>any(),
                        Mockito.eq(pageable)
                );
    }

    @Test
    void findAll_WithoutSearch() {
        Pageable pageable = PageRequest.of(0, 10);

        Warehouse warehouse = new Warehouse();
        Page<Warehouse> page = new PageImpl<>(List.of(warehouse));

        WarehouseDto dto = new WarehouseDto();

        Mockito.when(warehouseRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        Page<WarehouseDto> response = warehouseService.findAll(pageable, null);

        Assertions.assertEquals(1, response.getContent().size());
    }


    @Test
    void delete_Success() {
        Warehouse warehouse = new Warehouse();
        warehouse.setDeleted(false);

        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(warehouse);

        Mockito.when(warehouseRepository.save(warehouse))
                .thenReturn(warehouse);

        warehouseService.delete("WH1");

        Assertions.assertTrue(warehouse.isDeleted());
        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void delete_WhenNotFound() {
        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(null);

        warehouseService.delete("WH1");

        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }
}