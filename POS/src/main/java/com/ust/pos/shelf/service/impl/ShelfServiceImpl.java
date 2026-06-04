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
    public List<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        if (pageable == null) {
            return modelMapper.map(shelfRepository.findAll(), listType);
        }
        Page<Shelf> shelfPage = shelfRepository.findAll(pageable);
        return modelMapper.map(shelfPage.getContent(), listType);
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfRepository.findByIdentifier(identifier), ShelfDto.class);
    }

    public List<ShelfDto> findActiveShelfs() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(
                shelfRepository.findByStatusTrue(),
                listType
        );
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf == null) {
            shelfDto.setSuccess(true);
            shelfRepository.save(modelMapper.map(shelfDto, Shelf.class));
            shelfDto.setMessage("Successfully added the shelf");
            shelfDto.setSuccess(true);
        } else {
            shelfDto.setMessage("Shelf " + identifier + " already exists");
            shelfDto.setSuccess(false);
        }
        return shelfDto;
    }

    @Override
    @Transactional
    public ShelfDto updateStatus(String identifier, boolean status) {
        ShelfDto response = new ShelfDto();

        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf == null) {
            response.setSuccess(false);
            response.setMessage("Shelf not found");
            return response;
        }

        shelf.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);
        if (existingShelf == null) {
            shelfDto.setMessage("Shelf not found");
            shelfDto.setSuccess(false);
            return shelfDto;
        }

        modelMapper.map(shelfDto, existingShelf);
        shelfRepository.save(existingShelf);
        shelfDto.setMessage("Shelf updated successfully");
        shelfDto.setSuccess(true);

        return shelfDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        shelfRepository.deleteByIdentifier(identifier);
    }
}
