package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.impl.ShelfServiceImpl;
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
public class ShelfServiceTest {

    @InjectMocks
    ShelfServiceImpl shelfService;

    @Mock
    ShelfRepository shelfRepository;
    @Mock
    ModelMapper modelMapper;

    @Test
    void saveTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Mockito.when(shelfRepository.findByIdentifier("Shelf1")).thenReturn(null);

        Shelf shelf = new Shelf();

        Mockito.when(modelMapper.map(shelfDto, Shelf.class)).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);

        ShelfDto response = shelfService.save(shelfDto);

        Assertions.assertEquals("Shelf1", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("Shelf1")).thenReturn(shelf);

        ShelfDto response = shelfService.save(shelfDto);

        Assertions.assertEquals("Shelf1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findAllWithPageableTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        List<Shelf> shelfs = List.of(shelf);
        List<ShelfDto> shelfDtos = List.of(shelfDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Shelf> shelfPage = new PageImpl<>(shelfs);

        Mockito.when(shelfRepository.findAll(pageable))
                .thenReturn(shelfPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(shelfs),
                Mockito.any(Type.class)
        )).thenReturn(shelfDtos);

        List<ShelfDto> response = shelfService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Shelf1", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        List<Shelf> shelfs = List.of(shelf);
        List<ShelfDto> shelfDtos = List.of(shelfDto);

        Mockito.when(shelfRepository.findAll())
                .thenReturn(shelfs);

        Mockito.when(modelMapper.map(
                Mockito.eq(shelfs),
                Mockito.any(Type.class)
        )).thenReturn(shelfDtos);

        List<ShelfDto> response = shelfService.findAll(null);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void updateTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");

        Mockito.when(shelfRepository.findByIdentifier(shelfDto.getIdentifier())).thenReturn(shelf);
        Mockito.when(shelfRepository.save(shelf)).thenReturn(shelf);

        ShelfDto response = shelfService.update(shelfDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");

        Mockito.when(shelfRepository.findByIdentifier(shelfDto.getIdentifier())).thenReturn(null);

        ShelfDto response = shelfService.update(shelfDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");

        Mockito.when(shelfRepository.findByIdentifier("Shelf1")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(shelfDto);

        ShelfDto response = shelfService.findByIdentifier("Shelf1");

        Assertions.assertEquals("Shelf1", response.getIdentifier());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(shelfRepository).deleteByIdentifier("Shelf1");

        shelfService.delete("Shelf1");

        Mockito.verify(shelfRepository).deleteByIdentifier("Shelf1");
    }

    @Test
    void toggleStatusSuccessTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");
        shelf.setStatus(false);

        Mockito.when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(shelf);

        ShelfDto response = shelfService.toggleStatus("Shelf1", true);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());

        Mockito.verify(shelfRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(null);

        ShelfDto response = shelfService.toggleStatus("Shelf1", true);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf not found", response.getMessage());
    }

    @Test
    void findAllActiveTest() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");

        List<ShelfDto> shelfDtos = List.of(shelfDto);
        List<Shelf> shelfs = List.of(shelf);

        Mockito.when(shelfRepository.findByStatusTrue()).thenReturn(shelfs);
        Mockito.when(modelMapper.map(
                Mockito.eq(shelfs),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(shelfDtos);
        List<ShelfDto> response = shelfService.findActiveShelves();
        Assertions.assertEquals(1, response.size());
    }

}
