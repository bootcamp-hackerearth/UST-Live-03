package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Shelf;
import com.ust.pos.modell.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @InjectMocks
    private ShelfServiceImpl shelfServiceimpl;

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(null);
        Shelf entity = new Shelf();
        Mockito.when(modelMapper.map(dto, Shelf.class)).thenReturn(entity);
        Mockito.when(shelfRepository.save(entity)).thenReturn(entity);
        ShelfDto response = shelfServiceimpl.save(dto);
        Assertions.assertEquals("S1", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void saveTestFailure() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(new Shelf());
        ShelfDto response = shelfServiceimpl.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));
    }

    @Test
    void findByIdentifierTest() {
        Shelf entity = new Shelf();
        entity.setIdentifier("S1");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(entity);
        Mockito.when(modelMapper.map(entity, ShelfDto.class)).thenReturn(dto);
        ShelfDto response = shelfServiceimpl.findByIdentifier("S1");
        Assertions.assertEquals("S1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Shelf existing = new Shelf();
        existing.setIdentifier("S1");
        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(existing);
        Mockito.when(shelfRepository.save(existing)).thenReturn(existing);
        ShelfDto response = shelfServiceimpl.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(null);
        ShelfDto response = shelfServiceimpl.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void deleteTest() {
        shelfServiceimpl.delete("S1");
        Mockito.verify(shelfRepository).deleteByIdentifier("S1");
    }

    @Test
    void findAllTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Admin");
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Admin");
        List<Shelf> shelves = List.of(shelf);
        List<ShelfDto> shelfDtos = List.of(shelfDto);
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Page<Shelf> shelfPage = new PageImpl<>(shelves, pageable, shelves.size());
        Mockito.when(shelfRepository.findAll(pageable)).thenReturn(shelfPage);
        Mockito.when(modelMapper.map(Mockito.eq(shelves), Mockito.any(java.lang.reflect.Type.class))).thenReturn(shelfDtos);
        WsDto<ShelfDto> response = shelfServiceimpl.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPage());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(shelfRepository, Mockito.times(1)).findAll(pageable);
        Mockito.verify(modelMapper, Mockito.times(1))
                .map(Mockito.eq(shelves), Mockito.any(java.lang.reflect.Type.class));
    }

    @Test
    void findAllActiveTest() {
        Shelf entity = new Shelf();
        entity.setIdentifier("S1");
        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        Mockito.when(shelfRepository.findByStatusTrue()).thenReturn(List.of(entity));
        Mockito.when(modelMapper.map(entity, ShelfDto.class)).thenReturn(dto);
        List<ShelfDto> response = shelfServiceimpl.findAllActive();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("S1", response.get(0).getIdentifier());
    }

    @Test
    void toggleStatus_trueToFalse() {
        Shelf entity = new Shelf();
        entity.setIdentifier("S1");
        entity.setStatus(true);
        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(entity);
        shelfServiceimpl.toggleStatus("S1");
        Assertions.assertFalse(entity.getStatus());
        Mockito.verify(shelfRepository).save(entity);
    }

    @Test
    void toggleStatus_falseToTrue() {
        Shelf entity = new Shelf();
        entity.setIdentifier("S1");
        entity.setStatus(false);
        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(entity);
        shelfServiceimpl.toggleStatus("S1");
        Assertions.assertTrue(entity.getStatus());
        Mockito.verify(shelfRepository).save(entity);
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(shelfRepository.findByIdentifier("S1")).thenReturn(null);
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () -> shelfServiceimpl.toggleStatus("S1")
        );
        Assertions.assertTrue(ex.getMessage().contains("Shelf not found"));
        Mockito.verify(shelfRepository, Mockito.never()).save(Mockito.any());
    }
}