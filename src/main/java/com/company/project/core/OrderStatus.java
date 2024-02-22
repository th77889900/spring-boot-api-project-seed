package com.company.project.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单状态
 * @author : Cody.Teng
 * @date : 2024-02-21 4:21 p.m.
 */
public enum OrderStatus {

    NEW_WEB_ORDER("2"),//网站下单
    NEW_PHONE_ORDER("3"),//手机下单
    INVOICE("4"),//关单 开票
    CANCELLED("5"),//已删除订单
    BACK_ORDER("17"),//延期缴获单
    NEW_AMAZON_FBA_US_ORDER("23"),//手机下单
    NEW_US_SHOPIFY_ORDER("24"),//手机下单
    NEW_CA_SHOPIFY_ORDER("25"),//手机下单
    NEW_AMAZON_CANADA_ORDER("27"),//手机下单
    NEW_AMAZON_FBA_CA_ORDER("28"),//手机下单
    ;
    private final String code;

    OrderStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * 获取创建订单的code 手机下单 网站下单
     * @return
     */
    public static List<String> getCreateCodes() {
        List<String> codes = new ArrayList<>();
        codes.add(NEW_WEB_ORDER.getCode());
        codes.add(NEW_PHONE_ORDER.getCode());
        codes.add(NEW_AMAZON_FBA_US_ORDER.getCode());
        codes.add(NEW_US_SHOPIFY_ORDER.getCode());
        codes.add(NEW_CA_SHOPIFY_ORDER.getCode());
        codes.add(NEW_AMAZON_CANADA_ORDER.getCode());
        codes.add(NEW_AMAZON_FBA_CA_ORDER.getCode());
        return codes;
    }

}
