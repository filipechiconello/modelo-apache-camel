package com.javatechie.spring.camel.api.processor;

import com.javatechie.spring.camel.api.service.OrderService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessorUpdate implements Processor {

    @Autowired
    private OrderService service;

    @Override
    public void process(Exchange exchange) throws Exception {

    }
}
