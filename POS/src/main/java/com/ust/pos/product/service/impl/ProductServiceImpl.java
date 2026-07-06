package com.ust.pos.product.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
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
public class ProductServiceImpl extends BaseService implements ProductService {

    private final ProductRepository productRepository;

    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> productPage = productRepository.findByIsDeletedFalse(pageable);

        WsDto<ProductDto> productWsDto = new WsDto<>();
        productWsDto.setContent(modelMapper.map(productPage.getContent(), listType));
        productWsDto.setTotalRecords(productPage.getTotalElements());
        productWsDto.setTotalPages(productPage.getTotalPages());
        productWsDto.setSizePerPage(pageable.getPageSize());
        productWsDto.setPage(pageable.getPageNumber());

        return productWsDto;
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.findByIdentifier(identifier), ProductDto.class);
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);

        if (existingProduct != null) {
            productDto.setMessage(
                    existingProduct.isDeleted()
                            ? "Product - " + identifier + " already exists but was deleted, Please contact Administrator"
                            : "Product - " + identifier + " already exists"
            );

            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        setCreatedDetails(product);
        productRepository.save(product);
        return productDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        Product product = productRepository.findByIdentifier(identifier);
        setModifiedDetails(product);
        softDelete(product);
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct == null) {
            productDto.setMessage("Product with product - " + identifier + " not found");
            productDto.setSuccess(false);
            return productDto;
        }
        modelMapper.map(productDto, existingProduct);
        setModifiedDetails(existingProduct);
        productRepository.save(existingProduct);
        return productDto;
    }

    @Override
    public void toggleStatus(String identifier) {

        Product product = productRepository
                .findByIdentifier(identifier);

        if (product != null) {
            product.setStatus(!product.isStatus());
            productRepository.save(product);
        }
    }

    @Override
    public WsDto<ProductDto> findAll(Specification<Product> example, Pageable pageable) {

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> page = productRepository.findAll(example, pageable);

        WsDto<ProductDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}
