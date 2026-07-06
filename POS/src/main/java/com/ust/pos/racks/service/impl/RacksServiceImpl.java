package com.ust.pos.racks.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
    public RacksDto findByIdentifier(String identifier) {
        Racks racks = racksRepository.findByIdentifier(identifier);
        if (racks == null) {
            throw new ResourceNotFoundException("Racks with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(racks, RacksDto.class);
    }

    @Override
    public RacksDto toggleStatus(String identifier) {
        Racks racks = racksRepository.findByIdentifier(identifier);
        racks.setStatus(!racks.isStatus());
        setAuditFields(racks, false);
        racksRepository.save(racks);
        return modelMapper.map(racks, RacksDto.class);
    }

    @Override
    public RacksDto save(RacksDto racksDto) {
        racksDto.setIdentifier(racksDto.getIdentifier().trim());
        String identifier = racksDto.getIdentifier();
        Racks existingRacks = racksRepository.findByIdentifier(identifier);
        if (existingRacks != null) {
            if (existingRacks.isDeleted()) {
                racksDto.setMessage("Racks with identifier " + identifier + " was previously deleted. " +
                        "Please contact backend team to restore."
                );
                racksDto.setSuccess(false);
                return racksDto;
            }
            racksDto.setMessage("Racks with identifier - " + identifier + " already exists");
            racksDto.setSuccess(false);
            return racksDto;
        }
        Racks racks = modelMapper.map(racksDto, Racks.class);
        setAuditFields(racks, true);
        racksRepository.save(racks);
        return racksDto;
    }

    @Override
    public RacksDto update(RacksDto racksDto) {
        String identifier = racksDto.getIdentifier();
        Racks existingRacks = racksRepository.findByIdentifier(identifier);
        if (existingRacks == null) {
            racksDto.setMessage("Racks with identifier - " + identifier + " not found");
            racksDto.setSuccess(false);
            return racksDto;
        }
        modelMapper.map(racksDto, existingRacks);
        setAuditFields(existingRacks, false);
        racksRepository.save(existingRacks);
        return racksDto;
    }

    @Override
    public boolean delete(String identifier) {
        Racks racks = racksRepository.findByIdentifier(identifier);
        if (racks == null) return false;
        softDelete(racks);
        setAuditFields(racks, false);
        racksRepository.save(racks);
        return true;
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
    public List<RacksDto> findIfTrue() {
        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        return modelMapper.map(racksRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }

    @Override
    public WsDto<RacksDto> findAll(Specification<Racks> example, Pageable pageable) {

        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Page<Racks> page = racksRepository.findAll(example, pageable);

        WsDto<RacksDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}