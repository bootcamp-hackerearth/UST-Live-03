package com.ust.pos.racks.service.impl;

import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
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

    @Autowired
    private RacksRepository racksRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RacksDto findByIdentifier(String identifier) {
        return modelMapper.map(racksRepository.findByIdentifier(identifier), RacksDto.class
        );
    }

    @Override
    public RacksDto save(RacksDto racksDto) {
        String identifier = racksDto.getIdentifier();
        Racks existingRack = racksRepository.findByIdentifier(identifier);

        if (existingRack != null) {
            racksDto.setMessage("Shelf with identifier - " + identifier + " already exists");
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
        Racks existingRack = racksRepository.findByIdentifier(identifier);

        if (existingRack == null) {
            racksDto.setMessage("Shelf with identifier - " + identifier + " not found");
            racksDto.setSuccess(false);
            return racksDto;
        }

        modelMapper.map(racksDto, existingRack);
        racksRepository.save(existingRack);
        return racksDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        racksRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<RacksDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Page<Racks> rackPage = racksRepository.findAll(pageable);

        WsDto<RacksDto> rackWsDto = new WsDto<>();
        rackWsDto.setDtoList(modelMapper.map(rackPage.getContent(), listType));
        rackWsDto.setTotalRecords(rackPage.getTotalElements());
        rackWsDto.setTotalPage(rackPage.getTotalPages());
        rackWsDto.setSizePerPage(pageable.getPageSize());
        rackWsDto.setPage(pageable.getPageNumber());

        return rackWsDto;
    }

    @Override
    public List<RacksDto> findAllActive() {
        return racksRepository.findByStatusTrue()
                .stream()
                .map(rack -> modelMapper.map(rack, RacksDto.class))
                .toList();
    }
}