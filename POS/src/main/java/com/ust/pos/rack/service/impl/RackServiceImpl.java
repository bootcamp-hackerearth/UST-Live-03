package com.ust.pos.rack.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.RackService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RackServiceImpl extends BaseService implements RackService {

    private final RackRepository rackRepository;
    private final ModelMapper modelMapper;

    public RackServiceImpl(RackRepository rackRepository, ModelMapper modelMapper) {
        this.rackRepository = rackRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PaginationResponseDto<RackDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();

        Page<Rack> rackPage = rackRepository.findByIsDeletedFalse(pageable);
        List<RackDto> rackDtoList = modelMapper.map(rackPage.getContent(), listType);

        PaginationResponseDto<RackDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(rackDtoList);
        paginationResponseDto.setPage(rackPage.getNumber());
        paginationResponseDto.setSizePerPage(rackPage.getSize());
        paginationResponseDto.setTotalPages(rackPage.getTotalPages());
        paginationResponseDto.setTotalRecords(rackPage.getTotalElements());

        return paginationResponseDto;
    }

    @Override
    public PaginationResponseDto<RackDto> findAll(Specification<Rack> example, Pageable pageable) {

        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        Page<Rack> page = rackRepository.findAll(example, pageable);

        PaginationResponseDto<RackDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationResponseDto.setTotalRecords(page.getTotalElements());
        paginationResponseDto.setTotalPages(page.getTotalPages());
        paginationResponseDto.setSizePerPage(pageable.getPageSize());
        paginationResponseDto.setPage(pageable.getPageNumber());

        return paginationResponseDto;
    }

    @Override
    public RackDto findByIdentifier(String identifier) {
        return modelMapper.map(rackRepository.findByIdentifier(identifier), RackDto.class);
    }

    public List<RackDto> findActiveRacks() {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        return modelMapper.map(
                rackRepository.findByStatusTrue(),
                listType
        );
    }

    @Override
    public RackDto save(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack == null) {
            rack = modelMapper.map(rackDto, Rack.class);
            setCreatedDetails(rack);
            rackRepository.save(rack);
            rackDto.setMessage("Successfully added the rack");
            rackDto.setSuccess(true);
        } else if (isSoftDeleted(rack)) {
            rackDto.setMessage(
                    getDeletedMessage("Rack", identifier)
            );
            rackDto.setSuccess(false);
        } else {
            rackDto.setMessage("Rack " + identifier + " already exists");
            rackDto.setSuccess(false);
        }
        return rackDto;
    }

    @Override
    public RackDto update(RackDto rackDto) {

        String identifier = rackDto.getIdentifier();

        Rack existingRack =
                rackRepository.findByIdentifier(identifier);

        if (existingRack == null) {
            rackDto.setMessage("Rack not found");
            rackDto.setSuccess(false);
            return rackDto;
        }

        if (isSoftDeleted(existingRack)) {
            rackDto.setSuccess(false);
            rackDto.setMessage(
                    getDeletedMessage("Rack", identifier)
            );
            return rackDto;
        }

        modelMapper.map(rackDto, existingRack);

        setModifiedDetails(existingRack);

        rackRepository.save(existingRack);

        rackDto.setMessage("Rack updated successfully");
        rackDto.setSuccess(true);

        return rackDto;
    }

    @Override
    @Transactional
    public RackDto updateStatus(String identifier, boolean status) {
        RackDto response = new RackDto();

        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack == null) {
            response.setSuccess(false);
            response.setMessage("Rack not found");
            return response;
        }

        setModifiedDetails(rack);
        rack.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        softDelete(rack);
        setModifiedDetails(rack);
        rackRepository.save(rack);
    }
}
