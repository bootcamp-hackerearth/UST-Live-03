package com.ust.pos.rack.service.impl;

import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.RackService;
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
public class RackServiceImpl implements RackService {
    @Autowired
    private RackRepository rackRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RackDto save(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifier(identifier);
        if (existingRack != null) {
            rackDto.setMessage("Rack with identifier - " + identifier + " already exists");
            rackDto.setSuccess(false);
            return rackDto;
        }
        Rack rack = modelMapper.map(rackDto, Rack.class);
        rackRepository.save(rack);
        return rackDto;
    }

    @Override
    public RackDto update(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifier(identifier);
        if (existingRack == null) {
            rackDto.setMessage("Rack with identifier - " + identifier + " is not found");
            rackDto.setSuccess(false);
            return rackDto;
        }
        Rack rack = modelMapper.map(rackDto, Rack.class);
        rackRepository.save(rack);
        return rackDto;
    }

    @Override
    public void delete(String identifier) {
        rackRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<RackDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        Page<Rack> rackPage = rackRepository.findAll(pageable);
        WsDto<RackDto> rackDtoWsDto = new WsDto<>();
        rackDtoWsDto.setDtoList(modelMapper.map(rackPage.getContent(), listType));
        rackDtoWsDto.setTotalRecords(rackPage.getTotalElements());
        rackDtoWsDto.setTotalPages(rackPage.getTotalPages());
        rackDtoWsDto.setSizePerPage(pageable.getPageSize());
        rackDtoWsDto.setPage(pageable.getPageNumber());
        return rackDtoWsDto;
    }

    @Override
    public RackDto findByIdentifier(String identifier) {
        return modelMapper.map(
                rackRepository.findByIdentifier(identifier.trim()), RackDto.class
        );
    }

    @Override
    public void toggleStatus(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        rack.setStatus(!rack.getStatus());
        rackRepository.save(rack);
    }
}