package com.ust.pos.shelf.service.impl;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.ShelfService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ShelfServiceImpl implements ShelfService {

    public static final String SHELF_NOT_FOUND = "Shelf not found";
    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ShelfDto createShelf(ShelfDto shelfDto) {

        if (shelfRepository.existsByIdentifier(shelfDto.getIdentifier())) {
            shelfDto.setSuccess(false);
            shelfDto.setMessage("Shelf already exists");
            return shelfDto;
        }

        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        shelfRepository.save(shelf);
        return shelfDto;
    }

    @Override
    public ShelfDto updateShelf(ShelfDto shelfDto) {
        ShelfDto dto = new ShelfDto();

        shelfRepository.findById(shelfDto.getId()).ifPresentOrElse(existing -> {
            existing.setIdentifier(shelfDto.getIdentifier());
            shelfRepository.save(existing);

            modelMapper.map(existing, dto);
            dto.setSuccess(true);
        }, () -> {
            dto.setSuccess(false);
            dto.setMessage(SHELF_NOT_FOUND);
        });

        return dto;
    }

    @Override
    public ShelfDto getShelf(Long id) {

        ShelfDto dto = new ShelfDto();

        shelfRepository.findById(id).ifPresentOrElse(shelf -> {
            modelMapper.map(shelf, dto);
            dto.setSuccess(true);
        }, () -> {
            dto.setSuccess(false);
            dto.setMessage(SHELF_NOT_FOUND);
        });

        return dto;
    }

    @Override
    public List<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> shelfPage = shelfRepository.findAll(pageable);
        return modelMapper.map(shelfPage.getContent(), listType);
    }

    @Override
    public boolean deleteShelf(Long id) {
        shelfRepository.deleteById(id);
        return true;
    }

    @Override
    public ShelfDto toggleStatus(Long id) {
        ShelfDto dto = new ShelfDto();

        shelfRepository.findById(id).ifPresentOrElse(shelf -> {
            shelf.setActive(!shelf.isActive());
            shelfRepository.save(shelf);

            modelMapper.map(shelf, dto);
            dto.setSuccess(true);
        }, () -> {
            dto.setSuccess(false);
            dto.setMessage(SHELF_NOT_FOUND);
        });

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