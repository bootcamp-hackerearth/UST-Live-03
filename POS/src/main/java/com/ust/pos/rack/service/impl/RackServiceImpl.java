package com.ust.pos.rack.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.RackService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RackServiceImpl extends BaseService implements RackService {

    private final RackRepository rackRepository;
    private final ModelMapper modelMapper;

    @Override
    public RackDto save(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifier(identifier);
        if (existingRack != null) {
            if (Boolean.TRUE.equals(existingRack.getDeleted())) {
                rackDto.setMessage("Rack identifier - " + identifier + " not available");
                rackDto.setSuccess(false);
                return rackDto;
            }
            rackDto.setMessage("Rack with identifier - " + identifier + " already exists");
            rackDto.setSuccess(false);
            return rackDto;
        }
        Rack rack = modelMapper.map(rackDto, Rack.class);
        setCreatedDetails(rack);
        setModifiedDetails(rack);
        rackRepository.save(rack);
        return rackDto;
    }

    @Override
    public RackDto update(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifier(identifier);
        if (existingRack == null) {
            rackDto.setMessage("rack with identifier - " + identifier + " not found");
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
        setModifiedDetails(rack);
        softDelete(rack);
    }

    @Override
    public WsDto<RackDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        Page<Rack> rackPage = rackRepository.findAllByDeletedFalse(pageable);

        WsDto<RackDto> rackWsDto = new WsDto<>();
        rackWsDto.setDtoList(modelMapper.map(rackPage.getContent(), listType));
        rackWsDto.setTotalRecords(rackPage.getTotalElements());
        rackWsDto.setTotalPages(rackPage.getTotalPages());
        rackWsDto.setSizePerPage(pageable.getPageSize());
        rackWsDto.setPage(pageable.getPageNumber());
        return rackWsDto;
    }

    @Override
    public RackDto findByIdentifier(String identifier) {
        return modelMapper.map(rackRepository.findByIdentifierAndDeletedFalse(identifier), RackDto.class);
    }

    @Override
    public List<RackDto> findActiveRacks() {
        List<Rack> rack = rackRepository.findByStatusTrueAndDeletedFalse();
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        return modelMapper.map(rack, listType);
    }

    @Override
    public RackDto toggleStatus(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        if (rack == null) {
            return null;
        }
        rack.setStatus(!rack.isStatus());
        setModifiedDetails(rack);
        rackRepository.save(rack);
        return modelMapper.map(rack, RackDto.class);
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
}
