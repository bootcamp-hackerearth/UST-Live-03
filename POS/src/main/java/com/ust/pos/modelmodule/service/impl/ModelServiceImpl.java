package com.ust.pos.modelmodule.service.impl;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.modelmodule.service.ModelService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ModelServiceImpl implements ModelService {

    @Autowired
    ModelRepository modelRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public ModelDto save(ModelDto modelDto) {
        Model existingModel = modelRepository.findByIdentifier(modelDto.getIdentifier().trim());
        if (existingModel != null) {
            modelDto.setMessage("Model with identifier - " + modelDto.getIdentifier() + " already exists");
            modelDto.setSuccess(false);
            return modelDto;
        }
        Model model = modelMapper.map(modelDto, Model.class);
        modelRepository.save(model);
        return modelDto;
    }

    @Override
    public WsDto<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        if (pageable == null) {
            List<ModelDto> modelDtoList = modelMapper.map(modelRepository.findAll(), listType);
            WsDto<ModelDto> response = new WsDto<>();
            response.setDtoList(modelDtoList);
            response.setTotalRecords(modelDtoList.size());
            return response;
        }
        Page<Model> modelPage = modelRepository.findAll(pageable);
        List<ModelDto> modelDtoList = modelMapper.map(modelPage.getContent(), listType);
        WsDto<ModelDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelDtoList);
        wsDto.setPage(modelPage.getNumber());
        wsDto.setSizePerPage(modelPage.getSize());
        wsDto.setTotalPages(modelPage.getTotalPages());
        wsDto.setTotalRecords(modelPage.getTotalElements());
        return wsDto;
    }

    @Override
    public ModelDto findByIdentifier(String identifier) {
        return modelMapper.map(modelRepository.findByIdentifier(identifier), ModelDto.class);
    }

    @Transactional
    @Override
    public void deleteByIdentifier(String identifier) {
        modelRepository.deleteByIdentifier(identifier);
    }

    @Override
    public ModelDto update(ModelDto modelDto) {
        Model existingModel = modelRepository.findByIdentifier(modelDto.getIdentifier());
        if (existingModel == null) {
            modelDto.setMessage("Model with identifier - " + modelDto.getIdentifier() + "not found");
            modelDto.setSuccess(false);
            return modelDto;
        }
        modelMapper.map(modelDto, existingModel);
        modelRepository.save(existingModel);
        return modelDto;
    }

    @Override
    @Transactional
    public ModelDto toggleStatus(String identifier, boolean status) {
        ModelDto response = new ModelDto();
        Model model = modelRepository.findByIdentifier(identifier);
        if (model == null) {
            response.setSuccess(false);
            response.setMessage("Model not found");
            return response;
        }
        model.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

}