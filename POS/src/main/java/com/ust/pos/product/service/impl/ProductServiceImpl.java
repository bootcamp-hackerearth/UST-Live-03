package com.ust.pos.product.service.impl;

import com.ust.pos.dto.ProductDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
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
    ProductRepository productRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public ProductDto save(ProductDto productDto) {
        Product product = productRepository.findByIdentifier(productDto.getIdentifier());
        if (product != null) {
            productDto.setSuccess(false);
            productDto.setMessage("This product Already Exist!");
            return productDto;
        }
        Product response = new Product();
        modelMapper.map(productDto, response);
        productRepository.save(response);
        productDto.setMessage("Product Added Successfully");
        return productDto;
    }

    @Override
    public List<ProductDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> productPage = productRepository.findAll(pageable);
        return modelMapper.map(productPage.getContent(), listType);

    }

    @Override
    public ProductDto update(ProductDto productDto) {
        Product product = productRepository.findByIdentifier(productDto.getIdentifier());

        if (product == null) {
            productDto.setSuccess(false);
            productDto.setMessage("Product not found!");
            return productDto;
        }

        if (!productDto.getIdentifier().equalsIgnoreCase(product.getIdentifier()) && productRepository.existsByIdentifier(productDto.getIdentifier())) {
            productDto.setMessage("This product already exist!");
            productDto.setSuccess(false);
            return productDto;
        }
        modelMapper.map(productDto, product);
        productRepository.save(product);
        productDto.setMessage("Product Updated Successfully");
        return productDto;
    }

    @Override
    public ProductDto findById(Long id) {

        return productRepository.findById(id)
                .map(product -> modelMapper.map(product, ProductDto.class))
                .orElse(null);

    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public ProductDto changeProductStatus(String identifier, boolean status) {
        Product product = productRepository.findByIdentifier(identifier);
        if (product != null) {
            product.setStatus(status);
            productRepository.save(product);
        }
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public List<ProductDto> findAllActiveProduct() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(productRepository.findByStatusTrue(), listType);
    }
}