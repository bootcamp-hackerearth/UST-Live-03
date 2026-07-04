package com.ust.pos.product.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
import com.ust.pos.model.*;
import com.ust.pos.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ProductServiceImpl extends BaseService implements ProductService {

    private static final String VALIDATION_MESSAGE = "Product with identifier - ";
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, StockRepository stockRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {

        Product product = productRepository.findByIdentifier(identifier);

        if (product == null) {
            throw new ResourseNotFoundException("Data cannot found");
        }

        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public ProductDto save(ProductDto productDto) {

        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);

        if (existingProduct != null) {
            productDto.setMessage(
                    existingProduct.isDeleted()
                            ? VALIDATION_MESSAGE + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : VALIDATION_MESSAGE + identifier
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
            productDto.setMessage(VALIDATION_MESSAGE + identifier + " not found");
            productDto.setSuccess(false);
            return productDto;
        }

        modelMapper.map(productDto, existingProduct);
        setModifiedDetails(existingProduct);
        productRepository.save(existingProduct);

        return productDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        Product product = productRepository.findByIdentifier(identifier);
        setModifiedDetails(product);
        softDelete(product);
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {

        Page<Product> productPage = productRepository.findByIsDeletedFalse(pageable);
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

    @Override
    public void toggleStatus(String identifier) {

        Product product = productRepository.findByIdentifier(identifier);

        if (product != null) {
            product.setStatus(!product.isStatus());
            productRepository.save(product);
        }
    }

    @Override
    public List<ProductDto> findActiveShelf() {

        List<Product> productPage = productRepository.findByStatus(true);
        return productPage.stream().map(product -> modelMapper.map(product, ProductDto.class)).toList();
    }

    @Override
    public WsDto<ProductDto> findAllWithQuantity(Pageable pageable) {

        Page<Product> productPage = productRepository.findByIsDeletedFalse(pageable);
        WsDto<ProductDto> paginationResponseDto = new WsDto<>();

        List<ProductDto> productDtos = productPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();

        for (ProductDto product : productDtos) {
            Stock stock = stockRepository.findByProduct(product.getIdentifier());
            if (stock != null) {
                product.setStockQuantity(stock.getQuantity());
            }
        }

        paginationResponseDto.setContent(productDtos);
        paginationResponseDto.setPage(productPage.getNumber());
        paginationResponseDto.setSizePerPage(productPage.getSize());
        paginationResponseDto.setTotalPages(productPage.getTotalPages());
        paginationResponseDto.setTotalRecords(productPage.getTotalElements());

        return paginationResponseDto;
    }
}


