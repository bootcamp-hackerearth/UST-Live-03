package com.ust.pos.models.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.ModelsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelsServiceImpl extends BaseService implements ModelsService {

    private final ModelsRepository modelsRepository;
    private final ModelMapper modelMapper;

    @Override
    public ModelsDto findByIdentifier(String identifier) {
        return modelMapper.map(modelsRepository.findByIdentifierAndDeletedFalse(identifier), ModelsDto.class);
    }

    @Override
    public ModelsDto save(ModelsDto modelsDto) {
        String identifier = modelsDto.getIdentifier();
        Models existingModels = modelsRepository.findByIdentifier(identifier);
        if (existingModels != null) {
            if (Boolean.TRUE.equals(existingModels.getDeleted())) {
                modelsDto.setMessage("Model with identifier - " + identifier + " already exists");
                modelsDto.setSuccess(false);
                return modelsDto;
            }
            modelsDto.setMessage("Model with identifier - " + identifier + " already exists");
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        Models models = modelMapper.map(modelsDto, Models.class);
        setCreatedDetails(models);
        modelsRepository.save(models);
        modelsDto.setSuccess(true);
        modelsDto.setMessage("Model Saved Successfully");
        return modelsDto;
    }

    @Override
    public ModelsDto update(ModelsDto modelsDto) {
        String identifier = modelsDto.getIdentifier();
        Models existingModels = modelsRepository.findByIdentifier(identifier);
        if (existingModels == null) {
            modelsDto.setMessage("Models with identifier - " + identifier + " was deleted and cannot be created again.");
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        modelMapper.map(modelsDto, existingModels);
        setModifiedDetails(existingModels);
        modelsRepository.save(existingModels);
        modelsDto.setSuccess(true);
        modelsDto.setMessage("Model Updated Successfully");
        return modelsDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Models models = modelsRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(models);
        setModifiedDetails(models);
        modelsRepository.save(models);
    }

    @Override
    public WsDto<ModelsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        Page<Models> modelPage = modelsRepository.findAllByDeletedFalse(pageable);
        WsDto<ModelsDto> modelDto = new WsDto<>();
        modelDto.setDtoList(modelMapper.map(modelPage.getContent(), listType));
        modelDto.setTotalRecords(modelPage.getTotalElements());
        modelDto.setTotalPage(modelPage.getTotalPages());
        modelDto.setSizePerPage(pageable.getPageSize());
        modelDto.setPage(pageable.getPageNumber());
        return modelDto;
    }

    @Override
    public ModelsDto toggleStatus(String identifier) {
        Models models = modelsRepository.findByIdentifier(identifier);
        models.setStatus(!models.isStatus());
        modelsRepository.save(models);
        setModifiedDetails(models);
        return modelMapper.map(models, ModelsDto.class);
    }

    public List<ModelsDto> findActiveModels() {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        return modelMapper.map(
                modelsRepository.findByStatusTrueAndDeletedFalse(),
                listType
        );
    }

}
