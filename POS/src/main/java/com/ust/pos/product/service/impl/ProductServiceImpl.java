package com.ust.pos.product.service.impl;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDto findByIdentifier(String identifier) {

        Product product = productRepository.findByIdentifier(identifier);

        if (product == null) {
            return null;
        }

        return modelMapper.map(product, ProductDto.class);
    }

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

        String identifier = productDto.getIdentifier();
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
    public void delete(String identifier) {

        productRepository.deleteByIdentifier(identifier);
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {

        Page<Product> productPage = productRepository.findAll(pageable);
        WsDto<ProductDto> paginationResponseDto = new WsDto<>();

        List<ProductDto> productDtos = productPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();

        paginationResponseDto.setContent(productDtos);
        paginationResponseDto.setPage(productPage.getNumber());
        paginationResponseDto.setSizePerPage(productPage.getSize());
        paginationResponseDto.setTotalPages(productPage.getTotalPages());
        paginationResponseDto.setTotalRecords(productPage.getTotalElements());

        return paginationResponseDto;
    }

    @Override
    public void toggleStatus(String identifier) {

        Product product = productRepository.findByIdentifier(identifier);

        if (product != null) {
            product.setStatus(!product.isStatus());
            productRepository.save(product);
        }
    }
}


