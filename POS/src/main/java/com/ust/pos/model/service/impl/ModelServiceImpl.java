package com.ust.pos.model.service.impl;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.model.service.ModelService;
import com.ust.pos.modell.Model;
import com.ust.pos.modell.ModelRepository;
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
public class ModelServiceImpl implements ModelService {

    public static final RuntimeException MODEL_NOT_FOUND = new RuntimeException("model not found");

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ModelRepository modelRepository;

    @Override
    public ModelDto findByIdentifier(String identifier) {
        return modelMapper.map(
                modelRepository.findByIdentifier(identifier),
                ModelDto.class
        );
    }

    @Override
    public ModelDto save(ModelDto modelDto) {
        String identifier = modelDto.getIdentifier();
        Model existingModel = modelRepository.findByIdentifier(identifier);

        if (existingModel != null) {
            modelDto.setMessage("Model with identifier - " + identifier + " already exists");
            modelDto.setSuccess(false);
            return modelDto;
        }
        Model model = modelMapper.map(modelDto, Model.class);
        modelRepository.save(model);
        return modelDto;
    }

    @Override
    public ModelDto update(ModelDto modelDto) {
        String identifier = modelDto.getIdentifier();
        Model existingModel = modelRepository.findByIdentifier(identifier);

        if (existingModel == null) {
            modelDto.setMessage("Model with identifier - " + identifier + " not found");
            modelDto.setSuccess(false);
            return modelDto;
        }
        modelMapper.map(modelDto, existingModel);
        modelRepository.save(existingModel);
        return modelDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        modelRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        Page<Model> modelPage = modelRepository.findAll(pageable);
        return modelMapper.map(modelPage.getContent(), listType);
    }

    @Override
    public void toggleStatus(String identifier) {
        Model model = modelRepository.findByIdentifier(identifier);
        if (model == null) {
            throw MODEL_NOT_FOUND;
        }
        model.setStatus(!model.isStatus());
        modelRepository.save(model);
    }

}