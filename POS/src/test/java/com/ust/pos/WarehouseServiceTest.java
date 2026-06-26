package com.ust.pos;

import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Warehouse;
import com.ust.pos.model.WarehouseRepository;
import com.ust.pos.warehouse.service.impl.WarehouseServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    // ================= SAVE =================

    @Test
    void save_success() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        dto.setStatus(true);

        Warehouse warehouse = new Warehouse();

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Warehouse.class))
                .thenReturn(warehouse);

        when(warehouseRepository.save(any(Warehouse.class)))
                .thenReturn(warehouse);

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Warehouse added successfully",
                response.getMessage()
        );

        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    void save_failure_identifierMissing() {

        WarehouseDto dto = new WarehouseDto();

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Identifier required",
                response.getMessage()
        );
    }

    @Test
    void save_failure_exists() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(new Warehouse());

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Warehouse already exists",
                response.getMessage()
        );
    }

    @Test
    void save_failure_softDeletedWarehouse() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse warehouse = new Warehouse();
        warehouse.setDeleted(true);

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(warehouse);

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(
                response.getMessage().contains("soft deleted")
        );
    }

    // ================= FIND =================

    @Test
    void find_success() {

        Warehouse warehouse = new Warehouse();

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(warehouse);

        when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        WarehouseDto response =
                warehouseService.findByIdentifier("W1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("W1", response.getIdentifier());
    }

    @Test
    void find_notFound() {

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(null);

        WarehouseDto response =
                warehouseService.findByIdentifier("W1");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Warehouse not found",
                response.getMessage()
        );
    }

    // ================= UPDATE =================

    @Test
    void update_success() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");
        dto.setWarehouseName("Main");
        dto.setStatus(true);

        Warehouse warehouse = new Warehouse();

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(warehouse);

        when(warehouseRepository.save(any(Warehouse.class)))
                .thenReturn(warehouse);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Warehouse updated successfully",
                response.getMessage()
        );
    }

    @Test
    void update_invalidIdentifier() {

        WarehouseDto dto = new WarehouseDto();

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Invalid identifier",
                response.getMessage()
        );
    }

    @Test
    void update_notFound() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(null);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Warehouse not found",
                response.getMessage()
        );
    }

    @Test
    void update_softDeleted() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse warehouse = new Warehouse();
        warehouse.setDeleted(true);

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(warehouse);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Warehouse is soft deleted",
                response.getMessage()
        );
    }

    // ================= DELETE =================

    @Test
    void delete_success() {

        Warehouse warehouse = new Warehouse();

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(warehouse);

        warehouseService.delete("W1");

        Assertions.assertTrue(warehouse.getDeleted());

        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void delete_notFound() {

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(null);

        warehouseService.delete("W1");

        verify(warehouseRepository, never()).save(any());
    }

    // ================= FIND ALL =================

    @Test
    void findAll_success() {

        Warehouse warehouse = new Warehouse();

        List<Warehouse> warehouses = List.of(warehouse);

        List<WarehouseDto> dtoList =
                List.of(new WarehouseDto());

        Pageable pageable = PageRequest.of(0, 5);

        Page<Warehouse> page =
                new PageImpl<>(warehouses);

        when(warehouseRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(warehouses),
                ArgumentMatchers.<Type>any()))
                .thenReturn(dtoList);

        WsDto<WarehouseDto> result =
                warehouseService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                1,
                result.getDtoList().size()
        );
    }

    // ================= TOGGLE STATUS =================

    @Test
    void toggle_success() {

        Warehouse warehouse = new Warehouse();
        warehouse.setStatus(true);

        WarehouseDto dto = new WarehouseDto();

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(warehouse);

        when(warehouseRepository.save(warehouse))
                .thenReturn(warehouse);

        when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        WarehouseDto response =
                warehouseService.toggleStatus("W1");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Status updated successfully",
                response.getMessage()
        );
    }

    @Test
    void toggle_notFound() {

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(null);

        WarehouseDto response =
                warehouseService.toggleStatus("W1");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Warehouse not found",
                response.getMessage()
        );
    }

    @Test
    void toggle_softDeleted() {

        Warehouse warehouse = new Warehouse();
        warehouse.setDeleted(true);

        when(warehouseRepository.findByIdentifier("W1"))
                .thenReturn(warehouse);

        WarehouseDto response =
                warehouseService.toggleStatus("W1");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Warehouse is soft deleted",
                response.getMessage()
        );
    }

    // ================= ACTIVE WAREHOUSES =================

    @Test
    void activeWarehouses_success() {

        Warehouse warehouse = new Warehouse();

        when(
                warehouseRepository
                        .findByStatusTrueAndDeletedFalse()
        ).thenReturn(List.of(warehouse));

        when(modelMapper.map(
                warehouse,
                WarehouseDto.class))
                .thenReturn(new WarehouseDto());

        List<WarehouseDto> result =
                warehouseService.findActiveWarehouses();

        Assertions.assertEquals(1, result.size());
    }
}