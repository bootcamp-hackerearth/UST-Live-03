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
import java.util.ArrayList;
import java.util.List;

@Service
public class ModelsServiceImpl implements ModelsService {

    @Autowired
    private ModelsRepository modelsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ModelsDto save(ModelsDto modelsDto) {
        if (modelsDto.getModelName() == null || modelsDto.getModelName().trim().isEmpty()) {
            modelsDto.setSuccess(false);
            modelsDto.setMessage("Model name is required");
            return modelsDto;
        }
        if (modelsRepository.findByIdentifier(modelsDto.getModelName()) != null) {
            modelsDto.setSuccess(false);
            modelsDto.setMessage("Model already exists");
            return modelsDto;
        }
        Models model = new Models();
        model.setIdentifier(modelsDto.getModelName());
        model.setModelName(modelsDto.getModelName());
        model.setStatus(modelsDto.getStatus());
        Models saved = modelsRepository.save(model);
        ModelsDto dto = modelMapper.map(saved, ModelsDto.class);
        dto.setSuccess(true);
        dto.setMessage("Model added successfully");
        return dto;
    }

    @Override
    public ModelsDto update(ModelsDto modelsDto) {
        Models model = modelsRepository.findByIdentifier(modelsDto.getIdentifier());
        if (model == null) {
            modelsDto.setSuccess(false);
            modelsDto.setMessage("Model not found");
            return modelsDto;
        }
        model.setStatus(modelsDto.getStatus());
        modelsRepository.save(model);
        modelsDto.setSuccess(true);
        modelsDto.setMessage("Model updated successfully");
        return modelsDto;
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
    public ModelsDto findByIdentifier(String identifier) {
        Models model = modelsRepository.findByIdentifier(identifier);
        if (model == null) return null;
        ModelsDto dto = new ModelsDto();
        dto.setIdentifier(model.getIdentifier());
        dto.setModelName(model.getModelName());
        dto.setStatus(model.getStatus());
        return dto;
    }

    @Override
    public void delete(String identifier) {
        modelsRepository.deleteByIdentifier(identifier);
    }

    @Override
    public ModelsDto toggleStatus(String identifier) {
        ModelsDto response = new ModelsDto();
        Models model = modelsRepository.findByIdentifier(identifier);
        if (model == null) {
            response.setSuccess(false);
            response.setMessage("Model not found");
            return response;
        }
        model.setStatus(!Boolean.TRUE.equals(model.getStatus()));
        Models saved = modelsRepository.save(model);
        response.setIdentifier(saved.getIdentifier());
        response.setModelName(saved.getModelName());
        response.setStatus(saved.getStatus());
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

    @Override
    public List<ModelsDto> findActiveModels() {
        List<Models> list = modelsRepository.findAll();
        List<ModelsDto> result = new ArrayList<>();
        for (Models model : list) {
            if (model.getStatus() != null && model.getStatus()) {
                ModelsDto dto = new ModelsDto();
                dto.setIdentifier(model.getIdentifier());
                dto.setModelName(model.getModelName());
                dto.setStatus(model.getStatus());
                result.add(dto);
            }
        }
        return result;
    }
}