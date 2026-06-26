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
    void saveTest() {

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Mockito.when(
                shelfRepository.findByIdentifier("Shelf1")
        ).thenReturn(null);

        Shelf shelf = new Shelf();

        Mockito.when(
                modelMapper.map(shelfDto, Shelf.class)
        ).thenReturn(shelf);

        Mockito.when(
                shelfRepository.save(shelf)
        ).thenReturn(shelf);

        ShelfDto response = shelfService.save(shelfDto);

        Assertions.assertEquals(
                "Shelf1",
                response.getIdentifier()
        );

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Shelf created successfully",
                response.getMessage()
        );
    }

    @Test
    void saveTestFailure() {

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Shelf shelf = new Shelf();

        Mockito.when(
                shelfRepository.findByIdentifier("Shelf1")
        ).thenReturn(shelf);

        ShelfDto response = shelfService.save(shelfDto);

        Assertions.assertEquals(
                "Shelf1",
                response.getIdentifier()
        );

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertNotNull(
                response.getMessage()
        );
    }

    @Test
    void findAllWithPageableTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf1");

        List<Shelf> shelves = List.of(shelf);
        List<ShelfDto> dtos = List.of(dto);

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<Shelf> shelfPage =
                new PageImpl<>(shelves);

        Mockito.when(
                shelfRepository.findByDeletedFalse(pageable)
        ).thenReturn(shelfPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(shelves),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<ShelfDto> response =
                shelfService.findAll(pageable);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Shelf1",
                response.getDtoList()
                        .get(0)
                        .getIdentifier()
        );
    }

    @Test
    void findAllWithoutPageableTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");

        ShelfDto dto = new ShelfDto();
        dto.setIdentifier("Shelf1");

        List<Shelf> shelves = List.of(shelf);
        List<ShelfDto> dtos = List.of(dto);

        Mockito.when(
                shelfRepository.findByDeletedFalse(null)
        ).thenReturn(new PageImpl<>(shelves));

        Mockito.when(
                modelMapper.map(
                        Mockito.any(),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<ShelfDto> response =
                shelfService.findAll(null);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Shelf1",
                response.getDtoList()
                        .get(0)
                        .getIdentifier()
        );
    }

    @Test
    void updateTest() {

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Shelf existingShelf = new Shelf();
        existingShelf.setIdentifier("Shelf1");

        Mockito.when(
                shelfRepository.findByIdentifier("Shelf1")
        ).thenReturn(existingShelf);

        Mockito.when(
                shelfRepository.save(existingShelf)
        ).thenReturn(existingShelf);

        ShelfDto response =
                shelfService.update(shelfDto);

        Assertions.assertTrue(
                response.isSuccess()
        );

        Assertions.assertEquals(
                "Shelf updated successfully",
                response.getMessage()
        );

        Mockito.verify(
                shelfRepository
        ).save(existingShelf);
    }

    @Test
    void updateTestFailure() {

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Mockito.when(
                shelfRepository.findByIdentifier("Shelf1")
        ).thenReturn(null);

        ShelfDto response =
                shelfService.update(shelfDto);

        Assertions.assertFalse(
                response.isSuccess()
        );

        Assertions.assertEquals(
                "Shelf with identifier - Shelf1 not found",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifierTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        Mockito.when(
                shelfRepository.findByIdentifier("Shelf1")
        ).thenReturn(shelf);

        Mockito.when(
                modelMapper.map(
                        shelf,
                        ShelfDto.class
                )
        ).thenReturn(shelfDto);

        ShelfDto response =
                shelfService.findByIdentifier("Shelf1");

        Assertions.assertEquals(
                "Shelf1",
                response.getIdentifier()
        );
    }

    @Test
    void deleteTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");

        Mockito.when(
                shelfRepository.findByIdentifier("Shelf1")
        ).thenReturn(shelf);

        shelfService.delete("Shelf1");

        Assertions.assertTrue(
                shelf.isDeleted()
        );

        Mockito.verify(
                shelfRepository
        ).save(shelf);
    }

    @Test
    void toggleStatusSuccessTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");
        shelf.setStatus(false);

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");
        shelfDto.setStatus(true);

        Mockito.when(
                shelfRepository.findByIdentifier("Shelf1")
        ).thenReturn(shelf);

        Mockito.when(
                shelfRepository.save(shelf)
        ).thenReturn(shelf);

        Mockito.when(
                modelMapper.map(
                        shelf,
                        ShelfDto.class
                )
        ).thenReturn(shelfDto);

        ShelfDto response =
                shelfService.toggleStatus(
                        "Shelf1",
                        true
                );

        Assertions.assertTrue(
                response.isSuccess()
        );

        Assertions.assertEquals(
                "Status updated successfully",
                response.getMessage()
        );

        Mockito.verify(
                shelfRepository
        ).save(shelf);
    }

    @Test
    void toggleStatusFailureTest() {

        Mockito.when(
                shelfRepository.findByIdentifier("Shelf1")
        ).thenReturn(null);

        ShelfDto response =
                shelfService.toggleStatus(
                        "Shelf1",
                        true
                );

        Assertions.assertFalse(
                response.isSuccess()
        );

        Assertions.assertEquals(
                "Shelf not found",
                response.getMessage()
        );
    }

    @Test
    void findAllActiveTest() {

        Shelf shelf = new Shelf();
        shelf.setIdentifier("Shelf1");

        ShelfDto shelfDto = new ShelfDto();
        shelfDto.setIdentifier("Shelf1");

        List<Shelf> shelves = List.of(shelf);
        List<ShelfDto> shelfDtos = List.of(shelfDto);

        Mockito.when(
                shelfRepository.findByStatusTrue()
        ).thenReturn(shelves);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(shelves),
                        Mockito.any(Type.class)
                )
        ).thenReturn(shelfDtos);

        List<ShelfDto> response =
                shelfService.findActiveShelves();

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.size()
        );

        Assertions.assertEquals(
                "Shelf1",
                response.get(0).getIdentifier()
        );
    }
}