package com.ust.pos.shelf.service.impl;

import com.ust.pos.commonservice.CommonService;
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

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ShelfServiceImpl extends CommonService implements ShelfService {

    private final ShelfRepository shelfRepository;

    private final ModelMapper modelMapper;

    public ShelfServiceImpl(ShelfRepository shelfRepository, ModelMapper modelMapper) {
        this.shelfRepository = shelfRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {

        Shelf existing = shelfRepository.findByIdentifier(shelfDto.getIdentifier());
        if (existing != null) {
            if (existing.isDeleted()) {
                shelfDto.setMessage("Shelf with identifier - " + existing + "has been soft deleted.(Rollback by changing status");
                shelfDto.setSuccess(false);
                return shelfDto;
            }
            shelfDto.setMessage("Shelf Already Exist!");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        setAuditFields(shelf, true);
        shelfRepository.save(shelf);
        shelfDto.setSuccess(true);
        return shelfDto;
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {

        Shelf shelf = shelfRepository.findByIdentifier(shelfDto.getIdentifier());

        if (shelf == null) {
            shelfDto.setSuccess(false);
            return shelfDto;
        }

        modelMapper.map(shelfDto, shelf);
        setAuditFields(shelf, false);
        shelfRepository.save(shelf);
        shelfDto.setSuccess(true);
        return shelfDto;
    }

    @Override
    public ShelfDto findById(Long id) {
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shelf not found with id " + id));
        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public WsDto<ShelfDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> shelfPage = shelfRepository.findByDeletedFalse(pageable);
        WsDto<ShelfDto> shelfWsDto = new WsDto<>();
        shelfWsDto.setDtoList(modelMapper.map(shelfPage.getContent(), listType));
        shelfWsDto.setTotalRecords(shelfPage.getTotalElements());
        shelfWsDto.setTotalPages(shelfPage.getTotalPages());
        shelfWsDto.setSizePerPage(pageable.getPageSize());
        shelfWsDto.setPage(pageable.getPageNumber());

        return shelfWsDto;

    }

    @Override
    public WsDto<ShelfDto> findAll(Specification<Shelf> spec, Pageable pageable) {
        Type listType = new TypeToken<List<ShelfDto>>() {}.getType();
        Page<Shelf> page = shelfRepository.findAll(spec, pageable);
        WsDto<ShelfDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public void delete(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        softDelete(shelf);
        setAuditFields(shelf, false);
        shelfRepository.save(shelf);
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf == null) {
            throw new ResourceNotFoundException("Shelf with identifier '" + identifier + "'not found");
        }
        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public ShelfDto changeShelfStatus(String identifier, boolean status) {

        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf == null) {
            return null; // test expects null
        }
        shelf.setStatus(status);
        shelfRepository.save(shelf);
        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public List<ShelfDto> findActiveShelf() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(shelfRepository.findByStatusTrue(true), listType);
    }
}