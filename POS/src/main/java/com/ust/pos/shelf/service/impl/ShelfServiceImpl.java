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
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ShelfServiceImpl implements ShelfService {

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ShelfDto findByIdentifier(String identifier) {

        Shelf shelf = shelfRepository.findByIdentifier(identifier);

        if (shelf == null) {
            return null;
        }

        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {

        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);

        if (existingShelf != null) {
            shelfDto.setMessage("Shelf with identifier - " + identifier + " already exists");
            shelfDto.setSuccess(false);
            return shelfDto;
        }

        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        shelfRepository.save(shelf);

        return shelfDto;
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {

        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);

        if (existingShelf == null) {
            shelfDto.setMessage("Shelf with identifier - " + identifier + " not found");
            shelfDto.setSuccess(false);
            return shelfDto;
        }

        modelMapper.map(shelfDto, existingShelf);
        shelfRepository.save(existingShelf);

        return shelfDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        shelfRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<ShelfDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> shelfPage = shelfRepository.findAll(pageable);

        return modelMapper.map(shelfPage.getContent(), listType);
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
    public List<Shelf> findActiveShelf() {

        return shelfRepository.findByStatus(true);
    }

}



