package com.ust.pos.shelf.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.shelf.service.ShelfService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShelfServiceImpl extends CommonService implements ShelfService {
    private final ShelfRepository shelfRepository;
    private final ModelMapper modelMapper;

    ShelfServiceImpl(ShelfRepository shelfRepository, ModelMapper modelMapper) {
        this.shelfRepository = shelfRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingshelf = shelfRepository.
                findByIdentifierAndIsDeleteFalse(identifier);
        if (existingshelf != null) {
            shelfDto.setMessage("Shelf already exists");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        setAuditFields(shelf, true);
        shelfRepository.save(shelf);
        return shelfDto;
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Optional<Shelf> optionalShelf = shelfRepository.findById(shelfDto.getId());
        if (optionalShelf.isEmpty()) {
            shelfDto.setSuccess(false);
            return shelfDto;
        } else {
            Shelf existingshelf = optionalShelf.get();
            if (!identifier.equalsIgnoreCase(existingshelf.getIdentifier()) && shelfRepository.findByIdentifier(identifier) != null) {
                shelfDto.setSuccess(false);
                shelfDto.setMessage("Shelf already exists");
                return shelfDto;
            } else {
                modelMapper.map(shelfDto, existingshelf);
                setAuditFields(existingshelf, false);
                shelfRepository.save(existingshelf);
                shelfDto.setSuccess(true);
            }
            return shelfDto;
        }
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfRepository.
                findByIdentifierAndIsDeleteFalse(identifier), ShelfDto.class);
    }

    @Override
    public List<ShelfDto> findAll() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(shelfRepository.findByIsDeleteFalse(), listType);
    }

    @Override
    public void delete(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (shelf != null) {
            shelf.setDelete(true);
            setAuditFields(shelf, false);
            shelfRepository.save(shelf);
        }
    }

    @Override
    public void updateStatusOnly(String identifier, boolean status) {
        Shelf shelf = shelfRepository.
                findByIdentifierAndIsDeleteFalse(identifier);
        shelf.setStatus(status);
        setAuditFields(shelf, false);
        shelfRepository.save(shelf);
    }

    @Override
    public List<ShelfDto> findAllByStatus() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        List<ShelfDto> shelfDtos = modelMapper.map(shelfRepository.findByIsDeleteFalse(), listType);
        return shelfDtos.stream().filter(s -> s.getStatus()).toList();
    }

    @Override
    public List<ShelfDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        Page<Shelf> rolePage = shelfRepository.findByIsDeleteFalse(pageable);
        return modelMapper.map(rolePage.getContent(), listOfType);
    }

    @Override
    public Page<ShelfDto> findAll(Pageable pageable, String search) {
        Page<Shelf> shelfs;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Shelf> specification = buildGlobalSearchSpec(Shelf.class, search);
            shelfs = shelfRepository.findAll(specification, pageable);
        } else {
            shelfs = shelfRepository.findByIsDeleteFalse(pageable);
        }

        return shelfs.map(shelf -> modelMapper.map(shelf, ShelfDto.class));
    }
}