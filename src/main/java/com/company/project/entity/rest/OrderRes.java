package com.company.project.entity.rest;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author : Cody.Teng
 * @date : 2024-02-14 4:54 p.m.
 */
@Data
public class OrderRes {

    private List<OrderResponse> response;

    @Data
    public static class OrderRow {
        private String rowId;
        private String productId;
        private String productName;
        private String productSku;
    }
    @Data
    public static class OrderResponse {
        private String id;
        private String parentOrderId;
        private String orderTypeCode;
        private String orderPaymentStatus;
        private Date createdOn;
        private Date updatedOn;
        private Map<String,OrderRow> orderRows;
        private List<OrderRow> orderRowList;
    }

    public void transfer(List<OrderResponse> response) {
        if (CollectionUtils.isEmpty(response)) {
            return;
        }
        for (OrderResponse orderResponse : response) {
            Map<String, OrderRow> orderRows = orderResponse.getOrderRows();
            if (CollectionUtils.isEmpty(orderRows)) {
                continue;
            }
            List<OrderRow> rowList = new ArrayList<>();
            for (String rowId : orderRows.keySet()) {
                OrderRow row = orderRows.get(rowId);
                row.setRowId(rowId);
                rowList.add(row);
            }
            orderResponse.setOrderRowList(rowList);
        }

    }
}
