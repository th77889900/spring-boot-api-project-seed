package com.company.project.entity.rest;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author : Cody.Teng
 * @date : 2024-02-14 4:54 p.m.
 */
@Data
public class OrderRes {

    private List<OrderResponse> response;

    @Data
    private static class OrderRow {
        private String productId;
        private String productName;
        private String productSku;
    }
    @Data
    private static class OrderResponse {
        private String id;
        private String parentOrderId;
        private String orderTypeCode;
        private String orderPaymentStatus;
        private Date createdOn;
        private Date updatedOn;
        private Map<String,OrderRow> orderRows;

    }
}
