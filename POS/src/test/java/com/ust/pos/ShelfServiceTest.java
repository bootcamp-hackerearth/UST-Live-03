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
class ShelfServiceTest {

    @InjectMocks
    private ShelfServiceImpl shelfService;

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void save_success() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(shelfDto, Shelf.class))
                .thenReturn(shelf);

        ShelfDto response = shelfService.save(shelfDto);

        Mockito.verify(shelfRepository).save(shelf);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Successfully added the shelf", response.getMessage());
    }

    @Test
    void save_failure_existingShelf() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Mockito.when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(new Shelf());

        ShelfDto response = shelfService.save(shelfDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf Shelf1 already exists", response.getMessage());
    }

    @Test
    void findByIdentifier_success() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf1");

        Mockito.when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class))
                .thenReturn(dto);

        ShelfDto response = shelfService.findByIdentifier("Shelf1");

        Assertions.assertEquals("Shelf1", response.getIdentifier());
    }

    @Test
    void findAllWithPageableTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        List<Shelf> shelves = List.of(shelf);
        List<ShelfDto> shelfDtos = List.of(shelfDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Shelf> shelfPage = new PageImpl<>(shelves);

        Mockito.when(shelfRepository.findAll(pageable))
                .thenReturn(shelfPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(shelves),
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

        List<Shelf> shelves = List.of(shelf);
        List<ShelfDto> shelfDtos = List.of(shelfDto);

        Mockito.when(shelfRepository.findAll())
                .thenReturn(shelves);

        Mockito.when(modelMapper.map(
                Mockito.eq(shelves),
                Mockito.any(Type.class)
        )).thenReturn(shelfDtos);

        List<ShelfDto> response = shelfService.findAll(null);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findActiveShelfs_success() {
        Shelf shelf = new Shelf();
        ShelfDto dto = new ShelfDto();

        Mockito.when(shelfRepository.findByStatusTrue())
                .thenReturn(List.of(shelf));
        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(List.of(dto));

        List<ShelfDto> response = shelfService.findActiveShelfs();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void update_success() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Shelf existingShelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(existingShelf);

        Mockito.when(shelfRepository.save(existingShelf))
                .thenReturn(existingShelf);

        ShelfDto response = shelfService.update(shelfDto);

        Mockito.verify(modelMapper).map(shelfDto, existingShelf);
        Mockito.verify(shelfRepository).save(existingShelf);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Shelf updated successfully", response.getMessage());
    }

    @Test
    void update_failure_shelfNotFound() {
        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Mockito.when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(null);

        ShelfDto response = shelfService.update(shelfDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf not found", response.getMessage());
    }

    @Test
    void updateStatus_success() {
        Shelf shelf = new Shelf();

        Mockito.when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(shelf);

        ShelfDto response = shelfService.updateStatus("Shelf1", true);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
    }

    @Test
    void updateStatus_failure_shelfNotFound() {
        Mockito.when(shelfRepository.findByIdentifier("Shelf1"))
                .thenReturn(null);

        ShelfDto response = shelfService.updateStatus("Shelf1", true);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Shelf not found", response.getMessage());
    }

    @Test
    void delete_success() {
        Mockito.doNothing()
                .when(shelfRepository)
                .deleteByIdentifier("Shelf1");

        shelfService.delete("Shelf1");

        Mockito.verify(shelfRepository).deleteByIdentifier("Shelf1");
    }
}