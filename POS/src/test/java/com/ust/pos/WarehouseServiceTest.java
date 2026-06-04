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

    @Test
    void saveTest() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier(" W1 ");

        Warehouse warehouse = new Warehouse();

        WarehouseDto mappedDto = new WarehouseDto();
        mappedDto.setIdentifier("W1");
        mappedDto.setSuccess(true);

        Mockito.when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);

        Mockito.when(modelMapper.map(dto, Warehouse.class)).thenReturn(warehouse);

        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(mappedDto);

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void saveDuplicateTest() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse warehouse = new Warehouse();

        Mockito.when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);

        WarehouseDto response = warehouseService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Warehouse with identifier W1 already exists", response.getMessage());
    }

    @Test
    void updateTest() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("W1");
        warehouse.setStatus(true);

        WarehouseDto mappedDto = new WarehouseDto();
        mappedDto.setSuccess(true);

        Mockito.when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);

        Mockito.doAnswer(invocation -> {

            WarehouseDto source = invocation.getArgument(0);
            Warehouse target = invocation.getArgument(1);

            target.setIdentifier(source.getIdentifier());

            return null;

        }).when(modelMapper).map(Mockito.any(WarehouseDto.class), Mockito.any(Warehouse.class));

        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(mappedDto);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertTrue(warehouse.isStatus());

        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void updateNotFoundTest() {

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Mockito.when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);

        WarehouseDto response = warehouseService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Warehouse not found", response.getMessage());
    }

    @Test
    void findByIdentifierTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("W1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        Mockito.when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);

        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(dto);

        WarehouseDto response = warehouseService.findByIdentifier("W1");

        Assertions.assertEquals("W1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNullTest() {

        Mockito.when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);

        WarehouseDto response = warehouseService.findByIdentifier("W1");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("W1");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> dtos = List.of(dto);

        Page<Warehouse> warehousePage = new PageImpl<>(warehouses);

        Mockito.when(warehouseRepository.findAll(pageable)).thenReturn(warehousePage);

        Mockito.when(modelMapper.map(Mockito.eq(warehouses), Mockito.any(Type.class))).thenReturn(dtos);

        List<WarehouseDto> response = warehouseService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(warehouseRepository).deleteByIdentifier("W1");

        boolean response = warehouseService.delete("W1");

        Assertions.assertTrue(response);

        Mockito.verify(warehouseRepository).deleteByIdentifier("W1");
    }

    @Test
    void toggleStatusTrueToFalseTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("W1");
        warehouse.setStatus(true);

        Mockito.when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);

        warehouseService.toggleStatus("W1");

        Assertions.assertFalse(warehouse.isStatus());

        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("W1");
        warehouse.setStatus(false);

        Mockito.when(warehouseRepository.findByIdentifier("W1")).thenReturn(warehouse);

        warehouseService.toggleStatus("W1");

        Assertions.assertTrue(warehouse.isStatus());

        Mockito.verify(warehouseRepository).save(warehouse);
    }

    @Test
    void toggleStatusNullTest() {

        Mockito.when(warehouseRepository.findByIdentifier("W1")).thenReturn(null);

        warehouseService.toggleStatus("W1");

        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findIfTrueTest() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("W1");
        warehouse.setStatus(true);

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("W1");

        List<Warehouse> warehouses = List.of(warehouse);
        List<WarehouseDto> dtos = List.of(dto);

        Mockito.when(warehouseRepository.findByStatusIsTrue()).thenReturn(warehouses);

        Mockito.when(modelMapper.map(Mockito.eq(warehouses), Mockito.any(Type.class))).thenReturn(dtos);

        List<WarehouseDto> response = warehouseService.findIfTrue();

        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("W1", response.get(0).getIdentifier());
    }
}