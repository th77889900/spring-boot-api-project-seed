package com.company.project.entity.rest;

import lombok.Data;

/**
 * Brightpearl 平台订单实体
 *
 * @author : Cody.Teng
 * @date : 2024-02-14 10:04 a.m.
 */
public class BrightpearlOrder {


    /**
     * 订单id
     */
    private String orderId;


    /**
     * 联系人id
     */
    private String contactId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
}
