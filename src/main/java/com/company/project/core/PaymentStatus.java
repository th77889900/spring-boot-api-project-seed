package com.company.project.core;

/**
 * 订单状态
 * @author : Cody.Teng
 * @date : 2024-02-21 4:21 p.m.
 */
public enum PaymentStatus {

    PAID("1"),//已经付款
    NOT_APPLICABLE("5"),//无需付款
    ;
    private final String code;

    PaymentStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
