package com.ust.pos.models.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.models.service.ModelService;
import jakarta.persistence.EntityNotFoundException;
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

    public static final String MODEL_WITH_IDENTIFIER = "Model with identifier - ";
    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    public ModelServiceImpl(
            ModelRepository modelRepository,
            ModelMapper modelMapper
    ) {
        this.modelRepository = modelRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ModelDto save(ModelDto modelDto) {
        Model existingModel = modelRepository.findByIdentifier(modelDto.getIdentifier());
        if (existingModel != null) {
            if (existingModel.isDeleted()) {
                modelDto.setMessage(MODEL_WITH_IDENTIFIER + modelDto.getIdentifier() + " has been soft deleted." );
                modelDto.setSuccess(false);
                return modelDto;
            }
            modelDto.setMessage(MODEL_WITH_IDENTIFIER + modelDto.getIdentifier() + " already exists");
            modelDto.setSuccess(false);
            return modelDto;
        }
        Model model = modelMapper.map(modelDto, Model.class);
        setCreatedDetails(model);
        modelRepository.save(model);
        return modelDto;
    }

    @Override
    public PaginationResponseDto<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {}.getType();
        PaginationResponseDto<ModelDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<Model> models = modelRepository.findAll();
            response.setDtoList(modelMapper.map(models, listType));
            response.setTotalRecords(models.size());
            response.setTotalPages(1);
            response.setSizePerPage(models.size());
            response.setPage(0);
        } else {
            Page<Model> modelPage = modelRepository.findByDeletedFalse(pageable);
            response.setDtoList(modelMapper.map(modelPage.getContent(), listType));
            response.setTotalRecords(modelPage.getTotalElements());
            response.setTotalPages(modelPage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
    }

    @Override
    public ModelDto findByIdentifier(String identifier) {
        return modelMapper.map(modelRepository.findByIdentifier(identifier), ModelDto.class);
    }

    @Transactional
    @Override
    public void deleteByIdentifier(String identifier) {
        Model model = modelRepository.findByIdentifier(identifier);
        if (model == null) {
            throw new EntityNotFoundException("Model not found");
        }
        softDelete(model);
        setModifiedDetails(model);
        modelRepository.save(model);
    }

    @Override
    public ModelDto update(ModelDto modelDto) {
        Model existingModel = modelRepository.findByIdentifier(modelDto.getIdentifier());
        if (existingModel == null) {
            modelDto.setMessage(MODEL_WITH_IDENTIFIER + modelDto.getIdentifier() + "not found");
            modelDto.setSuccess(false);
            return modelDto;
        }
        modelMapper.map(modelDto, existingModel);
        setModifiedDetails(existingModel);
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
        setModifiedDetails(model);
        modelRepository.save(model);
        response = modelMapper.map(model, ModelDto.class);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }
}