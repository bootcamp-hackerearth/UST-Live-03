package com.ust.pos;

import com.ust.pos.dto.UnitDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Unit;
import com.ust.pos.model.UnitRepository;
import com.ust.pos.unit.service.impl.UnitServiceImpl;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UnitServiceImpl unitService;

    @Test
    void findAll_success() {

        Unit unit = new Unit();
        UnitDto dto = new UnitDto();
        Pageable pageable = Mockito.mock(Pageable.class);


        Page<Unit> page = new PageImpl<>(List.of(unit));

        when(unitRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(
                        Mockito.eq(List.of(unit)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<UnitDto> result = unitService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());    }

    @Test
    void save_success() {

        UnitDto input = new UnitDto();
        input.setIdentifier("UNIT01");

        when(unitRepository.findByIdentifier("UNIT01"))
                .thenReturn(null);

        Unit entity = new Unit();

        when(modelMapper.map(input, Unit.class))
                .thenReturn(entity);

        when(unitRepository.save(entity))
                .thenReturn(entity);

        UnitDto result = unitService.save(input);

        assertEquals("UNIT01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        UnitDto input = new UnitDto();
        input.setIdentifier("UNIT01");

        when(unitRepository.findByIdentifier("UNIT01"))
                .thenReturn(new Unit());

        UnitDto result = unitService.save(input);

        Assertions.assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Unit unit = new Unit();
        unit.setIdentifier("UNIT01");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT01");

        when(unitRepository.findByIdentifier("UNIT01"))
                .thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        UnitDto result =
                unitService.findByIdentifier("UNIT01");

        assertEquals("UNIT01", result.getIdentifier());
    }

    @Test
    void update_success() {

        UnitDto input = new UnitDto();
        input.setIdentifier("UNIT01");

        Unit existing = new Unit();

        when(unitRepository.findByIdentifier("UNIT01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        when(unitRepository.save(existing))
                .thenReturn(existing);

        UnitDto result = unitService.update(input);

        assertEquals("UNIT01", result.getIdentifier());
    }

    @Test
    void update_failure_notFound() {

        UnitDto input = new UnitDto();
        input.setIdentifier("UNIT01");

        when(unitRepository.findByIdentifier("UNIT01"))
                .thenReturn(null);

        UnitDto result = unitService.update(input);

        Assertions.assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(unitRepository).deleteByIdentifier("UNIT01");

        unitService.delete("UNIT01");

        Mockito.verify(unitRepository)
                .deleteByIdentifier("UNIT01");
    }

    @Test
    void changeToggleStatus_enable() {

        Unit unit = new Unit();
        unit.setStatus(false);

        UnitDto dto = new UnitDto();

        when(unitRepository.findByIdentifier("UNIT01"))
                .thenReturn(unit);

        when(unitRepository.save(unit))
                .thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        UnitDto result =
                unitService.changeToggleStatus("UNIT01", true);

        Assertions.assertTrue(unit.isStatus());
        assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Unit unit = new Unit();
        unit.setStatus(true);

        UnitDto dto = new UnitDto();

        when(unitRepository.findByIdentifier("UNIT01"))
                .thenReturn(unit);

        when(unitRepository.save(unit))
                .thenReturn(unit);

        when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        UnitDto result =
                unitService.changeToggleStatus("UNIT01", false);

        Assertions.assertFalse(unit.isStatus());
        assertNotNull(result);
    }

    @Test
    void findActiveStatus_success() {

        Unit active = new Unit();
        active.setStatus(true);

        Unit inactive = new Unit();
        inactive.setStatus(false);

        when(unitRepository.findAll())
                .thenReturn(List.of(active, inactive));

        UnitDto dto = new UnitDto();

        when(modelMapper.map(
                        Mockito.eq(List.of(active)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<UnitDto> result = unitService.findActiveStatus();

        assertEquals(1, result.size());
    }

    @Test
    void testFindActiveStatus() {
        Unit active = new Unit();
        active.setStatus(true);

        Unit inactive = new Unit();
        inactive.setStatus(false);

        when(unitRepository.findAll())
                .thenReturn(List.of(active, inactive));

        UnitDto dto = new UnitDto();
        List<UnitDto> expectedDtoList = List.of(dto);

        when(modelMapper.map(
                Mockito.eq(List.of(active)),
                Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(expectedDtoList);

        List<UnitDto> result = unitService.findActiveStatus();

        assertNotNull(result, "The result list should not be null");
        assertEquals(1, result.size(), "The result list should contain exactly 1 active unit");
    }
}