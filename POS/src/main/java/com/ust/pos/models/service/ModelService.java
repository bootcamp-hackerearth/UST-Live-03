package com.ust.pos.models.service;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.model.Model;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ModelService {

    ModelDto save(ModelDto modelDto);

    ModelDto update(ModelDto modelDto);

    ModelDto delete(String identifier);

    PaginatedResponseDto<ModelDto> findAll(Pageable pageable);

    ModelDto findByIdentifier(String identifier);

    List<ModelDto> findAllActive();

    void changeStatus(String identifier, boolean status);

    PaginatedResponseDto<ModelDto> findAll(Specification<Model> example, Pageable pageable);
}