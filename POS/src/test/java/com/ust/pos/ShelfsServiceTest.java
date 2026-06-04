package com.ust.pos;

import com.ust.pos.dto.ShelfsDto;
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

        Page<Shelfs> page = new PageImpl<>(List.of(shelf));

        Mockito.when(shelfsRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(shelf)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<ShelfsDto> result =
                shelfsService.findAll(Mockito.mock(Pageable.class));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void findActiveStatus_success() {

        Shelfs active = new Shelfs();
        active.setStatus(true);

        Shelfs inactive = new Shelfs();
        inactive.setStatus(false);

        Mockito.when(shelfsRepository.findAll())
                .thenReturn(List.of(active, inactive));

        ShelfsDto dto = new ShelfsDto();

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(active)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<ShelfsDto> result = shelfsService.findActiveStatus();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void save_success() {

        ShelfsDto input = new ShelfsDto();
        input.setIdentifier("SHELF01");

        Mockito.when(shelfsRepository.findByIdentifier("SHELF01"))
                .thenReturn(null);

        Shelfs entity = new Shelfs();

        Mockito.when(modelMapper.map(input, Shelfs.class))
                .thenReturn(entity);

        Mockito.when(shelfsRepository.save(entity))
                .thenReturn(entity);

        ShelfsDto result = shelfsService.save(input);

        Assertions.assertEquals("SHELF01", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        ShelfsDto input = new ShelfsDto();
        input.setIdentifier("SHELF01");

        Mockito.when(shelfsRepository.findByIdentifier("SHELF01"))
                .thenReturn(new Shelfs());

        ShelfsDto result = shelfsService.save(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Shelfs shelf = new Shelfs();
        shelf.setIdentifier("SHELF01");

        ShelfsDto dto = new ShelfsDto();
        dto.setIdentifier("SHELF01");

        Mockito.when(shelfsRepository.findByIdentifier("SHELF01"))
                .thenReturn(shelf);

        Mockito.when(modelMapper.map(shelf, ShelfsDto.class))
                .thenReturn(dto);

        ShelfsDto result =
                shelfsService.findByIdentifier("SHELF01");

        Assertions.assertEquals("SHELF01", result.getIdentifier());
    }

    @Test
    void update_success() {

        ShelfsDto input = new ShelfsDto();
        input.setIdentifier("SHELF01");

        Shelfs existing = new Shelfs();

        Mockito.when(shelfsRepository.findByIdentifier("SHELF01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        Mockito.when(shelfsRepository.save(existing))
                .thenReturn(existing);

        ShelfsDto result = shelfsService.update(input);

        Assertions.assertEquals("SHELF01", result.getIdentifier());
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

        Mockito.when(shelfsRepository.findByIdentifier("SHELF01"))
                .thenReturn(shelf);

        Mockito.when(shelfsRepository.save(shelf))
                .thenReturn(shelf);

        Mockito.when(modelMapper.map(shelf, ShelfsDto.class))
                .thenReturn(dto);

        ShelfsDto result =
                shelfsService.changeToggleStatus("SHELF01", true);

        Assertions.assertTrue(shelf.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Shelfs shelf = new Shelfs();
        shelf.setStatus(true);

        ShelfsDto dto = new ShelfsDto();

        Mockito.when(shelfsRepository.findByIdentifier("SHELF01"))
                .thenReturn(shelf);

        Mockito.when(shelfsRepository.save(shelf))
                .thenReturn(shelf);

        Mockito.when(modelMapper.map(shelf, ShelfsDto.class))
                .thenReturn(dto);

        ShelfsDto result =
                shelfsService.changeToggleStatus("SHELF01", false);

        Assertions.assertFalse(shelf.isStatus());
        Assertions.assertNotNull(result);
    }
}
