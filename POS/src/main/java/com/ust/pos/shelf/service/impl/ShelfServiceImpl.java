package com.ust.pos.shelf.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.ShelfService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShelfServiceImpl extends BaseService implements ShelfService {

    private final ModelMapper modelMapper;

    private final ShelfRepository shelfRepository;

    public ShelfServiceImpl(ModelMapper modelMapper, ShelfRepository shelfRepository) {
        this.modelMapper = modelMapper;
        this.shelfRepository = shelfRepository;
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingshelf = shelfRepository.findByIdentifier(identifier);
        if (existingshelf != null) {
            shelfDto.setMessage("Shelf already exists");
            if (existingshelf.isDeleted()) {
                shelfDto.setMessage("Shelf with identifier - " + identifier + " was deleted , Please Contact the Administrator to add.");
            }
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        setCreatedDetails(shelf);
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
                setModifiedDetails(existingshelf);
                shelfRepository.save(existingshelf);
                shelfDto.setSuccess(true);
            }
            return shelfDto;
        }
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifierAndIsDeletedFalse(identifier);
        if (shelf == null) {
            throw new ResourceNotFoundException("Shelf with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public WsDto<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> shelfPage = shelfRepository.findByIsDeletedFalse(pageable);
        WsDto<ShelfDto> shelfWsDto = new WsDto<>();
        shelfWsDto.setDtoList(modelMapper.map(shelfPage.getContent(), listType));
        shelfWsDto.setTotalRecords(shelfPage.getTotalElements());
        shelfWsDto.setTotalPages(shelfPage.getTotalPages());
        shelfWsDto.setSizePerPage(pageable.getPageSize());
        shelfWsDto.setPage(pageable.getPageNumber());

        return shelfWsDto;
    }

    @Override
    public void delete(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        softDelete(shelf);
        setModifiedDetails(shelf);
    }

    @Override
    public void toggleStatus(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf != null) {
            shelf.setStatus(!shelf.getStatus());
            setModifiedDetails(shelf);
            shelfRepository.save(shelf);
        }
    }

    @Override
    public WsDto<ShelfDto> findAll(Specification<Shelf> example, Pageable pageable) {

        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> page = shelfRepository.findAll(example, pageable);

        WsDto<ShelfDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}