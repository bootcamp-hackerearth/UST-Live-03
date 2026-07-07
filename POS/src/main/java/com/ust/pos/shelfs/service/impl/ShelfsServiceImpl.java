package com.ust.pos.shelfs.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.WsDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Shelfs;
import com.ust.pos.model.ShelfsRepository;
import com.ust.pos.shelfs.service.ShelfsService;
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
public class ShelfsServiceImpl extends BaseService implements ShelfsService {
    private final ShelfsRepository shelfsRepository;
    private final ModelMapper modelMapper;

    public ShelfsServiceImpl(ShelfsRepository shelfsRepository, ModelMapper modelMapper) {
        this.shelfsRepository = shelfsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfsDto save(ShelfsDto shelfsDto) {

        String identifier = shelfsDto.getIdentifier();
        Shelfs existingShelfs = shelfsRepository.findByIdentifier(identifier);
        if (existingShelfs != null) {
            shelfsDto.setMessage("Shelfs with identifier - " + identifier + " already exists");
            if(existingShelfs.isDeleted()){
                shelfsDto.setMessage("Racks with identifier - " + identifier + " was deleted , Please Contact the Administrator to add.");
            }
            shelfsDto.setSuccess(false);
            return shelfsDto;
        }
        Shelfs shelfs = modelMapper.map(shelfsDto, Shelfs.class);
        setCreatedDetails(shelfs);
        shelfsRepository.save(shelfs);
        return shelfsDto;
    }

    @Override
    public ShelfsDto update(ShelfsDto shelfsDto) {
        String identifier = shelfsDto.getIdentifier();
        Shelfs existingShelfs = shelfsRepository.findByIdentifier(identifier);
        if (existingShelfs == null) {
            shelfsDto.setMessage("Shelfs with identifier - " + identifier + " not found");
            shelfsDto.setSuccess(false);
            return shelfsDto;
        }
        modelMapper.map(shelfsDto, existingShelfs);
        setModifiedDetails(existingShelfs);
        shelfsRepository.save(existingShelfs);
        return shelfsDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Shelfs shelfs = shelfsRepository.findByIdentifier(identifier);
        softDelete(shelfs);
        setModifiedDetails(shelfs);
    }

    @Override
    public WsDto<ShelfsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        Page<Shelfs> shelfsPage = shelfsRepository.findByIsDeletedFalse(pageable);

        WsDto<ShelfsDto> shelfsWsDto = new WsDto<>();
        shelfsWsDto.setDtoList(modelMapper.map(shelfsPage.getContent(), listType));
        shelfsWsDto.setTotalRecords(shelfsPage.getTotalElements());
        shelfsWsDto.setTotalPages(shelfsPage.getTotalPages());
        shelfsWsDto.setSizePerPage(pageable.getPageSize());
        shelfsWsDto.setPage(pageable.getPageNumber());

        return shelfsWsDto;
    }

    @Override
    public ShelfsDto findByIdentifier(String identifier) {
        Shelfs shelfs = shelfsRepository.findByIdentifierAndIsDeletedFalse(identifier);
        if (shelfs == null) {
            throw new ResourceNotFoundException("Shelfs with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(shelfs, ShelfsDto.class);
    }

    @Override
    public List<ShelfsDto> findAllActive() {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        return modelMapper.map(shelfsRepository.findByStatusTrueAndIsDeletedFalse(), listType);
    }

    @Override
    public void toggleStatus(String identifier) {
        Shelfs shelfs = shelfsRepository.findByIdentifier(identifier);
        if (shelfs != null) {
            shelfs.setStatus(!shelfs.isStatus());
            setModifiedDetails(shelfs);
            shelfsRepository.save(shelfs);
        }
    }

    @Override
    public WsDto<ShelfsDto> findAll(Specification<Shelfs> example, Pageable pageable) {

        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        Page<Shelfs> page = shelfsRepository.findAll(example, pageable);

        WsDto<ShelfsDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}
