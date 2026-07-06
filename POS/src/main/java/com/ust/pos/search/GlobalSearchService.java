package com.ust.pos.search;

import com.ust.pos.model.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GlobalSearchService {

    private final ProductRepository productRepo;
    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;

    public GlobalSearchService(ProductRepository productRepo,
                               CustomerRepository customerRepo,
                               OrderRepository orderRepo) {
        this.productRepo = productRepo;
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
    }

    public Map<String, Object> search(String keyword) {

        ExampleMatcher matcher = ExampleMatcher.matchingAny()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Product productExample = new Product();
        productExample.setName(keyword);

        Customer customerExample = new Customer();
        customerExample.setIdentifier(keyword);

        Orders orderExample = new Orders();
        orderExample.setIdentifier(keyword);

        List<Product> products = productRepo.findAll(
                Example.of(productExample, matcher)
        );

        List<Customer> customers = customerRepo.findAll(
                Example.of(customerExample, matcher)
        );

        List<Orders> orders = orderRepo.findAll(
                Example.of(orderExample, matcher)
        );

        Map<String, Object> result = new HashMap<>();
        result.put("products", products);
        result.put("customers", customers);
        result.put("orders", orders);

        return result;
    }
}
