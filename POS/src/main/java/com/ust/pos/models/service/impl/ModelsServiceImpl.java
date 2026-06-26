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
    public ModelsDto save(ModelsDto modelsDto) {
        String identifier = modelsDto.getIdentifier();
        Models existingModels = modelsRepository.findByIdentifier(identifier);
        if (existingModels != null) {
            if (Boolean.TRUE.equals(existingModels.getDeleted())) {
                modelsDto.setMessage("Models identifier - " + identifier + " not available");
                modelsDto.setSuccess(false);
                return modelsDto;
            }
            modelsDto.setMessage("Models with identifier - " + identifier + " already exists");
            modelsDto.setSuccess(false);
            return modelsDto;
        }
        Models models = modelMapper.map(modelsDto, Models.class);
        setCreatedDetails(models);
        setModifiedDetails(models);
        modelsRepository.save(models);
        return modelsDto;
    }

    @Override
    public ModelsDto update(ModelsDto modelsDto) {
        String identifier = modelsDto.getIdentifier();
        Models existingModels = modelsRepository.findByIdentifier(identifier);
        if (existingModels == null) {
            modelsDto.setMessage("models with identifier - " + identifier + " not found");
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
        Models models = modelsRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(models);
    }

    @Override
    public WsDto<ModelsDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelsDto>>() {
        }.getType();
        Page<Models> modelsPage = modelsRepository.findAllByDeletedFalse(pageable);

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
        return modelMapper.map(modelsRepository.findByIdentifierAndDeletedFalse(identifier), ModelsDto.class);
    }

    @Override
    public List<Models> findActiveModels() {
        return modelsRepository.findByStatusTrueAndDeletedFalse();
    }

    @Override
    public ModelsDto toggleStatus(String identifier) {
        Models models = modelsRepository.findByIdentifier(identifier);
        if (models == null) {
            return null;
        }
        models.setStatus(!models.isStatus());
        setModifiedDetails(models);
        modelsRepository.save(models);
        return modelMapper.map(models, ModelsDto.class);
    }
}
