package com.ust.pos.category.service.impl;

import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImplementation implements CategoryService {
    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    public CategoryServiceImplementation(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingcategory = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
        if(existingcategory != null)
        {
            categoryDto.setMessage("Category with identifier - "+ identifier+ " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        category.setDeleted(false);
        categoryRepository.save(category);
        return categoryDto;
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingcategory = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
        if(existingcategory == null)
        {
            categoryDto.setMessage("Category with identifier - "+identifier+" not found");
            return categoryDto;
        }
        modelMapper.map(categoryDto, existingcategory);
        categoryRepository.save(existingcategory);
        return categoryDto;
    }

    @Override
    public void delete(String identifier) {
        Category category = categoryRepository.findByIdentifierAndDeletedFalse(identifier);
        if(category != null)
        {
            category.setDeleted(true);
            categoryRepository.save(category);
        }
    }

    @Override
    public List<CategoryDto> findAll() {
        Type listType = new TypeToken<List<CategoryDto>>(){}.getType();
        return modelMapper.map(categoryRepository.findByDeletedFalse(), listType);
    }

    @Override
    public Page<CategoryDto> findAll(Example<Category> example, Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAll(example, pageable);

        List<Category> filtered = categoryPage.getContent().stream().filter(c -> !Boolean.TRUE.equals(c.getDeleted())).toList();

        Page<Category> filteredPage = new PageImpl<>(filtered, pageable, categoryPage.getTotalElements());
        return filteredPage.map(category -> modelMapper.map(category, CategoryDto.class));
    }

    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CategoryDto>>(){}.getType();
        Page<Category> categoryPage = categoryRepository.findByDeletedFalse(pageable);

        WsDto<CategoryDto> categoryDtoWsDto = new WsDto<>();
        categoryDtoWsDto.setDtoList(modelMapper.map(categoryPage.getContent(), listType));
        categoryDtoWsDto.setTotalRecords(categoryPage.getTotalElements());
        categoryDtoWsDto.setTotalPage(categoryPage.getTotalPages());
        categoryDtoWsDto.setSizePerPage(pageable.getPageSize());
        categoryDtoWsDto.setPage(pageable.getPageNumber());

        return categoryDtoWsDto;
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        return modelMapper.map(categoryRepository.findByIdentifierAndDeletedFalse(identifier), CategoryDto.class);
    }
}
