package com.ust.pos.shelfs.service.impl;

import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.model.Shelfs;
import com.ust.pos.model.ShelfsRepository;
import com.ust.pos.shelfs.service.ShelfsService;
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
    private ShelfsRepository shelfsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ShelfsDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfsRepository.findByIdentifier(identifier), ShelfsDto.class);
    }

    @Override
    public ShelfsDto toggleStatus(String identifier) {
        Shelfs shelfs = shelfsRepository.findByIdentifier(identifier);
        shelfs.setStatus(!shelfs.isStatus());
        shelfsRepository.save(shelfs);
        return modelMapper.map(shelfs, ShelfsDto.class);
    }

    @Override
    public List<ShelfsDto> findIfTrue() {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        return modelMapper.map(shelfsRepository.findByStatusIsTrue(), listType);
    }

    @Override
    public ShelfsDto save(ShelfsDto shelfsDto) {
        shelfsDto.setIdentifier(shelfsDto.getIdentifier().trim());
        String identifier = shelfsDto.getIdentifier();
        Shelfs existingShelfs = shelfsRepository.findByIdentifier(identifier);
        if (existingShelfs != null) {
            shelfsDto.setMessage("Shelfs with identifier - " + identifier + " already exists");
            shelfsDto.setSuccess(false);
            return shelfsDto;
        }
        Shelfs shelfs = modelMapper.map(shelfsDto, Shelfs.class);
        shelfsRepository.save(shelfs);
        return shelfsDto;
    }

    @Override
    public ShelfsDto update(ShelfsDto shelfsDto) {
        String identifier = shelfsDto.getIdentifier();
        Shelfs existingShelfs = shelfsRepository.findByIdentifier(identifier);
        if (existingShelfs == null) {
            shelfsDto.setMessage("Shelfs with identifier - " + identifier + " not found");
            shelfsDto.setSuccess(false);
            return shelfsDto;
        }
        modelMapper.map(shelfsDto, existingShelfs);
        shelfsRepository.save(existingShelfs);
        return shelfsDto;
    }

    @Override
    public boolean delete(String identifier) {
        shelfsRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public List<ShelfsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        Page<Shelfs> shelfsPage = shelfsRepository.findAll(pageable);
        return modelMapper.map(shelfsPage.getContent(), listType);
    }
}