package com.javatechie.spring.camel.api.resource;

import com.javatechie.spring.camel.api.processor.GetConsumesProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetConsumes extends RouteBuilder {

    @Autowired
    private GetConsumesProcessor getConsumesProcessor;

    @Override
    public void configure() throws Exception {
        from("direct:callExternalApi")
                .setHeader("CamelHttpMethod", constant("GET"))
                .to("http4://650c8f7447af3fd22f67ca32.mockapi.io/api/v1/tests?bridgeEndpoint=true")
                .convertBodyTo(String.class)
                .process(getConsumesProcessor).endRest();
    }
}
