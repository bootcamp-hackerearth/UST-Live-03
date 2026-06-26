package com.ust.pos.product.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                            ? " Product with identifier - " + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : " Product with identifier - " + identifier
                            + " already exists."
            );
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        setCreatedDetails(product);
        productRepository.save(product);
        return productDto;
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct == null) {
            productDto.setMessage("Product with identifier - " + identifier + " not found");
            productDto.setSuccess(false);
            return productDto;
        }
        modelMapper.map(productDto, existingProduct);
        setModifiedDetails(existingProduct);
        productRepository.save(existingProduct);
        return productDto;
    }

    @Transactional
    public void delete(String identifier) {

        Product product = productRepository.findByIdentifier(identifier);
        setModifiedDetails(product);
        softDelete(product);
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {

        Page<Product> productPage = productRepository.findByIsDeletedFalse(pageable);
        WsDto<ProductDto> wsDto = new WsDto<>();

        List<ProductDto> productDtos = productPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();

        wsDto.setContent(productDtos);
        wsDto.setPage(productPage.getNumber());
        wsDto.setSizePerPage(productPage.getSize());
        wsDto.setTotalPages(productPage.getTotalPages());
        wsDto.setTotalRecords(productPage.getTotalElements());

        return wsDto;
    }
}