package com.ust.pos.models.service.impl;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.models.service.ModelService;
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
@Transactional
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private static final String MODEL_WITH_IDENTIFIER = "Model with identifier - ";

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    @Override
    public ModelDto save(ModelDto modelDto) {
        String identifier = modelDto.getIdentifier();
        Model existingModel = modelRepository.findByIdentifier(identifier);

        if (existingModel != null) {
            if (Boolean.TRUE.equals(existingModel.getIsDeleted())) {
                modelDto.setMessage(MODEL_WITH_IDENTIFIER + identifier + " was deleted. Contact admin for further support or try with a different identifier.");
            } else {
                modelDto.setMessage(MODEL_WITH_IDENTIFIER + identifier + " already exists");
            }
            modelDto.setSuccess(false);
            return modelDto;
        }

        Model model = modelMapper.map(modelDto, Model.class);
        model.setIsDeleted(false);
        modelRepository.save(model);
        modelDto.setSuccess(true);
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
        modelRepository.save(existingModel);
        return modelDto;
    }

    @Override
    public ModelDto delete(String identifier) {
        ModelDto modelDto = new ModelDto();
        Model model = modelRepository.findByIdentifier(identifier);

        if (model == null) {
            modelDto.setMessage(MODEL_WITH_IDENTIFIER + identifier + " not found");
            modelDto.setSuccess(false);
            return modelDto;
        }

        model.setIsDeleted(true);
        model.setStatus(false);
        modelRepository.save(model);
        modelDto.setSuccess(true);
        modelDto.setMessage("Model deleted successfully");
        return modelDto;
    }

    @Override
    public PaginatedResponseDto<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        Page<Model> modelPage = modelRepository.findByIsDeleted(false, pageable);
        List<ModelDto> items = modelMapper.map(modelPage.getContent(), listType);
        PaginatedResponseDto<ModelDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(modelPage.getTotalElements());
        response.setTotalPages(modelPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());
        return response;
    }

    @Override
    public ModelDto findByIdentifier(String identifier) {
        Model model = modelRepository.findByIdentifier(identifier);
        if (model == null) {
            throw new ResourceNotFoundException("Model with identifier " + identifier + " not found");
        }
        return modelMapper.map(model, ModelDto.class);
    }

    @Override
    public List<ModelDto> findAllActive() {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        return modelMapper.map(modelRepository.findByStatusAndIsDeleted(true, false), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {
        Model model = modelRepository.findByIdentifier(identifier);
        model.setStatus(status);
        modelRepository.save(model);
    }
}