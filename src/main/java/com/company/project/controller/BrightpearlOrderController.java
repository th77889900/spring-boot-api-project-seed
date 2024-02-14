package com.company.project.controller;

import com.company.project.service.BrightpearlOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brightpearl/queen")
public class BrightpearlOrderController {

    @Autowired
    private BrightpearlOrderService brightpearlOrderService;

    @Autowired
    private RestTemplate restTemplate;


    @PostMapping("/orders")
    public List<String> getBatchOrder() {

        List<String> results = new ArrayList<>();

        results.add("order1");
        results.add("order2");

        String url = "https://use1.brightpearlconnect.com/public-api/" +
                "queenofthronestest/order-service/order-search?" +
                "contactId={contactId}&orderTypeNames={orderTypeNames}&orderStatusNames={orderStatusNames}";
        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer KinMfjMIiLnXhylcVN4Ojp+kdhYKRNb+oNCUTlMWzEM=");
        headers.add("brightpearl-dev-ref", " darwynncompany123");
        headers.add("brightpearl-app-ref", "queens");

        // Use HttpEntity to encapsulate headers, no body required for GET
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Prepare URL variables
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("contactId", "359537");
        urlVariables.put("orderTypeNames", "1");
        urlVariables.put("orderStatusNames", "2");

        // Make the call
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, urlVariables);

        // Use the response as needed
        System.out.println(response.getBody());

        return results;
    }
}
