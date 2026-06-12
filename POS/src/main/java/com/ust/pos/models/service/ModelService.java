package com.ust.pos.models.service;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PageDto;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ModelService {
  ModelDto save(ModelDto modelDto);

  ModelDto update(ModelDto modelDto);

  boolean delete(String identifier);

  PageDto<ModelDto >findAll(Pageable pageable);

   ModelDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<ModelDto> findActiveModels();
}
