package com.ust.pos.models.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.ModelsService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ModelsServiceImpl extends CommonService implements ModelsService {
    private static final String MODELS_WITH_IDENTIFIER = "Models with identifier - ";
    private static final String NOT_FOUND = " not found";
    private final ModelsRepository modelsRepository;
    private final ModelMapper modelMapper;

    public ModelsServiceImpl(ModelsRepository modelsRepository, ModelMapper modelMapper) {
        this.modelsRepository = modelsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ModelsDto save(ModelsDto modelsDto) {
        String identifier = modelsDto.getIdentifier();
        Models existingModels = modelsRepository.findByIdentifier(identifier);
        if (existingModels != null) {
            modelsDto.setMessage(MODELS_WITH_IDENTIFIER + identifier + " already exists");
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        Models models = modelMapper.map(modelsDto, Models.class);
        setAuditFields(models, true);
        modelsRepository.save(models);
        return modelsDto;
    }

    @Override
    public WsDto<ModelsDto> findAll(Pageable pageable) {
        Page<Models> modelsPage = modelsRepository.findByDeletedFalse(pageable);
        Type type = new TypeToken<List<ModelsDto>>() {
        }.getType();
        WsDto<ModelsDto> modelsWsDto = new WsDto<>();
        modelsWsDto.setDtoList(modelMapper.map(modelsPage.getContent(), type));
        modelsWsDto.setTotalRecords(modelsPage.getTotalElements());
        modelsWsDto.setTotalPages(modelsPage.getTotalPages());
        modelsWsDto.setSizePerPage(pageable.getPageSize());
        modelsWsDto.setPage(pageable.getPageNumber());
        return modelsWsDto;
    }

    @Override
    public WsDto<ModelsDto> findAll(Specification<Models> example, Pageable pageable) {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        Page<Models> page = modelsRepository.findAll(example, pageable);
        WsDto<ModelsDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public List<ModelsDto> findAllActive() {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        return modelMapper.map(modelsRepository.findAllByStatusAndDeletedFalse(true), listType);
    }

    @Override
    public ModelsDto findByIdentifier(String identifier) {
        Models models = modelsRepository.findByIdentifier(identifier);
        if(models==null){
            throw new ResourceNotFoundException("Models with identifier '" + identifier + "' not found");
        }
        ModelsDto modelsDto = modelMapper.map(models, ModelsDto.class);
        modelsDto.setSuccess(true);
        return modelsDto;
    }

    @Override
    public ModelsDto update(ModelsDto modelsDto) {
        String identifier = modelsDto.getIdentifier();
        Models existingModels = modelsRepository.findByIdentifier(modelsDto.getIdentifier());
        if (existingModels == null) {
            modelsDto.setMessage(MODELS_WITH_IDENTIFIER + identifier + NOT_FOUND);
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        modelMapper.map(modelsDto, existingModels);
        setAuditFields(existingModels, false);
        modelsRepository.save(existingModels);
        return modelsDto;
    }

    @Override
    public ModelsDto toggleStatus(String identifier) {
        Models models = modelsRepository.findByIdentifier(identifier);
        if (models == null) {
            ModelsDto dto = new ModelsDto();
            dto.setSuccess(false);
            dto.setMessage(MODELS_WITH_IDENTIFIER + identifier + NOT_FOUND);
            return dto;
        }
        models.setStatus(!models.isStatus());
        setAuditFields(models, false);
        modelsRepository.save(models);
        return modelMapper.map(modelsRepository.findByIdentifier(identifier), ModelsDto.class);
    }

    @Override
    public boolean delete(String identifier) {
        Models models = modelsRepository.findByIdentifier(identifier);
        if (models == null) return false;
        softDelete(models);
        setAuditFields(models, false);
        modelsRepository.save(models);
        return true;
    }
}