package com.ust.pos.stock.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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

@Service
@Transactional
public class StockServiceImpl extends BaseService implements StockService {

    public static final String STOCK_NOT_FOUND = "Stock not found";

    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public StockServiceImpl(StockRepository stockRepository,
                            ModelMapper modelMapper) {
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    private String calculateState(Stock stock) {

        if (Boolean.FALSE.equals(stock.getStatus())) {
            return "DISCONTINUED";
        }

        if (stock.getAvailableQuantity() == null || stock.getAvailableQuantity() <= 0) {
            return "OUT_OF_STOCK";
        }

        if (stock.getReorderLevel() != null
                && stock.getAvailableQuantity() <= stock.getReorderLevel()) {
            return "LOW_STOCK";
        }

        return "AVAILABLE";
    }

    @Override
    public StockDto save(StockDto dto) {

        dto.setIdentifier(dto.getProductIdentifier() + "_" + dto.getWarehouseIdentifier());
        Stock existing = stockRepository.findByIdentifier(dto.getIdentifier());

        if (existing != null) {

            if (!Boolean.TRUE.equals(existing.getDeleted())) { //to check delete
            } else {
                throw new ResourceNotFoundException(
                        "Product was deleted from this warehouse in '" + dto.getIdentifier()
                                + "' — restore it instead of creating new");
            }
        }

        Stock stock = modelMapper.map(dto, Stock.class);

        if (stock.getStatus() == null) {
            stock.setStatus(true);
        }

        setCreatedDetails(stock);
        Stock saved = stockRepository.save(stock);
        StockDto response = modelMapper.map(saved, StockDto.class);
        response.setStockState(calculateState(saved));
        response.setSuccess(true);
        response.setMessage("Stock saved successfully");

        return response;
    }

    @Override
    public StockDto update(StockDto dto) {

        dto.setIdentifier(dto.getProductIdentifier() + "_" + dto.getWarehouseIdentifier());

        Stock existing = stockRepository.findByIdentifier(dto.getIdentifier());

        if (existing == null || Boolean.TRUE.equals(existing.getDeleted())) {
            throw new ResourceNotFoundException(
                    "Stock with identifier '" + dto.getIdentifier() + "' not found");
        }

        existing.setAvailableQuantity(dto.getAvailableQuantity());
        existing.setReorderLevel(dto.getReorderLevel());
        existing.setStatus(dto.getStatus());
        setModifiedDetails(existing);
        Stock saved = stockRepository.save(existing);
        StockDto response = modelMapper.map(saved, StockDto.class);
        response.setStockState(calculateState(saved));
        response.setSuccess(true);
        response.setMessage("Stock updated successfully");
        return response;
    }

    @Override
    public StockDto findByIdentifier(String identifier) {

        Stock stock = stockRepository.findByIdentifier(identifier);

        if (stock == null || Boolean.TRUE.equals(stock.getDeleted())) {
            throw new ResourceNotFoundException(
                    "Stock with identifier '" + identifier + "' not found");
        }

        StockDto dto = modelMapper.map(stock, StockDto.class);
        dto.setStockState(calculateState(stock));
        dto.setSuccess(true);
        return dto;
    }

    @Override
    public WsDto<StockDto> findAll(Pageable pageable) {

        Page<Stock> stockPage = stockRepository.findByDeletedFalse(pageable);

        List<StockDto> dtoList = stockPage.getContent()
                .stream().map(s -> {
                    StockDto dto = modelMapper.map(s, StockDto.class);
                    dto.setStockState(calculateState(s));
                    return dto;}).toList();

        WsDto<StockDto> ws = new WsDto<>();
        ws.setDtoList(dtoList);
        ws.setTotalRecords(stockPage.getTotalElements());
        ws.setTotalPages(stockPage.getTotalPages());
        ws.setSizePerPage(pageable.getPageSize());
        ws.setPage(pageable.getPageNumber());
        return ws;
    }

    @Override
    public void delete(String identifier) {

        Stock stock = stockRepository.findByIdentifier(identifier);

        if (stock == null || Boolean.TRUE.equals(stock.getDeleted())) {
            throw new ResourceNotFoundException(
                    "Stock with identifier '" + identifier + "' not found");
        }

        stock.setDeleted(true);
        setModifiedDetails(stock);
        stockRepository.save(stock);
    }

    @Override
    public StockDto toggleStatus(String identifier) {

        Stock stock = stockRepository.findByIdentifier(identifier);

        if (stock == null || Boolean.TRUE.equals(stock.getDeleted())) {
            throw new ResourceNotFoundException(
                    "Stock with identifier '" + identifier + "' not found");
        }

        stock.setStatus(!Boolean.TRUE.equals(stock.getStatus()));
        setModifiedDetails(stock);
        Stock saved = stockRepository.save(stock);
        StockDto dto = modelMapper.map(saved, StockDto.class);
        dto.setStockState(calculateState(saved));
        dto.setSuccess(true);
        dto.setMessage("Stock status updated successfully");
        return dto;
    }

    @Override
    public List<StockDto> findActiveStock() {

        return stockRepository.findByStatusTrueAndDeletedFalse()
                .stream().map(s -> {
                    StockDto dto = modelMapper.map(s, StockDto.class);
                    dto.setStockState(calculateState(s));
                    return dto;}).toList();
    }

    @Override
    public boolean isStockAvailable(String productIdentifier, Integer quantity) {

        int totalAvailable = stockRepository.findByProductIdentifierAndDeletedFalse(productIdentifier)
                .stream().filter(s -> Boolean.TRUE.equals(s.getStatus()))
                .mapToInt(s -> s.getAvailableQuantity() != null ? s.getAvailableQuantity() : 0)
                .sum();
        return totalAvailable >= quantity;
    }

    @Override
    public StockDto reduceStock(String productIdentifier, Integer quantity) {

        List<Stock> stocks = stockRepository.findByProductIdentifierAndDeletedFalse(productIdentifier)
                .stream()
                .filter(s -> Boolean.TRUE.equals(s.getStatus()))
                .filter(s -> s.getAvailableQuantity() != null && s.getAvailableQuantity() > 0)
                .sorted((a, b) -> b.getAvailableQuantity() - a.getAvailableQuantity())
                .toList();

        int totalAvailable = stocks.stream()
                .mapToInt(Stock::getAvailableQuantity)
                .sum();

        if (totalAvailable < quantity) {
            StockDto dto = new StockDto();
            dto.setSuccess(false);
            dto.setMessage("Insufficient stock for product: " + productIdentifier);
            return dto;
        }

        int remaining = quantity;

        for (Stock s : stocks) {

            if (remaining <= 0) break;
            int take = Math.min(remaining, s.getAvailableQuantity());
            s.setAvailableQuantity(s.getAvailableQuantity() - take);
            setModifiedDetails(s);
            stockRepository.save(s);
            remaining -= take;
        }

        StockDto dto = new StockDto();
        dto.setProductIdentifier(productIdentifier);
        dto.setSuccess(true);
        dto.setMessage("Stock reduced successfully");

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