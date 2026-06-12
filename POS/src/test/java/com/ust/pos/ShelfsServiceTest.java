package com.ust.pos;

import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelfs;
import com.ust.pos.model.ShelfsRepository;
import com.ust.pos.shelfs.sevice.impl.ShelfsServiceImpl;
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
class ShelfsServiceTest {

    @Mock
    private ShelfsRepository shelfsRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ShelfsServiceImpl shelfsService;

    @Test
    void findAll_success() {

        Shelfs shelf = new Shelfs();
        ShelfsDto dto = new ShelfsDto();
        Pageable pageable = Mockito.mock(Pageable.class);

        Page<Shelfs> page = new PageImpl<>(List.of(shelf));

        when(shelfsRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(
                        Mockito.eq(List.of(shelf)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<ShelfsDto> result =
                shelfsService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());    }

    @Test
    void findActiveStatus_success() {

        Shelfs active = new Shelfs();
        active.setStatus(true);

        Shelfs inactive = new Shelfs();
        inactive.setStatus(false);

        when(shelfsRepository.findAll())
                .thenReturn(List.of(active, inactive));

        ShelfsDto dto = new ShelfsDto();

        when(modelMapper.map(
                        Mockito.eq(List.of(active)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<ShelfsDto> result = shelfsService.findActiveStatus();

        assertEquals(1, result.size());
    }

    @Test
    void save_success() {

        ShelfsDto input = new ShelfsDto();
        input.setIdentifier("SHELF01");

        when(shelfsRepository.findByIdentifier("SHELF01"))
                .thenReturn(null);

        Shelfs entity = new Shelfs();

        when(modelMapper.map(input, Shelfs.class))
                .thenReturn(entity);

        when(shelfsRepository.save(entity))
                .thenReturn(entity);

        ShelfsDto result = shelfsService.save(input);

        assertEquals("SHELF01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        ShelfsDto input = new ShelfsDto();
        input.setIdentifier("SHELF01");

        when(shelfsRepository.findByIdentifier("SHELF01"))
                .thenReturn(new Shelfs());

        ShelfsDto result = shelfsService.save(input);

        Assertions.assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Shelfs shelf = new Shelfs();
        shelf.setIdentifier("SHELF01");

        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("SHELF01");

        when(shelfsRepository.findByIdentifier("SHELF01"))
                .thenReturn(shelf);

        when(modelMapper.map(shelf, ShelfsDto.class))
                .thenReturn(dto);

        ShelfsDto result =
                shelfsService.findByIdentifier("SHELF01");

        assertEquals("SHELF01", result.getIdentifier());
    }

    @Test
    void update_success() {

        ShelfsDto input = new ShelfsDto();
        input.setIdentifier("SHELF01");

        Shelfs existing = new Shelfs();

        when(shelfsRepository.findByIdentifier("SHELF01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        when(shelfsRepository.save(existing))
                .thenReturn(existing);

        ShelfsDto result = shelfsService.update(input);

        assertEquals("SHELF01", result.getIdentifier());
    }

    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(shelfsRepository).deleteByIdentifier("SHELF01");

        shelfsService.delete("SHELF01");

        Mockito.verify(shelfsRepository)
                .deleteByIdentifier("SHELF01");
    }

    @Test
    void changeToggleStatus_enable() {

        Shelfs shelf = new Shelfs();
        shelf.setStatus(false);

        ShelfsDto dto = new ShelfsDto();

        when(shelfsRepository.findByIdentifier("SHELF01"))
                .thenReturn(shelf);

        when(shelfsRepository.save(shelf))
                .thenReturn(shelf);

        when(modelMapper.map(shelf, ShelfsDto.class))
                .thenReturn(dto);

        ShelfsDto result =
                shelfsService.changeToggleStatus("SHELF01", true);

        Assertions.assertTrue(shelf.isStatus());
        assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Shelfs shelf = new Shelfs();
        shelf.setStatus(true);

        ShelfsDto dto = new ShelfsDto();

        when(shelfsRepository.findByIdentifier("SHELF01"))
                .thenReturn(shelf);

        when(shelfsRepository.save(shelf))
                .thenReturn(shelf);

        when(modelMapper.map(shelf, ShelfsDto.class))
                .thenReturn(dto);

        ShelfsDto result =
                shelfsService.changeToggleStatus("SHELF01", false);

        Assertions.assertFalse(shelf.isStatus());
        assertNotNull(result);
    }

    @Test
    void testFindActiveStatus() {
        Shelfs active = new Shelfs();
        active.setStatus(true);

        Shelfs inactive = new Shelfs();
        inactive.setStatus(false);

        when(shelfsRepository.findAll())
                .thenReturn(List.of(active, inactive));

        ShelfsDto dto = new ShelfsDto();
        List<ShelfsDto> expectedDtoList = List.of(dto);

        when(modelMapper.map(
                Mockito.eq(List.of(active)),
                Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(expectedDtoList);

        List<ShelfsDto> result = shelfsService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
