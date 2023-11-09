package com.javatechie.spring.camel.api.resource;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javatechie.spring.camel.api.dto.Order;
import com.javatechie.spring.camel.api.dto.TestResponseDTO;
import com.javatechie.spring.camel.api.processor.OrderProcessor;
import com.javatechie.spring.camel.api.processor.Test;
import com.javatechie.spring.camel.api.service.OrderService;
import org.apache.camel.BeanInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;


@Component
public class ApplicationResource extends RouteBuilder {

    @Autowired
    private OrderService service;

    @BeanInject
    private OrderProcessor processor;

    @Override
    public void configure() throws Exception {
        restConfiguration().component("servlet").port(9090).host("localhost").bindingMode(RestBindingMode.json);

        rest().get("/hello-world").produces(MediaType.APPLICATION_JSON_VALUE).route()
                .setBody(constant("Welcome to java techie")).endRest();

        rest().get("/getOrders")
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .route().setBody(() -> service.getOrders());

        rest().post("/addOrder")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .type(Order.class)
                .outType(Test.class)
                .route().process(processor)
                .bean(processor, "customResponse")
                .endRest();


        rest().put("/update/{id}")
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .type(Order.class)
                .outType(Order.class)
                .route()
                .process(exchange -> {
                    int id = Integer.parseInt(exchange.getIn().getHeader("id", String.class));
                    service.update(id, exchange.getIn().getBody(Order.class));
                    exchange.getMessage().setBody(exchange.getIn().getBody(Order.class));
                }).endRest();

        rest().put("/api/{id}")
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .route()
                .to("direct:callExternalApi3");

        ObjectMapper objectMapper = new ObjectMapper();

        from("direct:callExternalApi3")
                .marshal().json(JsonLibrary.Jackson)
                .convertBodyTo(String.class)
                .setHeader("CamelHttpMethod", constant("PUT"))
                .setHeader("Content-Type", constant("application/json"))
                .setHeader("Accept", constant("application/json"))
                .process(exchange -> {
                    //String id = exchange.getIn().getHeader("id", String.class);
                    //exchange.getIn().setHeader("id", id);
                    JsonNode json = objectMapper.readTree(exchange.getIn().getBody().toString());
                    String requestBodyJson = objectMapper.writeValueAsString(new TestResponseDTO(json.get("name").toString(), null));
                    exchange.getIn().setBody(requestBodyJson);
                })
                .toD("http4://650c8f7447af3fd22f67ca32.mockapi.io/api/v1/tests/${header.id}?bridgeEndpoint=true")
                .process(exchange -> {
                    // Processar a resposta
                    String responseBodyJson = exchange.getIn().getBody(String.class);
                    TestResponseDTO response = objectMapper.readValue(responseBodyJson, TestResponseDTO.class);
                    exchange.getIn().setBody(response);
                }).endRest();

        rest().delete("/delete/{id}")
                .outType(Test.class)
                .route()
                .process(exchange -> {
                    int id = Integer.parseInt(exchange.getIn().getHeader("id", String.class));
                    service.getOrders().removeIf(order -> order.getId() == id);
                    exchange.getMessage().setBody("OK");
                }).endRest();

        //Chamando API EXTERNA GET ALL
        rest().get("/api")
                .id("get-all")
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
                .route()
                .to("direct:callExternalApi");

        rest().post("/create")
                .id("route-criar")
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .consumes(MediaType.APPLICATION_JSON_VALUE)
//                .type(TestResponseDTO.class)
                //.outType(TestResponseDTO.class)
                .responseMessage()
                .code(HttpStatus.OK.value()).message("Criado")
                .endResponseMessage()
                .route()
                .to("direct:callExternalApi2");

        from("direct:callExternalApi2")
                .marshal().json(JsonLibrary.Jackson)
                .convertBodyTo(String.class)
                .setHeader("CamelHttpMethod", constant("POST"))
                .setHeader("Content-Type", constant("application/json"))
                .process(exchange -> {
                    JsonNode json = objectMapper.readTree(exchange.getIn().getBody().toString());
                    String requestBodyJson = objectMapper.writeValueAsString(new TestResponseDTO(json.get("name").toString(), null));
                    exchange.getIn().setBody(requestBodyJson);
                })
                .to("http4://650c8f7447af3fd22f67ca32.mockapi.io/api/v1/tests?bridgeEndpoint=true")
                .process(exchange -> {
                    // Processar a resposta
                    String responseBodyJson = exchange.getIn().getBody(String.class);
                    TestResponseDTO response = objectMapper.readValue(responseBodyJson, TestResponseDTO.class);
                    exchange.getIn().setBody(response);
                }).log("AQUIIIIIIIIIIIIIIIIIIIII ${body}")
                .endRest();
    }
}