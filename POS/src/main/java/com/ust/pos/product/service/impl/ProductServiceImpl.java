package com.ust.pos.product.service.impl;

import com.ust.pos.CommonService;
import com.ust.pos.dto.ProductDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
public class ProductServiceImpl extends CommonService implements ProductService {

    private static final String PRODUCT_WITH_IDENTIFIER = "Product with identifier - ";

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto findByIdentifier(String identifier) {
        Product product =productRepository.findByIdentifier(identifier);
        if(product==null){
            throw new ResourceNotFoundException("Data Cannot Found");
        }
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public ProductDto save(ProductDto dto) {

        if (dto == null || dto.getIdentifier() == null) {
            throw new IllegalArgumentException("Identifier is required");
        }

        String identifier = dto.getIdentifier();
        Product existing = productRepository.findByIdentifier(identifier);

        if (existing != null) {
            if (!existing.getDeleted()) {
                dto.setSuccess(false);
                dto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " already exists");
                return dto;
            }

            dto.setSuccess(false);
            dto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        Product product = modelMapper.map(dto, Product.class);
        setAuditFields(product, true);

        Product saved = productRepository.save(product);

        ProductDto response = modelMapper.map(saved, ProductDto.class);
        response.setSuccess(true);
        response.setMessage("Product created successfully");

        return response;
    }

    @Override
    public ProductDto update(ProductDto dto) {

        String identifier = dto.getIdentifier();
        Product existing = productRepository.findByIdentifier(identifier);

        if (existing == null) {
            dto.setSuccess(false);
            dto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        if (existing.getDeleted()) {
            dto.setSuccess(false);
            dto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier +
                    " was previously deleted. Please contact backend team to restore.");
            return dto;
        }

        modelMapper.map(dto, existing);
        setAuditFields(existing, false);

        productRepository.save(existing);

        dto.setSuccess(true);
        dto.setMessage("Product updated successfully");

        return dto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {

        Product product = productRepository.findByIdentifier(identifier);

        if (product != null) {
            softDelete(product);
            setAuditFields(product, false);
            productRepository.save(product);
        }
    }

    @Override
    public WsDto<ProductDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> page = productRepository.findByDeletedFalse(pageable);

        WsDto<ProductDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public WsDto<ProductDto> findAll(Specification<Product> example, Pageable pageable) {

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();
        Page<Product> page = productRepository.findAll(example, pageable);

        WsDto<ProductDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public ProductDto toggleStatus(String identifier) {

        Product product = productRepository.findByIdentifier(identifier);

        if (product == null) {
            ProductDto dto = new ProductDto();
            dto.setSuccess(false);
            dto.setMessage(PRODUCT_WITH_IDENTIFIER + identifier + " not found");
            return dto;
        }

        product.setStatus(!product.getStatus());
        setAuditFields(product, false);

        productRepository.save(product);

        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public List<ProductDto> findIfTrue() {

        Type listType = new TypeToken<List<ProductDto>>() {
        }.getType();

        return modelMapper.map(
                productRepository.findByStatusIsTrueAndDeletedFalse(),
                listType
        );
    }
}