package com.ust.pos.models.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ModelsDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Models;
import com.ust.pos.model.ModelsRepository;
import com.ust.pos.models.service.ModelsService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ModelsServiceImpl extends BaseService implements ModelsService {

    public static final String MODEL_NOT_FOUND = "Model not found";
    private final ModelsRepository modelsRepository;
    private final ModelMapper modelMapper;

    public ModelsServiceImpl(ModelsRepository modelsRepository,ModelMapper modelMapper) {
        this.modelsRepository = modelsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ModelsDto save(ModelsDto dto) {

        if (dto.getModelName() == null || dto.getModelName().trim().isEmpty()) {
            dto.setSuccess(false);
            dto.setMessage("Model name is required");
            return dto;
        }

        String name = dto.getModelName().trim();

        Models existing = modelsRepository.findByIdentifier(name);

        if (existing != null && !Boolean.TRUE.equals(existing.getDeleted())) {
            dto.setSuccess(false);
            dto.setMessage("Model already exists");
            return dto;
        }

        Models model = new Models();
        model.setIdentifier(name);
        model.setModelName(name);
        model.setStatus(dto.getStatus());

        setCreatedDetails(model);

        modelsRepository.save(model);

        dto.setSuccess(true);
        dto.setMessage("Model saved successfully");
        dto.setIdentifier(model.getIdentifier());

        return dto;
    }

    @Override
    public ModelsDto update(ModelsDto dto) {

        Models model = modelsRepository.findByIdentifier(dto.getIdentifier());

        if (model == null || Boolean.TRUE.equals(model.getDeleted())) {
            dto.setSuccess(false);
            dto.setMessage(MODEL_NOT_FOUND);
            return dto;
        }

        if (dto.getModelName() != null && !dto.getModelName().trim().isEmpty()) {
            model.setModelName(dto.getModelName().trim());
        }

        model.setStatus(dto.getStatus());

        setModifiedDetails(model);

        modelsRepository.save(model);

        dto.setSuccess(true);
        dto.setMessage("Model updated successfully");

        return dto;
    }

    @Override
    public ModelsDto findByIdentifier(String identifier) {

        Models model = modelsRepository.findByIdentifier(identifier);

        if (model == null || Boolean.TRUE.equals(model.getDeleted())) {
            ModelsDto dto = new ModelsDto();
            dto.setSuccess(false);
            dto.setMessage(MODEL_NOT_FOUND);
            return dto;
        }

        return modelMapper.map(model, ModelsDto.class);
    }

    @Override
    public WsDto<ModelsDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ModelsDto>>() {}.getType();

        Page<Models> page = modelsRepository.findAll(pageable);

        WsDto<ModelsDto> ws = new WsDto<>();
        ws.setDtoList(modelMapper.map(page.getContent(), listType));
        ws.setTotalRecords(page.getTotalElements());
        ws.setTotalPages(page.getTotalPages());
        ws.setSizePerPage(pageable.getPageSize());
        ws.setPage(pageable.getPageNumber());

        return ws;
    }

    @Override
    public void delete(String identifier) {

        Models model = modelsRepository.findByIdentifier(identifier);

        if (model == null) return;

        model.setDeleted(true);
        setModifiedDetails(model);

        modelsRepository.save(model);
    }

    @Override
    public ModelsDto toggleStatus(String identifier) {

        Models model = modelsRepository.findByIdentifier(identifier);

        if (model == null || Boolean.TRUE.equals(model.getDeleted())) {
            ModelsDto dto = new ModelsDto();
            dto.setSuccess(false);
            dto.setMessage(MODEL_NOT_FOUND);
            return dto;
        }

        model.setStatus(!Boolean.TRUE.equals(model.getStatus()));

        setModifiedDetails(model);

        modelsRepository.save(model);

        ModelsDto dto = modelMapper.map(model, ModelsDto.class);
        dto.setSuccess(true);
        dto.setMessage("Status updated successfully");

        return dto;
    }

    @Override
    public List<ModelsDto> findActiveModels() {

        List<Models> list = modelsRepository.findByStatusTrueAndDeletedFalse();
        Type type = new TypeToken<List<ModelsDto>>() {}.getType();
        return modelMapper.map(list, type);
    }
}