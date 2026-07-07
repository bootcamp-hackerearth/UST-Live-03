package com.ust.pos.stock.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.models.Stock;
import com.ust.pos.models.StockRepository;
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
public class StockServiceImpl extends BaseService implements StockService {

    public static final String STOCK_WITH_IDENTIFIER = "Stock with identifier - ";

    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    @Override
    public StockDto findByIdentifier(String identifier) {
        Stock stock = stockRepository.findByIdentifierAndDeletedFalse(identifier.trim());

        if (stock == null) {
            throw new ResourceNotFoundException("Stock with identifier '" + identifier + "' not found");
        }

        return mapToDto(stock);
    }

    @Override
    public StockDto findById(Long id) {
        StockDto response = new StockDto();
        Stock stock = stockRepository.findByIdAndDeletedFalse(id).orElse(null);

        if (stock == null) {
            response.setMessage("Stock not found");
            response.setSuccess(false);
            return response;
        }
        return mapToDto(stock);
    }

    @Override
    public StockDto save(StockDto stockDto) {

        if (stockDto.getProductIdentifier() == null ||
                stockDto.getWarehouseIdentifier() == null) {

            stockDto.setMessage("Product & Warehouse required");
            stockDto.setSuccess(false);
            return stockDto;
        }

        String identifier = "STK-" + stockDto.getProductIdentifier() + "-" + stockDto.getWarehouseIdentifier();
        Stock existingStock = stockRepository.findByIdentifier(identifier);

        if (existingStock != null) {
            if (Boolean.TRUE.equals(existingStock.getDeleted())) {
                stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " was deleted and cannot be created again.");
                stockDto.setSuccess(false);
                return stockDto;
            }

            stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " already exists");
            stockDto.setSuccess(false);
            return stockDto;
        }

        Stock stock = new Stock();
        stock.setIdentifier(identifier);
        stock.setProductIdentifier(stockDto.getProductIdentifier());
        stock.setWarehouseIdentifier(stockDto.getWarehouseIdentifier());
        stock.setQuantity(stockDto.getQuantity());
        stock.setMinimumStock(stockDto.getMinimumStock());
        stock.setStatus(stockDto.getQuantity() > stockDto.getMinimumStock());
        setCreatedDetails(stock);
        stockRepository.save(stock);
        stockDto.setIdentifier(identifier);
        stockDto.setStatus(stock.getStatus());
        stockDto.setMessage("Stock created successfully");
        stockDto.setSuccess(true);
        return stockDto;
    }

    @Override
    public StockDto update(StockDto stockDto) {
        String identifier = stockDto.getIdentifier();
        Stock stock = stockRepository.findByIdentifierAndDeletedFalse(identifier);

        if (stock == null) {
            stockDto.setMessage(STOCK_WITH_IDENTIFIER + identifier + " not found");
            stockDto.setSuccess(false);
            return stockDto;
        }

        String newIdentifier = "STK-" + stockDto.getProductIdentifier() + "-" + stockDto.getWarehouseIdentifier();
        stock.setIdentifier(newIdentifier);
        stock.setProductIdentifier(stockDto.getProductIdentifier());
        stock.setWarehouseIdentifier(stockDto.getWarehouseIdentifier());
        stock.setQuantity(stockDto.getQuantity());
        stock.setMinimumStock(stockDto.getMinimumStock());
        stock.setStatus(stockDto.getQuantity() > stockDto.getMinimumStock());
        setModifiedDetails(stock);
        stockRepository.save(stock);
        stockDto.setIdentifier(newIdentifier);
        stockDto.setMessage("Stock updated successfully");
        stockDto.setSuccess(true);
        return stockDto;
    }

    @Override
    public void delete(String identifier) {
        Stock stock = stockRepository.findByIdentifierAndDeletedFalse(identifier);

        if (stock != null) {
            softDelete(stock);
            setModifiedDetails(stock);
            stockRepository.save(stock);
        }
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<StockDto>>() {}.getType();
        Page<Stock> stockPage = stockRepository.findAllByDeletedFalse(pageable);
        WsDto<StockDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(stockPage.getContent(), listType));
        wsDto.setTotalRecords(stockPage.getTotalElements());
        wsDto.setTotalPages(stockPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    private StockDto mapToDto(Stock stock) {
        StockDto dto = new StockDto();
        dto.setId(stock.getId());
        dto.setIdentifier(stock.getIdentifier());
        dto.setQuantity(stock.getQuantity());
        dto.setMinimumStock(stock.getMinimumStock());
        dto.setStatus(stock.getStatus());
        dto.setProductIdentifier(stock.getProductIdentifier());
        dto.setWarehouseIdentifier(stock.getWarehouseIdentifier());
        dto.setCreatedBy(stock.getCreatedBy());
        dto.setCreatedOn(stock.getCreatedOn());
        dto.setModifiedBy(stock.getModifiedBy());
        dto.setModifiedOn(stock.getModifiedOn());
        dto.setSuccess(true);
        return dto;
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