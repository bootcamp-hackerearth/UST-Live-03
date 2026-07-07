package com.ust.pos.models.service.impl;

import com.ust.pos.base.service.BaseService;
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
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ModelsServiceImpl extends BaseService implements ModelsService {

    public static final String MODELS_WITH_IDENTIFIER = "Models with identifier - ";
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
            if(existingModels.isDeleted()){
                modelsDto.setMessage(MODELS_WITH_IDENTIFIER + identifier + " was deleted , Please Contact the Administrator to add.");
            }
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        Models models = modelMapper.map(modelsDto, Models.class);
        setCreatedDetails(models);
        modelsRepository.save(models);
        return modelsDto;
    }

    @Override
    public ModelsDto update(ModelsDto modelsDto) {
        String identifier = modelsDto.getIdentifier();
        Models existingModels = modelsRepository.findByIdentifier(identifier);
        if (existingModels == null) {
            modelsDto.setMessage(MODELS_WITH_IDENTIFIER + identifier + " not found");
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        modelMapper.map(modelsDto, existingModels);
        setModifiedDetails(existingModels);
        modelsRepository.save(existingModels);
        return modelsDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Models models = modelsRepository.findByIdentifier(identifier);
        softDelete(models);
        setModifiedDetails(models);
    }

    @Override
    public WsDto<ModelsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        Page<Models> modelspage = modelsRepository.findByIsDeletedFalse(pageable);

        WsDto<ModelsDto> modelsWsDto = new WsDto<>();
        modelsWsDto.setDtoList(modelMapper.map(modelspage.getContent(), listType));
        modelsWsDto.setTotalRecords(modelspage.getTotalElements());
        modelsWsDto.setTotalPages(modelspage.getTotalPages());
        modelsWsDto.setSizePerPage(pageable.getPageSize());
        modelsWsDto.setPage(pageable.getPageNumber());

        return modelsWsDto;
    }

    @Override
    public ModelsDto findByIdentifier(String identifier) {
        Models models = modelsRepository.findByIdentifierAndIsDeletedFalse(identifier);
        if (models == null) {
            throw new ResourceNotFoundException("Models with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(models, ModelsDto.class);
    }

    @Override
    public List<ModelsDto> findAllActive() {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        return modelMapper.map(modelsRepository.findByStatusTrueAndIsDeletedFalse(), listType);
    }

    @Override
    public void toggleStatus(String identifier) {
        Models models = modelsRepository.findByIdentifier(identifier);
        if (models != null) {
            models.setStatus(!models.isStatus());
            setModifiedDetails(models);
            modelsRepository.save(models);
        }
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
}
