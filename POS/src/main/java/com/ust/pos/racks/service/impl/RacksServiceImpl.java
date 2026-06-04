package com.ust.pos.racks.service.impl;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.model.Racks;
import com.ust.pos.model.RacksRepository;
import com.ust.pos.racks.service.RacksService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class RacksServiceImpl implements RacksService {
    @Autowired
    private RacksRepository racksRepository;

    @Autowired
    private ModelMapper modelMapper;

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

        Racks existingRacks =
                racksRepository.findByIdentifier(racksDto.getIdentifier());

        if (existingRacks == null) {
            racksDto.setSuccess(false);
            racksDto.setMessage("Racks not found");
            return racksDto;
        }

        existingRacks.setDescription(racksDto.getDescription());
        existingRacks.setStatus(racksDto.isStatus());
        modelMapper.map(racksDto, existingRacks);
        racksRepository.save(existingRacks);

        return racksDto;
    }

    @Override
    public void delete(String identifier) {
        racksRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<RacksDto> findAll() {
        Type listOfType = new TypeToken<List<RacksDto>>() {
        }.getType();
        return modelMapper.map(racksRepository.findAll(), listOfType);
    }

    @Override
    public List<RacksDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Page<Racks> racksPage = racksRepository.findAll(pageable);
        return modelMapper.map(racksPage.getContent(), listOfType);
    }

    @Override
    public RacksDto findByIdentifier(String identifier) {
        return modelMapper.map(racksRepository.findByIdentifier(identifier), RacksDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Racks racks = racksRepository.findByIdentifier(identifier);
        if (racks != null) {
            racks.setStatus(!racks.getStatus());
            racksRepository.save(racks);
        }

    }
}
