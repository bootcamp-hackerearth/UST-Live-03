package com.ust.pos.rack.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.RackService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class RackServiceImpl extends BaseService implements RackService {

    public static final String RACK_NOT_FOUND = "Rack not found";

    private final RackRepository rackRepository;
    private final ModelMapper modelMapper;

    public RackServiceImpl(RackRepository rackRepository,ModelMapper modelMapper) {
        this.rackRepository = rackRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RackDto save(RackDto rackDto) {

        if (rackDto.getName() == null || rackDto.getName().trim().isEmpty()) {
            RackDto dto = new RackDto();
            dto.setSuccess(false);
            dto.setMessage("Rack name is required");
            return dto;
        }

        String name = rackDto.getName().trim();
        Rack existing = rackRepository.findByIdentifier(name);

        if (existing != null) {
            RackDto dto = new RackDto();
            dto.setSuccess(false);
            dto.setMessage("Rack already exists");
            return dto;
        }

        Rack rack = modelMapper.map(rackDto, Rack.class);
        rack.setName(name);
        rack.setIdentifier(name);

        if (rack.getStatus() == null) {
            rack.setStatus(true);
        }

        setCreatedDetails(rack);
        Rack saved = rackRepository.save(rack);
        RackDto response = modelMapper.map(saved, RackDto.class);
        response.setSuccess(true);
        response.setMessage("Rack saved successfully");
        return response;
    }

    @Override
    public RackDto update(RackDto rackDto) {

        String identifier = rackDto.getIdentifier();

        if (identifier == null || identifier.trim().isEmpty()) {
            RackDto dto = new RackDto();
            dto.setSuccess(false);
            dto.setMessage(RACK_NOT_FOUND);
            return dto;
        }

        Rack rack = rackRepository.findByIdentifier(identifier.trim());

        if (rack == null || Boolean.TRUE.equals(rack.getDeleted())) {
            RackDto dto = new RackDto();
            dto.setSuccess(false);
            dto.setMessage(RACK_NOT_FOUND);
            return dto;
        }

        if (rackDto.getName() != null && !rackDto.getName().trim().isEmpty()) {
            String newName = rackDto.getName().trim();
            rack.setName(newName);
            rack.setIdentifier(newName);
        }

        if (rackDto.getStatus() != null) {
            rack.setStatus(rackDto.getStatus());
        }

        if (rackDto.getShelfIdentifiers() != null) {
            rack.setShelfIdentifiers(rackDto.getShelfIdentifiers());
        }

        setModifiedDetails(rack);
        Rack saved = rackRepository.save(rack);
        RackDto response = modelMapper.map(saved, RackDto.class);
        response.setSuccess(true);
        response.setMessage("Rack updated successfully");
        return response;
    }

    @Override
    public RackDto findByIdentifier(String identifier) {

        Rack rack = rackRepository.findByIdentifier(identifier);

        if (rack == null || Boolean.TRUE.equals(rack.getDeleted())) {
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
    public WsDto<RackDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<RackDto>>() {}.getType();
        Page<Rack> rackPage = rackRepository.findByDeletedFalse(pageable);
        WsDto<RackDto> ws = new WsDto<>();
        ws.setDtoList(modelMapper.map(rackPage.getContent(), listType));
        ws.setTotalRecords(rackPage.getTotalElements());
        ws.setTotalPages(rackPage.getTotalPages());
        ws.setSizePerPage(pageable.getPageSize());
        ws.setPage(pageable.getPageNumber());
        return ws;
    }

    @Override
    public List<RackDto> getActiveRacks() {

        return rackRepository.findByStatusTrueAndDeletedFalse()
                .stream()
                .map(r -> modelMapper.map(r, RackDto.class))
                .toList();
    }

    @Override
    public void delete(String identifier) {

        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack == null) return;
        rack.setDeleted(true);
        setModifiedDetails(rack);
        rackRepository.save(rack);
    }

    @Override
    public RackDto toggleStatus(String identifier) {

        Rack rack = rackRepository.findByIdentifier(identifier);

        if (rack == null || Boolean.TRUE.equals(rack.getDeleted())) {
            RackDto dto = new RackDto();
            dto.setSuccess(false);
            dto.setMessage(RACK_NOT_FOUND);
            return dto;
        }

        rack.setStatus(!Boolean.TRUE.equals(rack.getStatus()));
        setModifiedDetails(rack);
        Rack saved = rackRepository.save(rack);
        RackDto dto = modelMapper.map(saved, RackDto.class);
        dto.setSuccess(true);
        dto.setMessage("Status updated successfully");
        return dto;
    }
}