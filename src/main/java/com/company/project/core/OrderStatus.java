package com.company.project.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单状态
 * @author : Cody.Teng
 * @date : 2024-02-21 4:21 p.m.
 */
public enum OrderStatus {

    INVOICE("4"),//关单 开票
    NEW_WEB_ORDER("2"),//网站下单
    NEW_PHONE_ORDER("3"),//手机下单
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
        return codes;
    }

}
