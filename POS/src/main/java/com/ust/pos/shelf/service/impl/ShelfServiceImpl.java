package com.ust.pos.shelf.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.ShelfService;
import jakarta.persistence.EntityNotFoundException;
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

    public static final String SHELF_WITH_IDENTIFIER = "Shelf with identifier - ";
    private final ShelfRepository shelfRepository;
    private final ModelMapper modelMapper;

    public ShelfServiceImpl(ShelfRepository shelfRepository, ModelMapper modelMapper) {
        this.shelfRepository = shelfRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        Shelf existingShelf = shelfRepository.findByIdentifier(shelfDto.getIdentifier());
        if (existingShelf != null) {
            if (existingShelf.isDeleted()) {shelfDto.setMessage(SHELF_WITH_IDENTIFIER + shelfDto.getIdentifier()
                                + " has been soft deleted. (Rollback by changing status)");
                shelfDto.setSuccess(false);
                return shelfDto;
            }
            shelfDto.setMessage(SHELF_WITH_IDENTIFIER + shelfDto.getIdentifier() + " already exists");
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
    public PaginationResponseDto<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {}.getType();
        PaginationResponseDto<ShelfDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<Shelf> shelfs = shelfRepository.findAll();
            response.setDtoList(modelMapper.map(shelfs, listType));
            response.setTotalRecords(shelfs.size());
            response.setTotalPages(1);
            response.setSizePerPage(shelfs.size());
            response.setPage(0);
        } else {
            Page<Shelf> shelfPage = shelfRepository.findByDeletedFalse(pageable);
            response.setDtoList(modelMapper.map(shelfPage.getContent(), listType));
            response.setTotalRecords(shelfPage.getTotalElements());
            response.setTotalPages(shelfPage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfRepository.findByIdentifier(identifier), ShelfDto.class);
    }

    @Override
    public void delete(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf == null) {
            throw new EntityNotFoundException("Shelf not found");
        }
        softDelete(shelf);
        setModifiedDetails(shelf);
        shelfRepository.save(shelf);
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {
        Shelf existingShelf = shelfRepository.findByIdentifier(shelfDto.getIdentifier());
        if (existingShelf == null) {
            shelfDto.setMessage(SHELF_WITH_IDENTIFIER + shelfDto.getIdentifier() + "not found");
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
    public ShelfDto toggleStatus(String identifier,boolean status) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf != null) {
            shelf.setStatus(!shelf.isStatus());
            setModifiedDetails(shelf);
            shelfRepository.save(shelf);
        }
        return modelMapper.map(shelf ,ShelfDto.class);
    }

    public List<ShelfDto> findActiveShelves() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(shelfRepository.findByStatusTrue(), listType);
    }
}