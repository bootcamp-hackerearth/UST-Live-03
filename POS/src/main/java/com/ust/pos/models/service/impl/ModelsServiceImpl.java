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
@Transactional
public class ModelsServiceImpl extends BaseService implements ModelsService {
    public static final String MODEL = "Model - ";
    private final ModelMapper modelMapper;
    private final ModelsRepository modelsRepository;

    public ModelsServiceImpl(ModelMapper modelMapper,
                             ModelsRepository modelsRepository) {
        this.modelMapper = modelMapper;
        this.modelsRepository = modelsRepository;
    }

    @Override
    public ModelsDto save(ModelsDto modelsDto) {
        String identifier = modelsDto.getIdentifier();
        Models existingModel = modelsRepository.findByIdentifier(identifier);
        if (existingModel != null) {
            if (Boolean.TRUE.equals(existingModel.getDeleted())) {
                modelsDto.setMessage(MODEL + identifier + " was deleted and cannot be recreated");
            } else {
                modelsDto.setMessage(MODEL + identifier + " already exists");
            }
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        Models models = modelMapper.map(modelsDto, Models.class);
        setCreatedDetails(models);
        modelsRepository.save(models);
        modelsDto.setSuccess(true);
        return modelsDto;
    }

    @Override
    public ModelsDto update(ModelsDto modelsDto) {
        String identifier = modelsDto.getIdentifier();
        Models existingModel = modelsRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingModel == null) {
            modelsDto.setMessage(MODEL + identifier + " not found");
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        modelMapper.map(modelsDto, existingModel);
        setModifiedDetails(existingModel);
        modelsRepository.save(existingModel);
        modelsDto.setSuccess(true);
        return modelsDto;
    }

    @Override
    public ModelsDto findByIdentifier(String identifier) {
        return modelMapper.map(
                modelsRepository.findByIdentifierAndDeletedFalse(identifier),
                ModelsDto.class
        );
    }

    @Override
    public WsDto<ModelsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        Page<Models> modelsPage = modelsRepository.findByDeletedFalse(pageable);
        WsDto<ModelsDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(modelsPage.getContent(), listType));
        wsDto.setTotalRecords(modelsPage.getTotalElements());
        wsDto.setTotalPages(modelsPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public void delete(String identifier) {
        Models model = modelsRepository.findByIdentifierAndDeletedFalse(identifier);
        if (model != null) {
            softDelete(model);
            setModifiedDetails(model);
            modelsRepository.save(model);
        }
    }

    @Override
    public void updateStatus(String identifier, boolean status) {
        Models model = modelsRepository.findByIdentifierAndDeletedFalse(identifier);
        if (model != null) {
            model.setStatus(status);
            setModifiedDetails(model);
            modelsRepository.save(model);
        }
    }

    @Override
    public List<ModelsDto> findAllActive() {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        return modelMapper.map(
                modelsRepository.findByStatusAndDeletedFalse(true),
                listType
        );
    }
}