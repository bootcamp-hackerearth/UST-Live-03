package com.ust.pos.shelf.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.ShelfService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ShelfServiceImpl extends CommonService implements ShelfService {
    public static final String SHELF_NOT_FOUND = "Shelf not found";
    private final ShelfRepository shelfRepository;
    private final ModelMapper modelMapper;

    public ShelfServiceImpl(ShelfRepository shelfRepository, ModelMapper modelMapper) {
        this.shelfRepository = shelfRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfDto createShelf(ShelfDto shelfDto) {

        if (shelfRepository.existsByIdentifier(shelfDto.getIdentifier())) {
            shelfDto.setSuccess(false);
            shelfDto.setMessage("Shelf already exists");
            return shelfDto;
        }

        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        setAuditFields(shelf, true);
        shelfRepository.save(shelf);
        return shelfDto;
    }

    @Override
    public ShelfDto updateShelf(ShelfDto shelfDto) {
        ShelfDto dto = new ShelfDto();

        Shelf existing = shelfRepository.findById(shelfDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Shelf with id '" + shelfDto.getId() + "' not found"));
        existing.setIdentifier(shelfDto.getIdentifier());
        setAuditFields(existing, false);
        shelfRepository.save(existing);

        modelMapper.map(existing, dto);
        dto.setSuccess(true);

        return dto;
    }

    @Override
    public ShelfDto getShelf(Long id) {

        ShelfDto dto = new ShelfDto();

        Shelf shelf = shelfRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Shelf with id '" + id + "' not found"));
        modelMapper.map(shelf, dto);
        dto.setSuccess(true);

        return dto;
    }

    @Override
    public List<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> shelfPage = shelfRepository.findByDeletedFalse(pageable);
        return modelMapper.map(shelfPage.getContent(), listType);
    }

    @Override
    public boolean deleteShelf(Long id) {
        Shelf shelf = shelfRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Shelf with id '" + id + "' not found"));
        softDelete(shelf);
        setAuditFields(shelf, false);
        shelfRepository.save(shelf);
        return true;
    }

    @Override
    public ShelfDto toggleStatus(Long id) {
        ShelfDto dto = new ShelfDto();

        Shelf shelf = shelfRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Shelf with id '" + id + "' not found"));
        shelf.setActive(!shelf.isActive());
        shelfRepository.save(shelf);

        modelMapper.map(shelf, dto);
        dto.setSuccess(true);

        return dto;
    }

    @Override
    public List<ShelfDto> getActiveShelves() {
        return shelfRepository.findByActiveTrue()
                .stream()
                .map(shelf -> modelMapper.map(shelf, ShelfDto.class))
                .toList();
    }
}