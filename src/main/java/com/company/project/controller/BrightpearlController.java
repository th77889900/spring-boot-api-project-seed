package com.company.project.controller;

import com.alibaba.fastjson2.JSON;
import com.company.project.core.Result;
import com.company.project.entity.rest.BrightpearlOrdersReq;
import com.company.project.entity.rest.OrderRes;
import com.company.project.entity.rest.RefreshAuthReq;
import com.company.project.entity.rest.RefreshAuthRes;
import com.company.project.service.BrightpearlOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/brightpearl/queen")
public class BrightpearlController {

    @Autowired
    private BrightpearlOrderService brightpearlOrderService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/orders")
    public Result<OrderRes> getBatchOrder(@RequestBody BrightpearlOrdersReq req) {

        log.info("BrightpearlController.getBatchOrder request is :{}", JSON.toJSONString(req));

        OrderRes orderRes = brightpearlOrderService.getBatchOrder(req);
        Result<OrderRes> results = new Result<>();
        if (Objects.isNull(orderRes)) {
            return results;
        }
        results.setData(orderRes);
        log.info("BrightpearlController.getBatchOrder result is :{}",
                JSON.toJSONString(orderRes));
        return results;
    }

    @PostMapping("/refresh-auth")
    public Result<RefreshAuthRes> refreshAuth(@RequestBody RefreshAuthReq req) {
        log.info("BrightpearlController.refreshAuth req is {}", JSON.toJSONString(req));
        RefreshAuthRes refreshAuthRes = brightpearlOrderService.refreshAuth(req);
        log.info("BrightpearlController.refreshAuth url request result is {}", JSON.toJSONString(refreshAuthRes));
        Result<RefreshAuthRes> resResult = new Result<>();
        resResult.setData(refreshAuthRes);
        return resResult;
    }

}
