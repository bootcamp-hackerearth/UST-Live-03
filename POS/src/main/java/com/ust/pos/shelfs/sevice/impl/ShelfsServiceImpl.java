package com.ust.pos.shelfs.sevice.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Shelfs;
import com.ust.pos.model.ShelfsRepository;
import com.ust.pos.shelfs.sevice.ShelfsService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ShelfsServiceImpl extends CommonService implements ShelfsService {

    private final ShelfsRepository shelfsRepository;

    private final ModelMapper modelMapper;

    public ShelfsServiceImpl(ShelfsRepository shelfsRepository, ModelMapper modelMapper) {
        this.shelfsRepository = shelfsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WsDto<ShelfsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        Page<Shelfs> userPage = shelfsRepository.findByDeletedFalse(pageable);

        WsDto<ShelfsDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPages(userPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());

        return userWsDto;
    }

    @Override
    public List<ShelfsDto> findActiveStatus() {
        List<Shelfs> allShelves = shelfsRepository.findAll();
        List<Shelfs> activeShelves = allShelves.stream().filter(Shelfs::isStatus).toList();

        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        return modelMapper.map(activeShelves, listType);
    }

    @Override
    public ShelfsDto changeToggleStatus(String identifier, boolean status) {
        Shelfs shelfs = shelfsRepository.findByIdentifier(identifier);
        if (shelfs != null) {
            shelfs.setStatus(status);
            shelfsRepository.save(shelfs);
        }
        return modelMapper.map(shelfs, ShelfsDto.class);
    }

    @Override
    public ShelfsDto save(ShelfsDto shelfsDto) {
        String identifier = shelfsDto.getIdentifier();
        Shelfs existingShelfs = shelfsRepository.findByIdentifier(identifier);
        if (existingShelfs != null) {
            if(existingShelfs.isDeleted()) {
                shelfsDto.setMessage("Shelfs with identifier - " + identifier + "has been soft deleted.(Rollback by changing status");
                shelfsDto.setSuccess(false);
                return shelfsDto;
            }
            shelfsDto.setMessage("Shelfs with identifier - " + identifier + " already exists");
            shelfsDto.setSuccess(false);
            return shelfsDto;
        }
        Shelfs shelfs = modelMapper.map(shelfsDto, Shelfs.class);
        setAuditFields(shelfs, true);
        shelfsRepository.save(shelfs);
        return shelfsDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Shelfs shelfs = shelfsRepository.findByIdentifier(identifier);
        softDelete(shelfs);
        setAuditFields(shelfs,false);
        shelfsRepository.save(shelfs) ;
    }

    @Override
    public ShelfsDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfsRepository.findByIdentifier(identifier), ShelfsDto.class);
    }

    @Override
    public ShelfsDto update(ShelfsDto shelfsDto) {
        String identifier = shelfsDto.getIdentifier();
        Shelfs existingShelfs = shelfsRepository.findByIdentifier(identifier);
        modelMapper.map(shelfsDto, existingShelfs);
        setAuditFields(existingShelfs,false);
        shelfsRepository.save(existingShelfs);
        return shelfsDto;
    }
}
