package com.ust.pos.racks.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Racks;
import com.ust.pos.modell.RacksRepository;
import com.ust.pos.racks.service.RacksService;
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
public class RacksServiceImpl extends BaseService implements RacksService {

    private final RacksRepository racksRepository;
    private final ModelMapper modelMapper;

    @Override
    public RacksDto findByIdentifier(String identifier) {
        return modelMapper.map(racksRepository.findByIdentifierAndDeletedFalse(identifier), RacksDto.class);
    }

    @Override
    public RacksDto save(RacksDto racksDto) {
        String identifier = racksDto.getIdentifier();
        Racks existingRack = racksRepository.findByIdentifier(identifier);

        if (existingRack != null) {
            if (Boolean.TRUE.equals(existingRack.getDeleted())) {
                racksDto.setMessage("Rack with Identifier " + identifier + " already exists (Soft-Deleted)");
                racksDto.setSuccess(false);
                return racksDto;
            }
            racksDto.setMessage("Shelf with identifier - " + identifier + " already exists");
            racksDto.setSuccess(false);
            return racksDto;
        }

        Racks racks = modelMapper.map(racksDto, Racks.class);
        if (racks.getStatus() == null) {
            racks.setStatus(true);
        }
        setCreatedDetails(racks);
        racksRepository.save(racks);
        return racksDto;
    }

    @Override
    public RacksDto update(RacksDto racksDto) {
        String identifier = racksDto.getIdentifier();
        Racks existingRack = racksRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingRack == null) {
            racksDto.setMessage("Shelf with identifier - " + identifier + " not found");
            racksDto.setSuccess(false);
            return racksDto;
        }
        String originalCreatedBy = existingRack.getCreatedBy();
        java.time.LocalDateTime originalCreatedOn = existingRack.getCreatedOn();
        modelMapper.map(racksDto, existingRack);

        existingRack.setCreatedBy(originalCreatedBy);
        existingRack.setCreatedOn(originalCreatedOn);

        setModifiedDetails(existingRack);
        racksRepository.save(existingRack);
        return racksDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Racks rack = racksRepository.findByIdentifierAndDeletedFalse(identifier);
        if (rack != null) {
            softDelete(rack);
            setModifiedDetails(rack);
            racksRepository.save(rack);
        }
    }

    @Override
    public WsDto<RacksDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Page<Racks> rackPage = racksRepository.findAllByDeletedFalse(pageable);

        WsDto<RacksDto> rackWsDto = new WsDto<>();
        rackWsDto.setDtoList(modelMapper.map(rackPage.getContent(), listType));
        rackWsDto.setTotalRecords(rackPage.getTotalElements());
        rackWsDto.setTotalPage(rackPage.getTotalPages());
        rackWsDto.setSizePerPage(pageable.getPageSize());
        rackWsDto.setPage(pageable.getPageNumber());

        return rackWsDto;
    }

    @Override
    public List<RacksDto> findAllActive() {
        return racksRepository.findByStatusTrueAndDeletedFalse()
                .stream()
                .map(rack -> modelMapper.map(rack, RacksDto.class))
                .toList();
    }

    @Override
    @Transactional
    public RacksDto toggleStatus(String identifier) {
        Racks racks = racksRepository.findByIdentifierAndDeletedFalse(identifier);
        if (racks == null) {
            throw new IllegalArgumentException("racks not found with identifier: " + identifier);
        }
        Boolean currentStatus = racks.getStatus();
        racks.setStatus(currentStatus == null ? Boolean.TRUE : !currentStatus);

        setModifiedDetails(racks);
        Racks saved = racksRepository.save(racks);
        return modelMapper.map(saved, RacksDto.class);
    }
}