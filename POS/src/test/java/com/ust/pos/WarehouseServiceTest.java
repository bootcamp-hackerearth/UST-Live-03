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

    // ---------------- SAVE ----------------

    @Test
    void saveTest_Success() {
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
    void saveTest_Failure_WhenAlreadyExists() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(new Warehouse());

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(warehouseRepository, Mockito.never())
                .save(Mockito.any());
    }

    // ---------------- UPDATE ----------------

    @Test
    void updateTest_Success() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Warehouse existing = new Warehouse();
        existing.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(dto, existing);

        Mockito.when(warehouseRepository.save(existing))
                .thenReturn(existing);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertEquals("WH1", response.getIdentifier());
        Mockito.verify(warehouseRepository).save(existing);
    }

    @Test
    void updateTest_Failure_WhenNotFound() {
        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(null);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(warehouseRepository, Mockito.never())
                .save(Mockito.any());
    }

    // ---------------- FIND BY IDENTIFIER ----------------

    @Test
    void findByIdentifierTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH1");

        Mockito.when(warehouseRepository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        WarehouseDto response = warehouseService.findByIdentifier("WH1");

        Assertions.assertEquals("WH1", response.getIdentifier());
    }

    // ---------------- FIND ALL ----------------

    @Test
    void findAllTest() {
        List<Warehouse> entities = List.of(new Warehouse());
        List<WarehouseDto> dtos = List.of(new WarehouseDto());

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();

        Mockito.when(warehouseRepository.findAll())
                .thenReturn(entities);
        Mockito.when(modelMapper.map(entities, listType))
                .thenReturn(dtos);

        List<WarehouseDto> response = warehouseService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    // ---------------- DELETE ----------------

    @Test
    void deleteTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        Mockito.when(
                        warehouseRepository.findByIdentifierAndDeletedFalse("WH1"))
                .thenReturn(warehouse);

        warehouseService.delete("WH1");

        Assertions.assertTrue(warehouse.isDeleted());

        Mockito.verify(warehouseRepository)
                .findByIdentifierAndDeletedFalse("WH1");

        Mockito.verify(warehouseRepository)
                .save(warehouse);
    }

    @Test
    void findAll_WithPagination_ShouldReturnWarehouseDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH1");

        Page<Warehouse> page =
                new PageImpl<>(List.of(warehouse));

        Mockito.when(
                        warehouseRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Mockito.when(
                        modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(warehouseDto);

        Page<WarehouseDto> response =
                warehouseService.findAll(null, pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getContent().size());

        Assertions.assertEquals(
                "WH1",
                response.getContent().get(0).getIdentifier());

        Mockito.verify(warehouseRepository)
                .findByDeletedFalse(pageable);

        Mockito.verify(modelMapper)
                .map(warehouse, WarehouseDto.class);
    }

    @Test
    void findAll_WithSearch_ShouldReturnWarehouseDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH1");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("WH1");

        Page<Warehouse> page =
                new PageImpl<>(List.of(warehouse));

        Mockito.when(
                        warehouseRepository
                                .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                                        "WH",
                                        pageable))
                .thenReturn(page);

        Mockito.when(
                        modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(warehouseDto);

        Page<WarehouseDto> response =
                warehouseService.findAll("WH", pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getContent().size());

        Assertions.assertEquals(
                "WH1",
                response.getContent().get(0).getIdentifier());

        Mockito.verify(warehouseRepository)
                .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                        "WH",
                        pageable);

        Mockito.verify(modelMapper)
                .map(warehouse, WarehouseDto.class);
    }
}