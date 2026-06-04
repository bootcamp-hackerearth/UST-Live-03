package com.ust.pos.shelfs.sevice.impl;

import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.model.Shelfs;
import com.ust.pos.model.ShelfsRepository;
import com.ust.pos.shelfs.sevice.ShelfsService;
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
public class ShelfsServiceImpl implements ShelfsService {

    @Autowired
    ShelfsRepository shelfsRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<ShelfsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        Page<Shelfs> shelfsPage=shelfsRepository.findAll(pageable);
        return modelMapper.map(shelfsPage.getContent(), listType);
    }

    @Override
    public List<ShelfsDto> findActiveStatus() {
        List<Shelfs> allShelves = shelfsRepository.findAll();
        List<Shelfs> activeShelves = allShelves.stream().filter(Shelfs::isStatus).toList();
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        return modelMapper.map(activeShelves, listType);
    }

    @Override
    public ShelfsDto changeToggleStatus(String identifier, boolean status) {
        Shelfs shelfs = shelfsRepository.findByIdentifier(identifier);
        if (shelfs != null) {
            shelfs.setStatus(status);
            shelfsRepository.save(shelfs);
        }
        return modelMapper.map(shelfs, ShelfsDto.class);
    }

    @Override
    public ShelfsDto save(ShelfsDto shelfsDto) {
        String identifier = shelfsDto.getIdentifier();
        Shelfs existingShelfs = shelfsRepository.findByIdentifier(identifier);
        if (existingShelfs != null) {
            shelfsDto.setMessage("Shelfs with identifier - " + identifier + " already exists");
            shelfsDto.setSuccess(false);
            return shelfsDto;
        }
        Shelfs price = modelMapper.map(shelfsDto, Shelfs.class);
        shelfsRepository.save(price);
        return shelfsDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        shelfsRepository.deleteByIdentifier(identifier);
    }

    @Override
    public ShelfsDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfsRepository.findByIdentifier(identifier), ShelfsDto.class);
    }

    @Override
    public ShelfsDto update(ShelfsDto shelfsDto) {
        String identifier = shelfsDto.getIdentifier();
        Shelfs existingShelfs = shelfsRepository.findByIdentifier(identifier);
        modelMapper.map(shelfsDto, existingShelfs);
        shelfsRepository.save(existingShelfs);
        return shelfsDto;
    }
}
