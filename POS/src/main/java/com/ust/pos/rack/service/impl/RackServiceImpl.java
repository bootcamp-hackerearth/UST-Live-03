package com.ust.pos.rack.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.RackDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.RackService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RackServiceImpl extends CommonService implements RackService {
    private final RackRepository rackRepository;
    private final ModelMapper modelMapper;

    public RackServiceImpl(RackRepository rackRepository, ModelMapper modelMapper) {
        this.rackRepository = rackRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RackDto createRack(RackDto rackDto) {

        if (rackRepository.existsByIdentifier(rackDto.getIdentifier())) {
            rackDto.setSuccess(false);
            rackDto.setMessage("Rack already exists");
            return rackDto;
        }

        Rack rack = modelMapper.map(rackDto, Rack.class);
        setAuditFields(rack, true);
        rackRepository.save(rack);
        return rackDto;
    }

    @Override
    public RackDto updateRack(RackDto rackDto) {
        Rack rack = modelMapper.map(rackDto, Rack.class);
        setAuditFields(rack, false);
        rackRepository.save(rack);
        return rackDto;
    }

    @Override
    public RackDto getRack(Long id) {

        RackDto dto = new RackDto();

        rackRepository.findById(id).ifPresentOrElse(rack -> {
            modelMapper.map(rack, dto);
            dto.setSuccess(true);
        }, () -> {
            dto.setSuccess(false);
            dto.setMessage("Rack not found");
        });

        return dto;
    }

    @Override
    public List<RackDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        Page<Rack> rackPage = rackRepository.findByDeletedFalse(pageable);
        return modelMapper.map(rackPage.getContent(), listType);
    }

    @Override
    public boolean deleteRack(Long id) {
        Rack rack = rackRepository.findById(id).orElse(null);
        if (rack == null) {
            return false;
        }
        softDelete(rack);
        setAuditFields(rack, false);
        rackRepository.save(rack);
        return true;
    }
}