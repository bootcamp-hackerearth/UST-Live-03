package com.ust.pos.models.service.impl;

import com.ust.pos.base.service.BaseService;
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
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ModelsServiceImpl extends BaseService implements ModelsService {

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
    public ModelsDto save(ModelsDto modelsDto) {
        String identifier = modelsDto.getIdentifier();
        Models existingModels = modelsRepository.findByIdentifier(identifier);
        if (existingModels != null) {
            modelsDto.setMessage(
                    existingModels.isDeleted()
                            ? " models with identifier - " + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : " models with identifier - " + identifier
                            + " already exists."
            );
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
            modelsDto.setMessage("Models with identifier - " + identifier + " not found");
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        modelMapper.map(modelsDto, existingModels);
        setModifiedDetails(existingModels);
        modelsRepository.save(existingModels);
        return modelsDto;
    }

    @Transactional
    public void delete(String identifier) {

        Models models = modelsRepository.findByIdentifier(identifier);
        setModifiedDetails(models);
        softDelete(models);
    }

    @Override
    public WsDto<ModelsDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();

        Page<Models> modelsPage = modelsRepository.findByIsDeletedFalse(pageable);

        List<ModelsDto> modelsDtos = modelMapper.map(
                modelsPage.getContent(),
                listType
        );

        WsDto<ModelsDto> wsDto =
                new WsDto<>();

        wsDto.setContent(modelsDtos);
        wsDto.setPage(modelsPage.getNumber());
        wsDto.setSizePerPage(modelsPage.getSize());
        wsDto.setTotalPages(modelsPage.getTotalPages());
        wsDto.setTotalRecords(modelsPage.getTotalElements());

        return wsDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Models models = modelsRepository.findByIdentifier(identifier);
        if (models != null) {
            models.setStatus(!models.isStatus());
            modelsRepository.save(models);
        }
    }
}