package com.ust.pos.racks.service.impl;

import com.ust.pos.api.BaseService;
import com.ust.pos.dto.RacksDto;
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
@Transactional
public class RacksServiceImpl extends BaseService implements RacksService {
    private final RacksRepository racksRepository;
    private final ModelMapper modelMapper;

    public RacksServiceImpl(RacksRepository racksRepository, ModelMapper modelMapper) {
        this.racksRepository = racksRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RacksDto save(RacksDto racksDto) {
        String identifier = racksDto.getIdentifier();
        Racks existingRacks = racksRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingRacks != null) {
            racksDto.setMessage("Racks with identifier - " + identifier + " already exists");
            racksDto.setSuccess(false);
            return racksDto;
        }
        Racks racks = modelMapper.map(racksDto, Racks.class);
        racksRepository.save(racks);
        return racksDto;
    }

    @Override
    public RacksDto update(RacksDto racksDto) {
        Racks existingRacks =
                racksRepository.findByIdentifierAndDeletedFalse(racksDto.getIdentifier());
        if (existingRacks == null) {
            racksDto.setSuccess(false);
            racksDto.setMessage("Racks not found");
            return racksDto;
        }
        modelMapper.map(racksDto, existingRacks);
        racksRepository.save(existingRacks);
        return racksDto;
    }

    @Override
    public void delete(String identifier) {
        Racks racks = racksRepository.findByIdentifierAndDeletedFalse(identifier);
        if (racks != null) {
            racks.setDeleted(true);
            racksRepository.save(racks);
        }
    }

    @Override
    public List<RacksDto> findAll() {
        Type listOfType = new TypeToken<List<RacksDto>>() {
        }.getType();
        return modelMapper.map(racksRepository.findAll(), listOfType);
    }

    @Override
    public RacksDto findByIdentifier(String identifier) {
        return modelMapper.map(racksRepository.findByIdentifierAndDeletedFalse(identifier), RacksDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Racks racks = racksRepository.findByIdentifierAndDeletedFalse(identifier);
        if (racks != null) {
            racks.setStatus(!racks.getStatus());
            racksRepository.save(racks);
        }
    }

    @Override
    public Page<RacksDto> findAll(String search, Pageable pageable) {
        Page<Racks> racks;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Racks> specification =
                    buildGlobalSearchSpec(Racks.class, search);
            racks = racksRepository.findAll(specification, pageable);
        } else {
            racks = racksRepository.findByDeletedFalse(pageable);
        }

        return racks.map(rack ->
                modelMapper.map(rack, RacksDto.class));
    }
}
