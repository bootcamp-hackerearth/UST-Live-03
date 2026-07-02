package com.ust.pos.models.service;
import com.ust.pos.dto.ModelDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.model.Model;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Transactional
public interface ModelService {
  ModelDto save(ModelDto modelDto);

  ModelDto update(ModelDto modelDto);

  boolean delete(String identifier);

  PageDto<ModelDto>findAll(Pageable pageable);

  ModelDto findByIdentifier(String identifier);

  void toggleStatus(String identifier);

  List<ModelDto> findActiveModels();

  PageDto<ModelDto> findAll(Specification<Model> spec, Pageable pageable, String keyword);
}
