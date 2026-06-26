package com.ust.pos.models.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.models.service.ModelService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ModelServiceImpl extends CommonService implements ModelService {

    public static final String MODEL_WITH_IDENTIFIER = "Model with identifier - ";
    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    public ModelServiceImpl(ModelRepository modelRepository, ModelMapper modelMapper) {
        this.modelRepository = modelRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ModelDto save(ModelDto modelDto) {
        String identifier = modelDto.getIdentifier();
        Model existingModel = modelRepository.findByIdentifier(identifier);
        if (existingModel != null) {
            if (Boolean.TRUE.equals(existingModel.getDeleted())) {
                modelDto.setMessage(MODEL_WITH_IDENTIFIER + identifier + " has been soft deleted. Restore it by changing status.");
                modelDto.setSuccess(false);
                return modelDto;
            }
            modelDto.setMessage(MODEL_WITH_IDENTIFIER + identifier + " already exists");
            modelDto.setSuccess(false);
            return modelDto;
        }
        Model model = modelMapper.map(modelDto, Model.class);
        model.setDeleted(false);
        model.setStatus(true);
        setAuditFields(model, true);
        modelRepository.save(model);
        return modelDto;
    }

    @Override
    public ModelDto update(ModelDto modelDto) {
        String identifier = modelDto.getIdentifier();
        Model existingModel = modelRepository.findByIdentifier(identifier);
        if (existingModel == null) {
            modelDto.setMessage(MODEL_WITH_IDENTIFIER + identifier + " not found");
            modelDto.setSuccess(false);
            return modelDto;
        }
        modelMapper.map(modelDto, existingModel);
        setAuditFields(existingModel, false);
        modelRepository.save(existingModel);
        return modelDto;
    }

    @Override
    public boolean delete(String identifier) {
        Model model = modelRepository.findByIdentifier(identifier);
        if (model == null) {
            return false;
        }
        softDelete(model);
        setAuditFields(model, false);
        modelRepository.save(model);
        return true;
    }

    @Override
    public PageDto<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {}.getType();
        Page<Model> modelPage = modelRepository.findByDeletedFalse(pageable);
        PageDto<ModelDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(modelPage.getContent(), listType));
        pageDto.setTotalRecords(modelPage.getTotalElements());
        pageDto.setTotalPages(modelPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public ModelDto findByIdentifier(String identifier) {
        Model model = modelRepository.findByIdentifier(identifier);
        if (model == null) {
            throw new ResourceNotFoundException("Model with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(model, ModelDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Model model = modelRepository.findByIdentifier(identifier);
        if (model != null) {
            boolean currentStatus = Boolean.TRUE.equals(model.getStatus());
            model.setStatus(!currentStatus);
            modelRepository.save(model);
        }
    }

    @Override
    public List<ModelDto> findActiveModels() {
        Type listType = new TypeToken<List<ModelDto>>() {}.getType();
        return modelMapper.map(modelRepository.findByStatusTrue(), listType);
    }
}