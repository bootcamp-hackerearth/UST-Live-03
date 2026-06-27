package com.ust.pos.model.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.service.ModelService;
import com.ust.pos.modell.Model;
import com.ust.pos.modell.ModelRepository;
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
public class ModelServiceImpl extends BaseService implements ModelService {

    public static final RuntimeException MODEL_NOT_FOUND = new RuntimeException("model not found");
    public static final String MODEL_WITH_IDENTIFIER = "Model with identifier - ";
    private final ModelMapper modelMapper;
    private final ModelRepository modelRepository;

    @Override
    public ModelDto findByIdentifier(String identifier) {
        return modelMapper.map(modelRepository.findByIdentifierAndDeletedFalse(identifier), ModelDto.class
        );
    }

    @Override
    public ModelDto save(ModelDto modelDto) {
        String identifier = modelDto.getIdentifier();
        Model existingModel = modelRepository.findByIdentifier(identifier);

        if (existingModel != null) {
            if (Boolean.TRUE.equals(existingModel.getDeleted())) {
                modelDto.setMessage(MODEL_WITH_IDENTIFIER + identifier + " was deleted and cannot be created again.");
                modelDto.setSuccess(false);
                return modelDto;
            }
            modelDto.setMessage(MODEL_WITH_IDENTIFIER + identifier + " already exists");
            modelDto.setSuccess(false);
            return modelDto;
        }
        Model model = modelMapper.map(modelDto, Model.class);
        setCreatedDetails(model);
        modelRepository.save(model);
        return modelDto;
    }

    @Override
    public ModelDto update(ModelDto modelDto) {
        String identifier = modelDto.getIdentifier();
        Model existingModel = modelRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingModel == null) {
            modelDto.setMessage(
                    MODEL_WITH_IDENTIFIER + identifier + " not found");
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
    public void delete(String identifier) {
        Model model = modelRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(model);
        setModifiedDetails(model);
        modelRepository.save(model);
    }

    @Override
    public WsDto<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        Page<Model> modelPage = modelRepository.findAllByDeletedFalse(pageable);
        WsDto<ModelDto> modelWsDto = new WsDto<>();
        modelWsDto.setDtoList(modelMapper.map(modelPage.getContent(), listType));
        modelWsDto.setTotalRecords(modelPage.getTotalElements());
        modelWsDto.setTotalPage(modelPage.getTotalPages());
        modelWsDto.setSizePerPage(pageable.getPageSize());
        modelWsDto.setPage(pageable.getPageNumber());
        return modelWsDto;
    }

    @Override
    public List<ModelDto> findAllActive() {
        return modelRepository.findByStatusTrueAndDeletedFalse()
                .stream()
                .map(model -> modelMapper.map(model, ModelDto.class))
                .toList();
    }

    @Override
    @Transactional
    public ModelDto toggleStatus(String identifier) {
        Model model = modelRepository.findByIdentifierAndDeletedFalse(identifier);
        if (model == null) {
            throw new IllegalArgumentException("model not found with identifier: " + identifier);
        }
        Boolean currentStatus = model.getStatus();
        model.setStatus(currentStatus == null ? Boolean.TRUE : !currentStatus);
        setModifiedDetails(model);
        Model saved = modelRepository.save(model);
        return modelMapper.map(saved, ModelDto.class);
    }

}