package com.ust.pos.rack.service.impl;

import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
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
    RackRepository rackRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public WsDto<RackDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();

        Page<Rack> rackPage = rackRepository.findAll(pageable);

        WsDto<RackDto> dto = new WsDto<>();

        dto.setContent(modelMapper.map(rackPage.getContent(), listType));
        dto.setTotalRecords(rackPage.getTotalElements());
        dto.setTotalPages(rackPage.getTotalPages());
        dto.setSizePerPage(pageable.getPageSize());
        dto.setPage(pageable.getPageNumber());

        return dto;
    }

    @Override
    public RackDto findByIdentifier(String identifier) {
        return modelMapper.map(rackRepository.findByIdentifier(identifier), RackDto.class);
    }

    @Override
    public RackDto save(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifier(identifier);
        if (existingRack != null) {
            rackDto.setMessage("Rack - " + identifier + " already exists");
            rackDto.setSuccess(false);
            return rackDto;
        }
        Rack rack = modelMapper.map(rackDto, Rack.class);
        rackRepository.save(rack);
        return rackDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        rackRepository.deleteByIdentifier(identifier);
    }

    @Override
    public RackDto update(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifier(identifier);
        if (existingRack == null) {
            rackDto.setMessage("Rack with rack - " + identifier + " not found");
            rackDto.setSuccess(false);
            return rackDto;
        }
        modelMapper.map(rackDto, existingRack);
        rackRepository.save(existingRack);
        return rackDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack != null) {
            rack.setStatus(!rack.isStatus());
            rackRepository.save(rack);
        }
    }

    @Override
    public List<RackDto> findActiveStatus() {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        return modelMapper.map(rackRepository.findByStatusTrue(), listType);
    }

    @Override
    public List<RackDto> findActiveRack() {
        return rackRepository.findByStatusTrue()
                .stream()
                .map(rack -> modelMapper.map(rack, RackDto.class))
                .toList();
    }
}
