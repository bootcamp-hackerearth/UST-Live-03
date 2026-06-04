package com.ust.pos.shelf.service.impl;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.ShelfService;
import jakarta.transaction.Transactional;
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

    @Autowired
    ShelfRepository shelfRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public List<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> shelfPage = shelfRepository.findAll(pageable);
        return modelMapper.map(shelfPage.getContent(), listType);
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfRepository.findByIdentifier(identifier), ShelfDto.class);
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);
        if (existingShelf != null) {
            shelfDto.setMessage("Shelf - " + identifier + " already exists");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        shelfRepository.save(shelf);
        return shelfDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        shelfRepository.deleteByIdentifier(identifier);
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);
        if (existingShelf == null) {
            shelfDto.setMessage("Shelf with shelf - " + identifier + " not found");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        modelMapper.map(shelfDto, existingShelf);
        shelfRepository.save(existingShelf);
        return shelfDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf != null) {
            shelf.setStatus(!shelf.isStatus());
            shelfRepository.save(shelf);
        }
    }

    @Override
    public List<ShelfDto> findActiveStatus() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(shelfRepository.findByStatusTrue(), listType);
    }

    @Override
    public List<ShelfDto> findActiveShelf() {
        List<Shelf> shelves = shelfRepository.findByStatusTrue();
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();

        return modelMapper.map(shelves, listType);
    }
}
