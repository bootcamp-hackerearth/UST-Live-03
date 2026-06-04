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
    public ModelDto findByIdentifier(String identifier) {
        return modelMapper.map(
                modelRepository.findByIdentifier(identifier),
                ModelDto.class
        );
    }

    @Override
    public ModelDto save(ModelDto modelDto) {

        Model existing = modelRepository.findByIdentifier(modelDto.getIdentifier());
        if (existing != null) {
            modelDto.setSuccess(false);
            modelDto.setMessage("Model already exists : " + modelDto.getIdentifier());
            return modelDto;
        }

        Model model = modelMapper.map(modelDto, Model.class);
        modelRepository.save(model);
        return modelDto;
    }

    @Override
    public ModelDto update(ModelDto modelDto) {

        Model existing = modelRepository.findByIdentifier(modelDto.getIdentifier());
        if (existing == null) {
            modelDto.setSuccess(false);
            modelDto.setMessage("Model not found : " + modelDto.getIdentifier());
            return modelDto;
        }

        modelMapper.map(modelDto, existing);
        modelRepository.save(existing);
        return modelDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        modelRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        Page<Model> customerPage = modelRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }

    @Override
    public ModelDto toggleStatus(String identifier) {
        Model model = modelRepository.findByIdentifier(identifier);
        model.setStatus(!model.isStatus());
        modelRepository.save(model);
        return modelMapper.map(model, ModelDto.class);
    }

    @Override
    public List<ModelDto> findIfTrue() {
        Type listType = new TypeToken<List<ModelDto>>() {

        }.getType();
        return modelMapper.map(modelRepository.findByStatusIsTrue(), listType);
    }
}