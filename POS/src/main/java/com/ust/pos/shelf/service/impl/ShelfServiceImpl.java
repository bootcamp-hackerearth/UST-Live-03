package com.ust.pos.shelf.service.impl;

import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
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

    public static final String SHELF_NOT_FOUND = "Shelf not found";

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        if (shelfDto.getName() == null || shelfDto.getName().trim().isEmpty()) {
            shelfDto.setSuccess(false);
            shelfDto.setMessage("Shelf name is required");
            return shelfDto;
        }
        Shelf existing = shelfRepository.findByIdentifier(shelfDto.getName());
        if (existing != null) {
            shelfDto.setSuccess(false);
            shelfDto.setMessage("Shelf already exists");
            return shelfDto;
        }
        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        shelf.setIdentifier(shelfDto.getName());
        shelfRepository.save(shelf);
        ShelfDto response = modelMapper.map(shelf, ShelfDto.class);
        response.setSuccess(true);
        response.setMessage("Shelf saved successfully");
        return response;
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {
        Shelf shelf = shelfRepository.findByIdentifier(shelfDto.getIdentifier());
        if (shelf == null) {
            ShelfDto dto = new ShelfDto();
            dto.setSuccess(false);
            dto.setMessage(SHELF_NOT_FOUND);
            return dto;
        }
        if (shelfDto.getStatus() != null) {
            shelf.setStatus(shelfDto.getStatus());
        }
        shelfRepository.save(shelf);
        ShelfDto response = modelMapper.map(shelf, ShelfDto.class);
        response.setSuccess(true);
        response.setMessage("Shelf updated successfully");
        return response;
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf == null) {
            ShelfDto dto = new ShelfDto();
            dto.setSuccess(false);
            dto.setMessage(SHELF_NOT_FOUND);
            return dto;
        }
        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public WsDto<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> userPage = shelfRepository.findAll(pageable);

        WsDto<ShelfDto> shelfWsDto = new WsDto<>();
        shelfWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        shelfWsDto.setTotalRecords(userPage.getTotalElements());
        shelfWsDto.setTotalPages(userPage.getTotalPages());
        shelfWsDto.setSizePerPage(pageable.getPageSize());
        shelfWsDto.setPage(pageable.getPageNumber());

        return shelfWsDto;
    }

    @Override
    public List<ShelfDto> getActiveShelves() {
        List<Shelf> list = shelfRepository.findByStatusTrue();
        Type type = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(list, type);
    }

    @Override
    public void delete(String identifier) {
        shelfRepository.deleteByIdentifier(identifier);
    }

    @Override
    public ShelfDto toggleStatus(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf == null) {
            ShelfDto dto = new ShelfDto();
            dto.setSuccess(false);
            dto.setMessage(SHELF_NOT_FOUND);
            return dto;
        }
        shelf.setStatus(!Boolean.TRUE.equals(shelf.getStatus()));
        Shelf saved = shelfRepository.save(shelf);
        ShelfDto response = modelMapper.map(saved, ShelfDto.class);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }
}