package com.company.project.controller;

import com.alibaba.fastjson2.JSON;
import com.company.project.core.Result;
import com.company.project.entity.rest.BrightpearlOrdersRes;
import com.company.project.entity.rest.RefreshAuthReq;
import com.company.project.entity.rest.RefreshAuthRes;
import com.company.project.service.BrightpearlOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/brightpearl/queen")
public class BrightpearlController {

    @Autowired
    private BrightpearlOrderService brightpearlOrderService;

    @Autowired
    private RestTemplate restTemplate;


    @PostMapping("/orders")
    public List<String> getBatchOrder() {
        List<String> results = new ArrayList<>();

        String orderListUrl = "https://use1.brightpearlconnect.com/public-api/" +
                "queenofthronestest/order-service/order-search?" +
                "contactId={contactId}&orderTypeNames={orderTypeNames}&orderStatusNames={orderStatusNames}";

        String orderUrl = "https://use1.brightpearlconnect.com/public-api/" +
                "queenofthronestest/order-service/order/";

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer KinMfjMIiLnXhylcVN4Ojp+kdhYKRNb+oNCUTlMWzEM=");
        headers.add("brightpearl-dev-ref", " darwynncompany123");
        headers.add("brightpearl-app-ref", "queens");

        // Use HttpEntity to encapsulate headers, no body required for GET
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Prepare order list URL variables
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("contactId", "359537");
        urlVariables.put("orderTypeNames", "1");
        urlVariables.put("orderStatusNames", "2");
        // Make the order list call
        ResponseEntity<BrightpearlOrdersRes> response = restTemplate.exchange(orderListUrl,
                HttpMethod.GET,
                entity,
                BrightpearlOrdersRes.class,
                urlVariables);

        BrightpearlOrdersRes body = response.getBody();

        BrightpearlOrdersRes.Response bodyResponse = body.getResponse();
        List<List<String>> resultList = bodyResponse.results;
        Set<String> orderIds = new TreeSet<>();
        for (List<String> order : resultList) {
            String orderId = order.get(0);
            orderIds.add(orderId);
        }

        String joinedOrderIds = orderIds.stream()
                .collect(Collectors.joining(","));
        StringBuilder sb = new StringBuilder();

        sb.append(orderUrl);
        sb.append(joinedOrderIds);

        // Make the order list call
        ResponseEntity<String> orderResponse = restTemplate.exchange(sb.toString(),
                HttpMethod.GET,
                entity,
                String.class);

        // Use the response as needed
        System.out.println(response.getBody());

        return results;
    }

    @PostMapping("/refresh-auth")
    public Result<RefreshAuthRes> refreshAuth(@RequestBody RefreshAuthReq req) {
        RefreshAuthRes refreshAuthRes = new RefreshAuthRes();

        String url = "https://use1.brightpearlconnect.com/oauth/token";

        String requestJson = JSON.toJSONString(req);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.put("grant_type", Collections.singletonList(req.getGrant_type()));
        map.put("refresh_token", Collections.singletonList(req.getRefresh_token()));
        map.put("client_id", Collections.singletonList(req.getClient_id()));
        map.put("account_code", Collections.singletonList(req.getAccount_code()));

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<RefreshAuthRes> response = restTemplate.postForEntity(url, entity, RefreshAuthRes.class);
        RefreshAuthRes res = response.getBody();
        Result<RefreshAuthRes> resResult = new Result<>();
        resResult.setData(res);
        return resResult;

    }



}
