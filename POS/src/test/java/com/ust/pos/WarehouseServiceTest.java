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
import org.springframework.data.domain.Pageable;
import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    @Test
    void findAll_success() {

        Warehouse warehouse = new Warehouse();
        WarehouseDto dto = new WarehouseDto();

        Page<Warehouse> page = new PageImpl<>(List.of(warehouse));

        Mockito.when(warehouseRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(warehouse)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<WarehouseDto> result =
                warehouseService.findAll(Mockito.mock(Pageable.class));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void save_success() {

        WarehouseDto input = new WarehouseDto();
        input.setIdentifier("WH01");

        Mockito.when(warehouseRepository.findByIdentifier("WH01"))
                .thenReturn(null);

        Warehouse entity = new Warehouse();

        Mockito.when(modelMapper.map(input, Warehouse.class))
                .thenReturn(entity);

        Mockito.when(warehouseRepository.save(entity))
                .thenReturn(entity);

        WarehouseDto result = warehouseService.save(input);

        Assertions.assertEquals("WH01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        WarehouseDto input = new WarehouseDto();
        input.setIdentifier("WH01");

        Mockito.when(warehouseRepository.findByIdentifier("WH01"))
                .thenReturn(new Warehouse());

        WarehouseDto result = warehouseService.save(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier("WH01");

        WarehouseDto dto = new WarehouseDto();
        dto.setIdentifier("WH01");

        Mockito.when(warehouseRepository.findByIdentifier("WH01"))
                .thenReturn(warehouse);

        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        WarehouseDto result =
                warehouseService.findByIdentifier("WH01");

        Assertions.assertEquals("WH01", result.getIdentifier());
    }

    @Test
    void update_success() {

        WarehouseDto input = new WarehouseDto();
        input.setIdentifier("WH01");

        Warehouse existing = new Warehouse();

        Mockito.when(warehouseRepository.findByIdentifier("WH01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        Mockito.when(warehouseRepository.save(existing))
                .thenReturn(existing);

        WarehouseDto result = warehouseService.update(input);

        Assertions.assertEquals("WH01", result.getIdentifier());
    }

    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(warehouseRepository).deleteByIdentifier("WH01");

        warehouseService.delete("WH01");

        Mockito.verify(warehouseRepository)
                .deleteByIdentifier("WH01");
    }

    @Test
    void changeToggleStatus_enable() {

        Warehouse warehouse = new Warehouse();
        warehouse.setStatus(false);

        WarehouseDto dto = new WarehouseDto();

        Mockito.when(warehouseRepository.findByIdentifier("WH01"))
                .thenReturn(warehouse);

        Mockito.when(warehouseRepository.save(warehouse))
                .thenReturn(warehouse);

        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        WarehouseDto result =
                warehouseService.changeToggleStatus("WH01", true);

        Assertions.assertTrue(warehouse.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Warehouse warehouse = new Warehouse();
        warehouse.setStatus(true);

        WarehouseDto dto = new WarehouseDto();

        Mockito.when(warehouseRepository.findByIdentifier("WH01"))
                .thenReturn(warehouse);

        Mockito.when(warehouseRepository.save(warehouse))
                .thenReturn(warehouse);

        Mockito.when(modelMapper.map(warehouse, WarehouseDto.class))
                .thenReturn(dto);

        WarehouseDto result =
                warehouseService.changeToggleStatus("WH01", false);

        Assertions.assertFalse(warehouse.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void findActiveStatus_success() {

        Warehouse active = new Warehouse();
        active.setStatus(true);

        Warehouse inactive = new Warehouse();
        inactive.setStatus(false);

        Mockito.when(warehouseRepository.findAll())
                .thenReturn(List.of(active, inactive));

        WarehouseDto dto = new WarehouseDto();

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(active)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<WarehouseDto> result =
                warehouseService.findActiveStatus();

        Assertions.assertEquals(1, result.size());
    }
}