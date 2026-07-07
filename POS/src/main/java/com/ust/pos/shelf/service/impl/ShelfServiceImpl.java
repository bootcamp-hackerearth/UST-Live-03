package com.ust.pos.shelf.service.impl;

import com.ust.pos.dto.CommonDto;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.service.BaseService;
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
public class ShelfServiceImpl extends BaseService implements ShelfService {
    private final ShelfRepository shelfRepository;

    private final ModelMapper modelMapper;

    public ShelfServiceImpl(ShelfRepository shelfRepository, ModelMapper modelMapper) {
        this.shelfRepository = shelfRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfDto save(ShelfDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelf existingShelf = shelfRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingShelf != null) {
            shelfDto.setMessage("Shelf with identifier - " + identifier + " already exists");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
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
                shelfRepository.save(existingshelf);
                shelfDto.setSuccess(true);
            }
            return shelfDto;
        }
    }

    @Override
    public void delete(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifierAndDeletedFalse(identifier);
        if (shelf != null) {
            shelf.setDeleted(true);
            shelfRepository.save(shelf);
        }    }

    @Override
    public List<ShelfDto> findAll() {
        Type listOfType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        return modelMapper.map(shelfRepository.findByDeletedFalse(), listOfType);
    }

    @Override
    public Page<ShelfDto> findAll(Pageable pageable , String search) {
        Page<Shelf> shelfPage;
        if(search!= null && !search.trim().isEmpty()){
            Specification<Shelf> specification = buildGlobalSearchSpec(Shelf.class, search);
            shelfPage = shelfRepository.findAll(specification, pageable);
        }
        else {
            shelfPage = shelfRepository.findByDeletedFalse(pageable);
        }
        return shelfPage.map(shelf -> modelMapper.map(shelf , ShelfDto.class));
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfRepository.findByIdentifierAndDeletedFalse(identifier), ShelfDto.class);
    }

    @Override
    public List<ShelfDto> findAllByStatus() {
        Type listType = new TypeToken<List<ShelfDto>>() {
        }.getType();
        List<ShelfDto> shelfDtos = modelMapper.map(shelfRepository.findByDeletedFalse(), listType);
        return shelfDtos.stream().filter(CommonDto::isStatus).toList();
    }

    @Override
    public void toggleStatus(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifierAndDeletedFalse(identifier);
        shelf.setStatus(!shelf.isStatus());
        shelfRepository.save(shelf);
    }

}
