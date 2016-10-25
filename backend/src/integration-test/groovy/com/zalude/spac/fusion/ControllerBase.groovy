package com.zalude.spac.fusion

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import spock.lang.Specification;
import spock.lang.Stepwise;

@Stepwise
@WebIntegrationTest
@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = [SpacFusionApplication.class] )
class ControllerBase extends Specification {

    @Value('${local.server.port}') int port
    RestTemplate restTemplate
    Map<String, String> headers

    String getBasePath() { "" }

    void setup() {
        println("Integration Test Service URI: ${serviceURI()}")
        restTemplate = new TestRestTemplate()
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter())

        headers = new LinkedMultiValueMap<String, String>()
        headers.add("Content-Type", "application/json");
    }

    URI serviceURI(String path= "") {
        new URI("http://localhost:$port${basePath}${path}")
    }
}