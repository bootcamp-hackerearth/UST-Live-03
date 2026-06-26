package com.ust.pos;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
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
    void saveShelfSuccess() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Shelf shelf = new Shelf();

        Mockito.when(
                shelfRepository.findByIdentifierAndDeletedFalse("S1")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(dto, Shelf.class)
        ).thenReturn(shelf);

        ShelfDto response = shelfService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(shelfRepository).save(shelf);

        Assertions.assertFalse(shelf.getDeleted());
    }

    @Test
    void saveShelfAlreadyExists() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(
                shelfRepository.findByIdentifierAndDeletedFalse("S1")
        ).thenReturn(new Shelf());

        ShelfDto response = shelfService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Shelf with identifier - S1 already exists",
                response.getMessage()
        );

        Mockito.verify(
                shelfRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void updateShelfSuccess() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");
        dto.setDescription("Top Shelf");
        dto.setStatus(true);

        Shelf shelf = new Shelf();

        Mockito.when(
                shelfRepository.findByIdentifierAndDeletedFalse("S1")
        ).thenReturn(shelf);

        ShelfDto response = shelfService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Top Shelf",
                shelf.getDescription()
        );

        Assertions.assertTrue(
                shelf.getStatus()
        );

        Mockito.verify(shelfRepository)
                .save(shelf);
    }

    @Test
    void updateShelfNotFound() {

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(
                shelfRepository.findByIdentifierAndDeletedFalse("S1")
        ).thenReturn(null);

        ShelfDto response = shelfService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Shelf not found",
                response.getMessage()
        );

        Mockito.verify(
                shelfRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void findAllShelvesTest() {

        List<Shelf> shelves =
                List.of(new Shelf(), new Shelf());

        List<ShelfDto> dtoList =
                List.of(new ShelfDto(), new ShelfDto());

        Mockito.when(
                shelfRepository.findByDeletedFalse()
        ).thenReturn(shelves);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(shelves),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<ShelfDto> response =
                shelfService.findAll();

        Assertions.assertEquals(
                2,
                response.size()
        );
    }

    @Test
    void findActiveShelvesTest() {

        List<Shelf> shelves =
                List.of(new Shelf(), new Shelf());

        List<ShelfDto> dtoList =
                List.of(new ShelfDto(), new ShelfDto());

        Mockito.when(
                shelfRepository.findByStatusTrueAndDeletedFalse()
        ).thenReturn(shelves);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(shelves),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<ShelfDto> response =
                shelfService.findActiveShelves();

        Assertions.assertEquals(
                2,
                response.size()
        );
    }

    @Test
    void findShelfByIdentifierTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("S1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("S1");

        Mockito.when(
                shelfRepository.findByIdentifierAndDeletedFalse("S1")
        ).thenReturn(shelf);

        Mockito.when(
                modelMapper.map(
                        shelf,
                        ShelfDto.class
                )
        ).thenReturn(dto);

        ShelfDto response =
                shelfService.findByIdentifier("S1");

        Assertions.assertEquals(
                "S1",
                response.getIdentifier()
        );
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Shelf> shelves =
                List.of(new Shelf());

        Page<Shelf> page =
                new PageImpl<>(shelves, pageable, 1);

        List<ShelfDto> dtoList =
                List.of(new ShelfDto());

        Type listType =
                new TypeToken<List<ShelfDto>>() {}.getType();

        Mockito.when(
                shelfRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(shelves, listType)
        ).thenReturn(dtoList);

        WsDto<ShelfDto> response =
                shelfService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void findAllSearchTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Shelf shelf = new Shelf();

        Page<Shelf> page =
                new PageImpl<>(List.of(shelf));

        Mockito.when(
                shelfRepository
                        .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                                "S1",
                                pageable
                        )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        shelf,
                        ShelfDto.class
                )
        ).thenReturn(new ShelfDto());

        Page<ShelfDto> response =
                shelfService.findAll("S1", pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void findAllWithoutSearchTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Shelf shelf = new Shelf();

        Page<Shelf> page =
                new PageImpl<>(List.of(shelf));

        Mockito.when(
                shelfRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        shelf,
                        ShelfDto.class
                )
        ).thenReturn(new ShelfDto());

        Page<ShelfDto> response =
                shelfService.findAll("", pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void deleteShelfTest() {

        Shelf shelf = new Shelf();
        shelf.setDeleted(false);

        Mockito.when(
                shelfRepository.findByIdentifierAndDeletedFalse("S1")
        ).thenReturn(shelf);

        shelfService.delete("S1");

        Assertions.assertTrue(
                shelf.getDeleted()
        );

        Mockito.verify(shelfRepository)
                .save(shelf);
    }

    @Test
    void deleteShelfNotFoundTest() {

        Mockito.when(
                shelfRepository.findByIdentifierAndDeletedFalse("S1")
        ).thenReturn(null);

        shelfService.delete("S1");

        Mockito.verify(
                shelfRepository,
                Mockito.never()
        ).save(Mockito.any());
    }

    @Test
    void toggleShelfStatusSuccess() {

        Shelf shelf = new Shelf();
        shelf.setStatus(true);

        Mockito.when(
                shelfRepository.findByIdentifierAndDeletedFalse("S1")
        ).thenReturn(shelf);

        shelfService.toggleStatus("S1");

        Assertions.assertFalse(
                shelf.getStatus()
        );

        Mockito.verify(shelfRepository)
                .save(shelf);
    }

    @Test
    void toggleShelfStatusNotFound() {

        Mockito.when(
                shelfRepository.findByIdentifierAndDeletedFalse("S1")
        ).thenReturn(null);

        shelfService.toggleStatus("S1");

        Mockito.verify(
                shelfRepository,
                Mockito.never()
        ).save(Mockito.any());
    }
}