package com.company.project.entity.rest;

import lombok.Data;

/**
 * brightpearl 订单列表数据请求entity
 *
 * @author : Cody.Teng
 * @date : 2024-02-14 9:13 a.m.
 */
@Data
public class BrightpearlOrdersReq {

    private String contactId;
    private String orderTypeNames;
    private String orderStatusNames;
    private String createdById;
    private String orderPaymentStatusId;
    private String token;
    private String brightpearlDevRef;
    private String brightpearlAppRef;

}
