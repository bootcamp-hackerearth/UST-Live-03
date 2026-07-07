package com.ust.pos.shelf.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.ShelfService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public PaginationResponseDto<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();

        Page<Shelf> shelfPage = shelfRepository.findByIsDeletedFalse(pageable);

        List<ShelfDto> shelfDtoList = modelMapper.map(shelfPage.getContent(), listType);

        PaginationResponseDto<ShelfDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(shelfDtoList);
        paginationResponseDto.setPage(shelfPage.getNumber());
        paginationResponseDto.setSizePerPage(shelfPage.getSize());
        paginationResponseDto.setTotalPages(shelfPage.getTotalPages());
        paginationResponseDto.setTotalRecords(shelfPage.getTotalElements());

        return paginationResponseDto;
    }

    @Override
    public PaginationResponseDto<ShelfDto> findAll(Specification<Shelf> example, Pageable pageable) {

        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> page = shelfRepository.findAll(example, pageable);

        PaginationResponseDto<ShelfDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationResponseDto.setTotalRecords(page.getTotalElements());
        paginationResponseDto.setTotalPages(page.getTotalPages());
        paginationResponseDto.setSizePerPage(pageable.getPageSize());
        paginationResponseDto.setPage(pageable.getPageNumber());

        return paginationResponseDto;
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
            shelf = modelMapper.map(shelfDto, Shelf.class);
            setCreatedDetails(shelf);
            shelfRepository.save(shelf);
            shelfDto.setMessage("Successfully added the shelf");
            shelfDto.setSuccess(true);
        } else if (isSoftDeleted(shelf)) {
            shelfDto.setMessage(
                    getDeletedMessage("Shelf", identifier)
            );
            shelfDto.setSuccess(false);
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

        setModifiedDetails(shelf);
        shelf.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {

        String identifier = shelfDto.getIdentifier();

        Shelf existingShelf =
                shelfRepository.findByIdentifier(identifier);

        if (existingShelf == null) {
            shelfDto.setMessage("Shelf not found");
            shelfDto.setSuccess(false);
            return shelfDto;
        }

        if (isSoftDeleted(existingShelf)) {
            shelfDto.setSuccess(false);
            shelfDto.setMessage(
                    getDeletedMessage("Shelf", identifier)
            );
            return shelfDto;
        }

        modelMapper.map(shelfDto, existingShelf);

        setModifiedDetails(existingShelf);

        shelfRepository.save(existingShelf);

        shelfDto.setMessage("Shelf updated successfully");
        shelfDto.setSuccess(true);

        return shelfDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        softDelete(shelf);
        setModifiedDetails(shelf);
        shelfRepository.save(shelf);
    }
}
