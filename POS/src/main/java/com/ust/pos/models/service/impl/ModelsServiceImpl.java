package com.ust.pos.models.service.impl;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.ModelsService;
import jakarta.transaction.Transactional;
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

    @Autowired
    private ModelsRepository modelsRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ModelsDto findByIdentifier(String identifier) {
        return modelMapper.map(modelsRepository.findByIdentifier(identifier), ModelsDto.class);
    }

    @Override
    public ModelsDto save(ModelsDto modelsDto) {
        String identifier = modelsDto.getIdentifier();
        Models existingModels = modelsRepository.findByIdentifier(identifier);
        if (existingModels != null) {
            modelsDto.setMessage("Models with identifier - " + identifier + " already exists");
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        Models models = modelMapper.map(modelsDto, Models.class);
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
        modelsRepository.save(existingModels);
        return modelsDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        modelsRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<ModelsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        Page<Models> modelsPage = modelsRepository.findAll(pageable);
        return modelMapper.map(modelsPage.getContent(), listType);
    }

    @Override
    public void toggleStatus(String identifier) {
        Models models = modelsRepository.findByIdentifier(identifier);
        if (models == null) {
            throw new IllegalArgumentException("Models not found");
        }
        models.setStatus(!models.isStatus());
        modelsRepository.save(models);
    }

    @Override
    public List<ModelsDto> findActiveModels() {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        return modelMapper.map(modelsRepository.findByStatusTrue(), listType);
    }

}
