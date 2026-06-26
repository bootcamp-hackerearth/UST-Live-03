package com.ust.pos.product.service.impl;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.CategoryRepository;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if(existingProduct != null)
        {
            productDto.setMessage("Product with identifier - "+ identifier + " already exists");
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        product.setDeleted(false);
        productRepository.save(product);
        return productDto;
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if(existingProduct == null)
        {
            productDto.setMessage("Product with identifier - "+identifier+" is not found");
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        productRepository.save(product);
        return productDto;
    }

    @Override
    public void delete(String identifier) {
        Product product = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if(product != null)
        {
            product.setDeleted(true);
            productRepository.save(product);
        }
    }

    @Override
    public List<ProductDto> findAll() {
        Type listOfType = new TypeToken<List<ProductDto>>(){}.getType();
        return modelMapper.map(productRepository.findByDeletedFalse(), listOfType);
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<ProductDto>>(){}.getType();
        Page<Product> productPage = productRepository.findByDeletedFalse(pageable);

        WsDto<ProductDto> productDtoWsDto = new WsDto<>();
        productDtoWsDto.setDtoList(modelMapper.map(productPage.getContent(), listOfType));
        productDtoWsDto.setTotalRecords(productPage.getTotalElements());
        productDtoWsDto.setTotalPage(productPage.getTotalPages());
        productDtoWsDto.setSizePerPage(pageable.getPageSize());
        productDtoWsDto.setPage(pageable.getPageNumber());

        return productDtoWsDto;
    }

    @Override
    public Page<ProductDto> findAll(String search, Pageable pageable) {
        Page<Product> rolePage;
        if(search != null && !search.trim().isEmpty())
        {
            rolePage = productRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse(search, pageable);
        }
        else
        {
            rolePage = productRepository.findByDeletedFalse(pageable);
        }
        return rolePage.map(product -> modelMapper.map(product, ProductDto.class));
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.findByIdentifierAndDeletedFalse(identifier), ProductDto.class);
    }

    @Override
    public List<ProductDto> listOfCategories() {
        Type listOfType = new TypeToken<List<ProductDto>>(){}.getType();
        return modelMapper.map(categoryRepository.findBySuperCategoryIsNotAndDeletedFalse(""), listOfType);
    }
}
