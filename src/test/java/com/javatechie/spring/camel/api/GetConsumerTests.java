package com.javatechie.spring.camel.api;

import com.javatechie.spring.camel.api.processor.OrderProcessor;
import com.javatechie.spring.camel.api.resource.ApplicationResource;
import com.javatechie.spring.camel.api.service.OrderService;
import org.apache.camel.BeanInject;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

public class GetConsumerTests {

    @InjectMocks
    private ApplicationResource applicationResource;


    @Test
    public void test() throws Exception {
        ApplicationResource applicationResource = new ApplicationResource();
        applicationResource.configure();
        assertNotNull(String.valueOf(applicationResource), "Not null");
    }
}
