package com.ust.pos.models.service;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Model;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ModelService {

    ModelDto save(ModelDto modelDto);

    PaginationResponseDto<ModelDto> findAll(Pageable pageable);

    ModelDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    ModelDto update(ModelDto modelDto);

    ModelDto toggleStatus(String identifier, boolean status);

    PaginationResponseDto<ModelDto> findAll(Specification<Model> example, Pageable pageable);
}