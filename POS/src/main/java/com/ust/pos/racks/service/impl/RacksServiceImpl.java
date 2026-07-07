package com.ust.pos.racks.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.RacksDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
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

    private static final String VALIDATION_MESSAGE = "Rack with identifier - ";
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
            throw new ResourseNotFoundException("Data cannot found");
        }

        return modelMapper.map(racks, RacksDto.class);
    }

    @Override
    public RacksDto save(RacksDto racksDto) {

        String identifier = racksDto.getIdentifier();
        Racks existingRacks = racksRepository.findByIdentifier(identifier);

        if (existingRacks != null) {
            racksDto.setMessage(
                    existingRacks.isDeleted()
                            ? VALIDATION_MESSAGE + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : VALIDATION_MESSAGE + identifier
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
            racksDto.setMessage(VALIDATION_MESSAGE + identifier + "+not found");
            racksDto.setSuccess(false);
            return racksDto;
        }

        modelMapper.map(racksDto, existingRacks);
        setModifiedDetails(existingRacks);
        racksRepository.save(existingRacks);

        return racksDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        Racks rack = racksRepository.findByIdentifier(identifier);
        setModifiedDetails(rack);
        softDelete(rack);
    }

    @Override
    public WsDto<RacksDto> findAll(Pageable pageable) {

        Page<Racks> racksPage = racksRepository.findByIsDeletedFalse(pageable);

        WsDto<RacksDto> racksDto = new WsDto<>();

        List<RacksDto> racksDtos = racksPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, RacksDto.class))
                .toList();

        racksDto.setContent(racksDtos);
        racksDto.setPage(racksPage.getNumber());
        racksDto.setSizePerPage(racksPage.getSize());
        racksDto.setTotalPages(racksPage.getTotalPages());
        racksDto.setTotalRecords(racksPage.getTotalElements());

        return racksDto;
    }

    @Override
    public WsDto<RacksDto> findAll(Specification<Racks> example, Pageable pageable) {

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Racks> page = racksRepository.findAll(example, pageable);

        WsDto<RacksDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

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
    public List<Racks> findActiveRacks() {

        return racksRepository.findByStatus(true);
    }

}



