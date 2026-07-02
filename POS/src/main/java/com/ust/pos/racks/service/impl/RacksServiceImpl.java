package com.ust.pos.racks.service.impl;
import com.ust.pos.common.CommonService;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Racks;
import com.ust.pos.model.RacksRepository;
import com.ust.pos.model.Role;
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
public class RacksServiceImpl extends CommonService implements RacksService{
    public static final String RACKS_WITH_IDENTIFIER = "Racks with identifier - ";
    private final RacksRepository racksRepository;

    private final ModelMapper modelMapper;

    public RacksServiceImpl(RacksRepository racksRepository, ModelMapper modelMapper) {
        this.racksRepository = racksRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RacksDto save(RacksDto racksDto) {
        String identifier = racksDto.getIdentifier();
        Racks existingRacks = racksRepository.findByIdentifier(identifier);
        if (existingRacks != null) {
            if (Boolean.TRUE.equals(existingRacks.getDeleted())) {
                racksDto.setMessage(RACKS_WITH_IDENTIFIER + identifier + " has been soft deleted. Restore it by changing status.");
                racksDto.setSuccess(false);
                return racksDto;
            }
            racksDto.setMessage(RACKS_WITH_IDENTIFIER + identifier + " already exists");
            racksDto.setSuccess(false);
            return racksDto;
        }
        Racks racks = modelMapper.map(racksDto, Racks.class);
        racks.setDeleted(false);
        racks.setStatus(true);
        setAuditFields(racks, true);
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
        setAuditFields(existingRacks,false);
        racksRepository.save(existingRacks);
        return racksDto;
    }

    @Override
    public boolean delete(String identifier) {

        Racks racks = racksRepository.findByIdentifier(identifier);

        if (racks == null) {
            return false;
        }
        softDelete(racks);
        setAuditFields(racks, false);
        racksRepository.save(racks);
        return true;
    }

    @Override
    public PageDto<RacksDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Page<Racks> racksPage = racksRepository.findByDeletedFalse(pageable);
        PageDto<RacksDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(racksPage.getContent(), listType));
        pageDto.setTotalRecords(racksPage.getTotalElements());
        pageDto.setTotalPages(racksPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public PageDto<RacksDto> findAll(Specification<Racks> spec, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<RacksDto>>() {
        }.getType();
        Page<Racks> racksPage = racksRepository.findAll(spec, pageable);
        PageDto<RacksDto> PageDto = new PageDto<>();
        PageDto.setDtoList(modelMapper.map(racksPage.getContent(), listType));
        PageDto.setTotalRecords(racksPage.getTotalElements());
        PageDto.setTotalPages(racksPage.getTotalPages());
        PageDto.setSizePerPage(pageable.getPageSize());
        PageDto.setPage(pageable.getPageNumber());
        PageDto.setKeyword(keyword);
        return PageDto;
    }

    @Override
    public RacksDto findByIdentifier(String identifier) {
        return modelMapper.map(racksRepository.findByIdentifier(identifier), RacksDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Racks racks = racksRepository.findByIdentifier(identifier);
        if (racks != null) {
            boolean currentStatus = Boolean.TRUE.equals(racks.getStatus());
            racks.setStatus(!currentStatus);

            racksRepository.save(racks);
        }
    }

    @Override
    public List<RacksDto> findActiveRacks() {
        Type listType = new TypeToken<List<RacksDto>>() {}.getType();
        return modelMapper.map(racksRepository.findByStatusTrue(),listType);
    }
}
