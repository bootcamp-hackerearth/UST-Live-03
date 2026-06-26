package com.ust.pos.rack.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.RackDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.RackService;
import jakarta.persistence.EntityNotFoundException;
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

    public static final String RACK_WITH_IDENTIFIER = "Rack with identifier - ";
    private final RackRepository rackRepository;
    private final ModelMapper modelMapper;

    public RackServiceImpl(
            RackRepository rackRepository,
            ModelMapper modelMapper
    ) {
        this.rackRepository = rackRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RackDto save(RackDto rackDto) {
        Rack existingRack = rackRepository.findByIdentifier(rackDto.getIdentifier());
        if (existingRack != null) {
            if (existingRack.isDeleted()) {
                rackDto.setMessage(RACK_WITH_IDENTIFIER + rackDto.getIdentifier() + " has been soft deleted.");
                rackDto.setSuccess(false);
                return rackDto;
            }
            rackDto.setMessage(RACK_WITH_IDENTIFIER + rackDto.getIdentifier() + " already exists");
            rackDto.setSuccess(false);
            return rackDto;
        }
        Rack rack = modelMapper.map(rackDto, Rack.class);
        setCreatedDetails(rack);
        rackRepository.save(rack);
        return rackDto;
    }

    @Override
    public PaginationResponseDto<RackDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RackDto>>() {}.getType();
        PaginationResponseDto<RackDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<Rack> racks = rackRepository.findAll();
            response.setDtoList(modelMapper.map(racks, listType));
            response.setTotalRecords(racks.size());
            response.setTotalPages(1);
            response.setSizePerPage(racks.size());
            response.setPage(0);
        } else {
            Page<Rack> rackPage = rackRepository.findByDeletedFalse(pageable);
            response.setDtoList(modelMapper.map(rackPage.getContent(), listType));
            response.setTotalRecords(rackPage.getTotalElements());
            response.setTotalPages(rackPage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
    }

    @Override
    public RackDto findByIdentifier(String identifier) {
        return modelMapper.map(rackRepository.findByIdentifier(identifier), RackDto.class);
    }

    @Override
    public void delete(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack == null) {
            throw new EntityNotFoundException("Rack not found");
        }
        softDelete(rack);
        setModifiedDetails(rack);
        rackRepository.save(rack);
    }

    @Override
    public RackDto update(RackDto rackDto) {
        Rack existingRack = rackRepository.findByIdentifier(rackDto.getIdentifier());
        if (existingRack == null) {
            rackDto.setMessage(RACK_WITH_IDENTIFIER + rackDto.getIdentifier() + "not found");
            rackDto.setSuccess(false);
            return rackDto;
        }
        modelMapper.map(rackDto, existingRack);
        setModifiedDetails(existingRack);
        rackRepository.save(existingRack);
        return rackDto;
    }

    @Override
    @Transactional
    public RackDto toggleStatus(String identifier, boolean status) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack != null) {
            rack.setStatus(!rack.isStatus());
            setModifiedDetails(rack);
            rackRepository.save(rack);
        }
        return modelMapper.map(rack , RackDto.class);
    }

    public List<RackDto> findActiveRacks() {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        return modelMapper.map(rackRepository.findByStatusTrue(), listType);
    }
}