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
import java.util.List;

@Service
@Transactional
public class RackServiceImpl implements RackService {

    @Autowired
    RackRepository rackRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public RackDto save(RackDto rackDto) {
        Rack existingRack = rackRepository.findByIdentifier(rackDto.getIdentifier());
        if (existingRack != null) {
            rackDto.setMessage("Rack with identifier - " + rackDto.getIdentifier() + " already exists");
            rackDto.setSuccess(false);
            return rackDto;
        }
        Rack rack = modelMapper.map(rackDto, Rack.class);
        rackRepository.save(rack);
        return rackDto;
    }

    @Override
    public List<RackDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        if (pageable == null) {
            return modelMapper.map(rackRepository.findAll(), listType);
        }
        Page<Rack> rackPage = rackRepository.findAll(pageable);
        return modelMapper.map(rackPage.getContent(), listType);
    }

    @Override
    public RackDto findByIdentifier(String identifier) {
        return modelMapper.map(rackRepository.findByIdentifier(identifier), RackDto.class);
    }

    @Override
    public void delete(String identifier) {
        rackRepository.deleteByIdentifier(identifier);
    }

    @Override
    public RackDto update(RackDto rackDto) {
        Rack existingRack = rackRepository.findByIdentifier(rackDto.getIdentifier());
        if (existingRack == null) {
            rackDto.setMessage("Rack with identifier - " + rackDto.getIdentifier() + "not found");
            rackDto.setSuccess(false);
            return rackDto;
        }
        modelMapper.map(rackDto, existingRack);
        rackRepository.save(existingRack);
        return rackDto;
    }

    @Override
    @Transactional
    public RackDto toggleStatus(String identifier, boolean status) {
        RackDto response = new RackDto();
        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack == null) {
            response.setSuccess(false);
            response.setMessage("Rack not found");
            return response;
        }
        rack.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

    public List<RackDto> findActiveRacks() {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        return modelMapper.map(
                rackRepository.findByStatusTrue(),
                listType
        );
    }

}