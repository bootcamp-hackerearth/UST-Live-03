package com.ust.pos.shelf.service.impl;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.ShelfService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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

    private final ModelMapper modelMapper;
    private final ShelfRepository shelfRepository;

    public ShelfServiceImpl(ModelMapper modelMapper, ShelfRepository shelfRepository) {
        this.modelMapper = modelMapper;
        this.shelfRepository = shelfRepository;
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingshelf = shelfRepository.findByIdentifierAndDeletedFalse(identifier);

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
            if (!identifier.equalsIgnoreCase(existingshelf.getIdentifier()) && shelfRepository.findByIdentifierAndDeletedFalse(identifier) != null) {
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
        return modelMapper.map(shelfRepository.findByIdentifierAndDeletedFalse(identifier), ShelfDto.class);
    }

    @Override
    public List<ShelfDto> findAll() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(shelfRepository.findByDeletedFalse(), listType);
    }

    @Override
    public void delete(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifierAndDeletedFalse(identifier);

        if (shelf != null) {
            shelf.setDeleted(true);
            shelfRepository.save(shelf);
        }
    }

    @Override
    public void toggleStatus(String identifier) {
        Shelf shelfs = shelfRepository.findByIdentifierAndDeletedFalse(identifier);
        if (shelfs != null) {
            shelfs.setStatus(!shelfs.isStatus());
            shelfRepository.save(shelfs);
        }
    }

    @Override
    public List<ShelfDto> findActive() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(shelfRepository.findByStatusIsAndDeletedFalse(true), listType);
    }

    @Override
    public Page<ShelfDto> findAll(Pageable pageable, String search) {
        Page<Shelf> shelfs;
        if (search != null && !search.trim().isEmpty()) {
            shelfs = shelfRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse(search, pageable);
        } else {
            shelfs = shelfRepository.findByDeletedFalse(pageable);
        }
        return shelfs.map(shelf -> modelMapper.map(shelf, ShelfDto.class));
    }
}