package com.ust.pos;

import com.ust.pos.dto.UnitDto;
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

        Page<Unit> page = new PageImpl<>(List.of(unit));

        Mockito.when(unitRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(unit)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<UnitDto> result =
                unitService.findAll(Mockito.mock(Pageable.class));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void save_success() {

        UnitDto input = new UnitDto();
        input.setIdentifier("UNIT01");

        Mockito.when(unitRepository.findByIdentifier("UNIT01"))
                .thenReturn(null);

        Unit entity = new Unit();

        Mockito.when(modelMapper.map(input, Unit.class))
                .thenReturn(entity);

        Mockito.when(unitRepository.save(entity))
                .thenReturn(entity);

        UnitDto result = unitService.save(input);

        Assertions.assertEquals("UNIT01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        UnitDto input = new UnitDto();
        input.setIdentifier("UNIT01");

        Mockito.when(unitRepository.findByIdentifier("UNIT01"))
                .thenReturn(new Unit());

        UnitDto result = unitService.save(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Unit unit = new Unit();
        unit.setIdentifier("UNIT01");

        UnitDto dto = new UnitDto();
        dto.setIdentifier("UNIT01");

        Mockito.when(unitRepository.findByIdentifier("UNIT01"))
                .thenReturn(unit);

        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        UnitDto result =
                unitService.findByIdentifier("UNIT01");

        Assertions.assertEquals("UNIT01", result.getIdentifier());
    }

    @Test
    void update_success() {

        UnitDto input = new UnitDto();
        input.setIdentifier("UNIT01");

        Unit existing = new Unit();

        Mockito.when(unitRepository.findByIdentifier("UNIT01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        Mockito.when(unitRepository.save(existing))
                .thenReturn(existing);

        UnitDto result = unitService.update(input);

        Assertions.assertEquals("UNIT01", result.getIdentifier());
    }

    @Test
    void update_failure_notFound() {

        UnitDto input = new UnitDto();
        input.setIdentifier("UNIT01");

        Mockito.when(unitRepository.findByIdentifier("UNIT01"))
                .thenReturn(null);

        UnitDto result = unitService.update(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
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

        Mockito.when(unitRepository.findByIdentifier("UNIT01"))
                .thenReturn(unit);

        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        UnitDto result =
                unitService.changeToggleStatus("UNIT01", true);

        Assertions.assertTrue(unit.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Unit unit = new Unit();
        unit.setStatus(true);

        UnitDto dto = new UnitDto();

        Mockito.when(unitRepository.findByIdentifier("UNIT01"))
                .thenReturn(unit);

        Mockito.when(unitRepository.save(unit))
                .thenReturn(unit);

        Mockito.when(modelMapper.map(unit, UnitDto.class))
                .thenReturn(dto);

        UnitDto result =
                unitService.changeToggleStatus("UNIT01", false);

        Assertions.assertFalse(unit.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void findActiveStatus_success() {

        Unit active = new Unit();
        active.setStatus(true);

        Unit inactive = new Unit();
        inactive.setStatus(false);

        Mockito.when(unitRepository.findAll())
                .thenReturn(List.of(active, inactive));

        UnitDto dto = new UnitDto();

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(active)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<UnitDto> result = unitService.findActiveStatus();

        Assertions.assertEquals(1, result.size());
    }
}