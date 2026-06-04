package com.ust.pos.models.service.impl;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WarehouseDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.ModelsService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ModelsServiceImpl implements ModelsService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ModelsRepository modelsRepository;

    @Override
    public ModelsDto save(ModelsDto modelsDto) {
        String identifier = modelsDto.getIdentifier();
        Models existingmodel = modelsRepository.findByIdentifier(identifier);
        if (existingmodel != null) {
            modelsDto.setMessage("Model - " + identifier + " already exists");
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
        Models existingmodel = modelsRepository.findByIdentifier(identifier);
        if (existingmodel == null) {
            modelsDto.setMessage("Model - " + identifier + " not found");
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        Models models = modelMapper.map(modelsDto, Models.class);
        modelsRepository.save(models);
        return modelsDto;
    }

    @Override
    public ModelsDto findByIdentifier(String identifier) {
        return modelMapper.map(modelsRepository.findByIdentifier(identifier), ModelsDto.class);
    }

    @Override
    public List<ModelsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        Page<Models> modelsPage = modelsRepository.findAll(pageable);
        return modelMapper.map(modelsPage.getContent(), listType);
    }

    @Override
    public void delete(String identifier) {
        modelsRepository.deleteByIdentifier(identifier);
    }

    @Override
    public void updateStatus(String identifier, boolean status) {
        Models models = modelsRepository.findByIdentifier(identifier);
        models.setStatus(status);
        modelsRepository.save(models);
    }

    @Override
    public List<ModelsDto> findAllActive() {
        Type listType = new TypeToken<List<WarehouseDto>>() {
        }.getType();
        return modelMapper.map(modelsRepository.findByStatus(true), listType);
    }
}