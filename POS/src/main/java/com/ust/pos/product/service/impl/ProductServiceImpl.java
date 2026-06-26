package com.ust.pos.product.service.impl;

import com.ust.pos.common.CommonService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ProductServiceImpl extends CommonService implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct != null) {
            if (!existingProduct.isDeleted()) {
                productDto.setMessage("Product with identifier - " + identifier + " already exists");
                productDto.setSuccess(false);
                return productDto;
            }
            productDto.setMessage("Product with identifier - " + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        product.setCategories(productDto.getCategories() == null ? List.of() : productDto.getCategories());
        setAuditFields(product, true);
        productRepository.save(product);
        productDto.setSuccess(true);
        productDto.setMessage("Product created successfully");
        return productDto;
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {
        Page<Product> productPage = productRepository.findByDeletedFalse(pageable);
        Type type = new TypeToken<List<ProductDto>>() {
        }.getType();
        WsDto<ProductDto> productWsDto = new WsDto<>();
        productWsDto.setDtoList(modelMapper.map(productPage.getContent(), type));
        productWsDto.setTotalRecords(productPage.getTotalElements());
        productWsDto.setTotalPages(productPage.getTotalPages());
        productWsDto.setSizePerPage(pageable.getPageSize());
        productWsDto.setPage(pageable.getPageNumber());
        return productWsDto;
    }

    @Override
    public List<ProductDto> findAllActive() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findAllByStatusAndDeletedFalse(true), listType);
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product == null) return null;
        ProductDto dto = modelMapper.map(product, ProductDto.class);
        dto.setCategories(product.getCategories());
        return dto;
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        Product existing = productRepository.findByIdentifier(productDto.getIdentifier());
        if (existing == null) {
            productDto.setSuccess(false);
            productDto.setMessage("Product not found");
            return productDto;
        }
        existing.setBrand(productDto.getBrand());
        existing.setModel(productDto.getModel());
        existing.setName(productDto.getName());
        existing.setCategories(productDto.getCategories() == null ? List.of() : productDto.getCategories());
        setAuditFields(existing, false);
        productRepository.save(existing);
        productDto.setSuccess(true);
        return productDto;
    }

    @Override
    public ProductDto toggleStatus(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        product.setStatus(!product.isStatus());
        setAuditFields(product, false);
        productRepository.save(product);
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public boolean delete(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product == null) return false;
        softDelete(product);
        setAuditFields(product, false);
        productRepository.save(product);
        return true;
    }
}
