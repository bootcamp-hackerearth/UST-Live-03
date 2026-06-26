package com.ust.pos.models.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.models.service.ModelService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ModelServiceImpl extends CommonService implements ModelService {

    private final ModelMapper modelMapper;

    private final ModelRepository modelRepository;

    public ModelServiceImpl(ModelMapper modelMapper, ModelRepository modelRepository) {
        this.modelMapper = modelMapper;
        this.modelRepository = modelRepository;
    }

    @Override
    public ModelDto findByIdentifier(String identifier) {
        return modelMapper.map(modelRepository.findByIdentifier(identifier), ModelDto.class);
    }

    @Override
    public ModelDto save(ModelDto modelDto) {
        Model existing = modelRepository.findByIdentifier(modelDto.getIdentifier());
        if (existing != null) {
            if(existing.isDeleted()) {
                modelDto.setMessage("Model with identifier - " + modelDto.getIdentifier() + "has been soft deleted.(Rollback by changing status");
                modelDto.setSuccess(false);
                return modelDto;
            }
            modelDto.setSuccess(false);
            modelDto.setMessage("Model already exists : " + modelDto.getIdentifier());
            return modelDto;
        }

        Model model = modelMapper.map(modelDto, Model.class);
        setAuditFields(model, true);
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
        setAuditFields(existing,false);
        modelRepository.save(existing);
        return modelDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Model model = modelRepository.findByIdentifier(identifier);
        softDelete(model);
        setAuditFields(model,false);
        modelRepository.save(model);    }

    @Override
    public WsDto<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        Page<Model> userPage = modelRepository.findByDeletedFalse(pageable);

        WsDto<ModelDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPages(userPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());

        return userWsDto;
    }

    @Override
    public ModelDto changeToggleStatus(String identifier, boolean status) {
        Model model = modelRepository.findByIdentifier(identifier);
        if (model != null) {
            model.setStatus(status);
            modelRepository.save(model);
        }
        return modelMapper.map(model, ModelDto.class);
    }

    @Override
    public List<ModelDto> findActiveStatus() {
        List<Model> allModels = modelRepository.findAll();
        List<Model> activeModels = allModels.stream().filter(Model::isStatus).toList();

        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        return modelMapper.map(activeModels, listType);
    }
}
