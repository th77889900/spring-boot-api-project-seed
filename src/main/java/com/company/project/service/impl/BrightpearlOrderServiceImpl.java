package com.company.project.service.impl;

import com.alibaba.fastjson2.JSON;
import com.company.project.core.OrderStatus;
import com.company.project.entity.rest.*;
import com.company.project.service.BrightpearlOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author : Cody.Teng
 * @date : 2024-02-13 4:38 p.m.
 */
@Slf4j
@Service
public class BrightpearlOrderServiceImpl implements BrightpearlOrderService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${darwynn.warehouse.id}")
    private String wareHouseIdVal;

    @Value("${Brightpearl.url.host}")
    private String host;

    @Value("${Brightpearl.url.orderListPath}")
    private String orderListPath;

    @Value("${Brightpearl.url.orderPath}")
    private String orderPath;

    @Value("${Brightpearl.url.orderClosePath}")
    private String orderClosePath;

//    private static final String orderUrl = "https://use1.brightpearlconnect.com/public-api/" +
//            "queenofthronestest/order-service/order/";

    @Value("${Brightpearl.url.refresh.token}")
    private String refreshTokenUrl;

//    private static final String orderCloseUrl = "https://use1.brightpearlconnect.com/public-api/" +
//            "queenofthronestest/order-service/sales-order/%s/close";

    @Override
    public OrderRes getBatchOrder(BrightpearlOrdersReq req) {

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + req.getToken());
        headers.add("brightpearl-dev-ref", req.getBrightpearlDevRef());
        headers.add("brightpearl-app-ref", req.getBrightpearlAppRef());

        // Use HttpEntity to encapsulate headers, no body required for GET
        HttpEntity<String> entity = new HttpEntity<>(headers);

        StringBuilder urlSb = new StringBuilder();
        urlSb.append(host);
        urlSb.append(req.getEcshopId());
        urlSb.append(orderListPath);
        urlSb.append("orderTypeId=1");
        urlSb.append(StringUtils.isEmpty(req.getCreatedById()) ?
                StringUtils.EMPTY : "&createdById=" + req.getCreatedById());
        urlSb.append(StringUtils.isEmpty(req.getOrderPaymentStatusId()) ?
                StringUtils.EMPTY : "&orderPaymentStatusId=" + req.getOrderPaymentStatusId());

        // Make the order list call
        log.info("BrightpearlController.getBatchOrder to call order list API and the url is :{}, " +
                        "and the request type is {} and the params is {}", urlSb,
                HttpMethod.GET.name(),
                JSON.toJSONString(entity));

        ResponseEntity<BrightpearlOrdersRes> response = restTemplate.exchange(urlSb.toString(),
                HttpMethod.GET,
                entity,
                BrightpearlOrdersRes.class);
        log.info("BrightpearlController.getBatchOrder to call order list API and the response is :{}",
                JSON.toJSONString(response));
        BrightpearlOrdersRes body = response.getBody();
        if (Objects.isNull(body)) {
            return null;
        }
        BrightpearlOrdersRes.Response bodyResponse = body.getResponse();
        List<List<String>> resultList = bodyResponse.results;
        if (CollectionUtils.isEmpty(resultList)) {
            log.info("BrightpearlController.getBatchOrder to call order list API and the response body is null");
            return null;
        }
        Set<String> orderIds = new TreeSet<>();
        for (List<String> order : resultList) {
            String orderId = order.get(0);
            String orderStatus = order.get(3);
            // todo modify
            List<String> createCodes = OrderStatus.getCreateCodes();
            // 只需要手机下单和网站下单的数据
            if (StringUtils.isEmpty(orderStatus) || !createCodes.contains(orderStatus)) {
                continue;
            }
            orderIds.add(orderId);
        }

        if (CollectionUtils.isEmpty(orderIds)) {
            log.info("BrightpearlController.getBatchOrder to call orders and handle it ," +
                    "but the result of order id is empty");
            return null;
        }

        String joinedOrderIds = String.join(",", orderIds);
        StringBuilder orderUrlSb = new StringBuilder();
        orderUrlSb.append(host);
        orderUrlSb.append(req.getEcshopId());
        orderUrlSb.append(orderPath);
        orderUrlSb.append(joinedOrderIds);
        log.info("BrightpearlController.getBatchOrder to call order API and the url is :{} " +
                        "and the request type is {} and the param is {}",
                orderUrlSb, HttpMethod.GET.name(), JSON.toJSONString(entity));
        // Make the order list call
        ResponseEntity<OrderRes> orderResponse = restTemplate.exchange(orderUrlSb.toString(),
                HttpMethod.GET,
                entity,
                OrderRes.class);
        OrderRes orderRes = orderResponse.getBody();
        if (Objects.isNull(orderRes)) {
            log.info("BrightpearlController.getBatchOrder to call order API by order ids {} " +
                    "and the response is null", joinedOrderIds);
            return null;
        }
        List<OrderRes.OrderResponse> resResponse = orderRes.getResponse();
        if (CollectionUtils.isEmpty(resResponse)) {
            log.info("BrightpearlController.getBatchOrder to call order API by order ids {} " +
                    "and the response body list is empty", joinedOrderIds);
            return null;
        }
        List<OrderRes.OrderResponse> resultOrders = new ArrayList<>();
        for (OrderRes.OrderResponse orderDetail : resResponse) {
            String warehouseId = orderDetail.getWarehouseId();
            // 只获取darwynn（id 13）仓库的的数据
            if (StringUtils.isEmpty(warehouseId) || !warehouseId.equals(wareHouseIdVal)) {
                continue;
            }
            resultOrders.add(orderDetail);
        }
        if (CollectionUtils.isEmpty(resultOrders)) {
            log.info("BrightpearlController.getBatchOrder to call order API but there is not have darwynn order");
            return null;
        }
        orderRes.setResponse(resultOrders);
        return orderRes.transfer(orderRes);
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
                refreshTokenUrl, HttpMethod.POST.name(), JSON.toJSONString(entity));
        ResponseEntity<RefreshAuthRes> response = restTemplate.postForEntity(refreshTokenUrl, entity, RefreshAuthRes.class);
        return response.getBody();
    }

    @Override
    public String orderClose(OrderCloseReq req) {
        log.info("BrightpearlOrderServiceImpl.orderClose ...");
        if (StringUtils.isEmpty(req.getOrderId())) {
            return "order id is empty";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + req.getToken());
        headers.add("brightpearl-dev-ref", req.getBrightpearlDevRef());
        headers.add("brightpearl-app-ref", req.getBrightpearlAppRef());

        String orderCloseUrl = host + req.getEcshopId() + orderClosePath;

        String baseUrl = String.format(orderCloseUrl, req.getOrderId());
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        log.info("BrightpearlController.refreshAuth url is {}, and the request type is {} and the param is {}",
                baseUrl, HttpMethod.POST.name(), JSON.toJSONString(entity));
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(baseUrl, entity, String.class);
        } catch (Exception e) {
            log.error("BrightpearlOrderServiceImpl.orderClose url response error is {}, url is {} and the param is {} , and the response is {}",
                    e, baseUrl, JSON.toJSONString(entity), JSON.toJSONString(response));
            return e.getMessage();
        }
        log.info("BrightpearlOrderServiceImpl.orderClose url response is {}, and the param is {} , and the response is {}",
                baseUrl, JSON.toJSONString(entity), JSON.toJSONString(response));
        return "OK";
    }
}
