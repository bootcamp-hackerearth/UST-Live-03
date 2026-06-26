package com.ust.pos.rack.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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

    public RackServiceImpl(RackRepository rackRepository,
                           ModelMapper modelMapper) {
        this.rackRepository = rackRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RackDto save(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifier(identifier);
        if (existingRack != null) {
            if (Boolean.TRUE.equals(existingRack.getDeleted())) {
                rackDto.setMessage("Rack - " + identifier + " was deleted and cannot be recreated");
            } else {
                rackDto.setMessage("Rack with identifier - " + identifier + " already exists");
            }
            rackDto.setSuccess(false);
            return rackDto;
        }
        Rack rack = modelMapper.map(rackDto, Rack.class);
        setCreatedDetails(rack);
        rackRepository.save(rack);
        rackDto.setSuccess(true);
        return rackDto;
    }

    @Override
    public RackDto update(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingRack == null) {
            rackDto.setMessage("Rack with identifier - " + identifier + " not found");
            rackDto.setSuccess(false);
            return rackDto;
        }
        modelMapper.map(rackDto, existingRack);
        setModifiedDetails(existingRack);
        rackRepository.save(existingRack);
        rackDto.setSuccess(true);
        return rackDto;
    }

    @Override
    public void delete(String identifier) {
        Rack rack = rackRepository.findByIdentifierAndDeletedFalse(identifier);
        if (rack != null) {
            softDelete(rack);
            setModifiedDetails(rack);
            rackRepository.save(rack);
        }
    }

    @Override
    public WsDto<RackDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        Page<Rack> rackPage = rackRepository.findByDeletedFalse(pageable);
        WsDto<RackDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(rackPage.getContent(), listType));
        wsDto.setTotalRecords(rackPage.getTotalElements());
        wsDto.setTotalPages(rackPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public RackDto findByIdentifier(String identifier) {
        Rack rack = rackRepository.findByIdentifierAndDeletedFalse(identifier);
        if (rack == null) {
            throw new ResourceNotFoundException("rack with identifier " + identifier + " not found");
        }
        return modelMapper.map(rack, RackDto.class);
    }

    @Override
    public void updateStatus(String identifier, boolean status) {
        Rack rack = rackRepository.findByIdentifierAndDeletedFalse(identifier);
        if (rack != null) {
            rack.setStatus(status);
            setModifiedDetails(rack);
            rackRepository.save(rack);
        }
    }

    @Override
    public List<RackDto> findAllActive() {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        return modelMapper.map(
                rackRepository.findByStatusAndDeletedFalse(true),
                listType
        );
    }
}