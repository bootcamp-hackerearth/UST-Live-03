package com.ust.pos.racks.service.impl;

import com.ust.pos.base.service.BaseService;
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
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RacksServiceImpl extends BaseService implements RacksService {

    private final RacksRepository racksRepository;
    private final ModelMapper modelMapper;

    public RacksServiceImpl(RacksRepository racksRepository, ModelMapper modelMapper) {
        this.racksRepository = racksRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RacksDto findByIdentifier(String identifier) {
        return modelMapper.map(racksRepository.findByIdentifier(identifier), RacksDto.class);
    }

    @Override
    public RacksDto save(RacksDto racksDto) {
        String identifier = racksDto.getIdentifier();
        Racks existingRacks = racksRepository.findByIdentifier(identifier);
        if (existingRacks != null) {
            racksDto.setMessage(
                    existingRacks.isDeleted()
                            ? " Racks with identifier - " + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : " Racks with identifier - " + identifier
                            + " already exists."
            );
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
            racksDto.setMessage("Racks with identifier - " + identifier + " not found");
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
        setModifiedDetails(racks);
        softDelete(racks);
    }

    @Override
    public WsDto<RacksDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();

        Page<Racks> racksPage = racksRepository.findByIsDeletedFalse(pageable);

        List<RacksDto> racksDtos = modelMapper.map(
                racksPage.getContent(),
                listType
        );

        WsDto<RacksDto> wsDto =
                new WsDto<>();

        wsDto.setContent(racksDtos);
        wsDto.setPage(racksPage.getNumber());
        wsDto.setSizePerPage(racksPage.getSize());
        wsDto.setTotalPages(racksPage.getTotalPages());
        wsDto.setTotalRecords(racksPage.getTotalElements());

        return wsDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Racks racks = racksRepository.findByIdentifier(identifier);
        if (racks != null) {
            racks.setStatus(!racks.isStatus());
            racksRepository.save(racks);
        }
    }
    
    @Override
    public WsDto<RacksDto> findAll(Specification<Racks> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Page<Racks> racksPage = racksRepository.findAll(example,pageable);
        WsDto<RacksDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(racksPage.getContent(), listType));
        wsDto.setTotalRecords(racksPage.getTotalElements());
        wsDto.setTotalPages(racksPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}