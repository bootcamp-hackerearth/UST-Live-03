package com.ust.pos.shelf.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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

@Transactional
@Service
public class ShelfServiceImpl extends CommonService implements ShelfService {
    public static final String SHELF_WITH_IDENTIFIER = "Shelf with identifier - ";
    private final ShelfRepository shelfRepository;
    private final ModelMapper modelMapper;

    public ShelfServiceImpl(ShelfRepository shelfRepository, ModelMapper modelMapper) {
        this.shelfRepository = shelfRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);
        if (existingShelf != null) {
            if (!existingShelf.isDeleted()) {
                shelfDto.setMessage(SHELF_WITH_IDENTIFIER + identifier + " already exists");
                shelfDto.setSuccess(false);
                return shelfDto;
            }
            shelfDto.setMessage(SHELF_WITH_IDENTIFIER + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        setAuditFields(shelf, true);
        shelfRepository.save(shelf);
        shelfDto.setSuccess(true);
        shelfDto.setMessage("Shelf created successfully");
        return shelfDto;
    }

    @Override
    public WsDto<ShelfDto> findAll(Pageable pageable) {
        Page<Shelf> shelfPage = shelfRepository.findByDeletedFalse(pageable);
        Type type = new TypeToken<List<ShelfDto>>() {
        }.getType();
        WsDto<ShelfDto> shelfWsDto = new WsDto<>();
        shelfWsDto.setDtoList(modelMapper.map(shelfPage.getContent(), type));
        shelfWsDto.setTotalRecords(shelfPage.getTotalElements());
        shelfWsDto.setTotalPages(shelfPage.getTotalPages());
        shelfWsDto.setSizePerPage(pageable.getPageSize());
        shelfWsDto.setPage(pageable.getPageNumber());
        return shelfWsDto;
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

    @Override
    public List<ShelfDto> findAllActive() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(shelfRepository.findAllByStatusAndDeletedFalse(true), listType);
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if(shelf==null){
            throw new ResourceNotFoundException("Shelf with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(shelf,ShelfDto.class);
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifier(shelfDto.getIdentifier());
        if (existingShelf == null) {
            shelfDto.setMessage(SHELF_WITH_IDENTIFIER + identifier + " not found");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        modelMapper.map(shelfDto, existingShelf);
        setAuditFields(existingShelf, false);
        shelfRepository.save(existingShelf);
        return shelfDto;
    }

    @Override
    public ShelfDto toggleStatus(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        shelf.setStatus(!shelf.isStatus());
        setAuditFields(shelf, false);
        shelfRepository.save(shelf);
        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public boolean delete(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        if (shelf == null) return false;
        softDelete(shelf);
        setAuditFields(shelf, false);
        shelfRepository.save(shelf);
        return true;
    }
}
