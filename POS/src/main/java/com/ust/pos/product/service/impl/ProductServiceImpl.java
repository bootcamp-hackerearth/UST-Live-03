package com.ust.pos.product.service.impl;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct != null) {
            productDto.setMessage("Product with identifier - " + identifier + " already exists");
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        productRepository.save(product);
        return productDto;
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        String identifier =productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct == null) {
           productDto.setMessage("Product with identifier - " + identifier + " not found");
           productDto.setSuccess(false);
            return productDto;
        }
        modelMapper.map(productDto, existingProduct);
        productRepository.save(existingProduct);
        return productDto;
    }

    @Override
    @Transactional
    public boolean delete(String identifier) {
        productRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Override
    public PageDto<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> productPage = productRepository.findAll(pageable);
        PageDto<ProductDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(productPage.getContent(), listType));
        pageDto.setTotalRecords(productPage.getTotalElements());
        pageDto.setTotalPages(productPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.findByIdentifier(identifier), ProductDto.class);
    }

    @Override
    public void toggleStatus(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product != null) {
            boolean currentStatus = Boolean.TRUE.equals(product.getStatus());
            product.setStatus(!currentStatus);

            productRepository.save(product);
        }
    }

    @Override
    public List<ProductDto> findActiveProducts() {
        Type listType = new TypeToken<List<ProductDto>>() {}.getType();
        return modelMapper.map(productRepository.findByStatusTrue(),listType);
    }
}
