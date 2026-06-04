package com.ust.pos.racks.service.impl;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.model.RacksRepository;
import com.ust.pos.model.Racks;
import com.ust.pos.racks.service.RacksService;
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
public class RacksServiceImpl implements RacksService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    RacksRepository racksRepository;

    @Override
    public List<RacksDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Page<Racks> racksPage=racksRepository.findAll(pageable);
        return modelMapper.map(racksPage.getContent(), listType);
    }

    @Override
    public RacksDto save(RacksDto racksDto) {
        String identifier = racksDto.getIdentifier();
        Racks existingRacks = racksRepository.findByIdentifier(identifier);
        if (existingRacks != null) {
            racksDto.setMessage("Racks with identifier - " + identifier + " already exists");
            racksDto.setSuccess(false);
            return racksDto;
        }
        Racks racks = modelMapper.map(racksDto, Racks.class);
        racksRepository.save(racks);
        return racksDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        racksRepository.deleteByIdentifier(identifier);
    }

    @Override
    public RacksDto findByIdentifier(String identifier) {
        return modelMapper.map(racksRepository.findByIdentifier(identifier), RacksDto.class);
    }

    @Override
    public RacksDto update(RacksDto racksDto) {
        String identifier = racksDto.getIdentifier();
        Racks existingRacks = racksRepository.findByIdentifier(identifier);
        modelMapper.map(racksDto, existingRacks);
        racksRepository.save(existingRacks);
        return racksDto;
    }

    @Override
    public RacksDto changeToggleStatus(String identifier, boolean status) {
        Racks rack = racksRepository.findByIdentifier(identifier);
        if (rack != null) {
            rack.setStatus(status);
            racksRepository.save(rack);
        }
        return modelMapper.map(rack, RacksDto.class);
    }
}
