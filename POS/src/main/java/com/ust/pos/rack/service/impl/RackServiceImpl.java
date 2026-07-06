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
@Transactional

@RequiredArgsConstructor
public class RackServiceImpl extends BaseService implements RackService {

    public static final String RACK_WITH_IDENTIFIER = "Rack with identifier - ";
    private final RackRepository rackRepository;
    private final ModelMapper modelMapper;

    @Override
    public RackDto findByIdentifier(String identifier) {
        return modelMapper.map(rackRepository.findByIdentifierAndDeletedFalse(identifier), RackDto.class);
    }

    @Override
    public RackDto save(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifier(identifier);
        if (existingRack != null) {
            if (Boolean.TRUE.equals(existingRack.getDeleted())) {
                rackDto.setMessage(RACK_WITH_IDENTIFIER + identifier + " was deleted and cannot be created again.");
                rackDto.setSuccess(false);
            }
            rackDto.setMessage(RACK_WITH_IDENTIFIER + identifier + " already exists");
            rackDto.setSuccess(false);
            return rackDto;
        }
        Rack rack = modelMapper.map(rackDto, Rack.class);
        setCreatedDetails(rack);
        rackRepository.save(rack);
        return rackDto;
    }

    @Override
    public RackDto update(RackDto rackDto) {
        String identifier = rackDto.getIdentifier();
        Rack existingRack = rackRepository.findByIdentifier(identifier);
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
        WsDto<RackDto> rackDto = new WsDto<>();
        rackDto.setDtoList(modelMapper.map(rackPage.getContent(), listType));
        rackDto.setTotalRecords(rackPage.getTotalElements());
        rackDto.setTotalPage(rackPage.getTotalPages());
        rackDto.setSizePerPage(pageable.getPageSize());
        rackDto.setPage(pageable.getPageNumber());
        return rackDto;
    }

    @Override
    public WsDto<RackDto> findAll(Specification<Rack> example, Pageable pageable) {

        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        Page<Rack> page = rackRepository.findAll(example, pageable);
        WsDto<RackDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPage(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public RackDto toggleStatus(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier);
        rack.setStatus(!rack.isStatus());
        setModifiedDetails(rack);
        rackRepository.save(rack);
        return modelMapper.map(rack, RackDto.class);
    }

    public List<RackDto> findActiveRacks() {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        return modelMapper.map(
                rackRepository.findByStatusTrueAndDeletedFalse(),
                listType
        );
    }

}
