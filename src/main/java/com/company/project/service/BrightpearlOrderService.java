package com.company.project.service;

import com.company.project.entity.rest.BrightpearlOrdersReq;
import com.company.project.entity.rest.OrderRes;
import com.company.project.entity.rest.RefreshAuthReq;
import com.company.project.entity.rest.RefreshAuthRes;

/**
 * @author : Cody.Teng
 * @date : 2024-02-13 4:36 p.m.
 */
public interface BrightpearlOrderService {

    OrderRes getBatchOrder(BrightpearlOrdersReq req);

    RefreshAuthRes refreshAuth(RefreshAuthReq req);

}
