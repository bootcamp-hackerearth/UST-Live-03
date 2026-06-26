package com.ust.pos.shelf.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.ShelfService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShelfServiceImpl extends BaseService implements ShelfService {

    private static final String VALIDATION_MESSAGE = "Shelf with identifier - ";
    private final ShelfRepository shelfRepository;
    private final ModelMapper modelMapper;


    public ShelfServiceImpl(ShelfRepository shelfRepository, ModelMapper modelMapper) {
        this.shelfRepository = shelfRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {

        Shelf shelf = shelfRepository.findByIdentifier(identifier);

        if (shelf == null) {
            return null;
        }

        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {

        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);

        if (existingShelf != null) {
            shelfDto.setMessage(
                    existingShelf.isDeleted()
                            ? VALIDATION_MESSAGE + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : VALIDATION_MESSAGE + identifier
                            + " already exists."
            );
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
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);

        if (existingShelf == null) {
            shelfDto.setMessage(VALIDATION_MESSAGE + identifier + " not found");
            shelfDto.setSuccess(false);
            return shelfDto;
        }

        modelMapper.map(shelfDto, existingShelf);
        setModifiedDetails(existingShelf);
        shelfRepository.save(existingShelf);

        return shelfDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        setModifiedDetails(shelf);
        softDelete(shelf);
    }

    @Override
    public WsDto<ShelfDto> findAll(Pageable pageable) {

        Page<Shelf> shelfPage = shelfRepository.findByIsDeletedFalse(pageable);

        WsDto<ShelfDto> shelfDto = new WsDto<>();

        List<ShelfDto> shelfDtos = shelfPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, ShelfDto.class))
                .toList();

        shelfDto.setContent(shelfDtos);
        shelfDto.setPage(shelfPage.getNumber());
        shelfDto.setSizePerPage(shelfPage.getSize());
        shelfDto.setTotalPages(shelfPage.getTotalPages());
        shelfDto.setTotalRecords(shelfPage.getTotalElements());

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
    public List<ShelfDto> findActiveShelf() {

        List<Shelf> shelves = shelfRepository.findByStatus(true);
        return shelves.stream().map(shelf -> modelMapper.map(shelf, ShelfDto.class)).toList();
    }

}



