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

    private final RackRepository rackRepository;
    private final ModelMapper modelMapper;

    public RackServiceImpl(RackRepository rackRepository, ModelMapper modelMapper) {
        this.rackRepository = rackRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RackDto save(RackDto rackDto) {
        String identifier = rackDto.getIdentifier().trim();
        Rack existingRack = rackRepository.findByIdentifier(identifier);
        if (existingRack != null) {
            if (existingRack.isDeleted()) {rackDto.setMessage("Rack with identifier " + identifier + " has been soft deleted. (Rollback by changing status)");
                rackDto.setSuccess(false);
                return rackDto;
            }
            rackDto.setMessage("Rack with identifier - " + identifier + " already exists");
            rackDto.setSuccess(false);
            return rackDto;
        }
        Rack rack = modelMapper.map(rackDto, Rack.class);
        setCreatedDetails(rack);
        rackRepository.save(rack);
        rackDto.setSuccess(true);
        rackDto.setMessage("Rack created successfully");
        return rackDto;
    }

    @Override
    public WsDto<RackDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        WsDto<RackDto> wsDto = new WsDto<>();
        if (pageable == null) {
            List<RackDto> rackDtoList = modelMapper.map(rackRepository.findByDeletedFalse(pageable), listType);
            wsDto.setDtoList(rackDtoList);
            wsDto.setTotalRecords(rackDtoList.size());
            return wsDto;
        }
        Page<Rack> rackPage = rackRepository.findByDeletedFalse(pageable);
        wsDto.setDtoList(modelMapper.map(rackPage.getContent(), listType));
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setTotalPages(rackPage.getTotalPages());
        wsDto.setTotalRecords(rackPage.getTotalElements());
        return wsDto;
    }

    @Override
    public RackDto findByIdentifier(String identifier) {
        return modelMapper.map(rackRepository.findByIdentifier(identifier), RackDto.class);
    }

    @Override
    public void delete(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        softDelete(rack);
        setModifiedDetails(rack);
        rackRepository.save(rack);
    }

    @Override
    public RackDto update(RackDto rackDto) {
        Rack existingRack = rackRepository.findByIdentifier(rackDto.getIdentifier());
        if (existingRack == null) {
            rackDto.setMessage("Rack with identifier - " + rackDto.getIdentifier() + " not found");
            rackDto.setSuccess(false);
            return rackDto;
        }
        modelMapper.map(rackDto, existingRack);
        setModifiedDetails(existingRack);
        rackRepository.save(existingRack);
        rackDto.setSuccess(true);
        rackDto.setMessage("Rack updated successfully");
        return rackDto;
    }

    @Override
    @Transactional
    public RackDto toggleStatus(String identifier, boolean status) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack == null) {
            RackDto response = new RackDto();
            response.setSuccess(false);
            response.setMessage("Rack not found");
            return response;
        }
        rack.setStatus(status);
        setModifiedDetails(rack);
        rackRepository.save(rack);
        RackDto response = modelMapper.map(rack, RackDto.class);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

    @Override
    public List<RackDto> findActiveRacks() {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        return modelMapper.map(rackRepository.findByStatusTrue(), listType);
    }

}