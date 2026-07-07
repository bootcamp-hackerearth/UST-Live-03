package com.ust.pos.rack.impl;

import com.ust.pos.CommonService;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.RackService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RackServiceImpl extends CommonService implements RackService {

    private static final String RACK_WITH_IDENTIFIER = "Rack with identifier - ";

    private final RackRepository rackRepository;
    private final ModelMapper modelMapper;

    public RackServiceImpl(RackRepository rackRepository,
                           ModelMapper modelMapper) {
        this.rackRepository = rackRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RackDto findByIdentifier(String identifier) {
        Rack rack =rackRepository.findByIdentifier(identifier);
        if(rack==null){
            throw new ResourceNotFoundException("Data Cannot Found");
        }
        return modelMapper.map(rack, RackDto.class);
    }

    @Override
    public RackDto save(RackDto rackDto) {

        if (rackDto == null || rackDto.getIdentifier() == null) {
            throw new IllegalArgumentException("Identifier is required");
        }

        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifier(identifier);

        if (existingRack != null) {
            if (BooleanUtils.isNotTrue(existingRack.getDeleted())) {
                rackDto.setMessage("Rack with identifier '" + identifier + "' already exists");
                rackDto.setSuccess(false);
                return rackDto;
            }

            rackDto.setMessage("Rack was previously deleted. Please contact backend team to restore.");
            rackDto.setSuccess(false);
            return rackDto;
        }

        Rack rack = modelMapper.map(rackDto, Rack.class);
        setAuditFields(rack, true);
        Rack savedRack = rackRepository.save(rack);
        RackDto response = modelMapper.map(savedRack, RackDto.class);
        response.setSuccess(true);
        response.setMessage("Rack created successfully");
        return response;
    }

    @Override
    public RackDto update(RackDto dto) {

        String identifier = dto.getIdentifier();
        Rack existing = rackRepository.findByIdentifier(identifier);

        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage(RACK_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        if (BooleanUtils.isTrue(existing.getDeleted())) {
            dto.setSuccess(false);
            dto.setMessage(RACK_WITH_IDENTIFIER + identifier +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        modelMapper.map(dto, existing);
        setAuditFields(existing, false);

        rackRepository.save(existing);

        dto.setSuccess(true);
        dto.setMessage("Rack updated successfully");

        return dto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        Rack rack = rackRepository.findByIdentifier(identifier);

        if (rack != null) {
            softDelete(rack);
            setAuditFields(rack, false);
            rackRepository.save(rack);
        }
    }

    @Override
    public WsDto<RackDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        Page<Rack> page = rackRepository.findByDeletedFalse(pageable);

        WsDto<RackDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public WsDto<RackDto> findAll(Specification<Rack> example, Pageable pageable) {

        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        Page<Rack> page = rackRepository.findAll(example, pageable);

        WsDto<RackDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public RackDto toggleStatus(String identifier) {

        Rack rack = rackRepository.findByIdentifier(identifier);

        if (rack == null) {
            RackDto dto = new RackDto();
            dto.setSuccess(false);
            dto.setMessage(RACK_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        rack.setStatus(!rack.getStatus());
        setAuditFields(rack, false);

        rackRepository.save(rack);

        return modelMapper.map(rack, RackDto.class);
    }

    @Override
    public List<RackDto> findIfTrue() {

        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();

        return modelMapper.map(
                rackRepository.findByStatusIsTrueAndDeletedFalse(),
                listType
        );
    }
}