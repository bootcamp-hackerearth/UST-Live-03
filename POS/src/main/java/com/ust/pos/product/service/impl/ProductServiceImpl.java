package com.ust.pos.product.service.impl;

import com.ust.pos.dto.PaginationResponseDto;
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
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PaginationResponseDto<ProductDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();

        if (pageable == null) {

            List<ProductDto> productDtoList =
                    modelMapper.map(
                            productRepository.findAll(),
                            listType
                    );

            PaginationResponseDto<ProductDto> response =
                    new PaginationResponseDto<>();

            response.setDtoList(productDtoList);
            response.setTotalRecords(productDtoList.size());

            return response;
        }

        Page<Product> productPage =
                productRepository.findAll(pageable);

        List<ProductDto> productDtoList =
                modelMapper.map(
                        productPage.getContent(),
                        listType
                );

        PaginationResponseDto<ProductDto> paginationResponseDto =
                new PaginationResponseDto<>();

        paginationResponseDto.setDtoList(productDtoList);
        paginationResponseDto.setPage(productPage.getNumber());
        paginationResponseDto.setSizePerPage(productPage.getSize());
        paginationResponseDto.setTotalPages(productPage.getTotalPages());
        paginationResponseDto.setTotalRecords(
                productPage.getTotalElements()
        );

        return paginationResponseDto;
    }

    @Override
    public List<ProductDto> findByStatusTrue() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findByStatusTrue(), listType);
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product product = productRepository.findByIdentifier(identifier);
        if (product == null) {
            productRepository.save(modelMapper.map(productDto, Product.class));
            productDto.setMessage("Successfully added the product");
            productDto.setSuccess(true);
        } else {
            productDto.setMessage("Product " + identifier + " already exists");
            productDto.setSuccess(false);
        }
        return productDto;
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        return modelMapper.map(productRepository.findByIdentifier(identifier), ProductDto.class);
    }

    @Transactional
    @Override
    public ProductDto update(ProductDto productDto) {
        Optional<Product> productOptional = productRepository.findById(productDto.getId());

        if (productOptional.isEmpty()) {
            productDto.setMessage("Email - " + productDto.getIdentifier() + " not found");
            productDto.setSuccess(false);
            return productDto;
        }

        Product existingProduct = productOptional.get();
        String productname = productDto.getIdentifier();

        boolean isProductnameChanged = !productname.equalsIgnoreCase(existingProduct.getIdentifier());

        if (isProductnameChanged && productRepository.findByIdentifier(productname) != null) {
            productDto.setMessage("Product " + productname + " already exists");
            productDto.setSuccess(false);
            return productDto;
        }

        productDto.setMessage("Product successfully edited");
        productDto.setSuccess(true);
        modelMapper.map(productDto, existingProduct);
        productRepository.save(existingProduct);

        return productDto;
    }

    @Override
    @Transactional
    public ProductDto updateStatus(String identifier, boolean status) {
        ProductDto response = new ProductDto();

        Product product = productRepository.findByIdentifier(identifier);
        if (product == null) {
            response.setSuccess(false);
            response.setMessage("Product not found");
            return response;
        }

        product.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        productRepository.deleteByIdentifier(identifier);
    }
}
