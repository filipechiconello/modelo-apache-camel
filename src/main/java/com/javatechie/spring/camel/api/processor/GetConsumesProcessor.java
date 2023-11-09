package com.javatechie.spring.camel.api.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javatechie.spring.camel.api.dto.TestResponseDTO;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GetConsumesProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<TestResponseDTO> list = new ArrayList<>();
        JsonNode json = mapper.readTree(exchange.getIn().getBody().toString());
        json.forEach(jsonNode -> {
            TestResponseDTO testResponseDTO = new TestResponseDTO();
            String name = jsonNode.get("name").toString().replaceAll("\\\\", "");
            testResponseDTO.setName(name + " deu certooooo");
            testResponseDTO.setId(jsonNode.get("id").toString().replaceAll("\\\\", ""));
            list.add(testResponseDTO);
        });
        exchange.getOut().setBody(list);
    }
}
