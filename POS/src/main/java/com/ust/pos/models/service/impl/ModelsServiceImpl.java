package com.ust.pos.models.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.ModelsService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ModelsServiceImpl extends CommonService implements ModelsService {

    private static final String MODELS_WITH_IDENTIFIER = "Models with identifier - ";

    private final ModelsRepository modelsRepository;

    private final ModelMapper modelMapper;

    public ModelsServiceImpl(ModelsRepository modelsRepository, ModelMapper modelMapper) {
        this.modelsRepository = modelsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ModelsDto findByIdentifier(String identifier) {
        return modelMapper.map(modelsRepository.findByIdentifier(identifier), ModelsDto.class);
    }

    @Override
    public ModelsDto toggleStatus(String identifier) {
        Models models = modelsRepository.findByIdentifier(identifier);
        models.setStatus(!models.isStatus());
        setAuditFields(models,false);
        modelsRepository.save(models);
        return modelMapper.map(models, ModelsDto.class);
    }

    @Override
    public ModelsDto save(ModelsDto modelsDto) {
        modelsDto.setIdentifier(modelsDto.getIdentifier().trim());
        String identifier = modelsDto.getIdentifier();
        Models existingModels = modelsRepository.findByIdentifier(identifier);
        if (existingModels != null) {
            if (!existingModels.isDeleted()) {
                modelsDto.setMessage(MODELS_WITH_IDENTIFIER + identifier + " already exists");
                modelsDto.setSuccess(false);
                return modelsDto;
            }
            modelsDto.setMessage(MODELS_WITH_IDENTIFIER + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        Models models = modelMapper.map(modelsDto, Models.class);
        setAuditFields(models,true);
        modelsRepository.save(models);
        modelsDto.setSuccess(true);
        modelsDto.setMessage("Models created successfully");
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
        setAuditFields(existingModels,false);
        modelsRepository.save(existingModels);
        return modelsDto;
    }

    @Override
    public boolean delete(String identifier) {
        Models models = modelsRepository.findByIdentifier(identifier);
        if (models == null) return false;
        softDelete(models);
        setAuditFields(models,false);
        modelsRepository.save(models);
        return true;
    }

    @Override
    public WsDto<ModelsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        Page<Models> modelsPage = modelsRepository.findByDeletedFalse(pageable);
        WsDto<ModelsDto> modelsDtoWsDto = new WsDto<>();
        modelsDtoWsDto.setDtoList(modelMapper.map(modelsPage.getContent(), listType));
        modelsDtoWsDto.setTotalRecords(modelsPage.getTotalElements());
        modelsDtoWsDto.setTotalPages(modelsPage.getTotalPages());
        modelsDtoWsDto.setSizePerPage(pageable.getPageSize());
        modelsDtoWsDto.setPage(pageable.getPageNumber());
        return modelsDtoWsDto;
    }

    @Override
    public List<ModelsDto> findIfTrue() {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        return modelMapper.map(modelsRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }

    @Override
    public WsDto<ModelsDto> findAll(Specification<Models> example, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        Page<Models> modelsPage = modelsRepository.findAll(example, pageable);
        WsDto<ModelsDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(modelsPage.getContent(), listType));
        wsDto.setTotalRecords(modelsPage.getTotalElements());
        wsDto.setTotalPages(modelsPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}