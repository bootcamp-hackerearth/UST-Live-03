package com.ust.pos.models.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
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

    private static final String VALIDATION_MESSAGE = "Model with identifier - ";
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
            throw new ResourseNotFoundException("Data cannot found");
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
                            ? VALIDATION_MESSAGE + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : VALIDATION_MESSAGE + identifier
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
            modelsDto.setMessage(VALIDATION_MESSAGE + identifier + " not found");
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

        Page<Models> modelsPage = modelsRepository.findByIsDeletedFalse(pageable);

        WsDto<ModelsDto> modelsDto = new WsDto<>();

        List<ModelsDto> modelsDtos = modelsPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, ModelsDto.class))
                .toList();

        modelsDto.setContent(modelsDtos);
        modelsDto.setPage(modelsPage.getNumber());
        modelsDto.setSizePerPage(modelsPage.getSize());
        modelsDto.setTotalPages(modelsPage.getTotalPages());
        modelsDto.setTotalRecords(modelsPage.getTotalElements());

        return modelsDto;
    }

    @Override
    public WsDto<ModelsDto> findAll(Specification<Models> example, Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {
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

    @Override
    public void toggleStatus(String identifier) {

        Models models = modelsRepository.findByIdentifier(identifier);

        if (models != null) {
            models.setStatus(!models.isStatus());
            modelsRepository.save(models);
        }
    }

    @Override
    public List<ModelsDto> findActiveModels() {

        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();

        return modelMapper.map(modelsRepository.findByStatus(true), listType);
    }
}




