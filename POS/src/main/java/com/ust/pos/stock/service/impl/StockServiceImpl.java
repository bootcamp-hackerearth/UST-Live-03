package com.ust.pos.stock.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
import com.ust.pos.stock.service.StockService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl extends BaseService implements StockService {

    private final ModelMapper modelMapper;
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;

    @Override
    public StockDto save(StockDto stockDto) {
        Product product = productRepository.findByIdentifier(stockDto.getProduct());
        stockDto.setIdentifier(product.getName() + "_" + stockDto.getWarehouse());
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);
        if (existingStock != null) {
            if (Boolean.TRUE.equals(existingStock.getDeleted())) {
                stockDto.setMessage("Stock identifier - " + identifier + " not available");
                stockDto.setSuccess(false);
                return stockDto;
            }
            stockDto.setMessage("Stock with identifier - " + identifier + " already exists");
            stockDto.setSuccess(false);
            return stockDto;
        }
        if (stockDto.getQuantity() > stockDto.getMinimumStock()) {
            stockDto.setStockStatus("Available");
        } else if (stockDto.getQuantity() > 0 && stockDto.getQuantity() <= stockDto.getMinimumStock()) {
            stockDto.setStockStatus("Low Stock");
        } else {
            stockDto.setStockStatus("Out of Stock");
        }
        Stock stock = modelMapper.map(stockDto, Stock.class);
        setCreatedDetails(stock);
        setModifiedDetails(stock);
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);
        if (existingStock == null) {
            stockDto.setMessage("product with identifier - " + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }
        if (stockDto.getQuantity() > stockDto.getMinimumStock()) {
            stockDto.setStockStatus("Available");
        } else if (stockDto.getQuantity() > 0 && stockDto.getQuantity() <= stockDto.getMinimumStock()) {
            stockDto.setStockStatus("Low Stock");
        } else {
            stockDto.setStockStatus("Out of Stock");
        }
        modelMapper.map(stockDto, existingStock);
        setModifiedDetails(existingStock);
        stockRepository.save(existingStock);
        return stockDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Stock stock = stockRepository.findByIdentifierAndDeletedFalse(identifier);
        setModifiedDetails(stock);
        softDelete(stock);
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findAllByDeletedFalse(pageable);

        WsDto<StockDto> stockWsDto = new WsDto<>();
        stockWsDto.setDtoList(modelMapper.map(stockPage.getContent(), listType));
        stockWsDto.setTotalRecords(stockPage.getTotalElements());
        stockWsDto.setTotalPages(stockPage.getTotalPages());
        stockWsDto.setSizePerPage(pageable.getPageSize());
        stockWsDto.setPage(pageable.getPageNumber());
        return stockWsDto;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {
        return modelMapper.map(stockRepository.findByIdentifierAndDeletedFalse(identifier), StockDto.class);
    }

    @Override
    public WsDto<StockDto> findAll(Specification<Stock> example, Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> page = stockRepository.findAll(example, pageable);

        WsDto<StockDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}
