package com.ust.pos.models.service.impl;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.ModelsService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ModelsServiceImpl implements ModelsService {
    private static final String MODELS_WITH_IDENTIFIER = "Models with identifier - ";
    private static final String NOT_FOUND = " not found";

    @Autowired
    private ModelsRepository modelsRepository;

    @Autowired
    private ModelMapper modelMapper;

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
        modelsRepository.save(models);
        return modelsDto;
    }

    @Override
    public List<ModelsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        Page<Models> modelsPage = modelsRepository.findAll(pageable);
        return modelMapper.map(modelsPage.getContent(), listType);
    }

    @Override
    public List<ModelsDto> findAllActive() {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        return modelMapper.map(modelsRepository.findAllByStatus(true), listType);
    }

    @Override
    public ModelsDto findByIdentifier(String identifier) {
        Models models = modelsRepository.findByIdentifier(identifier);
        if (models == null) {
            ModelsDto modelsDto = new ModelsDto();
            modelsDto.setSuccess(false);
            modelsDto.setMessage(MODELS_WITH_IDENTIFIER + identifier + NOT_FOUND);
            return modelsDto;
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
        modelsRepository.save(models);
        return modelMapper.map(modelsRepository.findByIdentifier(identifier), ModelsDto.class);
    }

    @Override
    public boolean delete(String identifier) {
        modelsRepository.deleteByIdentifier(identifier);
        return true;
    }
}