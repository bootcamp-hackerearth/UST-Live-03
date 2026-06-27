package com.ust.pos.rack.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Rack;
import com.ust.pos.modell.RackRepository;
import com.ust.pos.rack.service.RackService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RackServiceImpl extends BaseService implements RackService {

    public static final String RACK_WITH_IDENTIFIER = "Rack with identifier - ";

    private final RackRepository rackRepository;
    private final ModelMapper modelMapper;

    @Override
    public RackDto findByIdentifier(String identifier) {
        return modelMapper.map(rackRepository.findByIdentifierAndDeletedFalse(identifier), RackDto.class
        );
    }

    @Override
    public RackDto save(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifier(identifier);

        if (existingRack != null) {
            if (Boolean.TRUE.equals(existingRack.getDeleted())) {
                rackDto.setMessage(RACK_WITH_IDENTIFIER + identifier + " was deleted and cannot be created again.");
                rackDto.setSuccess(false);
                return rackDto;
            }
            rackDto.setMessage(RACK_WITH_IDENTIFIER + identifier + " already exists");
            rackDto.setSuccess(false);
            return rackDto;
        }

        Rack rack = modelMapper.map(rackDto, Rack.class);
        rackRepository.save(rack);
        return rackDto;
    }

    @Override
    public RackDto update(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingRack == null) {
            rackDto.setMessage(RACK_WITH_IDENTIFIER + identifier + " not found");
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
    public void delete(String identifier) {
        Rack rack = rackRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(rack);
        setModifiedDetails(rack);
        rackRepository.save(rack);
    }

    @Override
    public WsDto<RackDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        Page<Rack> rackPage = rackRepository.findAllByDeletedFalse(pageable);
        WsDto<RackDto> rackWsDto = new WsDto<>();
        rackWsDto.setDtoList(modelMapper.map(rackPage.getContent(), listType));
        rackWsDto.setTotalRecords(rackPage.getTotalElements());
        rackWsDto.setTotalPage(rackPage.getTotalPages());
        rackWsDto.setSizePerPage(pageable.getPageSize());
        rackWsDto.setPage(pageable.getPageNumber());
        return rackWsDto;
    }

    @Override
    public List<RackDto> findAllActive() {
        return rackRepository.findByStatusTrueAndDeletedFalse()
                .stream()
                .map(rack -> modelMapper.map(rack, RackDto.class))
                .toList();
    }

    @Override
    @Transactional
    public RackDto toggleStatus(String identifier) {
        Rack rack = rackRepository.findByIdentifierAndDeletedFalse(identifier);

        if (rack == null) {
            throw new IllegalArgumentException("Rack not found with identifier: " + identifier);
        }

        Boolean currentStatus = rack.getStatus();
        rack.setStatus(currentStatus == null ? Boolean.TRUE : !currentStatus);
        setModifiedDetails(rack);
        Rack saved = rackRepository.save(rack);
        return modelMapper.map(saved, RackDto.class);
    }

}
