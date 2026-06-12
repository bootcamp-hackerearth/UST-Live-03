package com.ust.pos.models.service.impl;

import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
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
    @Autowired
    ModelsRepository modelsRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public ModelsDto save(ModelsDto modelsDto) {

        Models existingModels = modelsRepository.findByIdentifier(modelsDto.getIdentifier());
        if (existingModels != null) {
            modelsDto.setSuccess(false);
            modelsDto.setMessage("Models with Identifier" + modelsDto.getIdentifier() + "already exist!");
            return modelsDto;
        }
        Models models = modelMapper.map(modelsDto, Models.class);
        modelsRepository.save(models);
        return modelsDto;

    }

    @Override
    public ModelsDto findById(Long id) {

        Models models = modelsRepository.findById(id).orElseThrow(() -> new RuntimeException("Models not found with id " + id));
        return modelMapper.map(models, ModelsDto.class);

    }

    @Override
    public WsDto<ModelsDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        Page<Models> modelsPage = modelsRepository.findAll(pageable);
        WsDto<ModelsDto> modelsWsDto = new WsDto<>();
        modelsWsDto.setDtoList(modelMapper.map(modelsPage.getContent(), listType));
        modelsWsDto.setTotalRecords(modelsPage.getTotalElements());
        modelsWsDto.setTotalPages(modelsPage.getTotalPages());
        modelsWsDto.setSizePerPage(pageable.getPageSize());
        modelsWsDto.setPage(pageable.getPageNumber());

        return modelsWsDto;
    }

    @Override
    public void deleteById(Long id) {
        modelsRepository.deleteById(id);
    }

    @Override
    public ModelsDto update(ModelsDto modelsDto) {

        Models models = modelsRepository.findByIdentifier(modelsDto.getIdentifier());
        if (models == null) {
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        modelMapper.map(modelsDto, models);
        modelsRepository.save(models);
        modelsDto.setSuccess(true);
        return modelsDto;
    }

    @Override
    public ModelsDto changeModelsStatus(String identifier, boolean status) {
        Models models = modelsRepository.findByIdentifier(identifier);
        if (models != null) {
            models.setStatus(status);
            modelsRepository.save(models);
        }
        return modelMapper.map(models, ModelsDto.class);
    }

    @Override
    public List<ModelsDto> findActiveModels() {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        return modelMapper.map(modelsRepository.findByStatus(true), listType);
    }
}