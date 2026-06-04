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

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ShelfDto save(ShelfDto shelfDto) {

        Shelf existing = shelfRepository.findByIdentifier(shelfDto.getIdentifier());
        if (existing != null) {
            shelfDto.setMessage("Shelf Already Exist!");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        shelfRepository.save(shelf);
        shelfDto.setSuccess(true);
        return shelfDto;
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {

        Shelf shelf = shelfRepository.findByIdentifier(shelfDto.getIdentifier());

        if (shelf == null) {
            shelfDto.setSuccess(false);
            return shelfDto;
        }

        modelMapper.map(shelfDto, shelf);
        shelfRepository.save(shelf);
        shelfDto.setSuccess(true);
        return shelfDto;
    }

    @Override
    public ShelfDto findById(Long id) {
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shelf not found with id " + id));
        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public List<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> shelfPage = shelfRepository.findAll(pageable);
        return modelMapper.map(shelfPage.getContent(), listType);
    }

    @Override
    public void deleteById(Long id) {
        shelfRepository.deleteById(id);
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public ShelfDto changeShelfStatus(String identifier, boolean status) {

        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf == null) {
            return null; // test expects null
        }
        shelf.setStatus(status);
        shelfRepository.save(shelf);
        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public List<ShelfDto> findActiveShelf() {
        return shelfRepository.findByStatusTrue()
                .stream()
                .map(shelf -> modelMapper.map(shelf, ShelfDto.class))
                .toList();
    }
}