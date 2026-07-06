package com.ust.pos.models.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginationResponseDto;
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
public class ModelServiceImpl extends BaseService implements ModelService {

    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    public ModelServiceImpl(ModelRepository modelRepository, ModelMapper modelMapper) {
        this.modelRepository = modelRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PaginationResponseDto<ModelDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();

        Page<Model> modelPage = modelRepository.findByIsDeletedFalse(pageable);
        List<ModelDto> modelDtoList = modelMapper.map(modelPage.getContent(), listType);

        PaginationResponseDto<ModelDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(modelDtoList);
        paginationResponseDto.setPage(modelPage.getNumber());
        paginationResponseDto.setSizePerPage(modelPage.getSize());
        paginationResponseDto.setTotalPages(modelPage.getTotalPages());
        paginationResponseDto.setTotalRecords(modelPage.getTotalElements());

        return paginationResponseDto;
    }

    @Override
    public PaginationResponseDto<ModelDto> findAll(Specification<Model> example, Pageable pageable) {

        Type listType = new TypeToken<List<ModelDto>>() {
        }.getType();
        Page<Model> page = modelRepository.findAll(example, pageable);

        PaginationResponseDto<ModelDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationResponseDto.setTotalRecords(page.getTotalElements());
        paginationResponseDto.setTotalPages(page.getTotalPages());
        paginationResponseDto.setSizePerPage(pageable.getPageSize());
        paginationResponseDto.setPage(pageable.getPageNumber());

        return paginationResponseDto;
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
        String identifier = modelDto.getIdentifier().trim();
        Model model = modelRepository.findByIdentifier(identifier);
        if (model == null) {
            model = modelMapper.map(modelDto, Model.class);
            setCreatedDetails(model);
            modelRepository.save(model);
            modelDto.setSuccess(true);
            modelDto.setMessage("Successfully added the model");
        } else if (isSoftDeleted(model)) {
            modelDto.setMessage(
                    getDeletedMessage("Model", identifier)
            );
            modelDto.setSuccess(false);
        } else {
            modelDto.setMessage("Model " + identifier + " already exists");
            modelDto.setSuccess(false);
        }
        return modelDto;
    }

    @Override
    public ModelDto update(ModelDto modelDto) {

        String identifier = modelDto.getIdentifier();

        Model existingModel =
                modelRepository.findByIdentifier(identifier);

        if (existingModel == null) {
            modelDto.setSuccess(false);
            modelDto.setMessage("Model does not exist");
            return modelDto;
        }

        if (isSoftDeleted(existingModel)) {
            modelDto.setSuccess(false);
            modelDto.setMessage(
                    getDeletedMessage("Model", identifier)
            );
            return modelDto;
        }

        modelMapper.map(modelDto, existingModel);

        setModifiedDetails(existingModel);

        modelRepository.save(existingModel);

        modelDto.setSuccess(true);
        modelDto.setMessage("Model updated successfully");

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

        setModifiedDetails(model);
        model.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Model model = modelRepository.findByIdentifier(identifier);
        softDelete(model);
        setModifiedDetails(model);
        modelRepository.save(model);
    }
}
