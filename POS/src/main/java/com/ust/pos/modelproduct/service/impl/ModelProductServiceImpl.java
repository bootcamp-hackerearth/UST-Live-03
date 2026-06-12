package com.ust.pos.modelproduct.service.impl;

import com.ust.pos.dto.ModelProductDto;
import com.ust.pos.model.ModelProduct;
import com.ust.pos.model.ModelProductRepository;
import com.ust.pos.modelproduct.service.ModelProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ModelProductServiceImpl implements ModelProductService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ModelProductRepository modelProductRepository;

    @Override
    public ModelProductDto save(ModelProductDto modelProductDto) {
        String identifier = modelProductDto.getIdentifier();
        ModelProduct existingmodel = modelProductRepository.findByIdentifier(identifier);
        if (existingmodel != null) {
            modelProductDto.setMessage("Model - " + identifier + " already exists");
            modelProductDto.setSuccess(false);
            return modelProductDto;
        }
        ModelProduct modelProduct = modelMapper.map(modelProductDto, ModelProduct.class);
        modelProductRepository.save(modelProduct);
        return modelProductDto;
    }

    @Override
    public ModelProductDto update(ModelProductDto modelProductDto) {
        String identifier = modelProductDto.getIdentifier();
        ModelProduct existingmodel = modelProductRepository.findByIdentifier(identifier);
        if (existingmodel == null) {
            modelProductDto.setMessage("Model - " + identifier + " not found");
            modelProductDto.setSuccess(false);
            return modelProductDto;
        }
        ModelProduct modelProduct = modelMapper.map(modelProductDto, ModelProduct.class);
        modelProductRepository.save(modelProduct);
        return modelProductDto;
    }

    @Override
    public ModelProductDto findByIdentifier(String identifier) {
        return modelMapper.map(modelProductRepository.findByIdentifier(identifier), ModelProductDto.class);
    }

    @Override
    public List<ModelProductDto> findAll() {
        Type listType = new TypeToken<List<ModelProductDto>>() {
        }.getType();
        return modelMapper.map(modelProductRepository.findAll(), listType);
    }

    @Override
    public List<ModelProductDto> findAll(Pageable pageable) {
        Type listtype = new TypeToken<List<ModelProductDto>>() {
        }.getType();
        Page<ModelProduct> modelProductPage = modelProductRepository.findAll(pageable);
        return modelMapper.map(modelProductPage.getContent(), listtype);
    }

    @Override
    public void delete(String identifier) {
        modelProductRepository.deleteByIdentifier(identifier);
    }


    @Override
    public void toggleStatus(String identifier) {
        ModelProduct modelProduct = modelProductRepository.findByIdentifier(identifier);
        modelProduct.setStatus(!modelProduct.isStatus());
        modelProductRepository.save(modelProduct);
    }
}
