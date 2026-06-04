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
public class ModelServiceImpl implements ModelService {

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        if (pageable == null) {
            return modelMapper.map(modelRepository.findAll(), listType);
        }
        Page<Model> modelPage = modelRepository.findAll(pageable);
        return modelMapper.map(modelPage.getContent(), listType);
    }

    @Override
    public List<ModelDto> findByStatusTrue() {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        return modelMapper.map(modelRepository.findByStatusTrue(), listType);
    }

    @Override
    public ModelDto findByIdentifier(String identifier) {
        return modelMapper.map(modelRepository.findByIdentifier(identifier), ModelDto.class);
    }

    @Override
    public ModelDto save(ModelDto modelDto) {
        String identifier = modelDto.getIdentifier();
        Model model = modelRepository.findByIdentifier(identifier);
        if (model == null) {
            modelRepository.save(modelMapper.map(modelDto, Model.class));
            modelDto.setSuccess(true);
            modelDto.setMessage("Successfully added the model");
        } else {
            modelDto.setMessage("Model " + identifier + " already exists");
            modelDto.setSuccess(false);
        }
        return modelDto;
    }

    @Override
    public ModelDto update(ModelDto modelDto) {
        String identifier = modelDto.getIdentifier();
        Model existingModel = modelRepository.findByIdentifier(identifier);
        if (existingModel == null) {
            modelDto.setSuccess(false);
            modelDto.setMessage("Model does not exist");
        } else {
            modelMapper.map(modelDto, existingModel);
            modelRepository.save(existingModel);
            modelDto.setSuccess(true);
            modelDto.setMessage("Model updated successfully");
        }
        return modelDto;
    }

    @Override
    @Transactional
    public ModelDto updateStatus(String identifier, boolean status) {
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

    @Override
    @Transactional
    public void delete(String identifier) {
        modelRepository.deleteByIdentifier(identifier);
    }
}
