package com.ust.pos.models.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.ModelService;
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
public class ModelServiceImpl extends BaseService implements ModelService {

    private final ModelsRepository modelsRepository;

    private final ModelMapper modelMapper;

    public ModelServiceImpl(ModelsRepository modelsRepository, ModelMapper modelMapper) {
        this.modelsRepository = modelsRepository;
        this.modelMapper = modelMapper;
    }

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
            modelsDto.setMessage(
                    existingModels.isDeleted()
                            ? "Models - " + identifier + " already exists but was deleted, Please contact Administrator"
                            : "Models - " + identifier + " already exists"
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

    @Override
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

    @Override
    public WsDto<ModelsDto> findAll(Specification<Models> example, Pageable pageable) {

        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        Page<Models> page = modelsRepository.findAll(example, pageable);

        WsDto<ModelsDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}
