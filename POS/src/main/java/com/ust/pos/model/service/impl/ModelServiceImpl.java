package com.ust.pos.model.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.service.ModelService;
import com.ust.pos.modell.Model;
import com.ust.pos.modell.ModelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl extends BaseService implements ModelService {

    public static final RuntimeException MODEL_NOT_FOUND = new RuntimeException("model not found");

    private final ModelMapper modelMapper;
    private final ModelRepository modelRepository;

    @Override
    public ModelDto findByIdentifier(String identifier) {
        Model model = modelRepository.findByIdentifierAndDeletedFalse(identifier);
        if (model == null) {
            throw new ResourceNotFoundException("Model with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(model, ModelDto.class);
    }

    @Override
    public ModelDto save(ModelDto modelDto) {
        String identifier = modelDto.getIdentifier();
        Model existingModel = modelRepository.findByIdentifier(identifier);

        if (existingModel != null) {
            if (Boolean.TRUE.equals(existingModel.getDeleted())) {
                modelDto.setMessage("Model with Identifier " + identifier + " already exists (Soft-Deleted)");
                modelDto.setSuccess(false);
                return modelDto;
            }
            modelDto.setMessage("Model with identifier - " + identifier + " already exists");
            modelDto.setSuccess(false);
            return modelDto;
        }
        Model model = modelMapper.map(modelDto, Model.class);

        if (model.getStatus() == null) {
            model.setStatus(true);
        }
        setCreatedDetails(model);
        modelRepository.save(model);
        return modelDto;
    }

    @Override
    public ModelDto update(ModelDto modelDto) {
        String identifier = modelDto.getIdentifier();
        Model existingModel = modelRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingModel == null) {
            modelDto.setMessage("Model with identifier - " + identifier + " not found");
            modelDto.setSuccess(false);
            return modelDto;
        }

        String originalCreatedBy = existingModel.getCreatedBy();
        java.time.LocalDateTime originalCreatedOn = existingModel.getCreatedOn();
        modelMapper.map(modelDto, existingModel);
        existingModel.setCreatedBy(originalCreatedBy);
        existingModel.setCreatedOn(originalCreatedOn);
        setModifiedDetails(existingModel);
        modelRepository.save(existingModel);
        return modelDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Model model = modelRepository.findByIdentifierAndDeletedFalse(identifier);

        if (model != null) {
            softDelete(model);
            setModifiedDetails(model);
            modelRepository.save(model);
        }
    }

    @Override
    public WsDto<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        Page<Model> modelPage = modelRepository.findAllByDeletedFalse(pageable);
        WsDto<ModelDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(modelPage.getContent(), listType));
        wsDto.setTotalRecords(modelPage.getTotalElements());
        wsDto.setTotalPage(modelPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public List<ModelDto> findAllActive() {
        return modelRepository.findByStatusTrueAndDeletedFalse().stream().map(model -> modelMapper.map(model, ModelDto.class)).toList();
    }

    @Override
    @Transactional
    public void toggleStatus(String identifier) {
        Model model = modelRepository.findByIdentifierAndDeletedFalse(identifier);

        if (model == null) {
            throw MODEL_NOT_FOUND;
        }
        model.setStatus(!model.getStatus());
        setModifiedDetails(model);
        modelRepository.save(model);
    }

    @Override
    public WsDto<ModelDto> findAll(Specification<Model> example, Pageable pageable) {

        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        Page<Model> page = modelRepository.findAll(example, pageable);

        WsDto<ModelDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPage(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}

