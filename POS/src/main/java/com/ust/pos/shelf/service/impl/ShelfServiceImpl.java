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
public class ShelfServiceImpl extends BaseService implements ShelfService {

    private final ShelfRepository shelfRepository;

    private final ModelMapper modelMapper;

    public ShelfServiceImpl(ShelfRepository shelfRepository, ModelMapper modelMapper) {
        this.shelfRepository = shelfRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WsDto<ShelfDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();

        Page<Shelf> shelfPage = shelfRepository.findByIsDeletedFalse(pageable);

        WsDto<ShelfDto> dto = new WsDto<>();

        dto.setContent(modelMapper.map(shelfPage.getContent(), listType));
        dto.setTotalRecords(shelfPage.getTotalElements());
        dto.setTotalPages(shelfPage.getTotalPages());
        dto.setSizePerPage(pageable.getPageSize());
        dto.setPage(pageable.getPageNumber());

        return dto;
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfRepository.findByIdentifier(identifier), ShelfDto.class);
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);

        if (existingShelf != null) {
            shelfDto.setMessage(
                    existingShelf.isDeleted()
                            ? "Shelf - " + identifier + " already exists but was deleted, Please contact Administrator"
                            : "Shelf - " + identifier + " already exists"
            );

            shelfDto.setSuccess(false);
            return shelfDto;
        }
        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        setCreatedDetails(shelf);
        shelfRepository.save(shelf);
        return shelfDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        setModifiedDetails(shelf);
        softDelete(shelf);
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);
        if (existingShelf == null) {
            shelfDto.setMessage("Shelf with shelf - " + identifier + " not found");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        modelMapper.map(shelfDto, existingShelf);
        setModifiedDetails(existingShelf);
        shelfRepository.save(existingShelf);
        return shelfDto;
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
    public List<ShelfDto> findActiveStatus() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(shelfRepository.findByStatusTrue(), listType);
    }

    @Override
    public List<ShelfDto> findActiveShelf() {

        List<Shelf> shelves = shelfRepository.findByStatusTrue();

        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();

        return modelMapper.map(shelves, listType);
    }
}
