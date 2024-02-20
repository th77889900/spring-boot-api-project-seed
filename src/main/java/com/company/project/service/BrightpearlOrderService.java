package com.company.project.service;

import com.company.project.entity.rest.*;

/**
 * @author : Cody.Teng
 * @date : 2024-02-13 4:36 p.m.
 */
public interface BrightpearlOrderService {

    OrderRes getBatchOrder(BrightpearlOrdersReq req);

    RefreshAuthRes refreshAuth(RefreshAuthReq req);

    String orderClose(OrderCloseReq req);

}
