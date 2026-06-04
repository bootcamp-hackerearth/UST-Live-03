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
import java.util.Optional;

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
        Optional<ModelProduct> optionalModelProduct = modelProductRepository.findById(modelProductDto.getId());
        if (optionalModelProduct.isEmpty()) {
            modelProductDto.setSuccess(false);
            return modelProductDto;
        } else {
            ModelProduct existingmodel = optionalModelProduct.get();
            if (!identifier.equals(existingmodel.getIdentifier()) && modelProductRepository.findByIdentifier(identifier) != null) {
                modelProductDto.setSuccess(false);
                modelProductDto.setMessage("Model Already Exists");
                return modelProductDto;
            } else {
                modelMapper.map(modelProductDto, existingmodel);
                modelProductRepository.save(existingmodel);
            }
            return modelProductDto;

        }
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
        Type listType = new TypeToken<List<ModelProductDto>>() {
        }.getType();
        Page<ModelProduct> modelProductPage = modelProductRepository.findAll(pageable);
        return modelMapper.map(modelProductPage.getContent(), listType);
    }

    @Override
    public void delete(String identifier) {
        modelProductRepository.deleteByIdentifier(identifier);
    }

    @Override
    public void toggleStatus(String identifier) {
        ModelProduct racks = modelProductRepository.findByIdentifier(identifier);
        if (racks != null) {
            // ✅ toggle status
            racks.setStatus(!racks.isStatus());
            modelProductRepository.save(racks);
        }

    }
}
