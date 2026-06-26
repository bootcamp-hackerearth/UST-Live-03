package com.ust.pos.shelf.service.impl;
import com.ust.pos.dto.WsDto;
import com.ust.pos.shelf.service.ShelfService;
import com.ust.pos.dto.ShelfDto;
import com.ust.pos.model.Shelf;
import com.ust.pos.model.ShelfRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ShelfServiceImpl implements ShelfService {
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
        if(existingShelf != null)
        {
            shelfDto.setMessage("Shelf with identifier - "+ identifier + " already exists");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        Shelf shelf = modelMapper.map(shelfDto, Shelf.class);
        shelf.setDeleted(false);
        shelfRepository.save(shelf);
        return shelfDto;
    }

    @Override
    public List<ShelfDto> findActiveShelves()
    {
        Type listType = new TypeToken<List<ShelfDto>>() {}.getType();
        return modelMapper.map(
                shelfRepository.findByStatusTrueAndDeletedFalse(),
                listType
        );
    }

    @Override
    public Page<ShelfDto> findAll(String search, Pageable pageable) {
        Page<Shelf> rolePage;
        if(search != null && !search.trim().isEmpty())
        {
            rolePage = shelfRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse(search, pageable);
        }
        else
        {
            rolePage = shelfRepository.findByDeletedFalse(pageable);
        }
        return rolePage.map(shelf -> modelMapper.map(shelf, ShelfDto.class));
    }

    @Override
    public ShelfDto update(ShelfDto shelfDto) {

        Shelf existingShelf =
                shelfRepository.findByIdentifierAndDeletedFalse(shelfDto.getIdentifier());

        if (existingShelf == null) {
            shelfDto.setSuccess(false);
            shelfDto.setMessage("Shelf not found");
            return shelfDto;
        }

        existingShelf.setDescription(shelfDto.getDescription());
        existingShelf.setStatus(shelfDto.isStatus());

        shelfRepository.save(existingShelf);

        return shelfDto;
    }

    @Override
    public void delete(String identifier) {

        Shelf shelf =
                shelfRepository.findByIdentifierAndDeletedFalse(identifier);

        if (shelf != null) {
            shelf.setDeleted(true);
            shelfRepository.save(shelf);
        }
    }

    @Override
    public List<ShelfDto> findAll() {
        Type listOfType = new TypeToken<List<ShelfDto>>(){}.getType();
        return modelMapper.map(shelfRepository.findByDeletedFalse(), listOfType);
    }

    @Override
    public ShelfDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfRepository.findByIdentifierAndDeletedFalse(identifier), ShelfDto.class);
    }

    @Override
    public WsDto<ShelfDto> findAll(Pageable pageable)
    {
        Type listtype = new TypeToken<List<ShelfDto>>(){}.getType();
        Page<Shelf> shelfPage = shelfRepository.findByDeletedFalse(pageable);

        WsDto<ShelfDto> shelfDtoWsDto = new WsDto<>();
        shelfDtoWsDto.setDtoList(modelMapper.map(shelfPage.getContent(), listtype));
        shelfDtoWsDto.setTotalRecords(shelfPage.getTotalElements());
        shelfDtoWsDto.setTotalPage(shelfPage.getTotalPages());
        shelfDtoWsDto.setSizePerPage(pageable.getPageSize());
        shelfDtoWsDto.setPage(pageable.getPageNumber());
        return shelfDtoWsDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Shelf shelf = shelfRepository.findByIdentifierAndDeletedFalse(identifier);
        if (shelf != null) {
            shelf.setStatus(!shelf.getStatus());
            shelfRepository.save(shelf);
        }

    }
}
