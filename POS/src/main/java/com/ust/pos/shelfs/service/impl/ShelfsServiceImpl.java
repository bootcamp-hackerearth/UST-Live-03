package com.ust.pos.shelfs.service.impl;
import com.ust.pos.common.CommonService;
import com.ust.pos.dto.PageDto;
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

import java.lang.reflect.Type;
import java.util.List;
@Service
public class ShelfsServiceImpl extends CommonService implements ShelfsService {

    public static final String SHELFS_WITH_IDENTIFIER = "Shelfs with identifier - ";
    private final ShelfsRepository shelfsRepository;

    private final ModelMapper modelMapper;

    public ShelfsServiceImpl(ShelfsRepository shelfsRepository, ModelMapper modelMapper) {
        this.shelfsRepository = shelfsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ShelfsDto save(ShelfsDto shelfsDto) {
        String identifier =shelfsDto.getIdentifier();
        Shelfs existingShelfs =shelfsRepository.findByIdentifier(identifier);

        if (existingShelfs!= null) {
            if (Boolean.TRUE.equals(existingShelfs.getDeleted())) {
                shelfsDto.setMessage(SHELFS_WITH_IDENTIFIER + identifier + " has been soft deleted. Restore it by changing status.");
                shelfsDto.setSuccess(false);
                return shelfsDto;
            }
            shelfsDto.setMessage(SHELFS_WITH_IDENTIFIER + identifier + " already exists");
            shelfsDto.setSuccess(false);
            return shelfsDto ;
        }
        Shelfs shelfs= modelMapper.map(shelfsDto, Shelfs.class);
        shelfs.setDeleted(false);
        shelfs.setStatus(true);
        setAuditFields(shelfs,true);
        shelfsRepository.save(shelfs);
        return shelfsDto ;
    }

    @Override
    public ShelfsDto update(ShelfsDto shelfsDto) {
        String identifier = shelfsDto.getIdentifier();
        Shelfs existingShelfs = shelfsRepository.findByIdentifier(identifier);
        if (existingShelfs== null) {
            shelfsDto.setMessage(SHELFS_WITH_IDENTIFIER + identifier + " not found");
            shelfsDto.setSuccess(false);
            return shelfsDto;
        }
        modelMapper.map(shelfsDto,existingShelfs);
        setAuditFields(existingShelfs,false);
        shelfsRepository.save(existingShelfs);
        return shelfsDto;
    }

    @Override
    public boolean delete(String identifier) {

        Shelfs shelfs = shelfsRepository.findByIdentifier(identifier);

        if (shelfs == null) {
            return false;
        }
        softDelete(shelfs);
        setAuditFields(shelfs, false);
        shelfsRepository.save(shelfs);
        return true;
    }

    @Override
    public PageDto<ShelfsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        Page<Shelfs> shelfsPage = shelfsRepository.findByDeletedFalse(pageable);
        PageDto<ShelfsDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(shelfsPage.getContent(), listType));
        pageDto.setTotalRecords(shelfsPage.getTotalElements());
        pageDto.setTotalPages(shelfsPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public PageDto<ShelfsDto> findAll(Specification<Shelfs> spec, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        Page<Shelfs> shelfsPage = shelfsRepository.findAll(spec, pageable);
        PageDto<ShelfsDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(shelfsPage.getContent(), listType));
        pageDto.setTotalRecords(shelfsPage.getTotalElements());
        pageDto.setTotalPages(shelfsPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        pageDto.setKeyword(keyword);
        return pageDto;
    }

    @Override
    public ShelfsDto findByIdentifier(String identifier) {
        Shelfs shelfs =shelfsRepository.findByIdentifier(identifier);
        if (shelfs == null) {
            throw new ResourceNotFoundException("Shelfs with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(shelfs, ShelfsDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Shelfs shelfs = shelfsRepository.findByIdentifier(identifier);
        if (shelfs != null) {
            boolean currentStatus = Boolean.TRUE.equals(shelfs.getStatus());
            shelfs.setStatus(!currentStatus);

            shelfsRepository.save(shelfs);
        }
    }

    @Override
    public List<ShelfsDto> findActiveShelves() {
        Type listType = new TypeToken<List<ShelfsDto>>() {}.getType();
        return modelMapper.map(shelfsRepository.findByStatusTrue(),listType);
    }
}
