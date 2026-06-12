package com.ust.pos.racks.service.impl;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
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
public class RacksServiceImpl implements RacksService {
    @Autowired
    private RacksRepository racksRepository;

    @Autowired
    private ModelMapper modelMapper;

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

    @Transactional
    public void delete(String identifier) {
        racksRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<RacksDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Page<Racks> racksPage = racksRepository.findAll(pageable);
        WsDto<RacksDto> racksWsDto = new WsDto<>();
        racksWsDto.setDtoList(modelMapper.map(racksPage.getContent(), listType));
        racksWsDto.setTotalRecords(racksPage.getTotalElements());
        racksWsDto.setTotalPages(racksPage.getTotalPages());
        racksWsDto.setSizePerPage(pageable.getPageSize());
        racksWsDto.setPage(pageable.getPageNumber());

        return racksWsDto;
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
