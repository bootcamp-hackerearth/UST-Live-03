package com.ust.pos.models.service.impl;

import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.ModelService;
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
public class ModelServiceImpl implements ModelService {

    @Autowired
    private ModelsRepository modelsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ModelsDto findByIdentifier(String identifier) {

        Models models = modelsRepository.findByIdentifier(identifier);

        if (models == null) {
            return null;
        }

        return modelMapper.map(models, ModelsDto.class);
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
    public WsDto<ModelsDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();

        Page<Models> modelsPage = modelsRepository.findAll(pageable);

        WsDto<ModelsDto> dto = new WsDto<>();

        dto.setContent(modelMapper.map(modelsPage.getContent(), listType));
        dto.setTotalRecords(modelsPage.getTotalElements());
        dto.setTotalPages(modelsPage.getTotalPages());
        dto.setSizePerPage(pageable.getPageSize());
        dto.setPage(pageable.getPageNumber());

        return dto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Models models = modelsRepository.findByIdentifier(identifier);
        if (models != null) {
            models.setStatus(!models.isStatus());
            modelsRepository.save(models);
        }
    }

    @Override
    public List<Models> findActiveModels() {
        return modelsRepository.findByStatus(true);
    }

}
