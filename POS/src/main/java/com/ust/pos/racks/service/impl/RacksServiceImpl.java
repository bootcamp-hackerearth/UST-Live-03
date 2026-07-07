package com.ust.pos.racks.service.impl;

import com.ust.pos.base.service.BaseService;
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
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RacksServiceImpl extends BaseService implements RacksService {

    public static final String RACKS_WITH_IDENTIFIER = "Racks with identifier - ";
    private final RacksRepository racksRepository;

    private final ModelMapper modelMapper;

    public RacksServiceImpl(RacksRepository racksRepository, ModelMapper modelMapper) {
        this.racksRepository = racksRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RacksDto findByIdentifier(String identifier) {
        Racks racks = racksRepository.findByIdentifierAndIsDeletedFalse(identifier);
        if (racks == null) {
            throw new ResourceNotFoundException("Racks with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(racks, RacksDto.class);
    }

    @Override
    public RacksDto save(RacksDto racksDto) {
        String identifier = racksDto.getIdentifier();
        Racks existingRacks = racksRepository.findByIdentifier(identifier);
        if (existingRacks != null) {
            racksDto.setMessage(RACKS_WITH_IDENTIFIER + identifier + " already exists");
            if (existingRacks.isDeleted()) {
                racksDto.setMessage(RACKS_WITH_IDENTIFIER + identifier + " was deleted , Please Contact the Administrator to add.");
            }
            racksDto.setSuccess(false);
            return racksDto;
        }
        Racks racks = modelMapper.map(racksDto, Racks.class);
        setCreatedDetails(racks);
        racksRepository.save(racks);
        return racksDto;
    }

    @Override
    public RacksDto update(RacksDto racksDto) {
        String identifier = racksDto.getIdentifier();
        Racks existingRacks = racksRepository.findByIdentifier(identifier);
        if (existingRacks == null) {
            racksDto.setMessage(RACKS_WITH_IDENTIFIER + identifier + " not found");
            racksDto.setSuccess(false);
            return racksDto;
        }
        modelMapper.map(racksDto, existingRacks);
        setModifiedDetails(existingRacks);
        racksRepository.save(existingRacks);
        return racksDto;
    }

    @Transactional
    public void delete(String identifier) {
        Racks racks = racksRepository.findByIdentifier(identifier);
        softDelete(racks);
        setModifiedDetails(racks);
    }

    @Override
    public WsDto<RacksDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Page<Racks> racksPage = racksRepository.findByIsDeletedFalse(pageable);
        WsDto<RacksDto> racksWsDto = new WsDto<>();
        racksWsDto.setDtoList(modelMapper.map(racksPage.getContent(), listType));
        racksWsDto.setTotalRecords(racksPage.getTotalElements());
        racksWsDto.setTotalPages(racksPage.getTotalPages());
        racksWsDto.setSizePerPage(pageable.getPageSize());
        racksWsDto.setPage(pageable.getPageNumber());

        return racksWsDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Racks racks = racksRepository.findByIdentifier(identifier);
        if (racks != null) {
            racks.setStatus(!racks.isStatus());
            racksRepository.save(racks);
            setModifiedDetails(racks);
        }
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
