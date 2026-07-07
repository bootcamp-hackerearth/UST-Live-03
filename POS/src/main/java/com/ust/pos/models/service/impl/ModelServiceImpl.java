package com.ust.pos.models.service.impl;

import com.ust.pos.CommonService;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Model;
import com.ust.pos.model.ModelRepository;
import com.ust.pos.models.service.ModelService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ModelServiceImpl extends CommonService implements ModelService {

    private static final String MODEL_WITH_IDENTIFIER = "Model with identifier - ";

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    public ModelServiceImpl(ModelRepository modelRepository,
                            ModelMapper modelMapper) {
        this.modelRepository = modelRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ModelDto findByIdentifier(String identifier) {
        Model model =modelRepository.findByIdentifier(identifier);
        if(model==null){
            throw new ResourceNotFoundException("Data Cannot Found");
        }
        return modelMapper.map(model, ModelDto.class);
    }

    @Override
    public ModelDto save(ModelDto dto) {

        if (dto == null || dto.getIdentifier() == null) {
            throw new IllegalArgumentException("Identifier is required");
        }

        String identifier = dto.getIdentifier();
        Model existing = modelRepository.findByIdentifier(identifier);

        if (existing != null) {
            if (!existing.getDeleted()) {
                dto.setSuccess(false);
                dto.setMessage(MODEL_WITH_IDENTIFIER + identifier + " already exists");
                return dto;
            }

            dto.setSuccess(false);
            dto.setMessage(MODEL_WITH_IDENTIFIER + identifier + " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        Model model = modelMapper.map(dto, Model.class);
        setAuditFields(model, true);

        Model saved = modelRepository.save(model);

        ModelDto response = modelMapper.map(saved, ModelDto.class);
        response.setSuccess(true);
        response.setMessage("Model created successfully");

        return response;
    }

    @Override
    public ModelDto update(ModelDto dto) {

        String identifier = dto.getIdentifier();
        Model existing = modelRepository.findByIdentifier(identifier);

        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage(MODEL_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        if (existing.getDeleted()) {
            dto.setSuccess(false);
            dto.setMessage(MODEL_WITH_IDENTIFIER + identifier + " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        modelMapper.map(dto, existing);
        setAuditFields(existing, false);

        modelRepository.save(existing);

        dto.setSuccess(true);
        dto.setMessage("Model updated successfully");

        return dto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        Model model = modelRepository.findByIdentifier(identifier);

        if (model != null) {
            softDelete(model);
            setAuditFields(model, false);
            modelRepository.save(model);
        }
    }

    @Override
    public WsDto<ModelDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        Page<Model> page = modelRepository.findByDeletedFalse(pageable);

        WsDto<ModelDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public WsDto<ModelDto> findAll(Specification<Model> example, Pageable pageable) {

        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        Page<Model> page = modelRepository.findAll(example, pageable);

        WsDto<ModelDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public ModelDto toggleStatus(String identifier) {

        Model model = modelRepository.findByIdentifier(identifier);

        if (model == null) {
            ModelDto dto = new ModelDto();
            dto.setSuccess(false);
            dto.setMessage(MODEL_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        model.setStatus(!model.getStatus());
        setAuditFields(model, false);

        modelRepository.save(model);

        return modelMapper.map(model, ModelDto.class);
    }

    @Override
    public List<ModelDto> findIfTrue() {

        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();

        return modelMapper.map(
                modelRepository.findByStatusIsTrueAndDeletedFalse(),
                listType
        );
    }
}