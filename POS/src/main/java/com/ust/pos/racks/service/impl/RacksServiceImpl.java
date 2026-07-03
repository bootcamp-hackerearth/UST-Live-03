package com.ust.pos.racks.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Racks;
import com.ust.pos.model.RacksRepository;
import com.ust.pos.racks.service.RacksService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RacksServiceImpl extends CommonService implements RacksService {

    private final RacksRepository racksRepository;

    private final ModelMapper modelMapper;

    public RacksServiceImpl(RacksRepository racksRepository, ModelMapper modelMapper) {
        this.racksRepository = racksRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RacksDto save(RacksDto racksDto) {

        Racks existing = racksRepository.findByIdentifier(racksDto.getIdentifier());
        if (existing != null) {
            if (existing.isDeleted()) {
                racksDto.setMessage("Racks with identifier - " + existing + "has been soft deleted.(Rollback by changing status");
                racksDto.setSuccess(false);
                return racksDto;
            }
            racksDto.setMessage("Racks Already Exist!");
            racksDto.setSuccess(false);
            return racksDto;
        }
        Racks racks = modelMapper.map(racksDto, Racks.class);
        setAuditFields(racks, true);
        racksRepository.save(racks);
        racksDto.setSuccess(true);
        return racksDto;
    }

    @Override
    public RacksDto findById(Long id) {
        Racks racks = racksRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Racks not found with id " + id));
        return modelMapper.map(racks, RacksDto.class);
    }

    @Override
    public WsDto<RacksDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Page<Racks> racksPage = racksRepository.findByDeletedFalse(pageable);
        WsDto<RacksDto> racksWsDto = new WsDto<>();
        racksWsDto.setDtoList(modelMapper.map(racksPage.getContent(), listType));
        racksWsDto.setTotalRecords(racksPage.getTotalElements());
        racksWsDto.setTotalPages(racksPage.getTotalPages());
        racksWsDto.setSizePerPage(pageable.getPageSize());
        racksWsDto.setPage(pageable.getPageNumber());

        return racksWsDto;
    }

    @Override
    public WsDto<RacksDto> findAll(Specification<Racks> spec, Pageable pageable) {
        Type listType = new TypeToken<List<RacksDto>>() {}.getType();
        Page<Racks> page = racksRepository.findAll(spec, pageable);
        WsDto<RacksDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public void delete(String identifier) {
        Racks racks = racksRepository.findByIdentifier(identifier);
        softDelete(racks);
        setAuditFields(racks, false);
        racksRepository.save(racks);
    }

    @Override
    public RacksDto findByIdentifier(String identifier) {
        Racks racks = racksRepository.findByIdentifier(identifier);
        return modelMapper.map(racks, RacksDto.class);
    }

    @Override
    public RacksDto update(RacksDto racksDto) {

        Racks racks = racksRepository.findByIdentifier(racksDto.getIdentifier());
        if (racks == null) {
            racksDto.setSuccess(false);
            return racksDto;
        }
        modelMapper.map(racksDto, racks);
        setAuditFields(racks, false);
        racksRepository.save(racks);
        racksDto.setSuccess(true);
        return racksDto;
    }

    @Override
    public RacksDto changeRacksStatus(String identifier, boolean status) {

        Racks racks = racksRepository.findByIdentifier(identifier);
        if (racks == null) {
            return null;
        }
        racks.setStatus(status);
        racksRepository.save(racks);
        return modelMapper.map(racks, RacksDto.class);
    }
}