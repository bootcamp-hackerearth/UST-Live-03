package com.ust.pos.rack.service.impl;

import com.ust.pos.dto.RackDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.model.ShelfRepository;
import com.ust.pos.rack.service.RackService;
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
public class RackServiceImpl implements RackService {

    @Autowired
    private RackRepository rackRepository;

    @Autowired
    private ShelfRepository shelfRepository;

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
            rackDto.setMessage("rack with identifier - " + identifier + " not found");
            rackDto.setSuccess(false);
            return rackDto;
        }
        modelMapper.map(rackDto, existingRack);
        rackRepository.save(existingRack);
        return rackDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        rackRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<RackDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        Page<Rack> rackPage = rackRepository.findAll(pageable);
        return modelMapper.map(rackPage.getContent(), listType);
    }

    @Override
    public RackDto findByIdentifier(String identifier) {
        return modelMapper.map(rackRepository.findByIdentifier(identifier), RackDto.class);
    }

    @Override
    public List<RackDto> findActiveRacks() {
        return rackRepository.findByStatus(true);
    }

    @Override
    public RackDto toggleStatus(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack == null) {
            return null;
        }
        rack.setStatus(!rack.isStatus());
        rackRepository.save(rack);
        return modelMapper.map(rack, RackDto.class);
    }
}
