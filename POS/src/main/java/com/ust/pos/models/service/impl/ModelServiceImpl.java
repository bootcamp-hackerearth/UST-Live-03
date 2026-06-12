package com.ust.pos.models.service.impl;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PageDto;
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
    public ModelDto save(ModelDto modelDto) {
        String identifier =modelDto.getIdentifier();
        Model existingModel =modelRepository.findByIdentifier(identifier);
        if (existingModel  != null) {
            modelDto .setMessage("Model with identifier - " + identifier + " already exists");
            modelDto .setSuccess(false);
            return modelDto ;
        }
        Model model= modelMapper.map(modelDto, Model.class);
        modelRepository.save(model);
        return modelDto ;

    }

    @Override
    public ModelDto update(ModelDto modelDto) {
        String identifier = modelDto.getIdentifier();
        Model existingModel = modelRepository.findByIdentifier(identifier);
        if (existingModel== null) {
            modelDto.setMessage("Model with identifier - " + identifier + " not found");
            modelDto.setSuccess(false);
            return modelDto;
        }
        modelMapper.map(modelDto,existingModel);
        modelRepository.save(existingModel);
        return modelDto;
    }

    @Override
    @Transactional
    public boolean delete(String identifier) {
        modelRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public PageDto<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        Page<Model>modelPage = modelRepository.findAll(pageable);
        PageDto<ModelDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(modelPage.getContent(), listType));
        pageDto.setTotalRecords(modelPage.getTotalElements());
        pageDto.setTotalPages(modelPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public ModelDto findByIdentifier(String identifier) {
        return modelMapper.map(modelRepository.findByIdentifier(identifier), ModelDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Model model = modelRepository.findByIdentifier(identifier);
        if (model != null) {
            boolean currentStatus = Boolean.TRUE.equals(model.getStatus());
            model.setStatus(!currentStatus);

            modelRepository.save(model);
        }
    }

    @Override
    public List<ModelDto> findActiveModels() {
        Type listType = new TypeToken<List<ModelDto>>() {}.getType();
        return modelMapper.map(modelRepository.findByStatusTrue(),listType);
    }
    }

