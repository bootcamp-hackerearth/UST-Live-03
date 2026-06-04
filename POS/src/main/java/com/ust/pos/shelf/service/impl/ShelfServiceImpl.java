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
    private ShelfRepository shelfRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(
                shelfRepository.findByIdentifier(identifier),
                ShelfDto.class
        );
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {

        Shelf existing = shelfRepository.findByIdentifier(shelfDto.getIdentifier());
        if (existing != null) {
            shelfDto.setSuccess(false);
            shelfDto.setMessage("Shelf already exists : " + shelfDto.getIdentifier());
            return shelfDto;
        }

        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        shelfRepository.save(shelf);
        return shelfDto;
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {

        Shelf existing = shelfRepository.findByIdentifier(shelfDto.getIdentifier());
        if (existing == null) {
            shelfDto.setSuccess(false);
            shelfDto.setMessage("Shelf not found : " + shelfDto.getIdentifier());
            return shelfDto;
        }

        modelMapper.map(shelfDto, existing);
        shelfRepository.save(existing);
        return shelfDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        shelfRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> customerPage = shelfRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }

    @Override
    public ShelfDto toggleStatus(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        shelf.setStatus(!shelf.isStatus());
        shelfRepository.save(shelf);
        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public List<ShelfDto> findIfTrue() {
        Type listType = new TypeToken<List<ShelfDto>>() {

        }.getType();
        return modelMapper.map(shelfRepository.findByStatusIsTrue(), listType);
    }


}