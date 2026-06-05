package com.ust.pos.models.service.impl;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.models.service.ModelService;
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
    private ModelRepository modelRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ModelDto save(ModelDto modelDto) {
        Model existingModel = modelRepository.findByIdentifier(modelDto.getIdentifier());
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
    public List<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        if(pageable == null){
            return modelMapper.map(modelRepository.findAll(),listType);
        }
        Page<Model> modelPage = modelRepository.findAll(pageable);
        return modelMapper.map(modelPage.getContent(), listType);
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
        Model model = modelRepository.findByIdentifier(identifier);
        if (model != null) {
            model.setStatus(!model.isStatus());
            modelRepository.save(model);
        }
        return modelMapper.map(model, ModelDto.class);
    }
}