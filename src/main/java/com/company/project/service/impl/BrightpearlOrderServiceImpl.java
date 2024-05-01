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

//    @Value("${darwynn.warehouse.id}")
//    private String wareHouseIdVal;

    @Value("${Brightpearl.url.host}")
    private String host;

    @Value("${Brightpearl.url.orderListPath}")
    private String orderListPath;

    @Value("${Brightpearl.url.orderPath}")
    private String orderPath;

    @Value("${Brightpearl.url.orderClosePath}")
    private String orderClosePath;

    @Value("${Brightpearl.url.refresh.token}")
    private String refreshTokenUrl;


    @Value("${Brightpearl.url.inv.sync}")
    private String invSyncUrl;

    @Value("${Brightpearl.url.inv.sync.path}")
    private String invSyncPath;

    @Value("${Brightpearl.url.productId.by.sku}")
    private String productBySkuUrl;

    @Value("${Brightpearl.url.productId.by.barcode}")
    private String productByBarcodeUrl;
    @Value("${Brightpearl.url.stock.by.product}")
    private String stockByProductUrl;

    @Value("${Brightpearl.url.price.list}")
    private String priceListUrl;

    @Value("${Brightpearl.url.product.value}")
    private String productValueUrl;

    @Value("${Brightpearl.url.product.value.path}")
    private String productValuePath;


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
            if (StringUtils.isEmpty(warehouseId) || !warehouseId.equals(req.getWareHouseId())) {
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

    @Override
    public String invSync(InvSyncReq req) {
        log.info("BrightpearlOrderServiceImpl.invSync ...");
        if (StringUtils.isEmpty(req.getSku())) {
            return "param sku is empty";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + req.getToken());
        headers.add("brightpearl-dev-ref", req.getBrightpearlDevRef());
        headers.add("brightpearl-app-ref", req.getBrightpearlAppRef());

        // 根据sku获取productId
        Integer productId = getProductIdByBarcode(req.getSku(), headers, req.getEcshopId());
        if (Objects.isNull(productId)) {
            return "param sku is " + req.getSku() + " and get product is null";
        }

        // 根据productId获取可以可用库存数量
        Integer inStock = getInStockByProductId(productId, req.getWarehouseId(), headers, req.getEcshopId());
        if (Objects.isNull(inStock)) {
            log.warn("param sku is " + req.getSku() + " and product id is " + productId + " get inStock is null");
            inStock = 0;
        }
        // 计算差值
        int num = req.getQuantity() - inStock;
        List<InvSyncParamEntity> paramEntities = new ArrayList<>();
        InvSyncParamEntity syncParamEntity = new InvSyncParamEntity();
        syncParamEntity.setLocationId(req.getLocationId());
        syncParamEntity.setProductId(productId);
        syncParamEntity.setQuantity(num);
        syncParamEntity.setReason(num > 0 ? "add stock" : "delete stock");

        if (num == 0) {
            return "param sku is " + req.getSku() + " and no change in inventory";
        } else if (num > 0) {
            // 获取当前环境的价格列表信息，根据列表信息id获取cost对应的价格信息id
            PriceInfoResEntity priceInfo = getPriceList(headers, req.getEcshopId());
            if (Objects.isNull(priceInfo)) {
                return "param sku is " + req.getSku() + " get price info is empty";
            }
            // 根据商品id 和价格信息id 获取cost 对应的价格
            CostParamInfo costParamInfo = getValueByProductId(productId, priceInfo.getId(), headers, req.getEcshopId());
            if (Objects.isNull(costParamInfo)) {

            }
            log.info("BrightpearlOrderServiceImpl.invSync param is {}, and cost param info is {} ",
                    JSON.toJSONString(req),
                    JSON.toJSONString(costParamInfo));
            syncParamEntity.setCost(costParamInfo);
        }
        paramEntities.add(syncParamEntity);
        StringBuilder invSyncConnect = new StringBuilder();
        invSyncConnect.append(host);
        invSyncConnect.append(req.getEcshopId());
        invSyncConnect.append(invSyncUrl);
        invSyncConnect.append(req.getWarehouseId());
        invSyncConnect.append(invSyncPath);

        MultiValueMap<String, InvSyncParamEntity> map = new LinkedMultiValueMap<>();
        map.put("corrections", paramEntities);
        HttpEntity<MultiValueMap<String, InvSyncParamEntity>> entity = new HttpEntity<>(map, headers);
        log.info("BrightpearlController.invSync url is {}, and the request type is {} and the param is {}",
                invSyncConnect, HttpMethod.POST.name(), JSON.toJSONString(entity));
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(invSyncConnect.toString(), entity, String.class);
        } catch (Exception e) {
            log.error("BrightpearlOrderServiceImpl.invSync url response error is {}, url is {} and the param is {} , and the response is {}",
                    e, invSyncConnect, JSON.toJSONString(entity), JSON.toJSONString(response));
            return e.getMessage();
        }
        log.info("BrightpearlOrderServiceImpl.invSync url response is {}, and the param is {} , and the response is {}",
                invSyncConnect, JSON.toJSONString(entity), JSON.toJSONString(response));
        return "sku: " + req.getSku() + " and qty: " + req.getQuantity() + " sync success";
    }

    private Integer getProductIdByBarcode(String barcode, HttpHeaders headers, String ecshopId) {
        // Use HttpEntity to encapsulate headers, no body required for GET
        HttpEntity<String> entity = new HttpEntity<>(headers);
        StringBuilder urlSb = new StringBuilder();
        urlSb.append(host);
        urlSb.append(ecshopId);
        urlSb.append(productByBarcodeUrl);
        urlSb.append(barcode);

        // Make the order list call
        log.info("getProductIdByBarcode to call order list API and the url is :{}, " +
                        "and the request type is {} and the params is {}", urlSb,
                HttpMethod.GET.name(),
                JSON.toJSONString(entity));

        ResponseEntity<BrightpearlOrdersRes> response = restTemplate.exchange(urlSb.toString(),
                HttpMethod.GET,
                entity,
                BrightpearlOrdersRes.class);
        log.info("getProductIdByBarcode to call order list API and the response is :{}",
                JSON.toJSONString(response));
        if (Objects.isNull(response.getBody()) ||
                Objects.isNull(response.getBody().getResponse()) ||
                CollectionUtils.isEmpty(response.getBody().getResponse().results)) {
            return null;
        }
        List<List<String>> results = response.getBody().getResponse().getResults();
        List<String> strings = results.get(0);
        String productId = strings.get(0);
        if (StringUtils.isEmpty(productId)) {
            return null;
        }
        return Integer.valueOf(productId);
    }

    private Integer getProductIdBySKU(String sku, HttpHeaders headers, String ecshopId) {
        // Use HttpEntity to encapsulate headers, no body required for GET
        HttpEntity<String> entity = new HttpEntity<>(headers);
        StringBuilder urlSb = new StringBuilder();
        urlSb.append(host);
        urlSb.append(ecshopId);
        urlSb.append(productBySkuUrl);
        urlSb.append(sku);

        // Make the order list call
        log.info("getProductIdBySKU to call order list API and the url is :{}, " +
                        "and the request type is {} and the params is {}", urlSb,
                HttpMethod.GET.name(),
                JSON.toJSONString(entity));

        ResponseEntity<BrightpearlOrdersRes> response = restTemplate.exchange(urlSb.toString(),
                HttpMethod.GET,
                entity,
                BrightpearlOrdersRes.class);
        log.info("getProductIdBySKU to call order list API and the response is :{}",
                JSON.toJSONString(response));
        if (Objects.isNull(response.getBody()) ||
                Objects.isNull(response.getBody().getResponse()) ||
                CollectionUtils.isEmpty(response.getBody().getResponse().results)) {
            return null;
        }
        List<List<String>> results = response.getBody().getResponse().getResults();
        List<String> strings = results.get(0);
        String productId = strings.get(0);
        if (StringUtils.isEmpty(productId)) {
            return null;
        }
        return Integer.valueOf(productId);
    }

    private Integer getInStockByProductId(Integer productId, String warehouseId, HttpHeaders headers, String ecshopId) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        StringBuilder urlSb = new StringBuilder();
        urlSb.append(host);
        urlSb.append(ecshopId);
        urlSb.append(stockByProductUrl);
        urlSb.append(productId);
        // Make the order list call
        log.info("getInStockByProductId to call order list API and the url is :{}, " +
                        "and the request type is {} and the params is {}", urlSb,
                HttpMethod.GET.name(),
                JSON.toJSONString(entity));

        ResponseEntity<BrightpearlProductAvailRes> response = restTemplate.exchange(urlSb.toString(),
                HttpMethod.GET,
                entity,
                BrightpearlProductAvailRes.class);
        log.info("getInStockByProductId to call order list API and the response is :{}",
                JSON.toJSONString(response));
        if (Objects.isNull(response.getBody()) ||
                CollectionUtils.isEmpty(response.getBody().getResponse())) {
            return null;
        }
        Map<String, ProductAvailResEntity> productAvailMap = response.getBody().getResponse();
        ProductAvailResEntity productAvailResEntity = productAvailMap.get(String.valueOf(productId));
        if (Objects.isNull(productAvailResEntity)) {
            return null;
        }
        Map<String, StockAvailResEntity> warehouseMap = productAvailResEntity.getWarehouses();
        if (CollectionUtils.isEmpty(warehouseMap)) {
            return null;
        }
        StockAvailResEntity stockEntity = warehouseMap.get(warehouseId);
        if (Objects.isNull(stockEntity)) {
            return null;
        }
        return stockEntity.getInStock();
    }


    private PriceInfoResEntity getPriceList(HttpHeaders headers, String ecshopId) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        StringBuilder urlSb = new StringBuilder();
        urlSb.append(host);
        urlSb.append(ecshopId);
        urlSb.append(priceListUrl);
        // Make the order list call
        log.info("getPriceList to call order list API and the url is :{}, and the request type is {} and the params is {}",
                urlSb,
                HttpMethod.GET.name(),
                JSON.toJSONString(entity));
        ResponseEntity<BrightpearlPriceInfoRes> response = restTemplate.exchange(urlSb.toString(),
                HttpMethod.GET,
                entity,
                BrightpearlPriceInfoRes.class);
        log.info("getProductIdBySKU to call order list API and the response is :{}",
                JSON.toJSONString(response));
        if (Objects.isNull(response.getBody()) || CollectionUtils.isEmpty(response.getBody().getResponse())) {
            return null;
        }
        List<PriceInfoResEntity> results = response.getBody().getResponse();
        for (PriceInfoResEntity result : results) {
            String code = result.getCode();
            if ("COST".equals(code)) {
                return result;
            }
        }
        return null;
    }


    private CostParamInfo getValueByProductId(Integer productId, Integer priceId, HttpHeaders headers, String ecshopId) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        StringBuilder urlSb = new StringBuilder();
        urlSb.append(host);
        urlSb.append(ecshopId);
        urlSb.append(productValueUrl);
        urlSb.append(productId);
        urlSb.append(productValuePath);
        urlSb.append(priceId);

        log.info("getValueByProductId to call order list API and the url is :{}, " +
                        "and the request type is {} and the params is {}", urlSb,
                HttpMethod.GET.name(),
                JSON.toJSONString(entity));

        ResponseEntity<ProductPriceRes> response = restTemplate.exchange(urlSb.toString(),
                HttpMethod.GET,
                entity,
                ProductPriceRes.class);
        log.info("getValueByProductId to call order list API and the response is :{}",
                JSON.toJSONString(response));
        if (Objects.isNull(response.getBody()) || CollectionUtils.isEmpty(response.getBody().getResponse())) {
            return null;
        }
        List<ProductPriceResEntity> results = response.getBody().getResponse();
        ProductPriceResEntity productPriceResEntity = results.get(0);
        if (Objects.isNull(productPriceResEntity)) {
            return null;
        }
        List<ProductPriceResEntity.CostInfoList> priceLists = productPriceResEntity.getPriceLists();
        ProductPriceResEntity.CostInfoList costInfoList = priceLists.get(0);
        if (Objects.isNull(costInfoList)) {
            return null;
        }
        Map<String, String> quantityPrice = costInfoList.getQuantityPrice();
        if (CollectionUtils.isEmpty(quantityPrice)) {
            return null;
        }
        CostParamInfo costParamInfo = new CostParamInfo();
        costParamInfo.setCurrency(costInfoList.getCurrencyCode());

        Collection<String> values = quantityPrice.values();
        for (String value : values) {
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            costParamInfo.setValue(value);
            // 只需要获取一个数据就直接返回
            return costParamInfo;
        }
        return null;
    }
}
