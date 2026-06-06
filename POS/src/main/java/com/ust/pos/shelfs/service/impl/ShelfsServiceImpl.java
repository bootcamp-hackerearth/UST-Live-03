package com.ust.pos.shelfs.service.impl;

import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.model.Shelfs;
import com.ust.pos.model.ShelfsRepository;
import com.ust.pos.shelfs.service.ShelfsService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ShelfsServiceImpl implements ShelfsService {
    @Autowired
    private ShelfsRepository shelfRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ShelfsDto save(ShelfsDto shelfDto) {
        String identifier = shelfDto.getIdentifier();
        Shelfs existingShelf = shelfRepository.findByIdentifier(identifier);
        if (existingShelf != null) {
            shelfDto.setMessage("Shelf with identifier - " + identifier + " already exists");
            shelfDto.setSuccess(false);
            return shelfDto;
        }
        Shelfs shelf = modelMapper.map(shelfDto, Shelfs.class);
        shelfRepository.save(shelf);
        return shelfDto;
    }

    @Override
    public ShelfsDto update(ShelfsDto shelfDto) {
        Shelfs existingShelf =
                shelfRepository.findByIdentifier(shelfDto.getIdentifier());
        if (existingShelf == null) {
            shelfDto.setSuccess(false);
            shelfDto.setMessage("Shelf not found");
            return shelfDto;
        }
        modelMapper.map(shelfDto, existingShelf);
        shelfRepository.save(existingShelf);
        return shelfDto;
    }

    @Override
    public void delete(String identifier) {
        shelfRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<ShelfsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        Page<Shelfs> shelfsPage = shelfRepository.findAll(pageable);
        return modelMapper.map(shelfsPage.getContent(), listType);
    }

    @Override
    public ShelfsDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfRepository.findByIdentifier(identifier), ShelfsDto.class);
    }

    public List<ShelfsDto> findActiveShelves() {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        return modelMapper.map(
                shelfRepository.findByStatusTrue(),
                listType
        );
    }

    @Override
    public void toggleStatus(String identifier) {
        Shelfs shelf = shelfRepository.findByIdentifier(identifier);
        shelf.setStatus(!shelf.getStatus());
        shelfRepository.save(shelf);
    }
}
