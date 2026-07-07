package com.ust.pos.category.impl;

import com.ust.pos.CommonService;
import com.ust.pos.category.service.CategoryService;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Category;
import com.ust.pos.model.CategoryRepository;
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
public class CategoryServiceImpl extends CommonService implements CategoryService {

    private static final String CATEGORY_WITH_IDENTIFIER = "Category with identifier - ";

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        String identifier = categoryDto.getIdentifier();
        Category existingCategory = categoryRepository.findByIdentifier(identifier);
        if (existingCategory != null) {
            if (existingCategory.getDeleted()) {
                categoryDto.setMessage("Category with identifier " + identifier + " was previously deleted. " +
                        "Please contact backend team to restore."
                );
                categoryDto.setSuccess(false);
                return categoryDto;
            }
            categoryDto.setMessage("Category with identifier - " + identifier + " already exists");
            categoryDto.setSuccess(false);
            return categoryDto;
        }
        Category category = modelMapper.map(categoryDto, Category.class);
        setAuditFields(category, true);
        categoryRepository.save(category);
        return categoryDto;
    }


    @Override
    public CategoryDto update(CategoryDto dto) {

        String identifier = dto.getIdentifier();
        Category existing = categoryRepository.findByIdentifier(identifier);

        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        if (existing.getDeleted()) {
            dto.setSuccess(false);
            dto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        existing.setSuperCategory(
                (dto.getSuperCategory() == null || dto.getSuperCategory().trim().isEmpty())
                        ? null : dto.getSuperCategory()
        );

        setAuditFields(existing, false);

        Category saved = categoryRepository.save(existing);

        CategoryDto response = modelMapper.map(saved, CategoryDto.class);
        response.setSuccess(true);
        response.setMessage("Category updated successfully");

        return response;
    }


    @Transactional
    @Override
    public void deleteByIdentifier(String identifier) {

        if (categoryRepository.existsBySuperCategory(identifier)) {
            throw new IllegalStateException("Cannot delete category. It is used as a super category.");
        }

        Category category = categoryRepository.findByIdentifier(identifier);

        if (category != null) {
            softDelete(category);
            setAuditFields(category, false);
            categoryRepository.save(category);
        }
    }

    @Override
    public CategoryDto findByIdentifier(String identifier) {
        Category category = categoryRepository.findByIdentifier(identifier);
        if (category == null) {
            throw new ResourceNotFoundException("Category with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(category, CategoryDto.class);
    }


    @Override
    public WsDto<CategoryDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> page = categoryRepository.findByDeletedFalse(pageable);

        WsDto<CategoryDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
    @Override
    public WsDto<CategoryDto> findAll(Specification<Category> example, Pageable pageable) {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        Page<Category> page = categoryRepository.findAll(example, pageable);

        WsDto<CategoryDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public List<CategoryDto> findChildCategories() {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(
                categoryRepository.findBySuperCategoryIsNotNullAndDeletedFalse(),
                listType
        );
    }

    @Override
    public CategoryDto toggleStatus(String identifier) {

        Category category = categoryRepository.findByIdentifier(identifier);

        if (category == null) {
            CategoryDto dto = new CategoryDto();
            dto.setSuccess(false);
            dto.setMessage(CATEGORY_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        category.setStatus(!category.getStatus());
        setAuditFields(category, false);

        categoryRepository.save(category);

        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> findIfTrue() {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(
                categoryRepository.findByStatusIsTrueAndDeletedFalse(),
                listType
        );
    }

    @Override
    public List<CategoryDto> findBySuperCategoryNotNull() {

        Type listType = new TypeToken<List<CategoryDto>>() {
        }.getType();
        return modelMapper.map(
                categoryRepository.findByStatusIsTrueAndSuperCategoryIsNotNullAndDeletedFalse(),
                listType
        );
    }
}
