package com.ust.pos.racks.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Racks;
import com.ust.pos.model.RacksRepository;
import com.ust.pos.racks.service.RacksService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RacksServiceImpl extends CommonService implements RacksService {

    private static final String RACKS_WITH_IDENTIFIER = "Racks with identifier - ";

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
    public RacksDto toggleStatus(String identifier) {
        Racks racks = racksRepository.findByIdentifier(identifier);
        racks.setStatus(!racks.isStatus());
        setAuditFields(racks,false);
        racksRepository.save(racks);
        return modelMapper.map(racks, RacksDto.class);
    }

    @Override
    public RacksDto save(RacksDto racksDto) {
        racksDto.setIdentifier(racksDto.getIdentifier().trim());
        String identifier = racksDto.getIdentifier();
        Racks existingRacks = racksRepository.findByIdentifier(identifier);
        if (existingRacks != null) {
            if (!existingRacks.isDeleted()) {
                racksDto.setMessage(RACKS_WITH_IDENTIFIER + identifier + " already exists");
                racksDto.setSuccess(false);
                return racksDto;
            }
            racksDto.setMessage(RACKS_WITH_IDENTIFIER + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            racksDto.setSuccess(false);
            return racksDto;
        }
        Racks racks = modelMapper.map(racksDto, Racks.class);
        setAuditFields(racks,true);
        racksRepository.save(racks);
        racksDto.setSuccess(true);
        racksDto.setMessage("Racks created successfully");
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
        setAuditFields(existingRacks,false);
        racksRepository.save(existingRacks);
        return racksDto;
    }

    @Override
    public boolean delete(String identifier) {
        Racks racks = racksRepository.findByIdentifier(identifier);
        if (racks == null) return false;
        softDelete(racks);
        setAuditFields(racks,false);
        racksRepository.save(racks);
        return true;
    }

    @Override
    public WsDto<RacksDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Page<Racks> racksPage = racksRepository.findByDeletedFalse(pageable);
        WsDto<RacksDto> racksDtoWsDto = new WsDto<>();
        racksDtoWsDto.setDtoList(modelMapper.map(racksPage.getContent(), listType));
        racksDtoWsDto.setTotalRecords(racksPage.getTotalElements());
        racksDtoWsDto.setTotalPages(racksPage.getTotalPages());
        racksDtoWsDto.setSizePerPage(pageable.getPageSize());
        racksDtoWsDto.setPage(pageable.getPageNumber());
        return racksDtoWsDto;
    }

    @Override
    public List<RacksDto> findIfTrue() {
        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        return modelMapper.map(racksRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }
}