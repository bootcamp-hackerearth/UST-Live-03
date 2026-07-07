package com.ust.pos.shelf.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.models.Shelf;
import com.ust.pos.models.ShelfRepository;
import com.ust.pos.shelf.service.ShelfService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelfServiceImpl extends BaseService implements ShelfService {

    public static final String SHELF_WITH_IDENTIFIER = "Shelf with identifier - ";

    private final ShelfRepository shelfRepository;
    private final ModelMapper modelMapper;

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifierAndDeletedFalse(identifier);

        if (shelf == null) {
            throw new ResourceNotFoundException("Shelf with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);

        if (existingShelf != null) {

            if (Boolean.TRUE.equals(existingShelf.getDeleted())) {
                shelfDto.setMessage(SHELF_WITH_IDENTIFIER + identifier + " was deleted and cannot be created again.");
                shelfDto.setSuccess(false);
                return shelfDto;
            }

            shelfDto.setMessage(SHELF_WITH_IDENTIFIER + identifier + " already exists");
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
        Shelf existingShelf = shelfRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingShelf == null) {
            shelfDto.setMessage(SHELF_WITH_IDENTIFIER + identifier + " not found");
            shelfDto.setSuccess(false);
            return shelfDto;
        }

        modelMapper.map(shelfDto, existingShelf);
        setModifiedDetails(existingShelf);
        shelfRepository.save(existingShelf);
        return shelfDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(shelf);
        setModifiedDetails(shelf);
        shelfRepository.save(shelf);
    }

    @Override
    public WsDto<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> shelfPage = shelfRepository.findAllByDeletedFalse(pageable);
        WsDto<ShelfDto> shelfWsDto = new WsDto<>();
        shelfWsDto.setDtoList(modelMapper.map(shelfPage.getContent(), listType));
        shelfWsDto.setTotalRecords(shelfPage.getTotalElements());
        shelfWsDto.setTotalPages(shelfPage.getTotalPages());
        shelfWsDto.setSizePerPage(pageable.getPageSize());
        shelfWsDto.setPage(pageable.getPageNumber());
        return shelfWsDto;
    }

    @Override
    public List<ShelfDto> findAllActive() {
        return shelfRepository.findByStatusTrueAndDeletedFalse()
                .stream()
                .map(shelf -> modelMapper.map(shelf, ShelfDto.class))
                .toList();
    }

    @Override
    @Transactional
    public ShelfDto toggleStatus(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifierAndDeletedFalse(identifier);

        if (shelf == null) {
            throw new IllegalArgumentException("Shelf not found with identifier: " + identifier);
        }

        Boolean currentStatus = shelf.getStatus();
        shelf.setStatus(currentStatus == null ? Boolean.TRUE : !currentStatus);
        setModifiedDetails(shelf);
        Shelf saved = shelfRepository.save(shelf);
        return modelMapper.map(saved, ShelfDto.class);
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