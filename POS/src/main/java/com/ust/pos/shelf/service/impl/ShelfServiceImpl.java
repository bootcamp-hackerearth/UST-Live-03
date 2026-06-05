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
@Transactional
public class ShelfServiceImpl implements ShelfService {

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        if (shelfDto.getId() != null) {
            shelfDto.setMessage("Invalid request: ID must be null while creating shelf");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        shelfRepository.save(shelf);
        shelfDto.setSuccess(true);
        return shelfDto;
    }

    @Override
    public List<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        if(pageable == null){
            return modelMapper.map(shelfRepository.findAll(),listType);
        }
        Page<Shelf> shelfPage = shelfRepository.findAll(pageable);
        return modelMapper.map(shelfPage.getContent(), listType);
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfRepository.findByIdentifier(identifier), ShelfDto.class);
    }

    @Override
    public void delete(String identifier) {
        shelfRepository.deleteByIdentifier(identifier);
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {
        Shelf existingShelf = shelfRepository.findByIdentifier(shelfDto.getIdentifier());
        if (existingShelf == null) {
            shelfDto.setMessage("Shelf with identifier - " + shelfDto.getIdentifier() + "not found");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        modelMapper.map(shelfDto, existingShelf);
        shelfRepository.save(existingShelf);
        return shelfDto;
    }

    @Override
    @Transactional
    public ShelfDto toggleStatus(String identifier,boolean status) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf != null) {
            shelf.setStatus(!shelf.isStatus());
            shelfRepository.save(shelf);
        }
        return modelMapper.map(shelf ,ShelfDto.class);
    }

    public List<ShelfDto> findActiveShelves() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(shelfRepository.findByStatusTrue(), listType);
    }
}