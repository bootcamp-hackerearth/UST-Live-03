package com.ust.pos.stock.service.impl;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
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
@Transactional
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private static final String STOCK_WITH_IDENTIFIER = "Stock with identifier - ";

    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    @Override
    public StockDto findByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        if (stock == null) {
            throw new ResourceNotFoundException(STOCK_WITH_IDENTIFIER + identifier + " not found");
        }
        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public StockDto save(StockDto stockDto) {
        String identifier = stockDto.getProduct() + "_" + stockDto.getWarehouse();
        stockDto.setIdentifier(identifier);

        Stock existingStock = stockRepository.findByIdentifier(identifier);

        if (existingStock != null) {
            if (Boolean.TRUE.equals(existingStock.getIsDeleted())) {
                stockDto.setMessage("Stock for Product '" + stockDto.getProduct() + "' in Warehouse '" + stockDto.getWarehouse() + "' was deleted. Contact admin for further support or try with a different identifier.");
            } else {
                stockDto.setMessage("Stock already exists for Product '" + stockDto.getProduct() + "' in Warehouse '" + stockDto.getWarehouse() + "'");
            }
            stockDto.setSuccess(false);
            return stockDto;
        }

        Stock stock = modelMapper.map(stockDto, Stock.class);
        stock.setIsDeleted(false);
        stockRepository.save(stock);
        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);

        if (existingStock == null) {
            stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }

        modelMapper.map(stockDto, existingStock);
        stockRepository.save(existingStock);
        return stockDto;
    }

    @Override
    public StockDto delete(String identifier) {
        StockDto stockDto = new StockDto();
        Stock stock = stockRepository.findByIdentifier(identifier);

        if (stock == null) {
            stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }

        stock.setIsDeleted(true);
        stock.setStatus(false);
        stockRepository.save(stock);
        stockDto.setSuccess(true);
        stockDto.setMessage("Stock deleted successfully");
        return stockDto;
    }

    @Override
    public PaginatedResponseDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> stockPage = stockRepository.findByIsDeleted(false, pageable);
        List<StockDto> items = modelMapper.map(stockPage.getContent(), listType);
        PaginatedResponseDto<StockDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(stockPage.getTotalElements());
        response.setTotalPages(stockPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());
        return response;
    }

    @Override
    public List<StockDto> findAllActive() {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        return modelMapper.map(stockRepository.findByStatusAndIsDeleted(true, false), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        stock.setStatus(status);
        stockRepository.save(stock);
    }

    @Override
    public PaginatedResponseDto<StockDto> findAll(Specification<Stock> example, Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> page = stockRepository.findAll(example, pageable);

        PaginatedResponseDto<StockDto> paginatedResponseDto = new PaginatedResponseDto<>();
        paginatedResponseDto.setItems(modelMapper.map(page.getContent(), listType));
        paginatedResponseDto.setTotalRecords(page.getTotalElements());
        paginatedResponseDto.setTotalPages(page.getTotalPages());
        paginatedResponseDto.setSizePerPage(pageable.getPageSize());
        paginatedResponseDto.setPage(pageable.getPageNumber());

        return paginatedResponseDto;
    }
}