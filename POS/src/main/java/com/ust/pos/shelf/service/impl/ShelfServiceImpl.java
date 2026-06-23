package com.ust.pos.shelf.service.impl;

import com.ust.pos.CommonService;
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
public class ShelfServiceImpl extends CommonService implements ShelfService {

    private static final String SHELF_WITH_IDENTIFIER = "Shelf with identifier - ";

    private final ShelfRepository shelfRepository;
    private final ModelMapper modelMapper;

    public ShelfServiceImpl(ShelfRepository shelfRepository,
                            ModelMapper modelMapper) {
        this.shelfRepository = shelfRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfRepository.findByIdentifier(identifier), ShelfDto.class);
    }

    @Override
    public ShelfDto save(ShelfDto dto) {

        if (dto == null || dto.getIdentifier() == null) {
            throw new IllegalArgumentException("Identifier is required");
        }

        String identifier = dto.getIdentifier();
        Shelf existing = shelfRepository.findByIdentifier(identifier);

        if (existing != null) {
            if (!existing.isDeleted()) {
                dto.setSuccess(false);
                dto.setMessage(SHELF_WITH_IDENTIFIER + identifier + " already exists");
                return dto;
            }

            dto.setSuccess(false);
            dto.setMessage(SHELF_WITH_IDENTIFIER + identifier +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        Shelf shelf = modelMapper.map(dto, Shelf.class);

        // ✅ FIX: audit applied on new object (not null existing)
        setAuditFields(shelf, true);

        shelfRepository.save(shelf);

        dto.setSuccess(true);
        dto.setMessage("Shelf created successfully");

        return dto;
    }

    @Override
    public ShelfDto update(ShelfDto dto) {

        String identifier = dto.getIdentifier();
        Shelf existing = shelfRepository.findByIdentifier(identifier);

        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage(SHELF_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        if (existing.isDeleted()) {
            dto.setSuccess(false);
            dto.setMessage(SHELF_WITH_IDENTIFIER + identifier +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        modelMapper.map(dto, existing);
        setAuditFields(existing, false);

        shelfRepository.save(existing);

        dto.setSuccess(true);
        dto.setMessage("Shelf updated successfully");

        return dto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        Shelf shelf = shelfRepository.findByIdentifier(identifier);

        if (shelf != null) {
            softDelete(shelf);
            setAuditFields(shelf, false);
            shelfRepository.save(shelf);
        }
    }

    @Override
    public WsDto<ShelfDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();

        Page<Shelf> page = shelfRepository.findByDeletedFalse(pageable);

        WsDto<ShelfDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public ShelfDto toggleStatus(String identifier) {

        Shelf shelf = shelfRepository.findByIdentifier(identifier);

        if (shelf == null) {
            ShelfDto dto = new ShelfDto();
            dto.setSuccess(false);
            dto.setMessage(SHELF_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        shelf.setStatus(!shelf.isStatus());
        setAuditFields(shelf, false);

        shelfRepository.save(shelf);

        return modelMapper.map(shelf, ShelfDto.class);
    }

    @Override
    public List<ShelfDto> findIfTrue() {

        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();

        return modelMapper.map(
                shelfRepository.findByStatusIsTrueAndDeletedFalse(),
                listType
        );
    }
}