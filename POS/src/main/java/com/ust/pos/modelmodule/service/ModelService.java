package com.ust.pos.modelmodule.service;

import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface ModelService {

    ModelDto save(ModelDto modelDto);

    WsDto<ModelDto> findAll(Pageable pageable);

    ModelDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    ModelDto update(ModelDto modelDto);

    ModelDto toggleStatus(String identifier, boolean status);

}