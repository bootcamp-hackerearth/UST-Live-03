package com.ust.pos.rack.service.impl;

import com.ust.pos.dto.RackDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.RackService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RackServiceImpl implements RackService {

    public static final String RACK_NOT_FOUND = "Rack not found";

    @Autowired
    private RackRepository rackRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RackDto save(RackDto rackDto) {
        if (rackDto.getIdentifier() == null || rackDto.getIdentifier().trim().isEmpty()) {
            RackDto dto = new RackDto();
            dto.setSuccess(false);
            dto.setMessage("Identifier required");
            return dto;
        }
        Rack existing = rackRepository.findByIdentifier(rackDto.getIdentifier());
        if (existing != null) {
            RackDto dto = new RackDto();
            dto.setSuccess(false);
            dto.setMessage("Rack already exists");
            return dto;
        }
        if (rackDto.getShelfIdentifiers() == null || rackDto.getShelfIdentifiers().isEmpty()) {
            RackDto dto = new RackDto();
            dto.setSuccess(false);
            dto.setMessage("Please select at least one shelf");
            return dto;
        }
        Rack rack = modelMapper.map(rackDto, Rack.class);
        Rack saved = rackRepository.save(rack);
        RackDto response = modelMapper.map(saved, RackDto.class);
        response.setSuccess(true);
        response.setMessage("Rack saved successfully");
        return response;
    }

    @Override
    public RackDto update(RackDto rackDto) {
        if (rackDto.getIdentifier() == null) {
            RackDto dto = new RackDto();
            dto.setSuccess(false);
            dto.setMessage("Identifier required");
            return dto;
        }
        if (rackDto.getShelfIdentifiers() == null || rackDto.getShelfIdentifiers().isEmpty()) {
            RackDto dto = new RackDto();
            dto.setSuccess(false);
            dto.setMessage("Please select at least one shelf");
            return dto;
        }
        Rack rack = rackRepository.findByIdentifier(rackDto.getIdentifier());
        if (rack == null) {
            RackDto dto = new RackDto();
            dto.setSuccess(false);
            dto.setMessage(RACK_NOT_FOUND);
            return dto;
        }
        if (rackDto.getName() != null) {
            rack.setName(rackDto.getName());
        }
        if (rackDto.getStatus() != null) {
            rack.setStatus(rackDto.getStatus());
        }
        if (rackDto.getShelfIdentifiers() != null) {
            rack.setShelfIdentifiers(rackDto.getShelfIdentifiers());
        }
        Rack saved = rackRepository.save(rack);
        RackDto response = modelMapper.map(saved, RackDto.class);
        response.setSuccess(true);
        response.setMessage("Rack updated successfully");
        return response;
    }

    @Override
    public RackDto findByIdentifier(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack == null) {
            RackDto dto = new RackDto();
            dto.setSuccess(false);
            dto.setMessage(RACK_NOT_FOUND);
            return dto;
        }
        RackDto dto = modelMapper.map(rack, RackDto.class);
        dto.setSuccess(true);
        return dto;
    }

    @Override
    public List<RackDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        Page<Rack> rackPage = rackRepository.findAll(pageable);
        return modelMapper.map(rackPage.getContent(), listType);
    }

    @Override
    public List<RackDto> getActiveRacks() {
        List<RackDto> active = new ArrayList<>();
        for (RackDto rack : findAll(null)) {
            if (Boolean.TRUE.equals(rack.getStatus())) {
                active.add(rack);
            }
        }
        return active;
    }

    @Override
    public void delete(String identifier) {
        rackRepository.deleteByIdentifier(identifier);
    }

    @Override
    public RackDto toggleStatus(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack == null) {
            RackDto rackDto = new RackDto();
            rackDto.setSuccess(false);
            rackDto.setMessage(RACK_NOT_FOUND);
            return rackDto;
        }
        rack.setStatus(!Boolean.TRUE.equals(rack.getStatus()));
        Rack saved = rackRepository.save(rack);
        RackDto rackDto = modelMapper.map(saved, RackDto.class);
        rackDto.setSuccess(true);
        rackDto.setMessage("Status updated successfully");
        return rackDto;
    }
}