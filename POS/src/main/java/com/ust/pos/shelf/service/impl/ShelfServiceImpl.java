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
    private final ShelfRepository shelfRepository;
    private final ModelMapper modelMapper;

    public ShelfServiceImpl(ShelfRepository shelfRepository,
                            ModelMapper modelMapper) {
        this.shelfRepository = shelfRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);
        if (existingShelf != null) {
            if (Boolean.TRUE.equals(existingShelf.getDeleted())) {
                shelfDto.setMessage("Shelf - " + identifier + " was deleted and cannot be recreated");
            } else {
                shelfDto.setMessage("Shelf with identifier - " + identifier + " already exists");
            }
            shelfDto.setSuccess(false);
            return shelfDto;
        }

        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        setCreatedDetails(shelf);
        shelfRepository.save(shelf);
        shelfDto.setSuccess(true);
        return shelfDto;
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingShelf == null) {
            shelfDto.setMessage("Shelf with identifier - " + identifier + " not found");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        modelMapper.map(shelfDto, existingShelf);
        setModifiedDetails(existingShelf);
        shelfRepository.save(existingShelf);
        shelfDto.setSuccess(true);
        return shelfDto;
    }

    @Override
    public void delete(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifierAndDeletedFalse(identifier);
        if (shelf != null) {
            softDelete(shelf);
            setModifiedDetails(shelf);
            shelfRepository.save(shelf);
        }
    }

    @Override
    public WsDto<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> shelfPage = shelfRepository.findByDeletedFalse(pageable);
        WsDto<ShelfDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(shelfPage.getContent(), listType));
        wsDto.setTotalRecords(shelfPage.getTotalElements());
        wsDto.setTotalPages(shelfPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(
                shelfRepository.findByIdentifierAndDeletedFalse(identifier),
                ShelfDto.class
        );
    }

    @Override
    public void updateStatus(String identifier, boolean status) {
        Shelf shelf = shelfRepository.findByIdentifierAndDeletedFalse(identifier);
        if (shelf != null) {
            shelf.setStatus(status);
            setModifiedDetails(shelf);
            shelfRepository.save(shelf);
        }
    }

    @Override
    public List<ShelfDto> findAllActive() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(
                shelfRepository.findByStatusAndDeletedFalse(true),
                listType
        );
    }
}