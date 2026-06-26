package com.ust.pos.shelf.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.ShelfService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ShelfServiceImpl extends BaseService implements ShelfService {

    public static final String SHELF_NOT_FOUND = "Shelf not found";

    private final ShelfRepository shelfRepository;
    private final ModelMapper modelMapper;

    public ShelfServiceImpl(ShelfRepository shelfRepository,ModelMapper modelMapper) {
        this.shelfRepository = shelfRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {

        if (shelfDto.getName() == null || shelfDto.getName().trim().isEmpty()) {
            shelfDto.setSuccess(false);
            shelfDto.setMessage("Shelf name is required");
            return shelfDto;
        }

        String name = shelfDto.getName().trim();
        Shelf existing = shelfRepository.findByIdentifier(name);
        if (existing != null) {
            shelfDto.setSuccess(false);
            shelfDto.setMessage("Shelf already exists");
            return shelfDto;
        }

        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        shelf.setName(name);
        shelf.setIdentifier(name);
        if (shelf.getStatus() == null) {
            shelf.setStatus(true);
        }

        setCreatedDetails(shelf);
        shelfRepository.save(shelf);
        ShelfDto response = modelMapper.map(shelf, ShelfDto.class);
        response.setSuccess(true);
        response.setMessage("Shelf saved successfully");
        return response;
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {

        String identifier = shelfDto.getIdentifier();

        if (identifier == null || identifier.trim().isEmpty()) {
            ShelfDto dto = new ShelfDto();
            dto.setSuccess(false);
            dto.setMessage(SHELF_NOT_FOUND);
            return dto;
        }

        Shelf shelf = shelfRepository.findByIdentifier(identifier.trim());

        if (shelf == null || Boolean.TRUE.equals(shelf.getDeleted())) {
            ShelfDto dto = new ShelfDto();
            dto.setSuccess(false);
            dto.setMessage(SHELF_NOT_FOUND);
            return dto;
        }

        if (shelfDto.getName() != null && !shelfDto.getName().trim().isEmpty()) {
            String newName = shelfDto.getName().trim();
            shelf.setName(newName);
            shelf.setIdentifier(newName);
        }

        if (shelfDto.getStatus() != null) {
            shelf.setStatus(shelfDto.getStatus());
        }

        setModifiedDetails(shelf);
        shelfRepository.save(shelf);
        ShelfDto response = modelMapper.map(shelf, ShelfDto.class);
        response.setSuccess(true);
        response.setMessage("Shelf updated successfully");

        return response;
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {

        Shelf shelf = shelfRepository.findByIdentifier(identifier);

        if (shelf == null || Boolean.TRUE.equals(shelf.getDeleted())) {
            ShelfDto dto = new ShelfDto();
            dto.setSuccess(false);
            dto.setMessage(SHELF_NOT_FOUND);
            return dto;
        }

        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public WsDto<ShelfDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ShelfDto>>() {}.getType();
        Page<Shelf> shelfPage = shelfRepository.findByDeletedFalse(pageable);
        WsDto<ShelfDto> ws = new WsDto<>();
        ws.setDtoList(modelMapper.map(shelfPage.getContent(), listType));
        ws.setTotalRecords(shelfPage.getTotalElements());
        ws.setTotalPages(shelfPage.getTotalPages());
        ws.setSizePerPage(pageable.getPageSize());
        ws.setPage(pageable.getPageNumber());
        return ws;
    }

    @Override
    public List<ShelfDto> getActiveShelves() {

        return shelfRepository.findByStatusTrueAndDeletedFalse()
                .stream()
                .map(s -> modelMapper.map(s, ShelfDto.class))
                .toList();
    }

    @Override
    public void delete(String identifier) {

        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf == null) return;
        shelf.setDeleted(true);
        setModifiedDetails(shelf);
        shelfRepository.save(shelf);
    }

    @Override
    public ShelfDto toggleStatus(String identifier) {

        Shelf shelf = shelfRepository.findByIdentifier(identifier);

        if (shelf == null || Boolean.TRUE.equals(shelf.getDeleted())) {
            ShelfDto dto = new ShelfDto();
            dto.setSuccess(false);
            dto.setMessage(SHELF_NOT_FOUND);
            return dto;
        }

        shelf.setStatus(!Boolean.TRUE.equals(shelf.getStatus()));
        setModifiedDetails(shelf);
        Shelf saved = shelfRepository.save(shelf);
        ShelfDto response = modelMapper.map(saved, ShelfDto.class);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }
}