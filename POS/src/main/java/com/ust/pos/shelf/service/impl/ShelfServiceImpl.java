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
import java.util.Optional;

@Service
@Transactional
public class ShelfServiceImpl implements ShelfService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ShelfRepository shelfRepository;

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingshelf = shelfRepository.findByIdentifier(identifier);
        if (existingshelf != null) {
            shelfDto.setMessage("Shelf already exists");
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
        Optional<Shelf> optionalShelf = shelfRepository.findById(shelfDto.getId());
        if (optionalShelf.isEmpty()) {
            shelfDto.setSuccess(false);
            return shelfDto;
        } else {
            Shelf existingshelf = optionalShelf.get();
            if (!identifier.equalsIgnoreCase(existingshelf.getIdentifier()) && shelfRepository.findByIdentifier(identifier) != null) {
                shelfDto.setSuccess(false);
                shelfDto.setMessage("Shelf already exists");
                return shelfDto;
            } else {
                modelMapper.map(shelfDto, existingshelf);
                shelfRepository.save(existingshelf);
                shelfDto.setSuccess(true);
            }
            return shelfDto;
        }
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfRepository.findByIdentifier(identifier), ShelfDto.class);
    }

    @Override
    public List<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> shelfPage = shelfRepository.findAll(pageable);
        return modelMapper.map(shelfPage.getContent(), listType);
    }

    @Override
    public void delete(String identifier) {
        shelfRepository.deleteByIdentifier(identifier);
    }

    @Override
    public void toggleStatus(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf != null) {
            shelf.setStatus(!shelf.isStatus());
            shelfRepository.save(shelf);
        }
    }
}