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

        String identifier = modelDto.getIdentifier();
        Model existingModel = modelRepository.findByIdentifier(identifier);
        if (existingModel != null) {
            modelDto.setMessage("Model with identifier - " + identifier + " already exists");
            modelDto.setSuccess(false);
            return modelDto;
        }
        Model model = modelMapper.map(modelDto, Model.class);
        modelRepository.save(model);
        modelDto.setSuccess(true);
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
    public ModelDto findByIdentifier(String identifier) {
        return modelMapper.map(modelRepository.findByIdentifier(identifier), ModelDto.class);
    }

    @Override
    public List<ModelDto> findAllActive() {

        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        return modelMapper.map(modelRepository.findByStatus(true), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {

        Model model = modelRepository.findByIdentifier(identifier);
        model.setStatus(status);
        modelRepository.save(model);
    }
}
