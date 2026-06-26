package com.ust.pos.models.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.ModelsService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ModelsServiceImpl extends CommonService implements ModelsService {

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
        setAuditFields(models, false);
        modelsRepository.save(models);
        return modelMapper.map(models, ModelsDto.class);
    }

    @Override
    public ModelsDto save(ModelsDto modelsDto) {
        modelsDto.setIdentifier(modelsDto.getIdentifier().trim());
        String identifier = modelsDto.getIdentifier();
        Models existingModels = modelsRepository.findByIdentifier(identifier);
        if (existingModels != null) {
            modelsDto.setMessage("Models with identifier - " + identifier + " already exists");
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        Models models = modelMapper.map(modelsDto, Models.class);
        setAuditFields(models, true);
        modelsRepository.save(models);
        return modelsDto;
    }

    @Override
    public ModelsDto update(ModelsDto modelsDto) {
        String identifier = modelsDto.getIdentifier();
        Models existingModels = modelsRepository.findByIdentifier(identifier);
        if (existingModels == null) {
            modelsDto.setMessage("Models with identifier - " + identifier + " not found");
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        modelMapper.map(modelsDto, existingModels);
        setAuditFields(existingModels, false);
        modelsRepository.save(existingModels);
        return modelsDto;
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

    @Override
    public WsDto<ModelsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        Page<Models> modelsPage = modelsRepository.findByDeletedFalse(pageable);
        WsDto<ModelsDto> modelsWsDto = new WsDto<>();
        modelsWsDto.setDtoList(modelMapper.map(modelsPage.getContent(), listType));
        modelsWsDto.setTotalRecords(modelsPage.getTotalElements());
        modelsWsDto.setTotalPages(modelsPage.getTotalPages());
        modelsWsDto.setSizePerPage(pageable.getPageSize());
        modelsWsDto.setPage(pageable.getPageNumber());

        return modelsWsDto;
    }

    @Override
    public List<ModelsDto> findIfTrue() {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        return modelMapper.map(modelsRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }

}