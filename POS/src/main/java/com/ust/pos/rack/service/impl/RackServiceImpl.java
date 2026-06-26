package com.ust.pos.rack.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
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
    public static final String RACK_WITH_IDENTIFIER = "Rack with identifier - ";
    private final RackRepository rackRepository;
    private final ModelMapper modelMapper;

    public RackServiceImpl(RackRepository rackRepository, ModelMapper modelMapper) {
        this.rackRepository = rackRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RackDto save(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifier(identifier);
        if (existingRack != null) {
            if (!existingRack.isDeleted()) {
                rackDto.setMessage(RACK_WITH_IDENTIFIER + identifier + " already exists");
                rackDto.setSuccess(false);
                return rackDto;
            }
            rackDto.setMessage(RACK_WITH_IDENTIFIER + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            rackDto.setSuccess(false);
            return rackDto;
        }
        Rack rack = modelMapper.map(rackDto, Rack.class);
        setAuditFields(rack, true);
        rackRepository.save(rack);
        rackDto.setSuccess(true);
        rackDto.setMessage("Rack created successfully");
        return rackDto;
    }

    @Override
    public WsDto<RackDto> findAll(Pageable pageable) {
        Page<Rack> rackPage = rackRepository.findByDeletedFalse(pageable);
        Type type = new TypeToken<List<RackDto>>() {
        }.getType();
        WsDto<RackDto> rackWsDto = new WsDto<>();
        rackWsDto.setDtoList(modelMapper.map(rackPage.getContent(), type));
        rackWsDto.setTotalRecords(rackPage.getTotalElements());
        rackWsDto.setTotalPages(rackPage.getTotalPages());
        rackWsDto.setSizePerPage(pageable.getPageSize());
        rackWsDto.setPage(pageable.getPageNumber());
        return rackWsDto;
    }

    @Override
    public List<RackDto> findAllActive() {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        return modelMapper.map(rackRepository.findAllByStatusAndDeletedFalse(true), listType);
    }

    @Override
    public RackDto findByIdentifier(String identifier) {
        return modelMapper.map(rackRepository.findByIdentifier(identifier), RackDto.class);
    }

    @Override
    public RackDto update(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifier(rackDto.getIdentifier());
        if (existingRack == null) {
            rackDto.setMessage(RACK_WITH_IDENTIFIER + identifier + " not found");
            rackDto.setSuccess(false);
            return rackDto;
        }
        modelMapper.map(rackDto, existingRack);
        setAuditFields(existingRack, false);
        rackRepository.save(existingRack);
        return rackDto;
    }

    @Override
    public RackDto toggleStatus(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        rack.setStatus(!rack.isStatus());
        setAuditFields(rack, false);
        rackRepository.save(rack);
        return modelMapper.map(rack, RackDto.class);
    }

    @Override
    public boolean delete(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack == null) return false;
        softDelete(rack);
        setAuditFields(rack, false);
        rackRepository.save(rack);
        return true;
    }
}
