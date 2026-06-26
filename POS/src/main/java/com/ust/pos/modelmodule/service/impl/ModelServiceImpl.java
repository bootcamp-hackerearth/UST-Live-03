package com.ust.pos.modelmodule.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.modelmodule.service.ModelService;
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
public class ModelServiceImpl extends BaseService implements ModelService {

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    public ModelServiceImpl(ModelRepository modelRepository, ModelMapper modelMapper) {
        this.modelRepository = modelRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ModelDto save(ModelDto modelDto) {
        String identifier = modelDto.getIdentifier().trim();
        Model existingModel = modelRepository.findByIdentifier(identifier);
        if (existingModel != null) {
            if (existingModel.isDeleted()) {
                modelDto.setMessage("Model with identifier " + identifier + " has been soft deleted. (Rollback by changing status)");
                modelDto.setSuccess(false);
                return modelDto;
            }
            modelDto.setMessage("Model with identifier - " + identifier + " already exists");
            modelDto.setSuccess(false);
            return modelDto;
        }
        Model model = modelMapper.map(modelDto, Model.class);
        setCreatedDetails(model);
        modelRepository.save(model);
        modelDto.setSuccess(true);
        modelDto.setMessage("Model created successfully");
        return modelDto;
    }

    @Override
    public WsDto<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        WsDto<ModelDto> wsDto = new WsDto<>();
        if (pageable == null) {
            List<ModelDto> modelDtoList = modelMapper.map(modelRepository.findByDeletedFalse(pageable), listType);
            wsDto.setDtoList(modelDtoList);
            wsDto.setTotalRecords(modelDtoList.size());
            return wsDto;
        }
        Page<Model> modelPage = modelRepository.findByDeletedFalse(pageable);
        wsDto.setDtoList(modelMapper.map(modelPage.getContent(), listType));
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setTotalPages(modelPage.getTotalPages());
        wsDto.setTotalRecords(modelPage.getTotalElements());
        return wsDto;
    }

    @Override
    public ModelDto findByIdentifier(String identifier) {
        return modelMapper.map(
                modelRepository.findByIdentifier(identifier),
                ModelDto.class
        );
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        Model model = modelRepository.findByIdentifier(identifier);
        softDelete(model);
        setModifiedDetails(model);
        modelRepository.save(model);
    }

    @Override
    public ModelDto update(ModelDto modelDto) {
        Model existingModel = modelRepository.findByIdentifier(modelDto.getIdentifier());
        if (existingModel == null) {
            modelDto.setMessage("Model with identifier - " + modelDto.getIdentifier() + " not found");
            modelDto.setSuccess(false);
            return modelDto;
        }
        modelMapper.map(modelDto, existingModel);
        setModifiedDetails(existingModel);
        modelRepository.save(existingModel);
        modelDto.setSuccess(true);
        modelDto.setMessage("Model updated successfully");
        return modelDto;
    }

    @Override
    @Transactional
    public ModelDto toggleStatus(String identifier, boolean status) {
        Model model = modelRepository.findByIdentifier(identifier);
        if (model == null) {
            ModelDto response = new ModelDto();
            response.setSuccess(false);
            response.setMessage("Model not found");
            return response;
        }
        model.setStatus(status);
        setModifiedDetails(model);
        modelRepository.save(model);
        ModelDto response = modelMapper.map(model, ModelDto.class);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

}