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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Test
    void saveTestSuccess() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Warehouse warehouse = new Warehouse();

        Mockito.when(warehouseRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(warehouseDto, Warehouse.class)).thenReturn(warehouse);

        WarehouseDto response = warehouseService.save(warehouseDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void saveTestFailure() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Warehouse existing = new Warehouse();
        existing.setIdentifier("Admin");

        Mockito.when(warehouseRepository.findByIdentifier("Admin")).thenReturn(existing);

        WarehouseDto response = warehouseService.save(warehouseDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Warehouse Already Exist!", response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Mockito.when(warehouseRepository.findByIdentifier("Admin")).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(warehouseDto);

        WarehouseDto response = warehouseService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTestSuccess() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");

        Mockito.when(warehouseRepository.findByIdentifier("Admin")).thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);

        WarehouseDto response = warehouseService.update(warehouseDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Mockito.when(warehouseRepository.findByIdentifier("Admin")).thenReturn(null);

        WarehouseDto response = warehouseService.update(warehouseDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(warehouseRepository).deleteById(1L);

        warehouseService.delete(1L);

        Mockito.verify(warehouseRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void findAllTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Page<Warehouse> page = new PageImpl<>(List.of(warehouse));

        Mockito.when(warehouseRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(warehouseDto));

        Pageable pageable = PageRequest.of(0, 50, Sort.unsorted());
        List<WarehouseDto> response = warehouseService.findAll(pageable).getDtoList();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Admin", response.get(0).getIdentifier());
    }

    @Test
    void findByIdTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");

        Mockito.when(warehouseRepository.findById(1L))
                .thenReturn(Optional.of(warehouse));
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(warehouseDto);

        WarehouseDto response = warehouseService.findById(1L);

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findByIdNotFoundTest() {
        Mockito.when(warehouseRepository.findById(99L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                RuntimeException.class,
                () -> warehouseService.findById(99L)
        );
    }

    @Test
    void changeWarehouseStatusSuccessTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");
        warehouse.setStatus(false);

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");
        warehouseDto.setStatus(true);

        Mockito.when(warehouseRepository.findByIdentifier("Admin")).thenReturn(warehouse);
        Mockito.when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class)).thenReturn(warehouseDto);

        WarehouseDto response =
                warehouseService.changeWarehouseStatus("Admin", true);

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void changeWarehouseStatusFailureTest() {
        Mockito.when(warehouseRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        WarehouseDto response =
                warehouseService.changeWarehouseStatus("Admin", true);

        Assertions.assertNull(response);
        Mockito.verify(warehouseRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllActiveWarehouseTest() {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("Admin");
        warehouse.setStatus(true);

        WarehouseDto warehouseDto = new WarehouseDto();
        warehouseDto.setIdentifier("Admin");
        warehouseDto.setStatus(true);

        Mockito.when(warehouseRepository.findByStatusTrue())
                .thenReturn(List.of(warehouse));
        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(warehouseDto);

        List<WarehouseDto> response =
                warehouseService.findAllActiveWarehouse();

        Assertions.assertEquals(1, response.size());
        Assertions.assertTrue(response.get(0).isStatus());
    }
}