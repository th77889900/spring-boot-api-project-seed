package com.company.project.service.impl;

import com.alibaba.fastjson2.JSON;
import com.company.project.entity.rest.*;
import com.company.project.service.BrightpearlOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : Cody.Teng
 * @date : 2024-02-13 4:38 p.m.
 */
@Slf4j
@Service
public class BrightpearlOrderServiceImpl implements BrightpearlOrderService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String orderListUrl = "https://use1.brightpearlconnect.com/public-api/" +
            "queenofthronestest/order-service/order-search?" +
            "contactId={contactId}&orderTypeNames={orderTypeNames}&orderStatusNames={orderStatusNames}";

    private static final String orderUrl = "https://use1.brightpearlconnect.com/public-api/" +
            "queenofthronestest/order-service/order/";

    private static final String url = "https://use1.brightpearlconnect.com/oauth/token";

    @Override
    public OrderRes getBatchOrder(BrightpearlOrdersReq req) {

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + req.getToken());
        headers.add("brightpearl-dev-ref", req.getBrightpearlDevRef());
        headers.add("brightpearl-app-ref", req.getBrightpearlAppRef());

        // Use HttpEntity to encapsulate headers, no body required for GET
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Prepare order list URL variables
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("contactId", req.getContactId());
        urlVariables.put("orderTypeNames", req.getOrderTypeNames());
        urlVariables.put("orderStatusNames", req.getOrderStatusNames());
        // Make the order list call
        log.info("BrightpearlController.getBatchOrder to call order list API and the url is :{}, " +
                        "and the request type is {} and the params is {}",
                orderListUrl,
                HttpMethod.GET.name(),
                JSON.toJSONString(entity));

        ResponseEntity<BrightpearlOrdersRes> response = restTemplate.exchange(orderListUrl,
                HttpMethod.GET,
                entity,
                BrightpearlOrdersRes.class,
                urlVariables);
        log.info("BrightpearlController.getBatchOrder to call order list API and the response is :{}",
                JSON.toJSONString(response));
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
        StringBuilder urlSb = new StringBuilder();

        urlSb.append(orderUrl);
        urlSb.append(joinedOrderIds);
        log.info("BrightpearlController.getBatchOrder to call order API and the url is :{} " +
                        "and the request type is {} and the param is {}",
                urlSb.toString(), HttpMethod.GET.name(), JSON.toJSONString(entity));
        // Make the order list call
        ResponseEntity<OrderRes> orderResponse = restTemplate.exchange(urlSb.toString(),
                HttpMethod.GET,
                entity,
                OrderRes.class);
        if (Objects.isNull(orderResponse)) {
            return null;
        }
        return orderResponse.getBody();
    }

    @Override
    public RefreshAuthRes refreshAuth(RefreshAuthReq req) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("grant_type", Collections.singletonList(req.getGrant_type()));
        map.put("refresh_token", Collections.singletonList(req.getRefresh_token()));
        map.put("client_id", Collections.singletonList(req.getClient_id()));
        map.put("account_code", Collections.singletonList(req.getAccount_code()));

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        log.info("BrightpearlController.refreshAuth url is {}, and the request type is {} and the param is {}",
                url, HttpMethod.POST.name(), JSON.toJSONString(entity));
        ResponseEntity<RefreshAuthRes> response = restTemplate.postForEntity(url, entity, RefreshAuthRes.class);
        return response.getBody();
    }
}
