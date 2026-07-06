package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @Mock
    private ShelfRepository shelfsRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ShelfServiceImpl shelfsService;

    @Test
    void saveTestSuccess() {
        ShelfDto shelfsDto = new ShelfDto();
        shelfsDto.setIdentifier("Shelf");

        Shelf shelf = new Shelf();

        Mockito.when(shelfsRepository.findByIdentifier("Shelf")).thenReturn(null);
        Mockito.when(modelMapper.map(shelfsDto, Shelf.class)).thenReturn(shelf);

        ShelfDto response = shelfsService.save(shelfsDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure_alreadyExists() {
        ShelfDto shelfsDto = new ShelfDto();
        shelfsDto.setIdentifier("Shelf");

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf");
        shelf.setDeleted(false);

        Mockito.when(shelfsRepository.findByIdentifier("Shelf")).thenReturn(shelf);

        ShelfDto response = shelfsService.save(shelfsDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestFailure_softDeletedRecord() {
        ShelfDto shelfsDto = new ShelfDto();
        shelfsDto.setIdentifier("Shelf");

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf");
        shelf.setDeleted(true);

        Mockito.when(shelfsRepository.findByIdentifier("Shelf")).thenReturn(shelf);

        ShelfDto response = shelfsService.save(shelfsDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(shelfsRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf");

        Mockito.when(shelfsRepository.findByIdentifier("Shelf")).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);

        ShelfDto response = shelfsService.findByIdentifier("Shelf");

        Assertions.assertEquals("Shelf", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(shelfsRepository.findByIdentifier("Shelf")).thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> shelfsService.findByIdentifier("Shelf")
        );

        Assertions.assertEquals("Shelf with identifier 'Shelf'not found", exception.getMessage());
    }

    @Test
    void updateTestSuccess() {
        ShelfDto shelfsDto = new ShelfDto();
        shelfsDto.setIdentifier("Shelf");

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf");

        Mockito.when(shelfsRepository.findByIdentifier("Shelf")).thenReturn(shelf);
        Mockito.when(shelfsRepository.save(shelf)).thenReturn(shelf);

        ShelfDto response = shelfsService.update(shelfsDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        ShelfDto shelfsDto = new ShelfDto();
        shelfsDto.setIdentifier("Shelf");

        Mockito.when(shelfsRepository.findByIdentifier("Shelf")).thenReturn(null);

        ShelfDto response = shelfsService.update(shelfsDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdTestSuccess() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf");

        Mockito.when(shelfsRepository.findById(1L)).thenReturn(Optional.of(shelf));
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);

        ShelfDto response = shelfsService.findById(1L);

        Assertions.assertEquals("Shelf", response.getIdentifier());
    }

    @Test
    void findByIdTestNotFound() {
        Mockito.when(shelfsRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class,
                () -> shelfsService.findById(99L));
    }

    @Test
    void findAllTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf");

        Page<Shelf> page = new PageImpl<>(List.of(shelf));

        Mockito.when(shelfsRepository.findByDeletedFalse(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(dto));

        Pageable pageable = PageRequest.of(0, 50, Sort.unsorted());
        List<ShelfDto> response = shelfsService.findAll(pageable).getDtoList();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllWithSpecificationTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf");

        Page<Shelf> page = new PageImpl<>(List.of(shelf));
        Specification<Shelf> spec = Mockito.mock(Specification.class);

        Mockito.when(shelfsRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(page);

        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(dto));

        Pageable pageable = PageRequest.of(0, 50, Sort.unsorted());
        List<ShelfDto> response = shelfsService.findAll(spec, pageable).getDtoList();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findActiveShelfTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf");
        shelf.setStatus(true);

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf");
        dto.setStatus(true);

        Mockito.when(shelfsRepository.findByStatusTrue(true))
                .thenReturn(List.of(shelf));

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));

        List<ShelfDto> response = shelfsService.findActiveShelf();

        Assertions.assertEquals(1, response.size());
        Assertions.assertTrue(response.get(0).isStatus());
    }

    @Test
    void deleteTest_softDeletesAndSaves() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf");
        shelf.setStatus(true);
        shelf.setDeleted(false);

        Mockito.when(shelfsRepository.findByIdentifier("Shelf")).thenReturn(shelf);

        shelfsService.delete("Shelf");

        Assertions.assertTrue(shelf.isDeleted());
        Assertions.assertFalse(shelf.isStatus());
        Mockito.verify(shelfsRepository, Mockito.times(1)).save(shelf);
    }

    @Test
    void changeShelfStatusSuccessTest() {
        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf");
        shelf.setStatus(false);

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf");
        dto.setStatus(true);

        Mockito.when(shelfsRepository.findByIdentifier("Shelf")).thenReturn(shelf);
        Mockito.when(shelfsRepository.save(shelf)).thenReturn(shelf);
        Mockito.when(modelMapper.map(shelf, ShelfDto.class)).thenReturn(dto);

        ShelfDto response = shelfsService.changeShelfStatus("Shelf", true);

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void changeShelfStatusFailureTest() {
        Mockito.when(shelfsRepository.findByIdentifier("Shelf")).thenReturn(null);

        ShelfDto response = shelfsService.changeShelfStatus("Shelf", true);

        Assertions.assertNull(response);
        Mockito.verify(shelfsRepository, Mockito.never()).save(Mockito.any());
    }
}