package com.ust.pos.rack.impl;

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
public class RackServiceImpl implements RackService {

    @Autowired
    private RackRepository rackRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RackDto findByIdentifier(String identifier) {
        return modelMapper.map(
                rackRepository.findByIdentifier(identifier),
                RackDto.class
        );
    }

    @Override
    public RackDto save(RackDto rackDto) {

        Rack existing = rackRepository.findByIdentifier(rackDto.getIdentifier());
        if (existing != null) {
            rackDto.setSuccess(false);
            rackDto.setMessage("Rack already exists : " + rackDto.getIdentifier());
            return rackDto;
        }

        Rack rack = modelMapper.map(rackDto, Rack.class);
        rackRepository.save(rack);
        return rackDto;
    }

    @Override
    public RackDto update(RackDto rackDto) {

        Rack existing = rackRepository.findByIdentifier(rackDto.getIdentifier());
        if (existing == null) {
            rackDto.setSuccess(false);
            rackDto.setMessage("Rack not found : " + rackDto.getIdentifier());
            return rackDto;
        }

        modelMapper.map(rackDto, existing);
        rackRepository.save(existing);
        return rackDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        rackRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<RackDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        Page<Rack> customerPage = rackRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }

    @Override
    public RackDto toggleStatus(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        rack.setStatus(!rack.isStatus());
        rackRepository.save(rack);
        return modelMapper.map(rack, RackDto.class);
    }

    @Override
    public List<RackDto> findIfTrue() {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        return modelMapper.map(rackRepository.findByStatusTrue(), listType);
    }

}