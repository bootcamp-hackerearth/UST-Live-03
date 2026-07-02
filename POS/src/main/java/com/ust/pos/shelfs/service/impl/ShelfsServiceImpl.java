package com.ust.pos.shelfs.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.Shelfs;
import com.ust.pos.model.ShelfsRepository;
import com.ust.pos.shelfs.service.ShelfsService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.lang.reflect.Type;
import java.util.List;

@Service
public class ShelfsServiceImpl extends CommonService implements ShelfsService {

    private static final String SHELFS_WITH_IDENTIFIER = "Shelfs with identifier - ";

    private final ShelfsRepository shelfsRepository;

    private final ModelMapper modelMapper;

    public ShelfsServiceImpl(ShelfsRepository shelfsRepository, ModelMapper modelMapper) {
        this.shelfsRepository = shelfsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfsDto findByIdentifier(String identifier) {
        return modelMapper.map(shelfsRepository.findByIdentifier(identifier), ShelfsDto.class);
    }

    @Override
    public ShelfsDto toggleStatus(String identifier) {
        Shelfs shelfs = shelfsRepository.findByIdentifier(identifier);
        shelfs.setStatus(!shelfs.isStatus());
        setAuditFields(shelfs,false);
        shelfsRepository.save(shelfs);
        return modelMapper.map(shelfs, ShelfsDto.class);
    }

    @Override
    public ShelfsDto save(ShelfsDto shelfsDto) {
        shelfsDto.setIdentifier(shelfsDto.getIdentifier().trim());
        String identifier = shelfsDto.getIdentifier();
        Shelfs existingShelfs = shelfsRepository.findByIdentifier(identifier);
        if (existingShelfs != null) {
            if (!existingShelfs.isDeleted()) {
                shelfsDto.setMessage(SHELFS_WITH_IDENTIFIER + identifier + " already exists");
                shelfsDto.setSuccess(false);
                return shelfsDto;
            }
            shelfsDto.setMessage(SHELFS_WITH_IDENTIFIER + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            shelfsDto.setSuccess(false);
            return shelfsDto;
        }
        Shelfs shelfs = modelMapper.map(shelfsDto, Shelfs.class);
        setAuditFields(shelfs, true);
        shelfsRepository.save(shelfs);
        shelfsDto.setSuccess(true);
        shelfsDto.setMessage("Shelfs created successfully");
        return shelfsDto;
    }

    @Override
    public ShelfsDto update(ShelfsDto shelfsDto) {
        String identifier = shelfsDto.getIdentifier();
        Shelfs existingShelfs = shelfsRepository.findByIdentifier(identifier);
        if (existingShelfs == null) {
            shelfsDto.setMessage(SHELFS_WITH_IDENTIFIER + identifier + " not found");
            shelfsDto.setSuccess(false);
            return shelfsDto;
        }
        modelMapper.map(shelfsDto, existingShelfs);
        setAuditFields(existingShelfs,false);
        shelfsRepository.save(existingShelfs);
        return shelfsDto;
    }

    @Override
    public boolean delete(String identifier) {
        Shelfs shelfs = shelfsRepository.findByIdentifier(identifier);
        if (shelfs == null) return false;
        softDelete(shelfs);
        setAuditFields(shelfs,false);
        shelfsRepository.save(shelfs);
        return true;
    }

    @Override
    public WsDto<ShelfsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        Page<Shelfs> shelfsPage = shelfsRepository.findByDeletedFalse(pageable);
        WsDto<ShelfsDto> shelfsDtoWsDto = new WsDto<>();
        shelfsDtoWsDto.setDtoList(modelMapper.map(shelfsPage.getContent(), listType));
        shelfsDtoWsDto.setTotalRecords(shelfsPage.getTotalElements());
        shelfsDtoWsDto.setTotalPages(shelfsPage.getTotalPages());
        shelfsDtoWsDto.setSizePerPage(pageable.getPageSize());
        shelfsDtoWsDto.setPage(pageable.getPageNumber());
        return shelfsDtoWsDto;
    }

    @Override
    public List<ShelfsDto> findIfTrue() {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        return modelMapper.map(shelfsRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }

    @Override
    public WsDto<ShelfsDto> findAll(Specification<Shelfs> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        Page<Shelfs>  shelfsPage= shelfsRepository.findAll(example, pageable);
        WsDto<ShelfsDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(shelfsPage.getContent(), listType));
        wsDto.setTotalRecords(shelfsPage.getTotalElements());
        wsDto.setTotalPages(shelfsPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}