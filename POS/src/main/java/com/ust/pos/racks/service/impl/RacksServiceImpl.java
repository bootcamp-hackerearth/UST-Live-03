package com.ust.pos.racks.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Racks;
import com.ust.pos.model.RacksRepository;
import com.ust.pos.racks.service.RacksService;
import jakarta.transaction.Transactional;
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

    private final ModelMapper modelMapper;

    private final RacksRepository racksRepository;

    public RacksServiceImpl(ModelMapper modelMapper, RacksRepository racksRepository) {
        this.modelMapper = modelMapper;
        this.racksRepository = racksRepository;
    }

    @Override
    public WsDto<RacksDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Page<Racks> userPage = racksRepository.findByDeletedFalse(pageable);

        WsDto<RacksDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPages(userPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());

        return userWsDto;
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

    @Override
    public RacksDto save(RacksDto racksDto) {
        String identifier = racksDto.getIdentifier();
        Racks existingRacks = racksRepository.findByIdentifier(identifier);
        if (existingRacks != null) {
            if(existingRacks.isDeleted()) {
                racksDto.setMessage("Racks with identifier - " + identifier + "has been soft deleted.(Rollback by changing status");
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
    @Transactional
    public void delete(String identifier) {
        Racks rack = racksRepository.findByIdentifier(identifier);
        softDelete(rack);
        setAuditFields(rack,false);
        racksRepository.save(rack);    }

    @Override
    public RacksDto findByIdentifier(String identifier) {
        return modelMapper.map(racksRepository.findByIdentifier(identifier), RacksDto.class);
    }

    @Override
    public RacksDto update(RacksDto racksDto) {
        String identifier = racksDto.getIdentifier();
        Racks existingRacks = racksRepository.findByIdentifier(identifier);
        modelMapper.map(racksDto, existingRacks);
        setAuditFields(existingRacks,false);
        racksRepository.save(existingRacks);
        return racksDto;
    }

    @Override
    public RacksDto changeToggleStatus(String identifier, boolean status) {
        Racks rack = racksRepository.findByIdentifier(identifier);
        if (rack != null) {
            rack.setStatus(status);
            racksRepository.save(rack);
        }
        return modelMapper.map(rack, RacksDto.class);
    }

    @Override
    public List<RacksDto> findActiveStatus() {
        List<Racks> allRacks = racksRepository.findAll();
        List<Racks> activeRacks = allRacks.stream().filter(Racks::isStatus).toList();

        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        return modelMapper.map(activeRacks, listType);
    }
}
