package com.ust.pos.shelf.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.ShelfService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShelfServiceImpl extends BaseService implements ShelfService {

    public static final String SHELF_WITH_IDENTIFIER = "Shelf with identifier - ";
    private final ShelfRepository shelfRepository;
    private final ModelMapper modelMapper;

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfRepository.findByIdentifierAndDeletedFalse(identifier), ShelfDto.class);
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
        Shelf existingShelf = shelfRepository.findByIdentifier(identifier);
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
        WsDto<ShelfDto> shelfDto = new WsDto<>();
        shelfDto.setDtoList(modelMapper.map(shelfPage.getContent(), listType));
        shelfDto.setTotalRecords(shelfPage.getTotalElements());
        shelfDto.setTotalPage(shelfPage.getTotalPages());
        shelfDto.setSizePerPage(pageable.getPageSize());
        shelfDto.setPage(pageable.getPageNumber());
        return shelfDto;
    }

    @Override
    public ShelfDto toggleStatus(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifier(identifier);
        shelf.setStatus(!shelf.isStatus());
        shelfRepository.save(shelf);
        setModifiedDetails(shelf);
        return modelMapper.map(shelf, ShelfDto.class);
    }

    public List<ShelfDto> findActiveShelves() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(
                shelfRepository.findByStatusTrueAndDeletedFalse(),
                listType
        );
    }

}
