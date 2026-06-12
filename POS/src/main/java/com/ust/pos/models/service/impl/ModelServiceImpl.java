package com.ust.pos.models.service.impl;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
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
        return modelMapper.map(modelRepository.findByIdentifier(identifier), ModelDto.class);
    }

    @Override
    public ModelDto save(ModelDto dto) {

        Model existing = modelRepository.findByIdentifier(dto.getIdentifier());
        if (existing != null) {
            dto.setSuccess(false);
            dto.setMessage("Model already exists : " + dto.getIdentifier());
            return dto;
        }

        Model model = modelMapper.map(dto, Model.class);
        modelRepository.save(model);
        return dto;
    }

    @Override
    public ModelDto update(ModelDto dto) {

        Model existing = modelRepository.findByIdentifier(dto.getIdentifier());
        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage("Model not found : " + dto.getIdentifier());
            return dto;
        }

        modelMapper.map(dto, existing);
        modelRepository.save(existing);
        return dto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        modelRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        Page<Model> modelPage = modelRepository.findAll(pageable);

        WsDto<ModelDto> modelWsDto = new WsDto<>();
        modelWsDto.setDtoList(modelMapper.map(modelPage.getContent(), listType));
        modelWsDto.setTotalRecords(modelPage.getTotalElements());
        modelWsDto.setTotalPages(modelPage.getTotalPages());
        modelWsDto.setSizePerPage(pageable.getPageSize());
        modelWsDto.setPage(pageable.getPageNumber());

        return modelWsDto;
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