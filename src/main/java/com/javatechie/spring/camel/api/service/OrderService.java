package com.javatechie.spring.camel.api.service;

import com.javatechie.spring.camel.api.dto.Order;
import com.javatechie.spring.camel.api.processor.Test;
import org.apache.camel.Header;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private List<Order> list = new ArrayList<>();

    @PostConstruct
    public void initDB() {
        list.add(new Order(67, "Mobile", 5000));
        list.add(new Order(89, "Book", 300));
        list.add(new Order(45, "Shoes", 400));
    }

    public Order addOrder(Order order) {
        list.add(order);
        return order;
    }

    public List<Order> getOrders() {
        return list;
    }

    public Order update(int id, Order order) {
        list.forEach(order1 -> {
            if (id == order1.getId()) {
                order1.setId(id);
                order1.setName(order.getName());
                order1.setPrice(order.getPrice());
            }
        });

        return order;
    }
}