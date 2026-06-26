package com.ust.pos.product.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.PriceRepository;
import com.ust.pos.model.Product;
import com.ust.pos.model.ProductRepository;
import com.ust.pos.product.service.ProductService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl extends BaseService implements ProductService {

    public static final String PRODUCT_NOT_FOUND = "Product not found";

    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository,PriceRepository priceRepository,
                              ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.priceRepository = priceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto save(ProductDto productDto) {

        String identifier = productDto.getIdentifier().trim();
        Product existing = productRepository.findByIdentifier(identifier);

        if (existing != null && !Boolean.TRUE.equals(existing.getDeleted())) {
            productDto.setSuccess(false);
            productDto.setMessage("Product already exists");
            return productDto;
        }

        Product product = modelMapper.map(productDto, Product.class);
        product.setIdentifier(identifier);
        setCreatedDetails(product);
        Product saved = productRepository.save(product);
        ProductDto response = modelMapper.map(saved, ProductDto.class);
        response.setSuccess(true);
        response.setMessage("Product saved successfully");
        return response;
    }

    @Override
    public ProductDto update(ProductDto productDto) {

        Product product = productRepository.findByIdentifier(productDto.getIdentifier());

        if (product == null || Boolean.TRUE.equals(product.getDeleted())) {
            ProductDto dto = new ProductDto();
            dto.setSuccess(false);
            dto.setMessage(PRODUCT_NOT_FOUND);
            return dto;
        }

        modelMapper.map(productDto, product);
        setModifiedDetails(product);
        Product saved = productRepository.save(product);
        ProductDto dto = modelMapper.map(saved, ProductDto.class);
        dto.setSuccess(true);
        dto.setMessage("Product updated successfully");
        return dto;
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {

        Product product = productRepository.findByIdentifier(identifier);

        if (product == null || Boolean.TRUE.equals(product.getDeleted())) {
            ProductDto dto = new ProductDto();
            dto.setSuccess(false);
            dto.setMessage(PRODUCT_NOT_FOUND);
            return dto;
        }
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ProductDto>>() {}.getType();
        Page<Product> page = productRepository.findByDeletedFalse(pageable);
        WsDto<ProductDto> ws = new WsDto<>();
        ws.setDtoList(modelMapper.map(page.getContent(), listType));
        ws.setTotalRecords(page.getTotalElements());
        ws.setTotalPages(page.getTotalPages());
        ws.setSizePerPage(pageable.getPageSize());
        ws.setPage(pageable.getPageNumber());
        return ws;
    }

    @Override
    public void delete(String identifier) {

        Product product = productRepository.findByIdentifier(identifier);
        if (product == null) return;
        product.setDeleted(true);
        setModifiedDetails(product);
        productRepository.save(product);
    }

    @Override
    public List<ProductDto> findActiveProducts() {

        List<Product> list = productRepository.findByStatusTrueAndDeletedFalse();
        Type type = new TypeToken<List<ProductDto>>() {}.getType();
        return modelMapper.map(list, type);
    }

    @Override
    public ProductDto toggleStatus(String identifier) {

        Product product = productRepository.findByIdentifier(identifier);

        if (product == null || Boolean.TRUE.equals(product.getDeleted())) {
            ProductDto dto = new ProductDto();
            dto.setSuccess(false);
            dto.setMessage(PRODUCT_NOT_FOUND);
            return dto;
        }

        product.setStatus(!Boolean.TRUE.equals(product.getStatus()));
        setModifiedDetails(product);
        Product saved = productRepository.save(product);
        ProductDto dto = modelMapper.map(saved, ProductDto.class);
        dto.setSuccess(true);
        dto.setMessage("Status updated successfully");
        return dto;
    }

    @Override
    public List<ProductDto> searchProduct(String query) {

        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        List<Product> products = productRepository.searchActiveProducts(query);
        return products.stream()
                .filter(p -> priceRepository.countActivePriceTypes(p.getIdentifier()) == 3)
                .map(p -> modelMapper.map(p, ProductDto.class))
                .toList();
    }
}