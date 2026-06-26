package com.ust.pos.shelf.service.impl;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.ShelfService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ShelfServiceImpl implements ShelfService {

    private static final String SHELF_WITH_IDENTIFIER = "Shelf with identifier - ";

    private final ShelfRepository shelfRepository;
    private final ModelMapper modelMapper;

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);

        if (existingShelf != null) {
            if (Boolean.TRUE.equals(existingShelf.getIsDeleted())) {
                shelfDto.setMessage(SHELF_WITH_IDENTIFIER + identifier + " was deleted. Contact admin for further support or try with a different identifier.");
            } else {
                shelfDto.setMessage(SHELF_WITH_IDENTIFIER + identifier + " already exists");
            }
            shelfDto.setSuccess(false);
            return shelfDto;
        }

        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        shelf.setIsDeleted(false);
        shelfRepository.save(shelf);
        shelfDto.setSuccess(true);
        return shelfDto;
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);

        if (existingShelf == null) {
            shelfDto.setMessage(SHELF_WITH_IDENTIFIER + identifier + " not found");
            shelfDto.setSuccess(false);
            return shelfDto;
        }

        modelMapper.map(shelfDto, existingShelf);
        shelfRepository.save(existingShelf);
        return shelfDto;
    }

    @Override
    public ShelfDto delete(String identifier) {
        ShelfDto shelfDto = new ShelfDto();
        Shelf shelf = shelfRepository.findByIdentifier(identifier);

        if (shelf == null) {
            shelfDto.setMessage(SHELF_WITH_IDENTIFIER + identifier + " not found");
            shelfDto.setSuccess(false);
            return shelfDto;
        }

        shelf.setIsDeleted(true);
        shelf.setStatus(false);
        shelfRepository.save(shelf);
        shelfDto.setSuccess(true);
        shelfDto.setMessage("Shelf deleted successfully");
        return shelfDto;
    }

    @Override
    public PaginatedResponseDto<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> shelfPage = shelfRepository.findByIsDeleted(false, pageable);
        List<ShelfDto> items = modelMapper.map(shelfPage.getContent(), listType);
        PaginatedResponseDto<ShelfDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(shelfPage.getTotalElements());
        response.setTotalPages(shelfPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());
        return response;
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfRepository.findByIdentifier(identifier), ShelfDto.class);
    }

    @Override
    public List<ShelfDto> findAllActive() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(shelfRepository.findByStatusAndIsDeleted(true, false), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        shelf.setStatus(status);
        shelfRepository.save(shelf);
    }
}