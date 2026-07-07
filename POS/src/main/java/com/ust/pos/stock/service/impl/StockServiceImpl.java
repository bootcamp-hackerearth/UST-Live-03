package com.ust.pos.stock.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Stock;
import com.ust.pos.model.StockRepository;
import com.ust.pos.stock.service.StockService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StockServiceImpl extends BaseService implements StockService {

    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository, ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    public PaginationResponseDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();

        Page<Stock> stockPage = stockRepository.findByIsDeletedFalse(pageable);
        List<StockDto> stockDtoList = modelMapper.map(stockPage.getContent(), listType);

        PaginationResponseDto<StockDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(stockDtoList);
        paginationResponseDto.setPage(stockPage.getNumber());
        paginationResponseDto.setSizePerPage(stockPage.getSize());
        paginationResponseDto.setTotalPages(stockPage.getTotalPages());
        paginationResponseDto.setTotalRecords(stockPage.getTotalElements());

        return paginationResponseDto;
    }

    @Override
    public PaginationResponseDto<StockDto> findAll(Specification<Stock> example, Pageable pageable) {

        Type listType = new TypeToken<List<StockDto>>() {
        }.getType();
        Page<Stock> page = stockRepository.findAll(example, pageable);

        PaginationResponseDto<StockDto> paginationResponseDto = new PaginationResponseDto<>();
        paginationResponseDto.setDtoList(modelMapper.map(page.getContent(), listType));
        paginationResponseDto.setTotalRecords(page.getTotalElements());
        paginationResponseDto.setTotalPages(page.getTotalPages());
        paginationResponseDto.setSizePerPage(pageable.getPageSize());
        paginationResponseDto.setPage(pageable.getPageNumber());

        return paginationResponseDto;
    }

    @Override
    public StockDto save(StockDto stockDto) {

        String identifier =
                stockDto.getProduct() + stockDto.getWarehouse();

        Stock existingStock =
                stockRepository.findByIdentifier(identifier);

        if (existingStock != null) {

            if (existingStock.isDeleted()) {
                stockDto.setMessage(
                        "Stock " + identifier +
                                " has been deleted. Please contact the administrator."
                );
                stockDto.setSuccess(false);
                return stockDto;
            }

            stockDto.setMessage(
                    "Stock already exists for this Product and Warehouse"
            );
            stockDto.setSuccess(false);
            return stockDto;
        }

        stockDto.setIdentifier(identifier);

        Stock stock =
                modelMapper.map(stockDto, Stock.class);

        setCreatedDetails(stock);

        stockRepository.save(stock);

        stockDto.setMessage("Successfully added the stock");
        stockDto.setSuccess(true);

        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        Optional<Stock> stockOptional =
                stockRepository.findById(stockDto.getId());

        if (stockOptional.isEmpty()) {
            stockDto.setMessage(
                    "Stock not found with id: " + stockDto.getId()
            );
            stockDto.setSuccess(false);
            return stockDto;
        }

        Stock existingStock = stockOptional.get();

        if (existingStock.isDeleted()) {
            stockDto.setMessage(
                    "Stock " + existingStock.getIdentifier()
                            + " has been deleted. Please contact the administrator."
            );
            stockDto.setSuccess(false);
            return stockDto;
        }

        modelMapper.map(stockDto, existingStock);

        setModifiedDetails(existingStock);

        Stock updatedStock =
                stockRepository.save(existingStock);

        StockDto response =
                modelMapper.map(updatedStock, StockDto.class);

        response.setMessage("Stock updated successfully");
        response.setSuccess(true);

        return response;
    }

    public StockDto findByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);

        return modelMapper.map(stock, StockDto.class);
    }

    @Override
    public void delete(String identifier) {
        Stock stock = stockRepository.findByIdentifier(identifier);
        softDelete(stock);
        setModifiedDetails(stock);
        stockRepository.save(stock);
    }
}
