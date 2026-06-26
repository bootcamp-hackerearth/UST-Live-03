package com.ust.pos.product.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.model.StockRepository;
import com.ust.pos.product.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl extends BaseService implements ProductService {

    public static final String PRODUCT_WITH_IDENTIFIER = "Product with identifier - ";
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    @Override
    public ProductDto findByIdentifier(String identifier) {
        Product product = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if (product == null) {
            ProductDto productDto = new ProductDto();
            productDto.setSuccess(false);
            productDto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " not found");
            return productDto;
        }
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifier(identifier);
        if (existingProduct != null) {
            if (Boolean.TRUE.equals(existingProduct.getDeleted())) {
                productDto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + "  was deleted and cannot be created again.");
                productDto.setSuccess(false);
                return productDto;
            }
            productDto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " already exists");
            productDto.setSuccess(false);
            return productDto;
        }
        Product product = modelMapper.map(productDto, Product.class);
        setCreatedDetails(product);
        productRepository.save(product);
        productDto.setSuccess(true);
        productDto.setMessage("Product created successfully");
        return productDto;
    }

    @Override
    public ProductDto update(ProductDto productDto) {
        String identifier = productDto.getIdentifier();
        Product existingProduct = productRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingProduct == null) {
            productDto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " not found");
            productDto.setSuccess(false);
            return productDto;
        }
        modelMapper.map(productDto, existingProduct);
        setModifiedDetails(existingProduct);
        productRepository.save(existingProduct);
        productDto.setSuccess(true);
        productDto.setMessage("Product updated successfully");
        return productDto;
    }

    @Override
    public void delete(String identifier) {
        Product product = productRepository.findByIdentifierAndDeletedFalse(identifier);
        product.setDeleted(true);
        setModifiedDetails(product);
        productRepository.save(product);
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> productPage = productRepository.findByDeletedFalse(pageable);
        WsDto<ProductDto> productWsDto = new WsDto<>();
        productWsDto.setDtoList(modelMapper.map(productPage.getContent(), listType));
        productWsDto.setTotalRecords(productPage.getTotalElements());
        productWsDto.setTotalPage(productPage.getTotalPages());
        productWsDto.setSizePerPage(pageable.getPageSize());
        productWsDto.setPage(pageable.getPageNumber());
        return productWsDto;
    }

    @Override
    public ProductDto toggleStatus(String identifier) {
        Product product = productRepository.findByIdentifierAndDeletedFalse(identifier);
        product.setStatus(!product.isStatus());
        productRepository.save(product);
        return modelMapper.map(product, ProductDto.class);
    }

    public List<ProductDto> findActiveProducts() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        return modelMapper.map(
                productRepository.findByStatusTrueAndDeletedFalse(),
                listType
        );
    }

    public List<ProductDto> findActiveProductsWithStock() {
        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        List<String> inStockProductIdentifiers = stockRepository.findProductIdentifiersWithStock();
        if (inStockProductIdentifiers.isEmpty()) {
            return List.of();
        }
        List<Product> products = productRepository
                .findByStatusTrueAndDeletedFalseAndIdentifierIn(inStockProductIdentifiers);
        return modelMapper.map(products, listType);
    }

}
