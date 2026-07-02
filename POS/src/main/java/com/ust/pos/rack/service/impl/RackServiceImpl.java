package com.ust.pos.rack.service.impl;
import com.ust.pos.CommonService;
import com.ust.pos.dto.RackDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Rack;
import com.ust.pos.model.RackRepository;
import com.ust.pos.rack.service.RackService;
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
public class RackServiceImpl extends CommonService implements RackService {

    private static final String ENTITY_NAME = "Rack";
    private static final String IDENTIFIER_TEXT = " with identifier - ";
    private static final String NOT_FOUND = " is not found";
    private static final String ALREADY_EXISTS = " already exists";

    private final RackRepository rackRepository;
    private final ModelMapper modelMapper;

    public RackServiceImpl(RackRepository rackRepository, ModelMapper modelMapper) {
        this.rackRepository = rackRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RackDto save(RackDto rackDto) {
        String identifier = rackDto.getIdentifier().trim();
        Rack existingRack = rackRepository.findByIdentifier(identifier);
        if (existingRack != null) {
            if(existingRack.isDeleted()){
                rackDto.setMessage("Rack identifier - " + identifier + " not available");
                rackDto.setSuccess(false);
                return rackDto;
            }
            rackDto.setMessage(ENTITY_NAME + IDENTIFIER_TEXT + identifier + ALREADY_EXISTS);
            rackDto.setSuccess(false);
            return rackDto;
        }
        Rack rack = modelMapper.map(rackDto, Rack.class);
        setAuditFields(rack, true);
        rackRepository.save(rack);
        rackDto.setSuccess(true);
        return rackDto;
    }

    @Override
    public RackDto update(RackDto rackDto) {
        String identifier = rackDto.getIdentifier().trim();
        Rack existingRack = rackRepository.findByIdentifier(identifier);
        if (existingRack == null) {
            rackDto.setMessage(ENTITY_NAME + IDENTIFIER_TEXT + identifier + NOT_FOUND);
            rackDto.setSuccess(false);
            return rackDto;
        }
        modelMapper.map(rackDto, existingRack);
        setAuditFields(existingRack, false);
        rackRepository.save(existingRack);
        rackDto.setSuccess(true);
        return rackDto;
    }

    @Override
    public void delete(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier.trim());
        if (rack != null) {
            softDelete(rack);
            setAuditFields(rack, false);
            rackRepository.save(rack);
        }
    }

    @Override
    public WsDto<RackDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<RackDto>>() {}.getType();
        Page<Rack> rackPage = rackRepository.findByIsDeletedFalse(pageable);
        WsDto<RackDto> response = new WsDto<>();
        response.setDtoList(modelMapper.map(rackPage.getContent(), listType));
        response.setTotalRecords(rackPage.getTotalElements());
        response.setTotalPages(rackPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());
        return response;
    }

    @Override
    public RackDto findByIdentifier(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier.trim());
        if (rack == null) {
            RackDto dto = new RackDto();
            dto.setSuccess(false);
            dto.setMessage(ENTITY_NAME + IDENTIFIER_TEXT + identifier + NOT_FOUND
            );
            return dto;
        }
        return modelMapper.map(rack, RackDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Rack rack = rackRepository.findByIdentifier(identifier.trim());
        if (rack != null) {
            rack.setStatus(!rack.getStatus());
            setAuditFields(rack, false);
            rackRepository.save(rack);
        }
    }

    @Override
    public WsDto<RackDto> findAll(Specification<Rack> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<RackDto>>() {
        }.getType();
        Page<Rack> page = rackRepository.findAll(example, pageable);
        WsDto<RackDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}