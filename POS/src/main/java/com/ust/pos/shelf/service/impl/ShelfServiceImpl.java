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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ShelfServiceImpl extends BaseService implements ShelfService {

    private final ShelfRepository shelfRepository;
    private final ModelMapper modelMapper;

    public ShelfServiceImpl(ShelfRepository shelfRepository, ModelMapper modelMapper) {
        this.shelfRepository = shelfRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier().trim();
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);
        if (existingShelf != null) {
            if (existingShelf.isDeleted()) {
                shelfDto.setMessage("Shelf with identifier " + identifier + " has been soft deleted. (Rollback by changing status)");
                shelfDto.setSuccess(false);
                return shelfDto;
            }
            shelfDto.setMessage("Shelf with identifier - " + identifier + " already exists");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        setCreatedDetails(shelf);
        shelfRepository.save(shelf);
        shelfDto.setSuccess(true);
        shelfDto.setMessage("Shelf created successfully");
        return shelfDto;
    }

    @Override
    public WsDto<ShelfDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        WsDto<ShelfDto> wsDto = new WsDto<>();
        if (pageable == null) {
            List<ShelfDto> shelfDtoList = modelMapper.map(shelfRepository.findByDeletedFalse(pageable), listType);
            wsDto.setDtoList(shelfDtoList);
            wsDto.setTotalRecords(shelfDtoList.size());
            return wsDto;
        }
        Page<Shelf> shelfPage = shelfRepository.findByDeletedFalse(pageable);
        wsDto.setDtoList(modelMapper.map(shelfPage.getContent(), listType));
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setTotalPages(shelfPage.getTotalPages());
        wsDto.setTotalRecords(shelfPage.getTotalElements());
        return wsDto;
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfRepository.findByIdentifier(identifier), ShelfDto.class);
    }

    @Override
    public void delete(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        softDelete(shelf);
        setModifiedDetails(shelf);
        shelfRepository.save(shelf);
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {
        Shelf existingShelf = shelfRepository.findByIdentifier(shelfDto.getIdentifier());
        if (existingShelf == null) {
            shelfDto.setMessage("Shelf with identifier - " + shelfDto.getIdentifier() + " not found");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        modelMapper.map(shelfDto, existingShelf);
        setModifiedDetails(existingShelf);
        shelfRepository.save(existingShelf);
        shelfDto.setSuccess(true);
        shelfDto.setMessage("Shelf updated successfully");
        return shelfDto;
    }

    @Override
    @Transactional
    public ShelfDto toggleStatus(String identifier, boolean status) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf == null) {
            ShelfDto response = new ShelfDto();
            response.setSuccess(false);
            response.setMessage("Shelf not found");
            return response;
        }
        shelf.setStatus(status);
        setModifiedDetails(shelf);
        shelfRepository.save(shelf);
        ShelfDto response = modelMapper.map(shelf, ShelfDto.class);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

    @Override
    public List<ShelfDto> findActiveShelves() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(shelfRepository.findByStatusTrue(), listType);
    }

    @Override
    public WsDto<ShelfDto> findAll(Specification<Shelf> specification,
                                   Pageable pageable) {

        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();

        Page<Shelf> page =
                shelfRepository.findAll(specification, pageable);

        WsDto<ShelfDto> wsDto = new WsDto<>();

        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}