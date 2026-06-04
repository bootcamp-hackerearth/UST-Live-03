package com.ust.pos.racks.service.impl;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.modell.Racks;
import com.ust.pos.modell.RacksRepository;
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

    public static final RuntimeException SHELF_NOT_FOUND = new RuntimeException("Shelf not found");

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RacksRepository racksRepository;

    @Override
    public RacksDto findByIdentifier(String identifier) {
        return modelMapper.map(racksRepository.findByIdentifier(identifier), RacksDto.class);
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
    public RacksDto update(RacksDto racksDto) {
        String identifier = racksDto.getIdentifier();
        Racks existingRacks = racksRepository.findByIdentifier(identifier);

        if (existingRacks == null) {
            racksDto.setMessage("Racks with identifier - " + identifier + " not found");
            racksDto.setSuccess(false);
            return racksDto;
        }
        modelMapper.map(racksDto, existingRacks);
        racksRepository.save(existingRacks);
        return racksDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        racksRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<RacksDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Page<Racks> racksPage = racksRepository.findAll(pageable);
        return modelMapper.map(racksPage.getContent(), listType);
    }

    @Override
    public void toggleStatus(String identifier) {
        Racks racks = racksRepository.findByIdentifier(identifier);
        if (racks == null) {
            throw SHELF_NOT_FOUND;
        }
        racks.setStatus(!racks.isStatus());
        racksRepository.save(racks);
    }

    @Override
    public List<RacksDto> findAllActive() {
        return racksRepository.findByStatusTrue()
                .stream()
                .map(racks -> modelMapper.map(racks, RacksDto.class))
                .toList();
    }

}
