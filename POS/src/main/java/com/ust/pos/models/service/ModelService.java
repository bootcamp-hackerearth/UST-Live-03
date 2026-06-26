package com.ust.pos.models.service;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PaginatedResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelService {

    ModelDto save(ModelDto modelDto);

    ModelDto update(ModelDto modelDto);

    ModelDto delete(String identifier);

    PaginatedResponseDto<ModelDto> findAll(Pageable pageable);

    ModelDto findByIdentifier(String identifier);

    List<ModelDto> findAllActive();

    void changeStatus(String identifier, boolean status);
}